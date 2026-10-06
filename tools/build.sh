#!/bin/sh
# usage: tools/build.sh [project_root]  -> check-tags (fast) then build-preview in background (~2 min). Poll: tail /tmp/build.log
export DOTNET_SYSTEM_GLOBALIZATION_INVARIANT=1; cd "${1:-.}/docs/qb" || exit 1
/home/claude/pwsh/pwsh -NoProfile -File build/check-tags.ps1 2>&1 | tail -2
nohup /home/claude/pwsh/pwsh -NoProfile -File build/build-preview.ps1 > /tmp/build.log 2>&1 &
echo "build started"
