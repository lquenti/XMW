var ce=t=>{throw TypeError(t)};var G=(t,e,i)=>e.has(t)||ce("Cannot "+i);var s=(t,e,i)=>(G(t,e,"read from private field"),i?i.call(t):e.get(t)),d=(t,e,i)=>e.has(t)?ce("Cannot add the same private member more than once"):e instanceof WeakSet?e.add(t):e.set(t,i),u=(t,e,i,r)=>(G(t,e,"write to private field"),r?r.call(t,i):e.set(t,i),i),l=(t,e,i)=>(G(t,e,"access private method"),i);import{S as Se,h as le,i as O,s as J,k as W,n as Oe,l as X,m as fe,t as xe,o as Ee,q as Ie,v as de,w as me,r as I,x as Qe}from"./index-SsTAdifg.js";var R,n,z,g,T,P,Q,S,N,_,L,F,U,w,k,a,q,Y,Z,ee,te,se,ie,re,ve,Re,we=(Re=class extends Se{constructor(e,i){super();d(this,a);d(this,R);d(this,n);d(this,z);d(this,g);d(this,T);d(this,P);d(this,Q);d(this,S);d(this,N);d(this,_);d(this,L);d(this,F);d(this,U);d(this,w);d(this,k,new Set);this.options=i,u(this,R,e),u(this,S,null),u(this,Q,le()),this.options.experimental_prefetchInRender||s(this,Q).reject(new Error("experimental_prefetchInRender feature flag is not enabled")),this.bindMethods(),this.setOptions(i)}bindMethods(){this.refetch=this.refetch.bind(this)}onSubscribe(){this.listeners.size===1&&(s(this,n).addObserver(this),pe(s(this,n),this.options)?l(this,a,q).call(this):this.updateResult(),l(this,a,te).call(this))}onUnsubscribe(){this.hasListeners()||this.destroy()}shouldFetchOnReconnect(){return ne(s(this,n),this.options,this.options.refetchOnReconnect)}shouldFetchOnWindowFocus(){return ne(s(this,n),this.options,this.options.refetchOnWindowFocus)}destroy(){this.listeners=new Set,l(this,a,se).call(this),l(this,a,ie).call(this),s(this,n).removeObserver(this)}setOptions(e,i){const r=this.options,f=s(this,n);if(this.options=s(this,R).defaultQueryOptions(e),this.options.enabled!==void 0&&typeof this.options.enabled!="boolean"&&typeof this.options.enabled!="function"&&typeof O(this.options.enabled,s(this,n))!="boolean")throw new Error("Expected enabled to be a boolean or a callback that returns a boolean");l(this,a,re).call(this),s(this,n).setOptions(this.options),r._defaulted&&!J(this.options,r)&&s(this,R).getQueryCache().notify({type:"observerOptionsUpdated",query:s(this,n),observer:this});const c=this.hasListeners();c&&be(s(this,n),f,this.options,r)&&l(this,a,q).call(this),this.updateResult(i),c&&(s(this,n)!==f||O(this.options.enabled,s(this,n))!==O(r.enabled,s(this,n))||W(this.options.staleTime,s(this,n))!==W(r.staleTime,s(this,n)))&&l(this,a,Y).call(this);const h=l(this,a,Z).call(this);c&&(s(this,n)!==f||O(this.options.enabled,s(this,n))!==O(r.enabled,s(this,n))||h!==s(this,w))&&l(this,a,ee).call(this,h)}getOptimisticResult(e){const i=s(this,R).getQueryCache().build(s(this,R),e),r=this.createResult(i,e);return Fe(this,r)&&(u(this,g,r),u(this,P,this.options),u(this,T,s(this,n).state)),r}getCurrentResult(){return s(this,g)}trackResult(e,i){const r={};return Object.keys(e).forEach(f=>{Object.defineProperty(r,f,{configurable:!1,enumerable:!0,get:()=>(this.trackProp(f),i==null||i(f),e[f])})}),r}trackProp(e){s(this,k).add(e)}getCurrentQuery(){return s(this,n)}refetch({...e}={}){return this.fetch({...e})}fetchOptimistic(e){const i=s(this,R).defaultQueryOptions(e),r=s(this,R).getQueryCache().build(s(this,R),i);return r.fetch().then(()=>this.createResult(r,i))}fetch(e){return l(this,a,q).call(this,{...e,cancelRefetch:e.cancelRefetch??!0}).then(()=>(this.updateResult(),s(this,g)))}createResult(e,i){var ue;const r=s(this,n),f=this.options,c=s(this,g),h=s(this,T),x=s(this,P),v=e!==r?e.state:s(this,z),{state:E}=e;let o={...E},j=!1,b;if(i._optimisticResults){const y=this.hasListeners(),D=!y&&pe(e,i),M=y&&be(e,r,i,f);(D||M)&&(o={...o,...Ie(E.data,e.options)}),i._optimisticResults==="isRestoring"&&(o.fetchStatus="idle")}let{error:B,errorUpdatedAt:A,status:m}=o;if(i.select&&o.data!==void 0)if(c&&o.data===(h==null?void 0:h.data)&&i.select===s(this,N))b=s(this,_);else try{u(this,N,i.select),b=i.select(o.data),b=de(c==null?void 0:c.data,b,i),u(this,_,b),u(this,S,null)}catch(y){u(this,S,y)}else b=o.data;if(i.placeholderData!==void 0&&b===void 0&&m==="pending"){let y;if(c!=null&&c.isPlaceholderData&&i.placeholderData===(x==null?void 0:x.placeholderData))y=c.data;else if(y=typeof i.placeholderData=="function"?i.placeholderData((ue=s(this,L))==null?void 0:ue.state.data,s(this,L)):i.placeholderData,i.select&&y!==void 0)try{y=i.select(y),u(this,S,null)}catch(D){u(this,S,D)}y!==void 0&&(m="success",b=de(c==null?void 0:c.data,y,i),j=!0)}s(this,S)&&(B=s(this,S),b=s(this,_),A=Date.now(),m="error");const H=o.fetchStatus==="fetching",K=m==="pending",$=m==="error",he=K&&H,oe=b!==void 0,C={status:m,fetchStatus:o.fetchStatus,isPending:K,isSuccess:m==="success",isError:$,isInitialLoading:he,isLoading:he,data:b,dataUpdatedAt:o.dataUpdatedAt,error:B,errorUpdatedAt:A,failureCount:o.fetchFailureCount,failureReason:o.fetchFailureReason,errorUpdateCount:o.errorUpdateCount,isFetched:o.dataUpdateCount>0||o.errorUpdateCount>0,isFetchedAfterMount:o.dataUpdateCount>v.dataUpdateCount||o.errorUpdateCount>v.errorUpdateCount,isFetching:H,isRefetching:H&&!K,isLoadingError:$&&!oe,isPaused:o.fetchStatus==="paused",isPlaceholderData:j,isRefetchError:$&&oe,isStale:ae(e,i),refetch:this.refetch,promise:s(this,Q)};if(this.options.experimental_prefetchInRender){const y=V=>{C.status==="error"?V.reject(C.error):C.data!==void 0&&V.resolve(C.data)},D=()=>{const V=u(this,Q,C.promise=le());y(V)},M=s(this,Q);switch(M.status){case"pending":e.queryHash===r.queryHash&&y(M);break;case"fulfilled":(C.status==="error"||C.data!==M.value)&&D();break;case"rejected":(C.status!=="error"||C.error!==M.reason)&&D();break}}return C}updateResult(e){const i=s(this,g),r=this.createResult(s(this,n),this.options);if(u(this,T,s(this,n).state),u(this,P,this.options),s(this,T).data!==void 0&&u(this,L,s(this,n)),J(r,i))return;u(this,g,r);const f={},c=()=>{if(!i)return!0;const{notifyOnChangeProps:h}=this.options,x=typeof h=="function"?h():h;if(x==="all"||!x&&!s(this,k).size)return!0;const p=new Set(x??s(this,k));return this.options.throwOnError&&p.add("error"),Object.keys(s(this,g)).some(v=>{const E=v;return s(this,g)[E]!==i[E]&&p.has(E)})};(e==null?void 0:e.listeners)!==!1&&c()&&(f.listeners=!0),l(this,a,ve).call(this,{...f,...e})}onQueryUpdate(){this.updateResult(),this.hasListeners()&&l(this,a,te).call(this)}},R=new WeakMap,n=new WeakMap,z=new WeakMap,g=new WeakMap,T=new WeakMap,P=new WeakMap,Q=new WeakMap,S=new WeakMap,N=new WeakMap,_=new WeakMap,L=new WeakMap,F=new WeakMap,U=new WeakMap,w=new WeakMap,k=new WeakMap,a=new WeakSet,q=function(e){l(this,a,re).call(this);let i=s(this,n).fetch(this.options,e);return e!=null&&e.throwOnError||(i=i.catch(Oe)),i},Y=function(){l(this,a,se).call(this);const e=W(this.options.staleTime,s(this,n));if(X||s(this,g).isStale||!fe(e))return;const r=xe(s(this,g).dataUpdatedAt,e)+1;u(this,F,setTimeout(()=>{s(this,g).isStale||this.updateResult()},r))},Z=function(){return(typeof this.options.refetchInterval=="function"?this.options.refetchInterval(s(this,n)):this.options.refetchInterval)??!1},ee=function(e){l(this,a,ie).call(this),u(this,w,e),!(X||O(this.options.enabled,s(this,n))===!1||!fe(s(this,w))||s(this,w)===0)&&u(this,U,setInterval(()=>{(this.options.refetchIntervalInBackground||Ee.isFocused())&&l(this,a,q).call(this)},s(this,w)))},te=function(){l(this,a,Y).call(this),l(this,a,ee).call(this,l(this,a,Z).call(this))},se=function(){s(this,F)&&(clearTimeout(s(this,F)),u(this,F,void 0))},ie=function(){s(this,U)&&(clearInterval(s(this,U)),u(this,U,void 0))},re=function(){const e=s(this,R).getQueryCache().build(s(this,R),this.options);if(e===s(this,n))return;const i=s(this,n);u(this,n,e),u(this,z,e.state),this.hasListeners()&&(i==null||i.removeObserver(this),e.addObserver(this))},ve=function(e){me.batch(()=>{e.listeners&&this.listeners.forEach(i=>{i(s(this,g))}),s(this,R).getQueryCache().notify({query:s(this,n),type:"observerResultsUpdated"})})},Re);function Te(t,e){return O(e.enabled,t)!==!1&&t.state.data===void 0&&!(t.state.status==="error"&&e.retryOnMount===!1)}function pe(t,e){return Te(t,e)||t.state.data!==void 0&&ne(t,e,e.refetchOnMount)}function ne(t,e,i){if(O(e.enabled,t)!==!1){const r=typeof i=="function"?i(t):i;return r==="always"||r!==!1&&ae(t,e)}return!1}function be(t,e,i,r){return(t!==e||O(r.enabled,t)===!1)&&(!i.suspense||t.state.status!=="error")&&ae(t,i)}function ae(t,e){return O(e.enabled,t)!==!1&&t.isStaleByTime(W(e.staleTime,t))}function Fe(t,e){return!J(t.getCurrentResult(),e)}var Ce=I.createContext(!1),Ue=()=>I.useContext(Ce);Ce.Provider;function De(){let t=!1;return{clearReset:()=>{t=!1},reset:()=>{t=!0},isReset:()=>t}}var Me=I.createContext(De()),Pe=()=>I.useContext(Me);function _e(t,e){return typeof t=="function"?t(...e):!!t}function ye(){}var Le=(t,e)=>{(t.suspense||t.throwOnError||t.experimental_prefetchInRender)&&(e.isReset()||(t.retryOnMount=!1))},ke=t=>{I.useEffect(()=>{t.clearReset()},[t])},je=({result:t,errorResetBoundary:e,throwOnError:i,query:r})=>t.isError&&!e.isReset()&&!t.isFetching&&r&&_e(i,[t.error,r]),Be=t=>{const e=t.staleTime;t.suspense&&(t.staleTime=typeof e=="function"?(...i)=>Math.max(e(...i),1e3):Math.max(e??1e3,1e3),typeof t.gcTime=="number"&&(t.gcTime=Math.max(t.gcTime,1e3)))},Ae=(t,e)=>t.isLoading&&t.isFetching&&!e,He=(t,e)=>(t==null?void 0:t.suspense)&&e.isPending,ge=(t,e,i)=>e.fetchOptimistic(t).catch(()=>{i.clearReset()});function qe(t,e,i){var o,j,b,B,A;const r=Qe(),f=Ue(),c=Pe(),h=r.defaultQueryOptions(t);(j=(o=r.getDefaultOptions().queries)==null?void 0:o._experimental_beforeQuery)==null||j.call(o,h),h._optimisticResults=f?"isRestoring":"optimistic",Be(h),Le(h,c),ke(c);const x=!r.getQueryCache().get(h.queryHash),[p]=I.useState(()=>new e(r,h)),v=p.getOptimisticResult(h),E=!f&&t.subscribed!==!1;if(I.useSyncExternalStore(I.useCallback(m=>{const H=E?p.subscribe(me.batchCalls(m)):ye;return p.updateResult(),H},[p,E]),()=>p.getCurrentResult(),()=>p.getCurrentResult()),I.useEffect(()=>{p.setOptions(h,{listeners:!1})},[h,p]),He(h,v))throw ge(h,p,c);if(je({result:v,errorResetBoundary:c,throwOnError:h.throwOnError,query:r.getQueryCache().get(h.queryHash)}))throw v.error;if((B=(b=r.getDefaultOptions().queries)==null?void 0:b._experimental_afterQuery)==null||B.call(b,h,v),h.experimental_prefetchInRender&&!X&&Ae(v,f)){const m=x?ge(h,p,c):(A=r.getQueryCache().get(h.queryHash))==null?void 0:A.promise;m==null||m.catch(ye).finally(()=>{p.updateResult()})}return h.notifyOnChangeProps?v:p.trackResult(v)}function ze(t,e){return qe(t,we)}const Ke=()=>ze({queryKey:["courses"],queryFn:()=>fetch("http://localhost:8080/exa/courses").then(t=>t.text())});export{ze as a,Ke as u};
