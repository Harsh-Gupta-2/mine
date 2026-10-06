$ErrorActionPreference = 'Stop'
$root = Split-Path $PSScriptRoot -Parent
$chrome = 'C:\Program Files\Google\Chrome\Application\chrome.exe'
if (-not (Test-Path $chrome)) { throw 'Installed Chrome not found; use the HTML Print full edition control.' }
$html = Join-Path $root 'dist\java-interview-handbook.html'
$pdf = Join-Path $root 'dist\java-interview-handbook.pdf'
if (-not (Test-Path $html)) { throw 'Build the HTML first.' }
$profile = Join-Path $env:TEMP ('java-handbook-print-' + [guid]::NewGuid().ToString('N'))
$url = ([uri]$html).AbsoluteUri + '?print=1'
$baseArguments = @('--headless', '--disable-gpu', '--disable-background-networking', '--disable-component-update', '--disable-sync', '--disable-extensions', '--host-resolver-rules="MAP * ~NOTFOUND"', '--proxy-server=http://127.0.0.1:9', '--proxy-bypass-list=<-loopback>', '--no-first-run', '--no-default-browser-check', '--no-pdf-header-footer', '--virtual-time-budget=8000', "--user-data-dir=`"$profile`"")
$domPath = $profile + '-dom.html'
$errorPath = $profile + '-stderr.txt'
$domProcess = Start-Process -FilePath $chrome -ArgumentList ($baseArguments + @('--dump-dom', "`"$url`"")) -Wait -PassThru -NoNewWindow -RedirectStandardOutput $domPath -RedirectStandardError $errorPath
if ($domProcess.ExitCode -ne 0) { throw 'Print DOM generation failed' }
$dom = [System.IO.File]::ReadAllText($domPath)
$staticHtml = [regex]::Replace($dom, '<script\b[^>]*>.*?</script\s*>', '', [System.Text.RegularExpressions.RegexOptions]::Singleline -bor [System.Text.RegularExpressions.RegexOptions]::IgnoreCase)
$expected = [System.IO.File]::ReadAllText((Join-Path $root 'dist\questions.json')) | ConvertFrom-Json
$rendered = [regex]::Matches($staticHtml, '<details class="question"').Count
$answers = [regex]::Matches($staticHtml, '<div class="answer"').Count
if ($rendered -ne $expected.Count -or $answers -ne $expected.Count) { throw "Print preflight failed: $rendered questions / $answers answers; expected $($expected.Count)" }
$printPath = Join-Path $root 'dist\java-interview-handbook.print.html'
[System.IO.File]::WriteAllText($printPath, $staticHtml, (New-Object System.Text.UTF8Encoding($false)))
$printUrl = ([uri]$printPath).AbsoluteUri
Write-Output "Print preflight passed: $rendered question bodies and $answers answers in script-free HTML."
$arguments = $baseArguments + @("--print-to-pdf=`"$pdf`"", "`"$printUrl`"")
$process = Start-Process -FilePath $chrome -ArgumentList $arguments -Wait -PassThru -NoNewWindow
if ($process.ExitCode -ne 0 -or -not (Test-Path $pdf)) { throw 'PDF export failed. HTML remains available.' }
$bytes = [System.IO.File]::ReadAllBytes($pdf)
if ($bytes.Length -lt 10000 -or [System.Text.Encoding]::ASCII.GetString($bytes,0,5) -ne '%PDF-') { throw 'Invalid PDF output' }
Write-Output "PDF generated: $pdf ($($bytes.Length) bytes)"