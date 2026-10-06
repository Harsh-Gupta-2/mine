$ErrorActionPreference = 'Stop'
$runtime = Get-Command node -ErrorAction SilentlyContinue
$previousElectronMode = $env:ELECTRON_RUN_AS_NODE
$executable = if ($runtime) { $runtime.Source } else { Join-Path $env:LOCALAPPDATA 'Programs\Microsoft VS Code\Code.exe' }
if (-not (Test-Path -LiteralPath $executable)) { throw 'No existing Node-compatible runtime; no downloads attempted.' }
if (-not $runtime) { $env:ELECTRON_RUN_AS_NODE = '1' }
Push-Location $PSScriptRoot
try {
    & $executable -e "import('./WarehouseLab.mjs').catch(error => { console.error(error); process.exitCode = 1; })" | Out-Host
    if ($LASTEXITCODE -ne 0) { throw 'Warehouse model checks failed.' }
} finally {
    Pop-Location
    $env:ELECTRON_RUN_AS_NODE = $previousElectronMode
}
