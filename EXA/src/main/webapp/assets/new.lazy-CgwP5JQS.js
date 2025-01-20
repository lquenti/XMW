import{c as j,e as y,u as N,A as c,j as e,a as v,g as k}from"./index-DM6Bh2lX.js";import{z as i,u as w,a as O,t as L}from"./use-exa-mutation-DPwzD6Z4.js";import{u as E}from"./use-courses-3SaIvtGQ.js";import"./useQuery-gAexkbtq.js";const C=i.object({course:i.string().min(1,"Please select a course"),date:i.string().min(1,"Please select a date and time"),isOnline:i.boolean(),isWritten:i.boolean(),roomOrLink:i.string().min(1,"Please provide a room or link")}),P=j("/exa/ui/exams/new")({component:S});function S(){const n=y(),{state:l}=N(),{data:o,isLoading:x}=E(),{mutate:u,isPending:m}=w({endpoint:"http://localhost:8080/exa/exams",redirectTo:"/exa/ui/exams"}),{register:r,handleSubmit:g,watch:h,formState:{errors:t,isSubmitting:d}}=O({resolver:L(C),defaultValues:{isOnline:!1,isWritten:!0}});if(l!==c.Admin&&l!==c.Lecturer)return n({to:"/exa/ui/exams"}),null;if(x)return e.jsx("div",{className:"flex items-center justify-center min-h-[400px]",children:e.jsx("div",{className:"animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"})});if(!o)return e.jsx("div",{className:"p-4 bg-yellow-50 text-yellow-600 rounded-lg",children:"No course data available"});const b=v(o),a=h("isOnline"),p=async s=>{try{u({course:s.course,date:s.date,is_online:s.isOnline?"1":"0",is_written:s.isWritten?"1":"0",room_or_link:s.roomOrLink})}catch(f){console.error("Error creating exam:",f),k.error("Error creating exam")}};return e.jsxs("div",{className:"max-w-3xl mx-auto p-6",children:[e.jsxs("div",{className:"mb-8",children:[e.jsx("h1",{className:"text-3xl font-bold text-gray-900",children:"Create New Exam"}),e.jsx("p",{className:"mt-2 text-sm text-gray-600",children:"Fill in the details below to create a new exam."})]}),e.jsxs("form",{onSubmit:g(p),className:"bg-white rounded-xl p-8 shadow-sm space-y-8",children:[e.jsxs("div",{className:"grid grid-cols-1 gap-x-6 gap-y-8",children:[e.jsxs("div",{children:[e.jsx("label",{htmlFor:"course",className:"block text-sm font-medium leading-6 text-gray-900",children:"Course"}),e.jsxs("div",{className:"relative mt-2",children:[e.jsxs("select",{id:"course",...r("course"),className:"block w-full rounded-md border-0 py-2.5 pl-3 pr-10 text-gray-900 ring-1 ring-inset ring-gray-300 focus:ring-2 focus:ring-inset focus:ring-blue-600 sm:text-sm sm:leading-6",children:[e.jsx("option",{value:"",children:"Select a course"}),b.map(s=>e.jsx("option",{value:s.id??"",children:s.name},s.id))]}),t.course&&e.jsx("p",{className:"mt-2 text-sm text-red-600",children:t.course.message})]})]}),e.jsxs("div",{children:[e.jsx("label",{htmlFor:"date",className:"block text-sm font-medium leading-6 text-gray-900",children:"Date and Time"}),e.jsxs("div",{className:"mt-2",children:[e.jsx("input",{type:"datetime-local",id:"date",...r("date"),className:"block w-full rounded-md border-0 py-2.5 text-gray-900 ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-blue-600 sm:text-sm sm:leading-6"}),t.date&&e.jsx("p",{className:"mt-2 text-sm text-red-600",children:t.date.message})]})]}),e.jsxs("fieldset",{children:[e.jsx("legend",{className:"text-sm font-medium leading-6 text-gray-900",children:"Exam Type"}),e.jsxs("div",{className:"mt-4 space-y-4 md:flex md:items-center md:space-y-0 md:space-x-10",children:[e.jsxs("div",{className:"flex items-center",children:[e.jsx("input",{type:"checkbox",id:"isWritten",...r("isWritten"),className:"h-4 w-4 rounded border-gray-300 text-blue-600 focus:ring-blue-600"}),e.jsx("label",{htmlFor:"isWritten",className:"ml-3 block text-sm font-medium leading-6 text-gray-900",children:"Written Exam"})]}),e.jsxs("div",{className:"flex items-center",children:[e.jsx("input",{type:"checkbox",id:"isOnline",...r("isOnline"),className:"h-4 w-4 rounded border-gray-300 text-blue-600 focus:ring-blue-600"}),e.jsx("label",{htmlFor:"isOnline",className:"ml-3 block text-sm font-medium leading-6 text-gray-900",children:"Online Exam"})]})]})]}),e.jsxs("div",{children:[e.jsx("label",{htmlFor:"roomOrLink",className:"block text-sm font-medium leading-6 text-gray-900",children:a?"Link to Exam":"Room Number"}),e.jsxs("div",{className:"mt-2",children:[e.jsxs("div",{className:"relative rounded-md",children:[a&&e.jsx("div",{className:"pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3",children:e.jsx("svg",{className:"h-5 w-5 text-gray-400",viewBox:"0 0 24 24",fill:"none",stroke:"currentColor",children:e.jsx("path",{strokeLinecap:"round",strokeLinejoin:"round",strokeWidth:2,d:"M13.828 10.172a4 4 0 00-5.656 0l-4 4a4 4 0 105.656 5.656l1.102-1.101m-.758-4.899a4 4 0 005.656 0l4-4a4 4 0 00-5.656-5.656l-1.1 1.1"})})}),e.jsx("input",{type:"text",id:"roomOrLink",...r("roomOrLink"),placeholder:a?"https://...":"e.g., A1.2.1",className:`block w-full rounded-md border-0 py-2.5 text-gray-900 ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-blue-600 sm:text-sm sm:leading-6 ${a?"pl-10":""}`})]}),t.roomOrLink&&e.jsx("p",{className:"mt-2 text-sm text-red-600",children:t.roomOrLink.message})]})]})]}),e.jsxs("div",{className:"flex items-center justify-end space-x-4 pt-4 border-t border-gray-200",children:[e.jsx("button",{type:"button",onClick:()=>n({to:"/exa/ui/exams"}),className:"rounded-md bg-white px-4 py-2.5 text-sm font-semibold text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50",children:"Cancel"}),e.jsx("button",{type:"submit",disabled:d||m,className:"rounded-md bg-blue-600 px-4 py-2.5 text-sm font-semibold text-white shadow-sm hover:bg-blue-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600 disabled:opacity-50 disabled:cursor-not-allowed",children:d||m?"Creating...":"Create Exam"})]})]})]})}export{P as Route};
