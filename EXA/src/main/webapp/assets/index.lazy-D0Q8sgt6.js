import{c as S,r as z,u as A,j as e,a as B,d as F,p as H,A as p,L as j}from"./index-CrEXsoWi.js";import{u as R}from"./use-courses-CHWFhleG.js";import{u as W}from"./use-lecturers-DOWiWoVD.js";import{u as X}from"./use-semesters-DhZjM4Z3.js";import"./useQuery-B0t8Uunr.js";const q=S("/exa/ui/courses/")({component:$});function $(){const{data:m,isLoading:v,error:a}=R(),{data:u,isLoading:f,error:n}=X(),{data:x,isLoading:b,error:o}=W(),[l,N]=z.useState("all"),{state:h}=A();if(v||f||b)return e.jsx("div",{className:"flex items-center justify-center min-h-[400px]",children:e.jsx("div",{className:"animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"})});if(a||n||o)return e.jsxs("div",{className:"p-4 bg-red-50 text-red-600 rounded-lg",children:["Error: ",(a==null?void 0:a.message)||(n==null?void 0:n.message)||(o==null?void 0:o.message)]});if(!m||!u||!x)return e.jsx("div",{className:"p-4 bg-yellow-50 text-yellow-600 rounded-lg",children:"No course data available"});const d=B(m),y=F(u),w=H(x),L=Array.from(new Set(d.map(s=>s.faculty??""))).sort(),i=l==="all"?d:d.filter(s=>s.faculty===l),k=i.reduce((s,t)=>(t.semester&&(s[t.semester]=s[t.semester]||[],s[t.semester].push(t)),s),{}),C=h===p.Admin||h===p.Lecturer;return e.jsxs("div",{className:"max-w-7xl mx-auto p-6",children:[e.jsxs("div",{className:"mb-8",children:[e.jsxs("div",{className:"flex items-center justify-between mb-4",children:[e.jsx("h1",{className:"text-3xl font-bold text-gray-900",children:"Courses"}),C&&e.jsxs(j,{to:"/exa/ui/courses/new",className:"px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors flex items-center space-x-2",children:[e.jsx("svg",{className:"w-5 h-5",fill:"none",stroke:"currentColor",viewBox:"0 0 24 24",children:e.jsx("path",{strokeLinecap:"round",strokeLinejoin:"round",strokeWidth:2,d:"M12 6v6m0 0v6m0-6h6m-6 0H6"})}),e.jsx("span",{children:"New Course"})]})]}),e.jsx("div",{className:"flex flex-wrap gap-4 mb-6",children:e.jsxs("select",{className:"px-4 py-2 rounded-lg border border-gray-300 bg-white",value:l,onChange:s=>N(s.target.value),children:[e.jsx("option",{value:"all",children:"All Faculties"}),L.map(s=>e.jsx("option",{value:s,children:s},s))]})}),e.jsxs("p",{className:"text-gray-600 mb-4",children:["Showing ",i.length," course",i.length!==1?"s":""]})]}),e.jsx("div",{className:"space-y-8",children:Object.entries(k).map(([s,t])=>{var g;return e.jsxs("div",{className:"bg-white rounded-xl p-6 shadow-sm",children:[e.jsx("h2",{className:"text-2xl font-bold text-gray-900 mb-4",children:(g=y.find(r=>r.id===s))==null?void 0:g.name}),e.jsx("div",{className:"grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6",children:t.map(r=>{const c=w.find(M=>M.id===r.lecturer);return e.jsxs(j,{to:"/exa/ui/courses/$courseId",params:{courseId:r.id??""},className:"block bg-gray-50 rounded-lg p-6 border border-gray-200 hover:shadow-md transition-shadow",children:[e.jsx("h3",{className:"text-xl font-semibold text-gray-900 mb-4",children:r.name}),e.jsxs("div",{className:"space-y-3",children:[e.jsxs("div",{className:"flex items-center text-gray-600",children:[e.jsx("svg",{className:"w-5 h-5 mr-2",fill:"none",stroke:"currentColor",viewBox:"0 0 24 24",children:e.jsx("path",{strokeLinecap:"round",strokeLinejoin:"round",strokeWidth:2,d:"M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"})}),r.faculty]}),e.jsxs("div",{className:"flex items-center text-gray-600",children:[e.jsx("svg",{className:"w-5 h-5 mr-2",fill:"none",stroke:"currentColor",viewBox:"0 0 24 24",children:e.jsx("path",{strokeLinecap:"round",strokeLinejoin:"round",strokeWidth:2,d:"M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"})}),"Max Students: ",r.maxStudents]}),e.jsxs("div",{className:"flex items-center text-gray-600",children:[e.jsx("svg",{className:"w-5 h-5 mr-2",fill:"none",stroke:"currentColor",viewBox:"0 0 24 24",children:e.jsx("path",{strokeLinecap:"round",strokeLinejoin:"round",strokeWidth:2,d:"M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"})}),c?`${c.firstname} ${c.name}`:"Unknown Lecturer"]})]})]},r.id)})})]},s)})}),i.length===0&&e.jsx("div",{className:"text-center py-12 bg-gray-50 rounded-lg",children:e.jsx("p",{className:"text-gray-600",children:"No courses match your filters"})})]})}export{q as Route};
