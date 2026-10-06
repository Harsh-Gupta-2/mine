$ErrorActionPreference = 'Stop'
$root = Split-Path $PSScriptRoot -Parent
& (Join-Path $PSScriptRoot 'check-tags.ps1')
$publication = [System.IO.File]::ReadAllText((Join-Path $root 'handbook.json')) | ConvertFrom-Json
$registry = [System.IO.File]::ReadAllText((Join-Path $root 'sources.yml')) | ConvertFrom-Json
$questions = @()
$sections = @()
foreach ($file in (Get-ChildItem (Join-Path $root 'data') -Filter '*.json' | Sort-Object Name)) {
    $section = [System.IO.File]::ReadAllText($file.FullName) | ConvertFrom-Json
    $sections += [pscustomobject]@{ id=$section.section; title=$section.section_title; count=$section.questions.Count; target=$publication.section_targets.($section.section) }
    foreach ($question in $section.questions) {
        $question | Add-Member -NotePropertyName section -NotePropertyValue $section.section -Force
        $question | Add-Member -NotePropertyName section_title -NotePropertyValue $section.section_title -Force
        $resumeWeight = if ($question.resume_hooks.Count) { 2 } else { 1 }
        $evidenceWeight = switch ($question.evidence) { 'E3' {4} 'E2' {3} 'E1' {2} default { switch ($question.frequency) { 'est. very common' {2} 'est. common' {1.5} default {1} } } }
        $statusWeight = switch ($question.harsh_status) { 'GAP' {4} 'SHAKY' {3} 'UNTESTED' {2} default {1} }
        $levelWeight = if ($question.level -in @('L2','L3')) {2} else {1}
        $question | Add-Member -NotePropertyName hot_score -NotePropertyValue ($resumeWeight * $evidenceWeight * $statusWeight * $levelWeight) -Force
        $question | Add-Member -NotePropertyName score_factors -NotePropertyValue "$resumeWeight x $evidenceWeight x $statusWeight x $levelWeight" -Force
        if ($question.code) {
            $codePath = Join-Path $root $question.code.path
            if (-not (Test-Path $codePath)) { throw "Missing code: $codePath" }
            $question.code | Add-Member -NotePropertyName source -NotePropertyValue ([System.IO.File]::ReadAllText($codePath)) -Force
        }
        $questions += $question
    }
}
$allIds = @($questions.id)
$verifyCount = @($questions | ForEach-Object { $_.verify } | Where-Object { $_ }).Count
$codeCount = @($questions | Where-Object code).Count
$countStatus = if ($questions.Count -ge $publication.target_min -and $questions.Count -le $publication.target_max) { 'Count target met' } else { 'Count target not met' }
$publication.scope_notice = "$($questions.Count) questions across $($sections.Count) sections. $countStatus ($($publication.target_min)-$($publication.target_max)). $codeCount Java examples have recorded executions; $verifyCount explicit VERIFY items remain open. Count completion is not full technical or source verification. SQL drills without executed examples are conceptual, not verified database output. Personal facts remain placeholders where unspecified."
$triggers = @()
foreach ($trigger in $publication.resume_triggers) {
    foreach ($id in $trigger.direct_ids) { if ($id -notin $allIds) { throw "Unknown resume question: $id" } }
    $linked = @($questions | Where-Object { $question = $_; @($question.resume_hooks | Where-Object { $_ -in $trigger.hooks }).Count -gt 0 } | Select-Object -ExpandProperty id)
    $triggers += [pscustomobject]@{ id=$trigger.id; bullet=$trigger.bullet; gap=$trigger.gap; direct_ids=@($trigger.direct_ids); question_ids=@(($linked + $trigger.direct_ids) | Sort-Object -Unique) }
}
foreach ($playbook in $publication.priority_playbooks) {
    foreach ($id in $playbook.prepare_first) { if ($id -notin $allIds) { throw "Unknown playbook question: $id" } }
    foreach ($sourceId in $playbook.source_ids) { if (-not @($registry.sources | Where-Object { $_.id -eq $sourceId -and $_.eligible }).Count) { throw "Ineligible playbook source: $sourceId" } }
}
$indexes = [ordered]@{
    hot_list = @($questions | Sort-Object @{Expression='hot_score';Descending=$true},id | Select-Object -First 100 -Property id,question,hot_score,score_factors,section,harsh_status)
    resume_triggers = $triggers
    gap_drills = @($questions | Where-Object harsh_status -in @('GAP','SHAKY') | Group-Object topic | Sort-Object Name | ForEach-Object { [pscustomobject]@{ topic=$_.Name; question_ids=@($_.Group.id) } })
    coverage = $sections
    company_coverage = @($questions | ForEach-Object { $question=$_; foreach ($company in $question.companies) { [pscustomobject]@{ company=$company.name; question_id=$question.id; evidence=$question.evidence } } })
}
$payload = [ordered]@{
    questions = $questions
    sources = $registry.sources
    publication = $publication
    indexes = $indexes
    stack = 'Java 21 / Spring Boot 3.x / Spring Framework 6.x'
}
$json = $payload | ConvertTo-Json -Depth 30 -Compress
$safeJson = $json.Replace('<', '\u003c').Replace('>', '\u003e').Replace('&', '\u0026')
$template = [System.IO.File]::ReadAllText((Join-Path $PSScriptRoot 'preview-template.html'))
if (-not $template.Contains('@@HANDBOOK_DATA@@')) { throw 'Missing template placeholder' }
$html = $template.Replace('@@HANDBOOK_DATA@@', $safeJson)
$dist = Join-Path $root 'dist'
New-Item -ItemType Directory -Path $dist -Force | Out-Null
$encoding = New-Object System.Text.UTF8Encoding($false)
$output = Join-Path $dist 'java-interview-handbook.html'
[System.IO.File]::WriteAllText($output, $html, $encoding)
[System.IO.File]::WriteAllText((Join-Path $dist 'questions.json'), (ConvertTo-Json -InputObject @($questions) -Depth 30), $encoding)
[System.IO.File]::WriteAllText((Join-Path $dist 'indexes.json'), ($indexes | ConvertTo-Json -Depth 30), $encoding)
Write-Output "Built $($questions.Count) questions: $output"