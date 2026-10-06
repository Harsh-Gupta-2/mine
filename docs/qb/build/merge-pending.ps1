$ErrorActionPreference = 'Stop'
$root = Split-Path $PSScriptRoot -Parent
$dataRoot = Join-Path $root 'data'
$jdk = Join-Path $env:USERPROFILE '.vscode\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin'
$encoding = New-Object System.Text.UTF8Encoding($false)
foreach ($recordFile in (Get-ChildItem $dataRoot -Recurse -Filter '*.json')) {
    $document = [System.IO.File]::ReadAllText($recordFile.FullName) | ConvertFrom-Json
    $changed = $false
    foreach ($record in $document.questions) {
        if ('resume_hooks' -notin $record.PSObject.Properties.Name) {
            $record | Add-Member -NotePropertyName resume_hooks -NotePropertyValue @()
            $changed = $true
        }
        if ('company_types' -notin $record.PSObject.Properties.Name) {
            $record | Add-Member -NotePropertyName company_types -NotePropertyValue @('Product','Service')
            $changed = $true
        }
    }
    if ($changed) { [System.IO.File]::WriteAllText($recordFile.FullName, ($document | ConvertTo-Json -Depth 30), $encoding) }
}
foreach ($file in (Get-ChildItem (Join-Path $dataRoot '.pending') -Filter '*.json' | Sort-Object Name)) {
    $fragment = [System.IO.File]::ReadAllText($file.FullName) | ConvertFrom-Json
    $destination = Join-Path $dataRoot ($fragment.section + '.json')
    $section = [System.IO.File]::ReadAllText($destination) | ConvertFrom-Json
    $existing = @{}
    foreach ($question in $section.questions) { $existing[$question.id] = $question }
    foreach ($question in $fragment.questions) {
        if ($question.id -notmatch ('^JH-' + $fragment.section + '-\d{3}$') -or $question.keywords.Count -lt 4 -or $question.keywords.Count -gt 8 -or $question.followups.Count -lt 2 -or $question.followups.Count -gt 3 -or ($question.answer -split '\s+').Count -lt 65) { throw "Invalid pending record: $($question.id)" }
        if ($question.evidence -ne 'E0' -or $question.companies.Count -ne 0) { throw "Unexpected pending attribution: $($question.id)" }
        if ($question.code) {
            $source = Join-Path $root $question.code.path
            $bin = Join-Path $root ('code\_build\' + $question.id)
            New-Item -ItemType Directory -Path $bin -Force | Out-Null
            & (Join-Path $jdk 'javac.exe') -d $bin $source
            if ($LASTEXITCODE -ne 0) { throw "Pending compile failure: $($question.id)" }
            $output = @(& (Join-Path $jdk 'java.exe') -cp $bin Main)
            if ($LASTEXITCODE -ne 0) { throw "Pending execution failure: $($question.id)" }
            $normalized = ($output -join "`n").TrimEnd("`r", "`n")
            if ($question.code.output -and $normalized -cne $question.code.output.Replace("`r`n", "`n").TrimEnd("`r", "`n")) { throw "Pending output mismatch: $($question.id)" }
            $question.code.output = $normalized
            $question.code.executed = $true
        }
        if ($existing.ContainsKey($question.id)) {
            if (($existing[$question.id] | ConvertTo-Json -Depth 30 -Compress) -cne ($question | ConvertTo-Json -Depth 30 -Compress)) { throw "Conflicting existing record: $($question.id)" }
        } else {
            $section.questions = @($section.questions) + $question
            $existing[$question.id] = $question
        }
    }
    $section.questions = @($section.questions | Sort-Object id)
    if (@($section.questions | Group-Object question | Where-Object Count -gt 1).Count) { throw "Duplicate question text: $($fragment.section)" }
    [System.IO.File]::WriteAllText($destination, ($section | ConvertTo-Json -Depth 30), $encoding)
    Write-Output "Merged $($file.Name): $($section.questions.Count) $($section.section) records"
}
& (Join-Path $PSScriptRoot 'check-tags.ps1')