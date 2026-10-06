param([string]$JdkHome = $env:JAVA_HOME)
$ErrorActionPreference = 'Stop'
$reportPath = Join-Path $PSScriptRoot 'execution.json'
$pom = Join-Path $PSScriptRoot 'pom.xml'
[xml]$definition = Get-Content -LiteralPath $pom -Raw
foreach ($source in @('src/main/java/guide/testing/JobService.java', 'src/test/java/guide/testing/JobServiceTest.java')) {
    if (-not (Test-Path -LiteralPath (Join-Path $PSScriptRoot $source))) { throw "Missing source $source" }
}
$compiler = if ($JdkHome) { Join-Path $JdkHome 'bin\javac.exe' } else { (Get-Command javac -ErrorAction SilentlyContinue).Source }
$maven = Get-Command mvn -ErrorAction SilentlyContinue
if (-not $compiler -or -not (Test-Path -LiteralPath $compiler) -or -not $maven) {
    $report = [ordered]@{ status='NOT EXECUTED'; reason='Existing JDK/Maven unavailable; no downloads authorized.'; staticStatus='PASS'; checks=@('POM XML parsed', 'Both Java source files present'); tests=0 }
    $report | ConvertTo-Json -Depth 4 | Set-Content -LiteralPath $reportPath -Encoding UTF8
    $report | ConvertTo-Json -Depth 4
    return
}
$previousJavaHome = $env:JAVA_HOME
try {
    if ($JdkHome) { $env:JAVA_HOME = $JdkHome }
    $output = & $maven.Source -o -B -f $pom test 2>&1
    $exitCode = $LASTEXITCODE
    $report = [ordered]@{ status=$(if ($exitCode -eq 0) { 'PASS' } else { 'FAIL' }); exitCode=$exitCode; output=@($output | ForEach-Object { "$_" }) }
    $report | ConvertTo-Json -Depth 4 | Set-Content -LiteralPath $reportPath -Encoding UTF8
    $report | ConvertTo-Json -Depth 4
    if ($exitCode -ne 0) { throw 'Offline Maven tests failed; no online retry authorized.' }
} finally { $env:JAVA_HOME = $previousJavaHome }