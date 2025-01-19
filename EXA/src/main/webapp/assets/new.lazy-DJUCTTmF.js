import{c as u,e as i,u as c,r as d,A as m,j as e}from"./index-DJV6m9OR.js";const g=u("/exa/ui/lecturers/new")({component:x});function x(){const s=i(),{state:l}=c(),[a,n]=d.useState({firstname:"",name:"",username:"",faculty:""});if(l!==m.Admin)return s({to:"/exa/ui/lecturers"}),null;const o=t=>{t.preventDefault(),console.log("Creating lecturer:",a),s({to:"/exa/ui/lecturers"})};return e.jsxs("div",{className:"max-w-7xl mx-auto p-6",children:[e.jsx("div",{className:"mb-8",children:e.jsxs("div",{className:"flex items-center justify-between",children:[e.jsx("h1",{className:"text-3xl font-bold text-gray-900",children:"New Lecturer"}),e.jsx("button",{onClick:()=>s({to:"/exa/ui/lecturers"}),className:"px-4 py-2 text-gray-600 hover:text-gray-900 transition-colors",children:"Cancel"})]})}),e.jsx("div",{className:"bg-white rounded-xl p-6 shadow-sm",children:e.jsxs("form",{onSubmit:o,className:"space-y-6 max-w-2xl",children:[e.jsxs("div",{className:"space-y-4",children:[e.jsxs("div",{children:[e.jsx("label",{htmlFor:"firstname",className:"block text-sm font-medium text-gray-700 mb-1",children:"First Name"}),e.jsx("input",{type:"text",id:"firstname",value:a.firstname,onChange:t=>n(r=>({...r,firstname:t.target.value})),className:"w-full px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-shadow",required:!0})]}),e.jsxs("div",{children:[e.jsx("label",{htmlFor:"name",className:"block text-sm font-medium text-gray-700 mb-1",children:"Last Name"}),e.jsx("input",{type:"text",id:"name",value:a.name,onChange:t=>n(r=>({...r,name:t.target.value})),className:"w-full px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-shadow",required:!0})]}),e.jsxs("div",{children:[e.jsx("label",{htmlFor:"username",className:"block text-sm font-medium text-gray-700 mb-1",children:"Username"}),e.jsx("input",{type:"text",id:"username",value:a.username,onChange:t=>n(r=>({...r,username:t.target.value})),className:"w-full px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-shadow",required:!0})]}),e.jsxs("div",{children:[e.jsx("label",{htmlFor:"faculty",className:"block text-sm font-medium text-gray-700 mb-1",children:"Faculty"}),e.jsx("input",{type:"text",id:"faculty",value:a.faculty,onChange:t=>n(r=>({...r,faculty:t.target.value})),className:"w-full px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-shadow",required:!0})]})]}),e.jsxs("div",{className:"flex justify-end space-x-4",children:[e.jsx("button",{type:"button",onClick:()=>s({to:"/exa/ui/lecturers"}),className:"px-6 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors",children:"Cancel"}),e.jsx("button",{type:"submit",className:"px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors",children:"Create Lecturer"})]})]})})]})}export{g as Route};
