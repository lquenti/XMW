import{c as F,j as e,a as X,p as P,b as R,f as z,L as h}from"./index-SsTAdifg.js";import{u as V}from"./use-courses-BCKvGkJt.js";import{u as Y}from"./use-exams-D7t73XWJ.js";import{u as q}from"./use-lecturers-DR4LyVYW.js";import{u as A}from"./use-lectures-Dxze9bPC.js";const G=F("/exa/ui/lecturers/$lecturerId")({component:J});function J(){const{lecturerId:b}=G.useParams(),{data:y,isLoading:O}=V(),{data:j,isLoading:H}=q(),{data:f,isLoading:I}=Y(),{data:N,isLoading:S}=A();if(O||H||I||S)return e.jsx("div",{className:"flex items-center justify-center min-h-[400px]",children:e.jsx("div",{className:"animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"})});if(!y||!j||!f||!N)return e.jsx("div",{className:"p-4 bg-yellow-50 text-yellow-600 rounded-lg",children:"No data available"});const v=X(y),W=P(j),C=R(f),E=z(N),i=W.find(s=>s.id===b),d=v.filter(s=>s.lecturer===b),k=C.filter(s=>d.some(r=>r.id===s.course)),M=E.filter(s=>d.some(r=>r.id===s.course)),m=s=>{if(!s)return null;try{const r=new Date(s);if(!isNaN(r.getTime()))return r;const t=s.match(/^(\d{4}-\d{2}-\d{2})T(\d+)T:(\d{2}):(\d{2})$/);if(!t)return null;const[,n,a,o,c]=t,x=`${a.padStart(2,"0")}:${o}:${c}`,p=`${n} ${x}`,D=new Date(p);return isNaN(D.getTime())?null:D}catch(r){return console.error("Error parsing date:",r),null}},T=s=>s?s.toLocaleString("en-US",{year:"numeric",month:"long",day:"numeric",hour:"numeric",minute:"2-digit"}):"Date not set",B=s=>{if(!s)return"Unknown Semester";const r=s.getFullYear(),t=s.getMonth();return t>=9||t<2?`Winter ${r}/${r+1}`:`Summer ${r}`},L=s=>s.getDay(),g=s=>s.getHours(),l=M.reduce((s,r)=>{var x;const t=m(r.start),n=m(r.end);if(!t||!n)return s;const a=L(t),o=g(t),c=g(n),u=d.find(p=>p.id===r.course);return s[a]||(s[a]=[]),s[a].push({type:"lecture",startHour:o,endHour:c,course:u,location:r.roomOrLink??"No location set",isOnline:((x=r.roomOrLink)==null?void 0:x.startsWith("http"))??!1,examId:null}),s},{});k.forEach(s=>{const r=m(s.date);if(!r)return;const t=L(r),n=g(r),a=d.find(o=>o.id===s.course);l[t]||(l[t]=[]),l[t].push({type:"exam",startHour:n,endHour:n+2,course:a,location:s.roomOrLink??"No location set",isOnline:s.isOnline,examId:s.id})}),Object.keys(l).forEach(s=>{l[Number(s)].sort((r,t)=>r.startHour-t.startHour)});const U=["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"],w=k.reduce((s,r)=>{const t=m(r.date),n=B(t);return s[n]||(s[n]=[]),s[n].push(r),s},{}),$=Object.keys(w).sort().reverse();return i?e.jsx("div",{className:"max-w-7xl mx-auto p-6",children:e.jsxs("div",{className:"mb-8",children:[e.jsxs(h,{to:"/exa/ui/courses",className:"text-gray-600 hover:text-gray-900 transition-colors flex items-center gap-2 mb-4",children:[e.jsx("svg",{className:"w-5 h-5",fill:"none",stroke:"currentColor",viewBox:"0 0 24 24",children:e.jsx("path",{strokeLinecap:"round",strokeLinejoin:"round",strokeWidth:2,d:"M10 19l-7-7m0 0l7-7m-7 7h18"})}),"Back to Courses"]}),e.jsxs("div",{className:"bg-white rounded-xl p-8 shadow-sm",children:[e.jsxs("div",{className:"flex items-start space-x-6 mb-8",children:[e.jsx("img",{src:`https://i.pravatar.cc/150?u=${i.id}`,alt:`${i.firstname} ${i.name}`,className:"w-24 h-24 rounded-full object-cover"}),e.jsxs("div",{children:[e.jsxs("h1",{className:"text-3xl font-bold text-gray-900 mb-2",children:[i.firstname," ",i.name]}),e.jsxs("div",{className:"space-y-2",children:[e.jsxs("div",{className:"flex items-center text-gray-600",children:[e.jsx("svg",{className:"w-5 h-5 mr-2",fill:"none",stroke:"currentColor",viewBox:"0 0 24 24",children:e.jsx("path",{strokeLinecap:"round",strokeLinejoin:"round",strokeWidth:2,d:"M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"})}),i.faculty??"No Faculty"]}),e.jsxs("div",{className:"flex items-center text-gray-600",children:[e.jsx("svg",{className:"w-5 h-5 mr-2",fill:"none",stroke:"currentColor",viewBox:"0 0 24 24",children:e.jsx("path",{strokeLinecap:"round",strokeLinejoin:"round",strokeWidth:2,d:"M16 12a4 4 0 10-8 0 4 4 0 008 0zm0 0v1.5a2.5 2.5 0 005 0V12a9 9 0 10-9 9m4.5-1.206a8.959 8.959 0 01-4.5 1.207"})}),i.username]})]})]})]}),e.jsxs("div",{className:"mb-8",children:[e.jsx("h2",{className:"text-xl font-semibold text-gray-900 mb-4",children:"Weekly Schedule"}),e.jsx("div",{className:"bg-white rounded-xl p-6 shadow-sm",children:e.jsx("div",{className:"grid grid-cols-7 gap-4",children:U.map((s,r)=>{var t,n;return e.jsxs("div",{className:"space-y-4",children:[e.jsx("h3",{className:"text-lg font-semibold text-gray-900 pb-2 border-b",children:s}),e.jsxs("div",{className:"space-y-2",children:[(t=l[r])==null?void 0:t.map((a,o)=>{var c,u;return e.jsxs(h,{to:a.type==="exam"?"/exa/ui/exams/$examId":"/exa/ui/courses/$courseId",params:a.type==="exam"?{examId:a.examId??""}:{courseId:((c=a.course)==null?void 0:c.id)??""},className:`block p-2 rounded-lg text-sm ${a.type==="exam"?"bg-purple-50 border border-purple-200 hover:border-purple-300":"bg-blue-50 border border-blue-200 hover:border-blue-300"} transition-colors`,children:[e.jsx("div",{className:"font-medium text-gray-900",children:((u=a.course)==null?void 0:u.name)??"Unknown Course"}),e.jsxs("div",{className:"text-gray-600",children:[a.startHour,":00 - ",a.endHour,":00"]}),e.jsx("div",{className:"text-gray-600 truncate",children:a.isOnline?"Online":a.location}),e.jsx("div",{className:"text-xs font-medium mt-1",children:a.type==="exam"?"Exam":"Lecture"})]},o)}),!((n=l[r])!=null&&n.length)&&e.jsx("div",{className:"text-sm text-gray-500 py-2",children:"No events"})]})]},s)})})})]}),e.jsxs("div",{className:"grid grid-cols-1 gap-8",children:[e.jsxs("div",{children:[e.jsx("h2",{className:"text-xl font-semibold text-gray-900 mb-4",children:"Courses"}),d.length>0?e.jsx("div",{className:"grid grid-cols-1 md:grid-cols-2 gap-4",children:d.map(s=>e.jsxs(h,{to:"/exa/ui/courses/$courseId",params:{courseId:s.id??""},className:"block p-4 rounded-lg border border-gray-200 hover:border-blue-300 transition-colors",children:[e.jsx("h3",{className:"text-lg font-semibold text-gray-900 mb-2",children:s.name}),e.jsxs("div",{className:"flex items-center justify-between",children:[e.jsx("span",{className:"text-gray-600",children:s.faculty}),e.jsxs("span",{className:"text-gray-600",children:["Max: ",s.maxStudents," students"]})]})]},s.id))}):e.jsx("p",{className:"text-gray-500",children:"No courses assigned"})]}),e.jsxs("div",{children:[e.jsx("h2",{className:"text-xl font-semibold text-gray-900 mb-4",children:"Exams by Semester"}),$.length>0?e.jsx("div",{className:"space-y-8",children:$.map(s=>e.jsxs("div",{children:[e.jsx("h3",{className:"text-lg font-semibold text-gray-900 mb-4 bg-gray-50 p-2 rounded",children:s}),e.jsx("div",{className:"space-y-4",children:w[s].map(r=>{const t=m(r.date),n=t?t>=new Date:!1,a=v.find(o=>o.id===r.course);return e.jsxs(h,{to:"/exa/ui/exams/$examId",params:{examId:r.id??""},className:`block p-4 rounded-lg border ${n?"bg-white border-blue-200 hover:border-blue-300":"bg-gray-50 border-gray-200 hover:border-gray-300"} transition-colors`,children:[e.jsxs("div",{className:"flex justify-between items-start mb-2",children:[e.jsxs("div",{children:[e.jsx("span",{className:`inline-block px-3 py-1 rounded-full text-sm mb-2 ${r.isWritten?"bg-purple-100 text-purple-700":"bg-green-100 text-green-700"}`,children:r.isWritten?"Written":"Oral"}),e.jsxs("h4",{className:"text-lg font-semibold text-gray-900",children:[(a==null?void 0:a.name)??"Unknown Course"," Exam"]})]}),e.jsx("span",{className:`text-sm ${n?"text-blue-600":"text-gray-500"}`,children:T(t)})]}),e.jsxs("div",{className:"text-gray-600",children:[r.isOnline?"Online":"On-site"," - ",r.roomOrLink]})]},r.id)})})]},s))}):e.jsx("p",{className:"text-gray-500",children:"No exams scheduled"})]})]})]})]})}):e.jsx("div",{className:"p-4 bg-red-50 text-red-600 rounded-lg",children:"Lecturer not found"})}export{G as Route};
