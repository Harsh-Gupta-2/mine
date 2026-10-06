$ErrorActionPreference = 'Stop'
$compose = Get-Content -LiteralPath (Join-Path $PSScriptRoot 'compose.json') -Raw | ConvertFrom-Json
if ($compose.services.probe.pull_policy -ne 'never' -or -not $compose.services.probe.read_only) { throw 'Local fixture isolation contract failed.' }
if ($compose.services.probe.ports[0] -ne '127.0.0.1:18080:8080') { throw 'Probe must bind only host loopback.' }
$dockerfile = Get-Content -LiteralPath (Join-Path $PSScriptRoot 'Dockerfile') -Raw
if (-not $dockerfile.Contains('COPY --from=build') -or -not $dockerfile.Contains('USER 10001:10001')) { throw 'Image contract failed.' }
$report = [ordered]@{
    status = 'NOT EXECUTED'
    staticStatus = 'PASS'
    checks = @('Compose JSON parsed', 'No implicit image pull', 'Loopback port and read-only runtime', 'Multi-stage and non-root instructions present')
    reason = 'Preflight only: Java compilation, image build and container execution not performed. Explicit existing base images and Docker are required; downloads are not authorized.'
    dockerOnPath = [bool](Get-Command docker -ErrorAction SilentlyContinue)
    javacOnPath = [bool](Get-Command javac -ErrorAction SilentlyContinue)
}
$report | ConvertTo-Json -Depth 4 | Set-Content -LiteralPath (Join-Path $PSScriptRoot 'execution.json') -Encoding UTF8
$report | ConvertTo-Json -Depth 4