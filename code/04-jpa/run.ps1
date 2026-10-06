param([string]$JdkHome = $env:JAVA_HOME)
$ErrorActionPreference = 'Stop'
$reportPath = Join-Path $PSScriptRoot 'execution.json'
$compiler = if ($JdkHome) { Join-Path $JdkHome 'bin\javac.exe' } else { (Get-Command javac -ErrorAction SilentlyContinue).Source }
$maven = Get-Command mvn -ErrorAction SilentlyContinue
if (-not $compiler -or -not (Test-Path -LiteralPath $compiler) -or -not $maven) {
    $report = [ordered]@{ status = 'NOT EXECUTED'; reason = 'Existing JDK, Maven and cached ORM dependencies required. Only diagram downloads were authorized.'; compile = 'not executed'; labs = @('JpaLab: not executed'); timestamp = (Get-Date).ToString('o') }
    $report | ConvertTo-Json -Depth 4 | Set-Content -LiteralPath $reportPath -Encoding UTF8
    $report | ConvertTo-Json -Depth 4
    return
}
$previousJavaHome = $env:JAVA_HOME
$previousPath = $env:PATH
Push-Location $PSScriptRoot
try {
    if ($JdkHome) { $env:JAVA_HOME = $JdkHome; $env:PATH = "$(Join-Path $JdkHome 'bin');$previousPath" }
    $output = & $maven.Source -o -B -Dstyle.color=never compile exec:java '-Dexec.mainClass=guide.jpa.JpaLab' 2>&1
    $exitCode = $LASTEXITCODE
    $report = [ordered]@{ status = $(if ($exitCode -eq 0) { 'PASS' } else { 'FAIL' }); offline = $true; exitCode = $exitCode; output = @($output | ForEach-Object { "$_" }); timestamp = (Get-Date).ToString('o') }
    $report | ConvertTo-Json -Depth 4 | Set-Content -LiteralPath $reportPath -Encoding UTF8
    $report | ConvertTo-Json -Depth 4
    if ($exitCode -ne 0) { throw 'Offline JPA build/lab failed; do not retry online without authorization.' }
} finally {
    Pop-Location
    $env:JAVA_HOME = $previousJavaHome
    $env:PATH = $previousPath
}