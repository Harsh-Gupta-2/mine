# tools/ - CSV merge pipeline (run from the project root, the folder that contains docs/)

Input: CSV (interview_id, company, interview_date, role, experience_years, round, question, source_type, source_url, your_answer_notes).
Output: updated project zip, Pages zip, merge map.

1. `sh tools/setup.sh`                      once per chat (PowerShell 7, javac wrapper, jsdom)
2. `python3 tools/match.py in.csv .`        top-3 existing candidates per row. Same ask -> match, else new.
3. Write decisions.json (schema in the header of tools/apply.py). Author text ONLY for new rows.
4. `python3 tools/apply.py decisions.json .`
5. `sh tools/build.sh .`                    then wait ~2 min, `tail -2 /tmp/build.log`
6. `python3 tools/verify.py .` and `node tools/dom_test.js docs/qb/dist/java-interview-handbook.html`
7. Append 5 lines to docs/qb/state.md and review-log.md, then `sh tools/pack.sh .`
8. Remind Harsh: upload the 3 files to Drive folder "Harsh QNA repo".

Token rules: never print full lists or whole files; look only at top-3 candidates; open an existing answer only for a borderline match;
write all new records in one decisions.json; skip PDF (needs Chrome on his Windows machine); tests are already in the scripts.

Content rules (PROMPT.md): answer >= 65 words, 4-8 keywords, 2-3 followups, a trap; no claims about Harsh's own project (use [[HARSH TO FILL: ...]]);
version-sensitive facts get a verify entry; correct wrong answers; tags only from the CSV company; blank company stays E0.
Known: code/S18/JH-S18-019 is timing-dependent; full build.ps1 needs Windows + Chrome and was never run here (gates replicated in verify.py).
