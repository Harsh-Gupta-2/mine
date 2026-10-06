param([string]$JdkHome = $env:JAVA_HOME)
$ErrorActionPreference = 'Stop'
$reportPath = Join-Path $PSScriptRoot 'execution.json'
$compiler = if ($JdkHome) { Join-Path $JdkHome 'bin\javac.exe' } else { (Get-Command javac -ErrorAction SilentlyContinue).Source }
$maven = Get-Command mvn -ErrorAction SilentlyContinue
if (-not $compiler -or -not (Test-Path -LiteralPath $compiler) -or -not $maven) {
    $report = [ordered]@{ status = 'NOT EXECUTED'; reason = 'Existing JDK and Maven are required; downloads are not authorized.'; compile = 'not executed'; labs = @('ProxyBoundaryLab: not executed', 'ContainerTransactionLab: not executed'); timestamp = (Get-Date).ToString('o') }
    $report | ConvertTo-Json -Depth 4 | Set-Content -LiteralPath $reportPath -Encoding UTF8
    $report | ConvertTo-Json -Depth 4
    return
}
$previousJavaHome = $env:JAVA_HOME
$previousPath = $env:PATH
Push-Location $PSScriptRoot
try {
    if ($JdkHome) {
        $env:JAVA_HOME = $JdkHome
        $env:PATH = "$(Join-Path $JdkHome 'bin');$previousPath"
    }
    $compilerOutput = & $maven.Source -o -B -Dstyle.color=never compile 2>&1
    $compileExit = $LASTEXITCODE
    $runs = @()
    if ($compileExit -eq 0) {
        foreach ($lab in @('guide.spring.ProxyBoundaryLab', 'guide.spring.ContainerTransactionLab')) {
            $output = & $maven.Source -o -B -Dstyle.color=never exec:java "-Dexec.mainClass=$lab" 2>&1
            $runs += [ordered]@{ lab = $lab; exitCode = $LASTEXITCODE; output = @($output | ForEach-Object { "$_" }) }
        }
    }
    $passed = $compileExit -eq 0 -and $runs.Count -eq 2 -and @($runs | Where-Object { $_.exitCode -ne 0 }).Count -eq 0
    $report = [ordered]@{ status = $(if ($passed) { 'PASS' } else { 'FAIL' }); offline = $true; compileExit = $compileExit; compilerOutput = @($compilerOutput | ForEach-Object { "$_" }); runs = $runs; timestamp = (Get-Date).ToString('o') }
    $report | ConvertTo-Json -Depth 6 | Set-Content -LiteralPath $reportPath -Encoding UTF8
    $report | ConvertTo-Json -Depth 6
    if (-not $passed) { throw 'Offline Maven build or lab failed. Missing cached dependencies must not trigger an online retry without authorization.' }
} finally {
    Pop-Location
    $env:JAVA_HOME = $previousJavaHome
    $env:PATH = $previousPath
}