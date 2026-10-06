# Compiles and runs every code sample under docs/qb/code and prints its real output.
$ErrorActionPreference = 'Stop'
$jdk = Join-Path $env:USERPROFILE '.vscode\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64'
if (-not (Test-Path $jdk)) { throw "JDK not found at $jdk. Update the path in run-all.ps1." }
$javac = Join-Path $jdk 'bin\javac.exe'
$java  = Join-Path $jdk 'bin\java.exe'

$root = $PSScriptRoot
$out  = Join-Path $root '_build'
Remove-Item $out -Recurse -Force -ErrorAction SilentlyContinue

$failed = @()
Get-ChildItem $root -Recurse -Filter 'Main.java' | ForEach-Object {
    $qid = $_.Directory.Name
    $bin = Join-Path $out $qid
    New-Item -ItemType Directory -Path $bin -Force | Out-Null

    Write-Host "=== $qid ===" -ForegroundColor Cyan
    & $javac -d $bin $_.FullName 2>&1 | Write-Host
    if ($LASTEXITCODE -ne 0) { $failed += "$qid (compile)"; return }

    & $java -cp $bin Main 2>&1 | Write-Host
    if ($LASTEXITCODE -ne 0) { $failed += "$qid (run)" }
}

if ($failed.Count) {
    Write-Host "`nFAILED: $($failed -join ', ')" -ForegroundColor Red
    exit 1
}
Write-Host "`nAll samples compiled and ran." -ForegroundColor Green
