$ErrorActionPreference = 'Stop'
$config = Get-Content -LiteralPath (Join-Path $PSScriptRoot 'main.tf.json') -Raw | ConvertFrom-Json
$bucket = $config.resource.aws_s3_bucket.artifact
$block = $config.resource.aws_s3_bucket_public_access_block.artifact
if ($bucket.force_destroy -ne $false -or $bucket.lifecycle.prevent_destroy -ne $true) { throw 'Deletion guards missing.' }
foreach ($setting in @('block_public_acls', 'block_public_policy', 'ignore_public_acls', 'restrict_public_buckets')) {
    if ($block.$setting -ne $true) { throw "Public access control missing: $setting" }
}
if ($config.resource.aws_s3_bucket_versioning.artifact.versioning_configuration[0].status -ne 'Enabled') { throw 'Versioning declaration missing.' }
if ($config.resource.aws_s3_bucket_server_side_encryption_configuration.artifact.rule[0].apply_server_side_encryption_by_default[0].sse_algorithm -ne 'AES256') { throw 'Encryption declaration mismatch.' }
$report = [ordered]@{
    status = 'NOT EXECUTED'
    staticStatus = 'PASS'
    checks = @('JSON parsed', 'Deletion guards present', 'Public access blocks declared', 'Versioning declared', 'Storage encryption declared')
    reason = 'No Terraform syntax/provider validation, init, plan, apply or AWS access. Provider downloads and account operations are not authorized by this preflight.'
    terraformOnPath = [bool](Get-Command terraform -ErrorAction SilentlyContinue)
    awsOnPath = [bool](Get-Command aws -ErrorAction SilentlyContinue)
}
$report | ConvertTo-Json -Depth 4 | Set-Content -LiteralPath (Join-Path $PSScriptRoot 'execution.json') -Encoding UTF8
$report | ConvertTo-Json -Depth 4