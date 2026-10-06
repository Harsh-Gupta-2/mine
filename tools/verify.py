#!/usr/bin/env python3
"""verify.py [project_root]: gates from build.ps1 + code re-execution. Run AFTER build-preview.ps1."""
import json,collections,subprocess,sys,os
from concurrent.futures import ThreadPoolExecutor
root=(sys.argv[1] if len(sys.argv)>1 else '.')+'/docs/qb'
Q=json.load(open(root+'/dist/questions.json')); I=json.load(open(root+'/dist/indexes.json')); H=json.load(open(root+'/handbook.json'))
ids={q['id'] for q in Q}; assert len(ids)==len(Q) and len({q['question'] for q in Q})==len(Q)
assert H['target_min']<=len(Q)<=H['target_max'],"count outside target"
assert len(I['coverage'])==21 and len(I['hot_list'])==min(100,len(Q))
p=1e9
for e in I['hot_list']: assert e['id'] in ids and e['hot_score']<=p; p=e['hot_score']
for g in I['resume_triggers']+I['gap_drills']: assert set(g['question_ids'])<=ids
for pb in H['priority_playbooks']: assert set(pb['prepare_first'])<=ids
print("short answers:",[q['id'] for q in Q if len(q['answer'].split())<65])
print(len(Q),"questions | evidence",dict(sorted(collections.Counter(q['evidence'] for q in Q).items())),"| VERIFY entries",sum(1 for q in Q if q['verify']))
def run(q):
    try:
        r=subprocess.run(['java','-cp',f"{root}/code/_build/{q['id']}",'Main'],capture_output=True,text=True,timeout=40)
        return q['id'],(r.returncode==0 and "\n".join(r.stdout.splitlines()).rstrip()==q['code']['output'].replace('\r\n','\n').rstrip())
    except subprocess.TimeoutExpired: return q['id'],False
res=list(ThreadPoolExecutor(4).map(run,[q for q in Q if q['code']]))
print("code samples:",len(res),"| not reproduced:",[i for i,ok in res if not ok],"(JH-S18-019 is a known flaky sample)")
