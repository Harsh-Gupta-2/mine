#!/bin/sh
# usage: tools/pack.sh [project_root] -> the three deliverables in /mnt/user-data/outputs (stale PDF is not shipped)
R="${1:-.}"; O=/mnt/user-data/outputs; mkdir -p $O /tmp/pk/site; rm -rf /tmp/pk/proj /tmp/pk/site/*; mkdir -p /tmp/pk/proj
rm -f "$R/docs/qb/dist/"*.pdf "$R/docs/qb/dist/"*.print.html
cp -r "$R" "/tmp/pk/proj/Java Interview Handbook"
cp "$R/docs/qb/dist/java-interview-handbook.html" /tmp/pk/site/index.html; touch /tmp/pk/site/.nojekyll
printf '# Java Interview Handbook\n\nStatic GitHub Pages site. Deploy: Settings > Pages > main / (root).\n' > /tmp/pk/site/README.md
D=$(date +%F); rm -f $O/Java_Interview_Handbook_merged.zip $O/handbook-site-v2.zip
(cd /tmp/pk/proj && zip -q -r $O/Java_Interview_Handbook_merged.zip "Java Interview Handbook")
(cd /tmp/pk/site && zip -q -r $O/handbook-site-v2.zip . .nojekyll)
cp "$R/docs/qb/merge-map-$D.md" $O/ 2>/dev/null; ls -la $O
