$ErrorActionPreference = 'Stop'
$manifest = Get-Content -LiteralPath (Join-Path $PSScriptRoot 'workload.json') -Raw | ConvertFrom-Json
$deployment = @($manifest.items | Where-Object { $_.kind -eq 'Deployment' })[0]
$service = @($manifest.items | Where-Object { $_.kind -eq 'Service' })[0]
$hpa = @($manifest.items | Where-Object { $_.kind -eq 'HorizontalPodAutoscaler' })[0]
$pod = $deployment.spec.template.spec
$container = $pod.containers[0]
if ($service.spec.selector.app -ne $deployment.spec.template.metadata.labels.app -or $deployment.spec.selector.matchLabels.app -ne $service.spec.selector.app) { throw 'Selectors do not agree.' }
if ($service.spec.ports[0].targetPort -ne $container.ports[0].name) { throw 'Named service port is not bound.' }
if ($hpa.spec.scaleTargetRef.name -ne $deployment.metadata.name) { throw 'HPA target mismatch.' }
if ($container.imagePullPolicy -ne 'Never' -or $pod.automountServiceAccountToken -ne $false) { throw 'Fixture isolation policy failed.' }
if ($container.readinessProbe.httpGet.path -ne '/ready' -or $container.livenessProbe.httpGet.path -ne '/live') { throw 'Probe contract mismatch.' }
$report = [ordered]@{
    status = 'NOT EXECUTED'
    staticStatus = 'PASS'
    checks = @('JSON parsed', 'Selectors and named ports agree', 'HPA target agrees', 'Local image and token policy', 'Readiness/liveness paths match probe')
    reason = 'Local file checks only; no Kubernetes API schema validation, admission, apply, scheduling or runtime test.'
    kubectlOnPath = [bool](Get-Command kubectl -ErrorAction SilentlyContinue)
}
$report | ConvertTo-Json -Depth 4 | Set-Content -LiteralPath (Join-Path $PSScriptRoot 'execution.json') -Encoding UTF8
$report | ConvertTo-Json -Depth 4