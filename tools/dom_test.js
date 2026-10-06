// node dom_test.js path/to/java-interview-handbook.html  (needs /tmp/jt/node_modules/jsdom)
const {JSDOM,VirtualConsole}=require('/tmp/jt/node_modules/jsdom');const fs=require('fs');
const f=process.argv[2],html=fs.readFileSync(f,'utf8'),err=[];const vc=new VirtualConsole();vc.on('jsdomError',e=>err.push(String(e.message).slice(0,150)));
(async()=>{const dom=new JSDOM(html,{runScripts:'dangerously',pretendToBeVisual:true,virtualConsole:vc,url:'file://'+f+'?print=1'});
await new Promise(r=>setTimeout(r,3000));const d=dom.window.document;
console.log('print mode: questions',d.querySelectorAll('details.question').length,'answers',d.querySelectorAll('.answer').length,'| sources',d.querySelectorAll('.source-record').length,'| href=null:',/href="null"/.test(d.body.innerHTML),'| script errors:',err.length?err:'none');})();
