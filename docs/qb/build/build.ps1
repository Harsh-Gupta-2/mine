param([switch]$RequireFullTarget)

$ErrorActionPreference = 'Stop'
$root = Split-Path $PSScriptRoot -Parent
if ($RequireFullTarget) {
    $publication = [System.IO.File]::ReadAllText((Join-Path $root 'handbook.json')) | ConvertFrom-Json
    $records = @(Get-ChildItem (Join-Path $root 'data') -Filter '*.json' | ForEach-Object { ([System.IO.File]::ReadAllText($_.FullName) | ConvertFrom-Json).questions })
    if ($records.Count -lt $publication.target_min -or $records.Count -gt $publication.target_max) {
        throw "Full-target gate failed: $($records.Count) questions; required $($publication.target_min)-$($publication.target_max). A preview is not full-target completion."
    }
    foreach ($target in $publication.section_targets.PSObject.Properties) {
        if ($null -eq $target.Value) { continue }
        $sectionRecords = @($records | Where-Object { $_.id -like "JH-$($target.Name)-*" })
        if ($sectionRecords.Count -lt [int]$target.Value) { throw "Section target failed: $($target.Name) has $($sectionRecords.Count), requires $($target.Value)" }
    }
}
& (Join-Path $PSScriptRoot 'check-tags.ps1')
& (Join-Path $root 'code\run-all.ps1')
& (Join-Path $PSScriptRoot 'build-preview.ps1')
$questions = [System.IO.File]::ReadAllText((Join-Path $root 'dist\questions.json')) | ConvertFrom-Json
$indexes = [System.IO.File]::ReadAllText((Join-Path $root 'dist\indexes.json')) | ConvertFrom-Json
$ids = @($questions.id)
if (@($indexes.coverage).Count -ne 21) { throw 'Expected coverage entries for 21 sections' }
if (@($indexes.hot_list).Count -ne [Math]::Min(100,$questions.Count)) { throw 'Hot List count mismatch' }
$previousScore = [double]::PositiveInfinity
foreach ($entry in $indexes.hot_list) {
    if ($entry.id -notin $ids -or $entry.hot_score -gt $previousScore) { throw 'Invalid Hot List rank or link' }
    $previousScore = $entry.hot_score
}
foreach ($group in @($indexes.resume_triggers) + @($indexes.gap_drills)) {
    foreach ($id in $group.question_ids) { if ($id -notin $ids) { throw "Broken index reference: $id" } }
}
$jdk = Join-Path $env:USERPROFILE '.vscode\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\java.exe'
foreach ($question in $questions) {
    if (($question.answer -split '\s+').Count -lt 65) { throw "Short answer: $($question.id)" }
    if ($question.code) {
        if (-not $question.code.executed -or -not $question.code.source) { throw "Missing code verification: $($question.id)" }
        $actual = @(& $jdk -cp (Join-Path $root "code\_build\$($question.id)") Main)
        if ($LASTEXITCODE -ne 0 -or ($actual -join "`n") -cne $question.code.output) { throw "Recorded output mismatch: $($question.id)" }
    }
}
& (Join-Path $PSScriptRoot 'build-pdf.ps1')
$bytes = [System.IO.File]::ReadAllBytes((Join-Path $root 'dist\java-interview-handbook.pdf'))
$pages = [regex]::Matches([System.Text.Encoding]::ASCII.GetString($bytes), '/Type\s*/Page\b').Count
if ($pages -lt 21) { throw 'PDF lacks expected multipage content' }
Write-Output "PUBLICATION PASS: $($questions.Count) questions; 21 sections; 100 Hot List entries; verified index links and code output; $pages PDF pages."