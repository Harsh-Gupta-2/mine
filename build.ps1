param([switch]$HtmlOnly, [switch]$Test)
$ErrorActionPreference = 'Stop'
$runtime = Get-Command node -ErrorAction SilentlyContinue
$previousElectronMode = $env:ELECTRON_RUN_AS_NODE
if ($runtime) {
    $executable = $runtime.Source
} else {
    $executable = Join-Path $env:LOCALAPPDATA 'Programs\Microsoft VS Code\Code.exe'
    if (-not (Test-Path -LiteralPath $executable)) { throw 'Node or the installed VS Code runtime is required. No runtime is downloaded by this script.' }
    $env:ELECTRON_RUN_AS_NODE = '1'
}
Push-Location $PSScriptRoot
try {
    if ($Test) {
        & $executable -e "import('./verify.mjs').catch(error => { console.error(error); process.exitCode = 1; })" | Out-Host
    } else {
        $pdf = if ($HtmlOnly) { 'false' } else { 'true' }
        & $executable -e "import('./build.mjs').then(module => module.build({pdf:$pdf})).catch(error => { console.error(error); process.exitCode = 1; })" | Out-Host
    }
    if ($LASTEXITCODE -ne 0) { throw "Guide command failed with exit code $LASTEXITCODE" }
} finally {
    Pop-Location
    $env:ELECTRON_RUN_AS_NODE = $previousElectronMode
}