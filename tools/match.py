#!/usr/bin/env python3
"""match.py input.csv [project_root] -> prints top-3 existing candidates per CSV row (compact)."""
import csv,json,re,math,collections,sys
root=(sys.argv[2] if len(sys.argv)>2 else '.')+'/docs/qb'
Q=json.load(open(root+'/dist/questions.json'))
stop=set("the a an of to in and or for is are what how do does can we you your with on vs between from by at as it be this that when why which use using".split())
tk=lambda s:[w for w in re.findall(r"[a-z0-9+#.]+",s.lower()) if w not in stop and len(w)>1]
docs=[tk(q['question']+' '+q['topic']+' '+' '.join(q['keywords'])) for q in Q]
df=collections.Counter(w for d in docs for w in set(d)); N=len(Q)
idf=lambda w:math.log((N+1)/(df.get(w,0)+1))+1
def sc(qt,d):
    c=collections.Counter(d); return sum(idf(w)*(1+math.log(c[w])) for w in set(qt) if w in c)
for i,r in enumerate(csv.DictReader(open(sys.argv[1],encoding='utf-8-sig')),1):
    qt=tk(r['question']); top=sorted(((sc(qt,docs[j]),j) for j in range(N)),reverse=True)[:3]
    print(f"#{i} [{r.get('company') or 'General'}] {r['question'][:80]}")
    for s,j in top: print(f"    {s:5.1f} {Q[j]['id']} {Q[j]['question'][:75]}")
