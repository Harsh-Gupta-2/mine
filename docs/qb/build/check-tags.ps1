# Fails if any company tag references a source id that is not in sources.yml.
$ErrorActionPreference = 'Stop'
$root = Split-Path $PSScriptRoot -Parent
$registry = [System.IO.File]::ReadAllText((Join-Path $root 'sources.yml')) | ConvertFrom-Json
$sourceById = @{}
foreach ($source in $registry.sources) {
    if ($sourceById.ContainsKey($source.id)) { throw "Duplicate source id: $($source.id)" }
    $sourceById[$source.id] = $source
}
$seenIds = @{}
$seenQuestions = @{}
$bad = @()
$total = 0

Get-ChildItem (Join-Path $root 'data') -Filter '*.json' | ForEach-Object {
    $d = Get-Content $_.FullName -Raw | ConvertFrom-Json
    $qs = $d.questions
    $total += $qs.Count
    Write-Host "$($d.section): $($qs.Count) questions"
    $qs | Group-Object evidence | Sort-Object Name | ForEach-Object {
        Write-Host "   $($_.Name): $($_.Count)"
    }
    ($qs.id | Group-Object | Where-Object Count -gt 1) | ForEach-Object {
        $bad += "duplicate id $($_.Name)"
    }
    foreach ($q in $qs) {
        $required = @('id','topic','question','level','level_basis','round','evidence','frequency','companies','company_types','resume_hooks','harsh_status','answer','keywords','followups','trap','fs_link','code','verify','notes')
        foreach ($field in $required) { if ($field -notin $q.PSObject.Properties.Name) { $bad += "$($q.id) missing required field: $field" } }
        foreach ($field in @('companies','company_types','resume_hooks','keywords','followups','verify')) { if ($q.$field -isnot [array]) { $bad += "$($q.id) field must be an array: $field" } }
        if ($seenIds.ContainsKey($q.id)) { $bad += "Duplicate global id: $($q.id)" }
        $seenIds[$q.id] = $true
        if ($seenQuestions.ContainsKey($q.question)) { $bad += "Duplicate question: $($q.id)" }
        $seenQuestions[$q.question] = $true
        if ($q.keywords.Count -lt 4 -or $q.keywords.Count -gt 8 -or $q.followups.Count -lt 2 -or $q.followups.Count -gt 3) { $bad += "Invalid answer metadata: $($q.id)" }
        if ([string]::IsNullOrWhiteSpace($q.answer) -or [string]::IsNullOrWhiteSpace($q.trap)) { $bad += "Missing answer/trap: $($q.id)" }
        $groups = @()
        foreach ($c in $q.companies) {
            foreach ($s in $c.sources) {
                $source = $sourceById[$s]
                if (-not $source) { $bad += "$($q.id) cites missing source $s"; continue }
                if (-not $source.eligible -or $source.company -ne $c.name -or $source.year -ne $c.year -or $q.id -notin $source.supported_question_ids) { $bad += "$($q.id) has unsupported company/date attribution: $s" }
                if ($source.type -ne 'user-provided' -and ([uri]$source.url).Host -notin $registry.meta.approved_hosts) { $bad += "Unapproved source host: $s" }
                $groups += $source.independence_group
            }
        }
        $n = @($groups | Select-Object -Unique).Count
        $expected = 'E' + [Math]::Min(3, $n)
        if ($q.evidence -ne $expected) { $bad += "$($q.id) evidence must be $expected, not $($q.evidence)" }
        if ($q.evidence -eq 'E0' -and ($q.companies.Count -gt 0 -or $q.frequency -notmatch '^est\. (very common|common|occasional)$')) { $bad += "$($q.id) has invalid E0 metadata" }
        $expectedFrequency = if ($n -eq 1) { 'seen in 1 source' } else { "seen in $n sources" }
        if ($n -gt 0 -and $q.frequency -ne $expectedFrequency) { $bad += "$($q.id) has incorrect source frequency" }
        if ($q.code -and $q.code.executed -eq $false) { $bad += "$($q.id) ships unexecuted code" }
    }
}

Write-Host "`ntotal questions: $total"
if ($bad.Count) {
    Write-Host "TAG CHECK FAILED:" -ForegroundColor Red
    $bad | ForEach-Object { Write-Host "  $_" -ForegroundColor Red }
    exit 1
}
Write-Host "tag check: PASS" -ForegroundColor Green
