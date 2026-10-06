$ErrorActionPreference = 'Stop'
$python = Get-Command python -ErrorAction SilentlyContinue
if (-not $python -or $python.Source -like '*WindowsApps*') {
    $report = [ordered]@{ status = 'NOT EXECUTED'; reason = 'No non-store Python runtime found on PATH; no downloads authorized.'; tests = 0 }
    $report | ConvertTo-Json | Set-Content -LiteralPath (Join-Path $PSScriptRoot 'execution.json') -Encoding UTF8
    $report | ConvertTo-Json
    return
}
& $python.Source (Join-Path $PSScriptRoot 'IntegrationLab.py')
if ($LASTEXITCODE -ne 0) { throw 'Integration model tests failed.' }