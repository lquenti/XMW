import{c as S,r as m,u as B,j as e,b as M,a as z,A as j,L as v}from"./index-SsTAdifg.js";import{u as F}from"./use-courses-BCKvGkJt.js";import{u as U}from"./use-exams-D7t73XWJ.js";const X=S("/exa/ui/exams/")({component:H});function H(){const{data:x,isLoading:b,error:o}=U(),{data:h,isLoading:f,error:i}=F(),[N,w]=m.useState("upcoming"),[y,k]=m.useState("all"),[L,C]=m.useState("all"),{state:p}=B();if(b||f)return e.jsx("div",{className:"flex items-center justify-center min-h-[400px]",children:e.jsx("div",{className:"animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"})});if(o||i)return e.jsxs("div",{className:"p-4 bg-red-50 text-red-600 rounded-lg",children:["Error: ",(o==null?void 0:o.message)||(i==null?void 0:i.message)]});if(!x||!h)return e.jsx("div",{className:"p-4 bg-yellow-50 text-yellow-600 rounded-lg",children:"No exam data available"});const E=M(x),A=z(h),l=t=>{if(!t)return null;try{const r=new Date(t);if(!isNaN(r.getTime()))return r;const s=t.match(/^(\d{4}-\d{2}-\d{2})T(\d+)T:(\d{2}):(\d{2})$/);if(!s)return null;const[,n,a,c,$]=s,D=`${a.padStart(2,"0")}:${c}:${$}`,O=`${n} ${D}`,g=new Date(O);return isNaN(g.getTime())?null:g}catch(r){return console.error("Error parsing date:",r),null}},T=E.sort((t,r)=>{const s=l(t.date),n=l(r.date);return!s&&!n?0:s?n?s.getTime()-n.getTime():-1:1}),u=new Date,d=T.sort((t,r)=>{const s=l(t.date),n=l(r.date),a=s?s>=u:!1,c=n?n>=u:!1;return a&&!c?-1:!a&&c?1:s&&n?s.getTime()-n.getTime():0}),W=p===j.Admin||p===j.Lecturer;return e.jsxs("div",{className:"max-w-7xl mx-auto p-6",children:[e.jsxs("div",{className:"mb-8",children:[e.jsxs("div",{className:"flex items-center justify-between mb-4",children:[e.jsx("h1",{className:"text-3xl font-bold text-gray-900",children:"Exams"}),W&&e.jsxs(v,{to:"/exa/ui/exams/new",className:"px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors flex items-center space-x-2",children:[e.jsx("svg",{className:"w-5 h-5",fill:"none",stroke:"currentColor",viewBox:"0 0 24 24",children:e.jsx("path",{strokeLinecap:"round",strokeLinejoin:"round",strokeWidth:2,d:"M12 6v6m0 0v6m0-6h6m-6 0H6"})}),e.jsx("span",{children:"New Exam"})]})]}),e.jsxs("div",{className:"flex flex-wrap gap-4 mb-6",children:[e.jsxs("select",{className:"px-4 py-2 rounded-lg border border-gray-300 bg-white",value:N,onChange:t=>w(t.target.value),children:[e.jsx("option",{value:"all",children:"All Exams"}),e.jsx("option",{value:"upcoming",children:"Upcoming"}),e.jsx("option",{value:"past",children:"Past"})]}),e.jsxs("select",{className:"px-4 py-2 rounded-lg border border-gray-300 bg-white",value:y,onChange:t=>k(t.target.value),children:[e.jsx("option",{value:"all",children:"All Types"}),e.jsx("option",{value:"written",children:"Written"}),e.jsx("option",{value:"oral",children:"Oral"})]}),e.jsxs("select",{className:"px-4 py-2 rounded-lg border border-gray-300 bg-white",value:L,onChange:t=>C(t.target.value),children:[e.jsx("option",{value:"all",children:"All Locations"}),e.jsx("option",{value:"online",children:"Online"}),e.jsx("option",{value:"onsite",children:"On-site"})]})]}),e.jsxs("p",{className:"text-gray-600 mb-4",children:["Showing ",d.length," exam",d.length!==1?"s":""]})]}),e.jsx("div",{className:"grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6",children:d.map(t=>{const r=A.find(a=>a.id===t.course),s=t.date?new Date(t.date):null,n=s?s>=u:!1;return e.jsxs(v,{to:"/exa/ui/exams/$examId",params:{examId:t.id??""},className:`block p-4 rounded-lg border ${n?"bg-white border-blue-200 hover:border-blue-300":"bg-gray-50 border-gray-200 hover:border-gray-300"} transition-colors`,children:[e.jsxs("div",{className:"flex justify-between items-start mb-4",children:[e.jsx("h3",{className:"text-xl font-semibold text-gray-900 flex-1",children:(r==null?void 0:r.name)??"Unknown Course"}),e.jsx("span",{className:`px-3 py-1 rounded-full text-sm ${t.isWritten?"bg-purple-100 text-purple-700":"bg-green-100 text-green-700"}`,children:t.isWritten?"Written":"Oral"})]}),e.jsxs("div",{className:"space-y-3",children:[e.jsxs("div",{className:"flex items-center text-gray-600",children:[e.jsx("svg",{className:"w-5 h-5 mr-2",fill:"none",stroke:"currentColor",viewBox:"0 0 24 24",children:e.jsx("path",{strokeLinecap:"round",strokeLinejoin:"round",strokeWidth:2,d:"M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"})}),(s==null?void 0:s.toLocaleString())??"Date not set"]}),e.jsxs("div",{className:"flex items-center text-gray-600",children:[e.jsxs("svg",{className:"w-5 h-5 mr-2",fill:"none",stroke:"currentColor",viewBox:"0 0 24 24",children:[e.jsx("path",{strokeLinecap:"round",strokeLinejoin:"round",strokeWidth:2,d:"M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"}),e.jsx("path",{strokeLinecap:"round",strokeLinejoin:"round",strokeWidth:2,d:"M15 11a3 3 0 11-6 0 3 3 0 016 0z"})]}),t.isOnline?"Online":"On-site"]}),e.jsxs("div",{className:"flex items-center text-gray-600 truncate text-nowrap",children:[e.jsx("svg",{className:"min-w-5 w-5 h-5 mr-2",fill:"none",stroke:"currentColor",viewBox:"0 0 24 24",children:e.jsx("path",{strokeLinecap:"round",strokeLinejoin:"round",strokeWidth:2,d:"M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"})}),t.roomOrLink]})]})]},t.id)})}),d.length===0&&e.jsx("div",{className:"text-center py-12 bg-gray-50 rounded-lg",children:e.jsx("p",{className:"text-gray-600",children:"No exams match your filters"})})]})}export{X as Route};
