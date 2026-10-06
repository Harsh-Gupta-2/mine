#!/usr/bin/env python3
"""apply.py decisions.json [project_root]
decisions = {"interviews":{"I001":{"company":"Accolite","date":"2026-09","role":"..","exp":2.5,"types":["Service"],"url":null}},
 "rows":[{"interview_id":"I001","question":"..","match":["JH-S02-001"],"notes":"optional"} |
         {"interview_id":"I001","question":"..","new":{"sec":"S02","topic":..,"question":..,"level":"L2","round":"Technical","answer":..(>=65 words),
           "keywords":[4-8],"followups":[{"q":..,"a":..}] (2-3),"trap":..,"resume_hooks":[],"related":[],"verify":[],"code_dir":"optional dir with Main.java"}}]}
Company blank -> General: question stays E0 (note only). Evidence = E(min(3, distinct sources)); one source per interview_id."""
import json,os,sys,shutil,subprocess,collections,datetime
dec=json.load(open(sys.argv[1])); root=(sys.argv[2] if len(sys.argv)>2 else '.')+'/docs/qb'
today=datetime.date.today().isoformat()
secs={f[:-5]:json.load(open(f'{root}/data/{f}',encoding='utf-8')) for f in sorted(os.listdir(root+'/data')) if f.endswith('.json')}
ex={q['id']:q for d in secs.values() for q in d['questions']}
reg=json.load(open(root+'/sources.yml',encoding='utf-8'))
nxt=max(int(s['id'][1:]) for s in reg['sources'])+1
sid={}; supp=collections.defaultdict(set); meta=dec['interviews']
for iid,m in meta.items():
    if not m.get('company'): continue
    s=f"S{nxt:03d}"; nxt+=1; sid[iid]=s
    yr=int(m['date'][:4]) if m.get('date') else None
    reg['sources'].append({"id":s,"url":m.get('url'),"site":"user-provided interview notes","date_accessed":today,"company":m['company'],
      "role":m.get('role') or "Java backend (role not stated)","experience":m.get('exp'),"year":yr,"post_date":None,
      "rounds":[],"type":"user-provided","eligible":True,"dated":bool(m.get('date')),"independence_group":s,
      **({"interview_date":m['date']} if m.get('date') else {}),"supported_question_ids":[],
      "notes":f"Interview {iid} supplied by Harsh via CSV on {today}. Only the fact the question was asked is evidence."})
def tag(q,iid):
    m=meta.get(iid,{})
    if iid not in sid: return
    s=sid[iid]; yr=int(m['date'][:4]) if m.get('date') else None
    if not any(s in c['sources'] for c in q['companies']):
        was0=not q['companies']
        q['companies'].append({"name":m['company'],"sources":[s],"year":yr})
        n=len({x for c in q['companies'] for x in c['sources']})
        q['evidence']=f"E{min(3,n)}"; q['frequency']="seen in 1 source" if n==1 else f"seen in {n} sources"
        t=m.get('types') or ["Service"]
        q['company_types']=t if was0 else list(dict.fromkeys(q['company_types']+t))
    supp[s].add(q['id'])
def jcompile(d):
    jc='javac' if shutil.which('javac') else None
    cmd=[jc] if jc else ['/tmp/javac.sh']
    subprocess.run(cmd+['-d',d,d+'/Main.java'],check=True)
mm=[]; nn={s:max(int(q['id'].split('-')[2]) for q in d['questions'])+1 for s,d in secs.items()}
for i,r in enumerate(dec['rows'],1):
    iid=r['interview_id']; m=meta.get(iid,{}); co=m.get('company') or 'General'
    cn=f"Candidate notes: {r['notes']}" if r.get('notes') else ""
    if r.get('match'):
        for qid in r['match']:
            q=ex[qid]; tag(q,iid)
            q['notes']=(q['notes']+" " if q.get('notes') else "")+f"Reported in interview {iid} ({co}{', '+m['date'] if m.get('date') else ''}). {cn}".strip()
            mm.append((i,r['question'],co,'existing',qid,q['evidence']))
    else:
        n=r['new']; s=n['sec']; qid=f"JH-{s}-{nn[s]:03d}"; nn[s]+=1
        code=None
        if n.get('code_dir'):
            dst=f"{root}/code/{s}/{qid}"; os.makedirs(dst,exist_ok=True); shutil.copy(n['code_dir']+'/Main.java',dst+'/Main.java')
            b=f"{root}/code/_build/{qid}"; os.makedirs(b,exist_ok=True); jcompile_dir=b
            subprocess.run((['javac'] if shutil.which('javac') else ['/tmp/javac.sh'])+['-d',b,dst+'/Main.java'],check=True)
            out=subprocess.run(['java','-cp',b,'Main'],capture_output=True,text=True,check=True).stdout
            jv=subprocess.run(['java','-version'],capture_output=True,text=True).stderr.splitlines()[0]
            code={"path":f"code/{s}/{qid}/Main.java","executed":True,"java":jv,"output":"\n".join(out.splitlines()).rstrip("\r\n")}
        rel=[x for x in n.get('related',[]) if x in ex]
        q={"id":qid,"topic":n['topic'],"question":n['question'],"level":n['level'],"level_basis":"est.","round":n['round'],
           "evidence":"E0","frequency":n.get('frequency','est. common'),"companies":[],"company_types":["Product","Service"],
           "resume_hooks":n.get('resume_hooks',[]),"harsh_status":"UNTESTED","answer":" ".join(n['answer'].split()),
           "keywords":n['keywords'],"followups":n['followups'],"trap":n['trap'],"fs_link":None,"code":code,"verify":n.get('verify',[]),
           "notes":(f"Reported in interview {iid} ({co}{', '+m['date'] if m.get('date') else ''}). Model answer written for this handbook."
                    +(" Related: "+", ".join(rel)+"." if rel else "")+(" "+cn if cn else ""))}
        tag(q,iid); secs[s]['questions'].append(q); ex[qid]=q
        mm.append((i,r['question'],co,'new',qid,q['evidence']))
for s,d in secs.items(): json.dump(d,open(f'{root}/data/{s}.json','w',encoding='utf-8',newline='\n'),ensure_ascii=False,indent=2)
for s in reg['sources']:
    if s['id'] in supp: s['supported_question_ids']=sorted(set(s['supported_question_ids'])|supp[s['id']])
json.dump(reg,open(root+'/sources.yml','w',encoding='utf-8',newline='\n'),ensure_ascii=False,indent=2)
L=[f"# Merge map {today}","","| CSV row | Question | Company | Result | Handbook ID | Evidence |","|---|---|---|---|---|---|"]+[f"| {a} | {b} | {c} | {d} | {e} | {f} |" for a,b,c,d,e,f in mm]
open(f"{root}/merge-map-{today}.md",'w',encoding='utf-8').write("\n".join(L)+"\n")
print(len(mm),"rows applied;",len(sid),"new sources;",sum(1 for m in mm if m[3]=='new'),"new records")
