import{c as B,j as e,a as H,p as I,b as S,g as O,L as o}from"./index-BzgIib2e.js";import{u as T}from"./use-courses-BwK9ZoyO.js";import{u as P}from"./use-exams-CJ2D9bbG.js";import{u as U}from"./use-lecturers-tAiyIeZo.js";import{u as X}from"./use-modules-BohN7zdm.js";const R=B("/exa/ui/exams/$examId")({component:V});function V(){const{examId:p}=R.useParams(),{data:d,isLoading:g}=T(),{data:c,isLoading:v}=U(),{data:m,isLoading:f}=P(),{data:x,isLoading:N}=X();if(g||v||f||N)return e.jsx("div",{className:"flex items-center justify-center min-h-[400px]",children:e.jsx("div",{className:"animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"})});if(!d||!c||!m||!x)return e.jsx("div",{className:"p-4 bg-yellow-50 text-yellow-600 rounded-lg",children:"No data available"});const b=H(d),y=I(c),k=S(m),L=O(x),r=k.find(t=>t.id===p),s=b.find(t=>t.id===(r==null?void 0:r.course)),a=y.find(t=>t.id===(s==null?void 0:s.lecturer)),n=L.find(t=>t.course===(s==null?void 0:s.id)),w=t=>{if(!t)return null;try{const i=new Date(t);if(!isNaN(i.getTime()))return i;const h=t.match(/^(\d{4}-\d{2}-\d{2})T(\d+)T:(\d{2}):(\d{2})$/);if(!h)return null;const[,C,D,$,W]=h,E=`${D.padStart(2,"0")}:${$}:${W}`,z=`${C} ${E}`,j=new Date(z);return isNaN(j.getTime())?null:j}catch(i){return console.error("Error parsing date:",i),null}},M=t=>t?t.toLocaleString("en-US",{year:"numeric",month:"long",day:"numeric",hour:"numeric",minute:"2-digit"}):"Date not set";if(!r)return e.jsx("div",{className:"p-4 bg-red-50 text-red-600 rounded-lg",children:"Exam not found"});const l=w(r.date),u=l?l>=new Date:!1;return e.jsx("div",{className:"max-w-7xl mx-auto p-6",children:e.jsxs("div",{className:"mb-8",children:[e.jsxs(o,{to:"/exa/ui/exams",className:"text-gray-600 hover:text-gray-900 transition-colors flex items-center gap-2 mb-4",children:[e.jsx("svg",{className:"w-5 h-5",fill:"none",stroke:"currentColor",viewBox:"0 0 24 24",children:e.jsx("path",{strokeLinecap:"round",strokeLinejoin:"round",strokeWidth:2,d:"M10 19l-7-7m0 0l7-7m-7 7h18"})}),"Back to Exams"]}),e.jsx("div",{className:"bg-white rounded-xl p-8 shadow-sm",children:e.jsxs("div",{className:"mb-6",children:[e.jsxs("div",{className:"flex justify-between items-start mb-6",children:[e.jsxs("div",{children:[e.jsxs("h1",{className:"text-3xl font-bold text-gray-900",children:[(s==null?void 0:s.name)??"Unknown Course"," - Exam"]}),n&&e.jsxs("h2",{className:"text-xl text-gray-600 mt-2",children:["Module: ",n.name]})]}),e.jsx("span",{className:"px-4 py-2 bg-blue-100 text-blue-800 rounded-full text-sm font-medium",children:s==null?void 0:s.faculty})]}),e.jsxs("div",{className:"grid grid-cols-1 gap-8",children:[e.jsxs("div",{className:"grid grid-cols-1 md:grid-cols-2 gap-8",children:[e.jsxs("div",{children:[e.jsx("h2",{className:"text-xl font-semibold text-gray-900 mb-4",children:"Course Details"}),e.jsxs("div",{className:"space-y-4",children:[e.jsxs("div",{className:"flex items-center text-gray-600",children:[e.jsx("svg",{className:"w-5 h-5 mr-2",fill:"none",stroke:"currentColor",viewBox:"0 0 24 24",children:e.jsx("path",{strokeLinecap:"round",strokeLinejoin:"round",strokeWidth:2,d:"M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"})}),"Max Students: ",s==null?void 0:s.maxStudents]}),e.jsxs(o,{to:"/exa/ui/courses/$courseId",params:{courseId:(s==null?void 0:s.id)??""},className:"flex items-center text-blue-600 hover:text-blue-800 transition-colors",children:[e.jsx("svg",{className:"w-5 h-5 mr-2",fill:"none",stroke:"currentColor",viewBox:"0 0 24 24",children:e.jsx("path",{strokeLinecap:"round",strokeLinejoin:"round",strokeWidth:2,d:"M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"})}),"View Course Details"]})]})]}),a&&e.jsxs("div",{children:[e.jsx("h2",{className:"text-xl font-semibold text-gray-900 mb-4",children:"Lecturer"}),e.jsx(o,{to:"/exa/ui/lecturers/$lecturerId",params:{lecturerId:a.id??""},className:"block bg-gray-50 rounded-lg p-6 border border-gray-200 hover:border-blue-300 transition-colors",children:e.jsxs("div",{className:"flex items-start space-x-4",children:[e.jsx("img",{src:`https://i.pravatar.cc/150?u=${a.id}`,alt:`${a.firstname} ${a.name}`,className:"w-16 h-16 rounded-full object-cover"}),e.jsxs("div",{children:[e.jsxs("h3",{className:"text-xl font-semibold text-gray-900",children:[a.firstname," ",a.name]}),e.jsxs("div",{className:"mt-2 space-y-2",children:[e.jsxs("div",{className:"flex items-center text-gray-600",children:[e.jsx("svg",{className:"w-5 h-5 mr-2",fill:"none",stroke:"currentColor",viewBox:"0 0 24 24",children:e.jsx("path",{strokeLinecap:"round",strokeLinejoin:"round",strokeWidth:2,d:"M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"})}),a.faculty??"No Faculty"]}),e.jsxs("div",{className:"flex items-center text-gray-600",children:[e.jsx("svg",{className:"w-5 h-5 mr-2",fill:"none",stroke:"currentColor",viewBox:"0 0 24 24",children:e.jsx("path",{strokeLinecap:"round",strokeLinejoin:"round",strokeWidth:2,d:"M16 12a4 4 0 10-8 0 4 4 0 008 0zm0 0v1.5a2.5 2.5 0 005 0V12a9 9 0 10-9 9m4.5-1.206a8.959 8.959 0 01-4.5 1.207"})}),a.username]})]})]})]})})]})]}),n&&e.jsxs("div",{children:[e.jsx("h2",{className:"text-xl font-semibold text-gray-900 mb-4",children:"Module Details"}),e.jsxs("div",{className:"bg-gray-50 rounded-lg p-4",children:[e.jsxs("div",{className:"flex items-center text-gray-600 mb-2",children:[e.jsx("svg",{className:"w-5 h-5 mr-2",fill:"none",stroke:"currentColor",viewBox:"0 0 24 24",children:e.jsx("path",{strokeLinecap:"round",strokeLinejoin:"round",strokeWidth:2,d:"M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"})}),e.jsxs("span",{className:"font-medium",children:["Credits: ",n.credits]})]}),e.jsx("p",{className:"text-gray-600",children:n.description}),n.studies.length>0&&e.jsxs("div",{className:"mt-4",children:[e.jsx("div",{className:"text-sm font-medium text-gray-600 mb-2",children:"Study Programs:"}),e.jsx("div",{className:"flex flex-wrap gap-2",children:n.studies.map((t,i)=>e.jsx("span",{className:"px-2 py-1 bg-blue-100 text-blue-800 rounded text-sm",children:t},i))})]})]})]}),e.jsxs("div",{children:[e.jsx("h2",{className:"text-xl font-semibold text-gray-900 mb-4",children:"Exam Details"}),e.jsxs("div",{className:"space-y-4",children:[e.jsxs("div",{className:"flex items-center justify-between mb-2",children:[e.jsx("span",{className:`px-3 py-1 rounded-full text-sm ${r.isWritten?"bg-purple-100 text-purple-700":"bg-green-100 text-green-700"}`,children:r.isWritten?"Written Exam":"Oral Exam"}),e.jsx("span",{className:`px-3 py-1 rounded-full text-sm ${u?"bg-blue-100 text-blue-800":"bg-gray-100 text-gray-700"}`,children:u?"Upcoming":"Past"})]}),e.jsxs("div",{className:"flex items-center text-gray-600",children:[e.jsx("svg",{className:"w-5 h-5 mr-2",fill:"none",stroke:"currentColor",viewBox:"0 0 24 24",children:e.jsx("path",{strokeLinecap:"round",strokeLinejoin:"round",strokeWidth:2,d:"M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"})}),M(l)]}),e.jsxs("div",{className:"flex items-center text-gray-600",children:[e.jsxs("svg",{className:"w-5 h-5 mr-2",fill:"none",stroke:"currentColor",viewBox:"0 0 24 24",children:[e.jsx("path",{strokeLinecap:"round",strokeLinejoin:"round",strokeWidth:2,d:"M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"}),e.jsx("path",{strokeLinecap:"round",strokeLinejoin:"round",strokeWidth:2,d:"M15 11a3 3 0 11-6 0 3 3 0 016 0z"})]}),r.isOnline?"Online":"On-site"," - ",r.roomOrLink]})]})]})]})]})})]})})}export{R as Route};
