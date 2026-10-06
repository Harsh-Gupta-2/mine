param([string]$JdkHome = $env:JAVA_HOME, [switch]$Benchmark)
$ErrorActionPreference = 'Stop'
$reportPath = Join-Path $PSScriptRoot 'execution.json'
[xml]$pom = Get-Content -LiteralPath (Join-Path $PSScriptRoot 'pom.xml') -Raw
foreach ($source in @('ProfilingLab.java', 'src/main/java/guide/performance/SumBenchmark.java')) {
    if (-not (Test-Path -LiteralPath (Join-Path $PSScriptRoot $source))) { throw "Missing source $source" }
}
$compiler = if ($JdkHome) { Join-Path $JdkHome 'bin\javac.exe' } else { (Get-Command javac -ErrorAction SilentlyContinue).Source }
$maven = Get-Command mvn -ErrorAction SilentlyContinue
if (-not $compiler -or -not (Test-Path -LiteralPath $compiler) -or ($Benchmark -and -not $maven)) {
    $report = [ordered]@{ status='NOT EXECUTED'; reason='Existing JDK or requested Maven prerequisites unavailable; no downloads authorized.'; staticStatus='PASS'; checks=@('POM XML parsed', 'JFR and JMH sources present'); jfr='NOT EXECUTED'; jmh='NOT EXECUTED' }
    $report | ConvertTo-Json -Depth 4 | Set-Content -LiteralPath $reportPath -Encoding UTF8
    $report | ConvertTo-Json -Depth 4
    return
}
$java = Join-Path (Split-Path $compiler) 'java.exe'
$outputDirectory = Join-Path $PSScriptRoot 'out'
[System.IO.Directory]::CreateDirectory($outputDirectory) | Out-Null
$output = @(& $compiler --release 21 --add-modules jdk.jfr -d $outputDirectory (Join-Path $PSScriptRoot 'ProfilingLab.java') 2>&1)
$passed = $LASTEXITCODE -eq 0
$jfrStatus = 'NOT EXECUTED'
$jmhStatus = 'NOT EXECUTED'
if ($passed) {
    $output += & $java --add-modules jdk.jfr -cp $outputDirectory ProfilingLab (Join-Path $outputDirectory 'guide-profile.jfr') 2>&1
    $passed = $LASTEXITCODE -eq 0
    $jfrStatus = $(if ($passed) { 'PASS' } else { 'FAIL' })
}
if ($Benchmark -and $passed) {
    $previousJavaHome = $env:JAVA_HOME
    try {
        if ($JdkHome) { $env:JAVA_HOME = $JdkHome }
        $output += & $maven.Source -o -B -f (Join-Path $PSScriptRoot 'pom.xml') package 2>&1
        $passed = $LASTEXITCODE -eq 0
        if ($passed) {
            $output += & $java -jar (Join-Path $PSScriptRoot 'target\benchmarks.jar') -rf json -rff (Join-Path $outputDirectory 'jmh-result.json') 2>&1
            $passed = $LASTEXITCODE -eq 0
            $jmhStatus = $(if ($passed) { 'PASS' } else { 'FAIL' })
        }
    } finally { $env:JAVA_HOME = $previousJavaHome }
}
$report = [ordered]@{ status=$(if ($passed) { 'PASS' } else { 'FAIL' }); jfr=$jfrStatus; jmh=$jmhStatus; output=@($output | ForEach-Object { "$_" }) }
$report | ConvertTo-Json -Depth 4 | Set-Content -LiteralPath $reportPath -Encoding UTF8
$report | ConvertTo-Json -Depth 4
if (-not $passed) { throw 'Profiling fixture failed; see execution.json.' }