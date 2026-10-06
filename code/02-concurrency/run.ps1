param([string]$JdkHome = $env:JAVA_HOME)
$ErrorActionPreference = 'Stop'
$reportPath = Join-Path $PSScriptRoot 'execution.json'
$compiler = if ($JdkHome) { Join-Path $JdkHome 'bin\javac.exe' } else { (Get-Command javac -ErrorAction SilentlyContinue).Source }
if (-not $compiler -or -not (Test-Path -LiteralPath $compiler)) {
    $report = [ordered]@{ status = 'NOT EXECUTED'; reason = 'No existing JDK compiler found. Downloads are not authorized.'; compile = 'not executed'; labs = @('SnapshotLab: not executed', 'ConcurrencyLab: not executed'); timestamp = (Get-Date).ToString('o') }
    $report | ConvertTo-Json -Depth 4 | Set-Content -LiteralPath $reportPath -Encoding UTF8
    $report | ConvertTo-Json -Depth 4
    return
}
$java = Join-Path (Split-Path $compiler) 'java.exe'
$outputDirectory = Join-Path $PSScriptRoot 'out'
[System.IO.Directory]::CreateDirectory($outputDirectory) | Out-Null
$compilerOutput = & $compiler --release 21 -Xlint:all -d $outputDirectory (Join-Path $PSScriptRoot 'SnapshotLab.java') (Join-Path $PSScriptRoot 'ConcurrencyLab.java') 2>&1
$compileExit = $LASTEXITCODE
$runs = @()
if ($compileExit -eq 0) {
    foreach ($lab in @('SnapshotLab', 'ConcurrencyLab')) {
        $output = & $java -cp $outputDirectory $lab 2>&1
        $runs += [ordered]@{ lab = $lab; exitCode = $LASTEXITCODE; output = @($output | ForEach-Object { "$_" }) }
    }
}
$passed = $compileExit -eq 0 -and $runs.Count -eq 2 -and @($runs | Where-Object { $_.exitCode -ne 0 }).Count -eq 0
$report = [ordered]@{ status = $(if ($passed) { 'PASS' } else { 'FAIL' }); compiler = $compiler; compileExit = $compileExit; compilerOutput = @($compilerOutput | ForEach-Object { "$_" }); runs = $runs; timestamp = (Get-Date).ToString('o') }
$report | ConvertTo-Json -Depth 6 | Set-Content -LiteralPath $reportPath -Encoding UTF8
$report | ConvertTo-Json -Depth 6
if (-not $passed) { throw 'Java lab compilation or checks failed; see execution.json.' }