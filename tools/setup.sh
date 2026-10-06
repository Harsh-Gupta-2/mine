#!/bin/sh
# One-time per chat: PowerShell 7 (runs the project's .ps1 build), javac wrapper, jsdom for HTML test.
[ -x /home/claude/pwsh/pwsh ] || (cd /home/claude && curl -sL -m 150 -o pwsh.tgz https://github.com/PowerShell/PowerShell/releases/download/v7.4.6/powershell-7.4.6-linux-x64.tar.gz && mkdir -p pwsh && tar -xzf pwsh.tgz -C pwsh && chmod +x pwsh/pwsh)
printf '#!/bin/sh\nexec java -m jdk.compiler/com.sun.tools.javac.Main "$@"\n' > /tmp/javac.sh; chmod +x /tmp/javac.sh
[ -d /tmp/jt/node_modules/jsdom ] || (mkdir -p /tmp/jt && cd /tmp/jt && npm install jsdom --silent --no-audit --no-fund)
echo setup done
