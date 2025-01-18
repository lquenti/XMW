var le=t=>{throw TypeError(t)};var X=(t,e,r)=>e.has(t)||le("Cannot "+r);var s=(t,e,r)=>(X(t,e,"read from private field"),r?r.call(t):e.get(t)),_=(t,e,r)=>e.has(t)?le("Cannot add the same private member more than once"):e instanceof WeakSet?e.add(t):e.set(t,r),m=(t,e,r,i)=>(X(t,e,"write to private field"),i?i.call(t,r):e.set(t,r),r),c=(t,e,r)=>(X(t,e,"access private method"),r);import{S as be,e as ce,r as k,s as J,f as V,n as ke,i as $,g as de,t as Re,h as Te,k as ge,l as _e,m as we,o as g,u as Se}from"./index-BYXnpnIT.js";var y,n,W,p,L,M,S,b,z,F,U,O,I,v,D,a,H,Y,Z,ee,te,se,re,ie,Ee,ye,ve=(ye=class extends be{constructor(e,r){super();_(this,a);_(this,y);_(this,n);_(this,W);_(this,p);_(this,L);_(this,M);_(this,S);_(this,b);_(this,z);_(this,F);_(this,U);_(this,O);_(this,I);_(this,v);_(this,D,new Set);this.options=r,m(this,y,e),m(this,b,null),m(this,S,ce()),this.options.experimental_prefetchInRender||s(this,S).reject(new Error("experimental_prefetchInRender feature flag is not enabled")),this.bindMethods(),this.setOptions(r)}bindMethods(){this.refetch=this.refetch.bind(this)}onSubscribe(){this.listeners.size===1&&(s(this,n).addObserver(this),he(s(this,n),this.options)?c(this,a,H).call(this):this.updateResult(),c(this,a,te).call(this))}onUnsubscribe(){this.hasListeners()||this.destroy()}shouldFetchOnReconnect(){return ne(s(this,n),this.options,this.options.refetchOnReconnect)}shouldFetchOnWindowFocus(){return ne(s(this,n),this.options,this.options.refetchOnWindowFocus)}destroy(){this.listeners=new Set,c(this,a,se).call(this),c(this,a,re).call(this),s(this,n).removeObserver(this)}setOptions(e,r){const i=this.options,d=s(this,n);if(this.options=s(this,y).defaultQueryOptions(e),this.options.enabled!==void 0&&typeof this.options.enabled!="boolean"&&typeof this.options.enabled!="function"&&typeof k(this.options.enabled,s(this,n))!="boolean")throw new Error("Expected enabled to be a boolean or a callback that returns a boolean");c(this,a,ie).call(this),s(this,n).setOptions(this.options),i._defaulted&&!J(this.options,i)&&s(this,y).getQueryCache().notify({type:"observerOptionsUpdated",query:s(this,n),observer:this});const l=this.hasListeners();l&&fe(s(this,n),d,this.options,i)&&c(this,a,H).call(this),this.updateResult(r),l&&(s(this,n)!==d||k(this.options.enabled,s(this,n))!==k(i.enabled,s(this,n))||V(this.options.staleTime,s(this,n))!==V(i.staleTime,s(this,n)))&&c(this,a,Y).call(this);const o=c(this,a,Z).call(this);l&&(s(this,n)!==d||k(this.options.enabled,s(this,n))!==k(i.enabled,s(this,n))||o!==s(this,v))&&c(this,a,ee).call(this,o)}getOptimisticResult(e){const r=s(this,y).getQueryCache().build(s(this,y),e),i=this.createResult(r,e);return Oe(this,i)&&(m(this,p,i),m(this,M,this.options),m(this,L,s(this,n).state)),i}getCurrentResult(){return s(this,p)}trackResult(e,r){const i={};return Object.keys(e).forEach(d=>{Object.defineProperty(i,d,{configurable:!1,enumerable:!0,get:()=>(this.trackProp(d),r==null||r(d),e[d])})}),i}trackProp(e){s(this,D).add(e)}getCurrentQuery(){return s(this,n)}refetch({...e}={}){return this.fetch({...e})}fetchOptimistic(e){const r=s(this,y).defaultQueryOptions(e),i=s(this,y).getQueryCache().build(s(this,y),r);return i.fetch().then(()=>this.createResult(i,r))}fetch(e){return c(this,a,H).call(this,{...e,cancelRefetch:e.cancelRefetch??!0}).then(()=>(this.updateResult(),s(this,p)))}createResult(e,r){var me;const i=s(this,n),d=this.options,l=s(this,p),o=s(this,L),R=s(this,M),E=e!==i?e.state:s(this,W),{state:T}=e;let u={...T},P=!1,f;if(r._optimisticResults){const x=this.hasListeners(),q=!x&&he(e,r),Q=x&&fe(e,i,r,d);(q||Q)&&(u={...u,...ge(T.data,e.options)}),r._optimisticResults==="isRestoring"&&(u.fetchStatus="idle")}let{error:B,errorUpdatedAt:j,status:w}=u;if(r.select&&u.data!==void 0)if(l&&u.data===(o==null?void 0:o.data)&&r.select===s(this,z))f=s(this,F);else try{m(this,z,r.select),f=r.select(u.data),f=_e(l==null?void 0:l.data,f,r),m(this,F,f),m(this,b,null)}catch(x){m(this,b,x)}else f=u.data;if(r.placeholderData!==void 0&&f===void 0&&w==="pending"){let x;if(l!=null&&l.isPlaceholderData&&r.placeholderData===(R==null?void 0:R.placeholderData))x=l.data;else if(x=typeof r.placeholderData=="function"?r.placeholderData((me=s(this,U))==null?void 0:me.state.data,s(this,U)):r.placeholderData,r.select&&x!==void 0)try{x=r.select(x),m(this,b,null)}catch(q){m(this,b,q)}x!==void 0&&(w="success",f=_e(l==null?void 0:l.data,x,r),P=!0)}s(this,b)&&(B=s(this,b),f=s(this,F),j=Date.now(),w="error");const A=u.fetchStatus==="fetching",K=w==="pending",G=w==="error",oe=K&&A,ue=f!==void 0,C={status:w,fetchStatus:u.fetchStatus,isPending:K,isSuccess:w==="success",isError:G,isInitialLoading:oe,isLoading:oe,data:f,dataUpdatedAt:u.dataUpdatedAt,error:B,errorUpdatedAt:j,failureCount:u.fetchFailureCount,failureReason:u.fetchFailureReason,errorUpdateCount:u.errorUpdateCount,isFetched:u.dataUpdateCount>0||u.errorUpdateCount>0,isFetchedAfterMount:u.dataUpdateCount>E.dataUpdateCount||u.errorUpdateCount>E.errorUpdateCount,isFetching:A,isRefetching:A&&!K,isLoadingError:G&&!ue,isPaused:u.fetchStatus==="paused",isPlaceholderData:P,isRefetchError:G&&ue,isStale:ae(e,r),refetch:this.refetch,promise:s(this,S)};if(this.options.experimental_prefetchInRender){const x=N=>{C.status==="error"?N.reject(C.error):C.data!==void 0&&N.resolve(C.data)},q=()=>{const N=m(this,S,C.promise=ce());x(N)},Q=s(this,S);switch(Q.status){case"pending":e.queryHash===i.queryHash&&x(Q);break;case"fulfilled":(C.status==="error"||C.data!==Q.value)&&q();break;case"rejected":(C.status!=="error"||C.error!==Q.reason)&&q();break}}return C}updateResult(e){const r=s(this,p),i=this.createResult(s(this,n),this.options);if(m(this,L,s(this,n).state),m(this,M,this.options),s(this,L).data!==void 0&&m(this,U,s(this,n)),J(i,r))return;m(this,p,i);const d={},l=()=>{if(!r)return!0;const{notifyOnChangeProps:o}=this.options,R=typeof o=="function"?o():o;if(R==="all"||!R&&!s(this,D).size)return!0;const h=new Set(R??s(this,D));return this.options.throwOnError&&h.add("error"),Object.keys(s(this,p)).some(E=>{const T=E;return s(this,p)[T]!==r[T]&&h.has(T)})};(e==null?void 0:e.listeners)!==!1&&l()&&(d.listeners=!0),c(this,a,Ee).call(this,{...d,...e})}onQueryUpdate(){this.updateResult(),this.hasListeners()&&c(this,a,te).call(this)}},y=new WeakMap,n=new WeakMap,W=new WeakMap,p=new WeakMap,L=new WeakMap,M=new WeakMap,S=new WeakMap,b=new WeakMap,z=new WeakMap,F=new WeakMap,U=new WeakMap,O=new WeakMap,I=new WeakMap,v=new WeakMap,D=new WeakMap,a=new WeakSet,H=function(e){c(this,a,ie).call(this);let r=s(this,n).fetch(this.options,e);return e!=null&&e.throwOnError||(r=r.catch(ke)),r},Y=function(){c(this,a,se).call(this);const e=V(this.options.staleTime,s(this,n));if($||s(this,p).isStale||!de(e))return;const i=Re(s(this,p).dataUpdatedAt,e)+1;m(this,O,setTimeout(()=>{s(this,p).isStale||this.updateResult()},i))},Z=function(){return(typeof this.options.refetchInterval=="function"?this.options.refetchInterval(s(this,n)):this.options.refetchInterval)??!1},ee=function(e){c(this,a,re).call(this),m(this,v,e),!($||k(this.options.enabled,s(this,n))===!1||!de(s(this,v))||s(this,v)===0)&&m(this,I,setInterval(()=>{(this.options.refetchIntervalInBackground||Te.isFocused())&&c(this,a,H).call(this)},s(this,v)))},te=function(){c(this,a,Y).call(this),c(this,a,ee).call(this,c(this,a,Z).call(this))},se=function(){s(this,O)&&(clearTimeout(s(this,O)),m(this,O,void 0))},re=function(){s(this,I)&&(clearInterval(s(this,I)),m(this,I,void 0))},ie=function(){const e=s(this,y).getQueryCache().build(s(this,y),this.options);if(e===s(this,n))return;const r=s(this,n);m(this,n,e),m(this,W,e.state),this.hasListeners()&&(r==null||r.removeObserver(this),e.addObserver(this))},Ee=function(e){we.batch(()=>{e.listeners&&this.listeners.forEach(r=>{r(s(this,p))}),s(this,y).getQueryCache().notify({query:s(this,n),type:"observerResultsUpdated"})})},ye);function Le(t,e){return k(e.enabled,t)!==!1&&t.state.data===void 0&&!(t.state.status==="error"&&e.retryOnMount===!1)}function he(t,e){return Le(t,e)||t.state.data!==void 0&&ne(t,e,e.refetchOnMount)}function ne(t,e,r){if(k(e.enabled,t)!==!1){const i=typeof r=="function"?r(t):r;return i==="always"||i!==!1&&ae(t,e)}return!1}function fe(t,e,r,i){return(t!==e||k(i.enabled,t)===!1)&&(!r.suspense||t.state.status!=="error")&&ae(t,r)}function ae(t,e){return k(e.enabled,t)!==!1&&t.isStaleByTime(V(e.staleTime,t))}function Oe(t,e){return!J(t.getCurrentResult(),e)}var Ce=g.createContext(!1),Ie=()=>g.useContext(Ce);Ce.Provider;function qe(){let t=!1;return{clearReset:()=>{t=!1},reset:()=>{t=!0},isReset:()=>t}}var Qe=g.createContext(qe()),Me=()=>g.useContext(Qe);function Fe(t,e){return typeof t=="function"?t(...e):!!t}function xe(){}var Ue=(t,e)=>{(t.suspense||t.throwOnError||t.experimental_prefetchInRender)&&(e.isReset()||(t.retryOnMount=!1))},De=t=>{g.useEffect(()=>{t.clearReset()},[t])},Pe=({result:t,errorResetBoundary:e,throwOnError:r,query:i})=>t.isError&&!e.isReset()&&!t.isFetching&&i&&Fe(r,[t.error,i]),Be=t=>{const e=t.staleTime;t.suspense&&(t.staleTime=typeof e=="function"?(...r)=>Math.max(e(...r),1e3):Math.max(e??1e3,1e3),typeof t.gcTime=="number"&&(t.gcTime=Math.max(t.gcTime,1e3)))},je=(t,e)=>t.isLoading&&t.isFetching&&!e,Ae=(t,e)=>(t==null?void 0:t.suspense)&&e.isPending,pe=(t,e,r)=>e.fetchOptimistic(t).catch(()=>{r.clearReset()});function He(t,e,r){var u,P,f,B,j;const i=Se(),d=Ie(),l=Me(),o=i.defaultQueryOptions(t);(P=(u=i.getDefaultOptions().queries)==null?void 0:u._experimental_beforeQuery)==null||P.call(u,o),o._optimisticResults=d?"isRestoring":"optimistic",Be(o),Ue(o,l),De(l);const R=!i.getQueryCache().get(o.queryHash),[h]=g.useState(()=>new e(i,o)),E=h.getOptimisticResult(o),T=!d&&t.subscribed!==!1;if(g.useSyncExternalStore(g.useCallback(w=>{const A=T?h.subscribe(we.batchCalls(w)):xe;return h.updateResult(),A},[h,T]),()=>h.getCurrentResult(),()=>h.getCurrentResult()),g.useEffect(()=>{h.setOptions(o,{listeners:!1})},[o,h]),Ae(o,E))throw pe(o,h,l);if(Pe({result:E,errorResetBoundary:l,throwOnError:o.throwOnError,query:i.getQueryCache().get(o.queryHash)}))throw E.error;if((B=(f=i.getDefaultOptions().queries)==null?void 0:f._experimental_afterQuery)==null||B.call(f,o,E),o.experimental_prefetchInRender&&!$&&je(E,d)){const w=R?pe(o,h,l):(j=i.getQueryCache().get(o.queryHash))==null?void 0:j.promise;w==null||w.catch(xe).finally(()=>{h.updateResult()})}return o.notifyOnChangeProps?E:h.trackResult(E)}function Ve(t,e){return He(t,ve)}const Ke=`<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Courses xmlns="http://www.w3.org/namespace/">
    <Course id="course-1" lecturer="lecturer-10" semester="semester-3">
        <name>quia 15ish</name>
        <faculty>dolor</faculty>
        <max_students>123</max_students>
    </Course>
    <Course id="course-3" lecturer="lecturer-10" semester="semester-3">
        <name>quia 15ish</name>
        <faculty>dolor</faculty>
        <max_students>123</max_students>
    </Course>
    <Course id="course-4" lecturer="lecturer-10" semester="semester-3">
        <name>quia 15ish</name>
        <faculty>dolor</faculty>
        <max_students>123</max_students>
    </Course>
    <Course id="course-5" lecturer="lecturer-10" semester="semester-3">
        <name>quia 15ish</name>
        <faculty>dolor</faculty>
        <max_students>123</max_students>
    </Course>
    <Course id="course-6" lecturer="lecturer-10" semester="semester-3">
        <name>quia 15ish</name>
        <faculty>dolor</faculty>
        <max_students>123</max_students>
    </Course>
    <Course id="course-7" lecturer="lecturer-10" semester="semester-3">
        <name>quia 15ish</name>
        <faculty>dolor</faculty>
        <max_students>123</max_students>
    </Course>
    <Course id="course-8" lecturer="lecturer-10" semester="semester-3">
        <name>quia 15ish</name>
        <faculty>dolor</faculty>
        <max_students>123</max_students>
    </Course>
    <Course id="course-9" lecturer="lecturer-10" semester="semester-3">
        <name>quia 15ish</name>
        <faculty>dolor</faculty>
        <max_students>123</max_students>
    </Course>
    <Course id="course-10" lecturer="lecturer-10" semester="semester-3">
        <name>quia 15ish</name>
        <faculty>dolor</faculty>
        <max_students>123</max_students>
    </Course>
    <Course id="course-11" lecturer="lecturer-10" semester="semester-3">
        <name>quia 15ish</name>
        <faculty>dolor</faculty>
        <max_students>123</max_students>
    </Course>
    <Course id="course-12" lecturer="lecturer-10" semester="semester-3">
        <name>quia 15ish</name>
        <faculty>dolor</faculty>
        <max_students>123</max_students>
    </Course>
    <Course id="course-13" lecturer="lecturer-10" semester="semester-3">
        <name>quia 15ish</name>
        <faculty>dolor</faculty>
        <max_students>123</max_students>
    </Course>
    <Course id="course-14" lecturer="lecturer-10" semester="semester-3">
        <name>quia 15ish</name>
        <faculty>dolor</faculty>
        <max_students>123</max_students>
    </Course>
    <Course id="course-15" lecturer="lecturer-10" semester="semester-3">
        <name>quia 15ish</name>
        <faculty>dolor</faculty>
        <max_students>123</max_students>
    </Course>
    <Course id="course-16" lecturer="lecturer-10" semester="semester-3">
        <name>quia</name>
        <faculty>dolor</faculty>
        <max_students>123</max_students>
    </Course>
    <Course id="course-17" lecturer="lecturer-9" semester="semester-3">
        <name>voluptas</name>
        <faculty>officiis</faculty>
        <max_students>377</max_students>
    </Course>
    <Course id="course-18" lecturer="lecturer-2" semester="semester-3">
        <name>minus</name>
        <faculty>illum</faculty>
        <max_students>280</max_students>
    </Course>
    <Course id="course-19" lecturer="lecturer-4" semester="semester-3">
        <name>nobis</name>
        <faculty>est</faculty>
        <max_students>347</max_students>
    </Course>
    <Course id="course-20" lecturer="lecturer-2" semester="semester-3">
        <name>enim</name>
        <faculty>quo</faculty>
        <max_students>297</max_students>
    </Course>
    <Course id="course-21" lecturer="lecturer-2" semester="semester-3">
        <name>harum</name>
        <faculty>sed</faculty>
        <max_students>197</max_students>
    </Course>
    <Course id="course-22" lecturer="lecturer-6" semester="semester-3">
        <name>sunt</name>
        <faculty>delectus</faculty>
        <max_students>233</max_students>
    </Course>
    <Course id="course-23" lecturer="lecturer-8" semester="semester-4">
        <name>dicta</name>
        <faculty>optio</faculty>
        <max_students>161</max_students>
    </Course>
    <Course id="course-24" lecturer="lecturer-9" semester="semester-4">
        <name>sed</name>
        <faculty>dicta</faculty>
        <max_students>331</max_students>
    </Course>
    <Course id="course-25" lecturer="lecturer-7" semester="semester-4">
        <name>amet</name>
        <faculty>expedita</faculty>
        <max_students>28</max_students>
    </Course>
    <Course id="course-26" lecturer="lecturer-6" semester="semester-4">
        <name>velit</name>
        <faculty>et</faculty>
        <max_students>215</max_students>
    </Course>
    <Course id="course-27" lecturer="lecturer-2" semester="semester-4">
        <name>possimus</name>
        <faculty>voluptatem</faculty>
        <max_students>226</max_students>
    </Course>
    <Course id="course-34" lecturer="lecturer-10" semester="semester-1">
        <name>rerum</name>
        <faculty>omnis</faculty>
        <max_students>76</max_students>
    </Course>
</Courses>
`,Ge=`<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Lecturers xmlns="http://www.w3.org/namespace/">
    <Lecturer id="lecturer-1" username="schmitt.samara">
        <firstname>Delbert</firstname>
        <name>Considine</name>
    </Lecturer>
    <Lecturer id="lecturer-2" username="powlowski.kevon">
        <firstname>Jacynthe</firstname>
        <name>Beier</name>
    </Lecturer>
    <Lecturer faculty="aut" id="lecturer-3" username="schmeler.mallie">
        <firstname>Bernice</firstname>
        <name>Stracke</name>
    </Lecturer>
    <Lecturer faculty="aspernatur" id="lecturer-4" username="lerdman">
        <firstname>Karine</firstname>
        <name>Mayer</name>
    </Lecturer>
    <Lecturer faculty="non" id="lecturer-5" username="terrill33">
        <firstname>Aylin</firstname>
        <name>Ferry</name>
    </Lecturer>
    <Lecturer faculty="neque" id="lecturer-6" username="iliana.rowe">
        <firstname>Nella</firstname>
        <name>Kub</name>
    </Lecturer>
    <Lecturer faculty="eveniet" id="lecturer-7" username="elinor21">
        <firstname>Riley</firstname>
        <name>Harris</name>
    </Lecturer>
    <Lecturer faculty="ut" id="lecturer-8" username="dveum">
        <firstname>Bettie</firstname>
        <name>Wehner</name>
    </Lecturer>
    <Lecturer id="lecturer-9" username="kuhn.ashtyn">
        <firstname>Marques</firstname>
        <name>O'Hara</name>
    </Lecturer>
    <Lecturer faculty="eius" id="lecturer-10" username="jzboncak">
        <firstname>Tad</firstname>
        <name>Watsica</name>
    </Lecturer>
    <Lecturer faculty="Computer Science" id="0da5f26c-3d21-4ebf-bfa0-b1d492c31e71" username="hbrosen">
        <name>Brosenne</name>
        <firstname>Hendrik</firstname>
    </Lecturer>
    <Lecturer faculty="Computer Science" id="d775f917-eb32-4e51-a2df-448c2058f4fb" username="wmay">
        <name>May</name>
        <firstname>Wolfgang</firstname>
    </Lecturer>
    <Lecturer faculty="Computer Science" id="03f1deb9-4902-4548-ae0b-e74006bb82d9" username="lars.quentin">
        <name>Quentin</name>
        <firstname>Lars</firstname>
    </Lecturer>
    <Lecturer faculty="Computer Science" id="9c378a84-2f6e-4dbe-a2e3-af4ab1888cc2" username="frederik.hennecke">
        <name>Hennecke</name>
        <firstname>Frederik</firstname>
    </Lecturer>
    <Lecturer faculty="Computer Science" id="cef812d3-06d3-4c87-8399-267ac31b8429" username="v.mattfeld">
        <name>Mattfeld</name>
        <firstname>Valerius Albert Gongjus</firstname>
    </Lecturer>
</Lecturers>
`,Xe=`<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Semesters xmlns="http://www.w3.org/namespace/">
    <Semester id="semester-1">
        <name>Winter Semester 2025/2026</name>
        <start>2025-10-01T00:00:00</start>
        <end>2026-03-31T00:00:00</end>
    </Semester>
    <Semester id="semester-2">
        <name>Summer Semester 2025</name>
        <start>2025-04-01T00:00:00</start>
        <end>2025-09-30T00:00:00</end>
    </Semester>
    <Semester id="semester-3">
        <name>Winter Semester 2026/2027</name>
        <start>2026-10-01T00:00:00</start>
        <end>2027-03-31T00:00:00</end>
    </Semester>
    <Semester id="semester-4">
        <name>Summer Semester 2026</name>
        <start>2026-04-01T00:00:00</start>
        <end>2026-09-30T00:00:00</end>
    </Semester>
</Semesters>`,Je=`<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Exams xmlns="http://www.w3.org/namespace/">
    <Exam id="exam-1" course="course-1">
        <date>2025-11-22T12:26:11</date>
        <is_online>1</is_online>
        <is_written>0</is_written>
        <room_or_link>http://robel.net/ipsa-enim-et-vel-ipsam-tempora-laborum.html</room_or_link>
    </Exam>
    <Exam id="exam-2">
        <date>2025-11-17T12:21:22</date>
        <is_online>0</is_online>
        <is_written>1</is_written>
        <room_or_link>Room 82219</room_or_link>
    </Exam>
    <Exam id="exam-3" course="course-3">
        <date>2026-01-06T12:55:07</date>
        <is_online>0</is_online>
        <is_written>0</is_written>
        <room_or_link>Room 2131</room_or_link>
    </Exam>
    <Exam id="exam-4" course="course-4">
        <date>2026-01-22T12:44:18</date>
        <is_online>0</is_online>
        <is_written>1</is_written>
        <room_or_link>Room 3857</room_or_link>
    </Exam>
    <Exam id="exam-5" course="course-4">
        <date>2026-02-19T14:34:02</date>
        <is_online>1</is_online>
        <is_written>1</is_written>
        <room_or_link>http://marks.com/</room_or_link>
    </Exam>
    <Exam id="exam-6" course="course-5">
        <date>2025-12-10T08:59:09</date>
        <is_online>0</is_online>
        <is_written>0</is_written>
        <room_or_link>Room 61963</room_or_link>
    </Exam>
    <Exam id="exam-7" course="course-5">
        <date>2026-01-17T19:37:33</date>
        <is_online>0</is_online>
        <is_written>0</is_written>
        <room_or_link>Room 972</room_or_link>
    </Exam>
    <Exam id="exam-8" course="course-6">
        <date>2025-11-18T18:52:07</date>
        <is_online>1</is_online>
        <is_written>0</is_written>
        <room_or_link>http://gleason.com/</room_or_link>
    </Exam>
    <Exam id="exam-9" course="course-7">
        <date>2025-08-28T07:41:13</date>
        <is_online>0</is_online>
        <is_written>1</is_written>
        <room_or_link>Room 1494</room_or_link>
    </Exam>
    <Exam id="exam-10" course="course-7">
        <date>2025-06-20T03:34:36</date>
        <is_online>0</is_online>
        <is_written>1</is_written>
        <room_or_link>Room 4479</room_or_link>
    </Exam>
    <Exam id="exam-11" course="course-8">
        <date>2025-09-12T12:06:47</date>
        <is_online>0</is_online>
        <is_written>0</is_written>
        <room_or_link>Room 3586</room_or_link>
    </Exam>
    <Exam id="exam-12" course="course-8">
        <date>2025-06-08T04:50:38</date>
        <is_online>1</is_online>
        <is_written>1</is_written>
        <room_or_link>http://wyman.net/dicta-quibusdam-possimus-nihil</room_or_link>
    </Exam>
    <Exam id="exam-13" course="course-9">
        <date>2025-09-01T05:03:13</date>
        <is_online>1</is_online>
        <is_written>1</is_written>
        <room_or_link>http://mohr.com/et-id-voluptas-explicabo-impedit-nulla.html</room_or_link>
    </Exam>
    <Exam id="exam-14" course="course-10">
        <date>2025-05-02T00:23:24</date>
        <is_online>0</is_online>
        <is_written>0</is_written>
        <room_or_link>Room 527</room_or_link>
    </Exam>
    <Exam id="exam-15" course="course-10">
        <date>2025-07-28T03:53:27</date>
        <is_online>0</is_online>
        <is_written>0</is_written>
        <room_or_link>Room 14981</room_or_link>
    </Exam>
    <Exam id="exam-16" course="course-11">
        <date>2025-08-12T05:20:27</date>
        <is_online>0</is_online>
        <is_written>1</is_written>
        <room_or_link>Room 8604</room_or_link>
    </Exam>
    <Exam id="exam-17" course="course-12">
        <date>2025-08-10T04:57:19</date>
        <is_online>0</is_online>
        <is_written>0</is_written>
        <room_or_link>Room 1169</room_or_link>
    </Exam>
    <Exam id="exam-18" course="course-12">
        <date>2025-04-28T00:48:00</date>
        <is_online>1</is_online>
        <is_written>0</is_written>
        <room_or_link>http://www.klein.info/veniam-aperiam-fuga-voluptate-fuga-doloremque-error-qui-qui
            </room_or_link>
    </Exam>
    <Exam id="exam-19" course="course-13">
        <date>2025-08-03T19:48:28</date>
        <is_online>0</is_online>
        <is_written>0</is_written>
        <room_or_link>Room 99326</room_or_link>
    </Exam>
    <Exam id="exam-20" course="course-14">
        <date>2025-05-04T17:39:59</date>
        <is_online>1</is_online>
        <is_written>1</is_written>
        <room_or_link>http://harber.com/</room_or_link>
    </Exam>
    <Exam id="exam-21" course="course-15">
        <date>2026-11-16T12:42:04</date>
        <is_online>0</is_online>
        <is_written>0</is_written>
        <room_or_link>Room 782</room_or_link>
    </Exam>
    <Exam id="exam-22" course="course-16">
        <date>2027-01-25T12:35:21</date>
        <is_online>1</is_online>
        <is_written>0</is_written>
        <room_or_link>http://rosenbaum.org/beatae-explicabo-optio-illo-iusto-reprehenderit-earum.html</room_or_link>
    </Exam>
    <Exam id="exam-23" course="course-16">
        <date>2027-01-31T06:21:10</date>
        <is_online>0</is_online>
        <is_written>0</is_written>
        <room_or_link>Room 20873</room_or_link>
    </Exam>
    <Exam id="exam-24" course="course-17">
        <date>2027-01-05T19:42:24</date>
        <is_online>0</is_online>
        <is_written>1</is_written>
        <room_or_link>Room 5419</room_or_link>
    </Exam>
    <Exam id="exam-25" course="course-18">
        <date>2026-11-11T00:02:36</date>
        <is_online>0</is_online>
        <is_written>0</is_written>
        <room_or_link>Room 563</room_or_link>
    </Exam>
    <Exam id="exam-26" course="course-18">
        <date>2026-11-04T23:16:36</date>
        <is_online>0</is_online>
        <is_written>1</is_written>
        <room_or_link>Room 10655</room_or_link>
    </Exam>
    <Exam id="exam-27" course="course-19">
        <date>2027-03-16T13:18:30</date>
        <is_online>0</is_online>
        <is_written>1</is_written>
        <room_or_link>Room 53194</room_or_link>
    </Exam>
    <Exam id="exam-28" course="course-19">
        <date>2026-12-29T20:00:55</date>
        <is_online>0</is_online>
        <is_written>0</is_written>
        <room_or_link>Room 5508</room_or_link>
    </Exam>
    <Exam id="exam-29" course="course-20">
        <date>2026-12-21T18:40:21</date>
        <is_online>0</is_online>
        <is_written>0</is_written>
        <room_or_link>Room 4069</room_or_link>
    </Exam>
    <Exam id="exam-30" course="course-21">
        <date>2026-12-21T21:46:16</date>
        <is_online>1</is_online>
        <is_written>0</is_written>
        <room_or_link>http://www.krajcik.info/</room_or_link>
    </Exam>
    <Exam id="exam-31" course="course-22">
        <date>2026-12-16T10:51:31</date>
        <is_online>0</is_online>
        <is_written>1</is_written>
        <room_or_link>Room 9393</room_or_link>
    </Exam>
    <Exam id="exam-32" course="course-22">
        <date>2027-01-01T22:56:51</date>
        <is_online>0</is_online>
        <is_written>1</is_written>
        <room_or_link>Room 861</room_or_link>
    </Exam>
    <Exam id="exam-33" course="course-23">
        <date>2026-08-18T12:23:36</date>
        <is_online>0</is_online>
        <is_written>0</is_written>
        <room_or_link>Room 67589</room_or_link>
    </Exam>
    <Exam id="exam-34" course="course-24">
        <date>2026-05-05T18:26:45</date>
        <is_online>1</is_online>
        <is_written>1</is_written>
        <room_or_link>
                http://www.volkman.com/officia-enim-asperiores-delectus-qui-molestiae-quia-reiciendis-libero.html
            </room_or_link>
    </Exam>
    <Exam id="exam-35" course="course-25">
        <date>2026-06-04T07:09:05</date>
        <is_online>0</is_online>
        <is_written>0</is_written>
        <room_or_link>Room 368</room_or_link>
    </Exam>
    <Exam id="exam-36" course="course-26">
        <date>2026-04-19T10:27:45</date>
        <is_online>0</is_online>
        <is_written>1</is_written>
        <room_or_link>Room 5315</room_or_link>
    </Exam>
    <Exam id="exam-37" course="course-27">
        <date>2026-04-27T17:50:07</date>
        <is_online>1</is_online>
        <is_written>0</is_written>
        <room_or_link>https://langworth.com/esse-blanditiis-aliquid-et-dolor-vel-quaerat-ipsum-ducimus.html
            </room_or_link>
    </Exam>
    <Exam id="exam-38" course="course-27">
        <date>2026-05-22T00:42:14</date>
        <is_online>0</is_online>
        <is_written>1</is_written>
        <room_or_link>Room 7543</room_or_link>
    </Exam>
    <Exam id="exam-39" course="course-23">
        <date>0025-03-29T11:11</date>
        <is_online>1</is_online>
        <is_written>0</is_written>
        <room_or_link>BIG BOI EXAM</room_or_link>
    </Exam>
</Exams>`;export{Je as T,Xe as a,Ge as b,Ke as c,Ve as u};
