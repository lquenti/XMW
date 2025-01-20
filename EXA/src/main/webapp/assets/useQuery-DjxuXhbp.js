var ce=t=>{throw TypeError(t)};var G=(t,e,r)=>e.has(t)||ce("Cannot "+r);var s=(t,e,r)=>(G(t,e,"read from private field"),r?r.call(t):e.get(t)),f=(t,e,r)=>e.has(t)?ce("Cannot add the same private member more than once"):e instanceof WeakSet?e.add(t):e.set(t,r),u=(t,e,r,i)=>(G(t,e,"write to private field"),i?i.call(t,r):e.set(t,r),r),l=(t,e,r)=>(G(t,e,"access private method"),r);import{S as Se,m as le,o as O,s as J,q,t as Oe,v as X,w as de,x as Ee,y as xe,z as Ie,B as fe,n as Re,r as I,l as Qe}from"./index-Cyvq--sK.js";import{s as we,n as pe}from"./utils-km2FGkQ4.js";var y,n,N,m,T,P,Q,S,V,_,L,F,U,w,B,a,z,Y,Z,ee,te,se,re,ie,ve,ye,Te=(ye=class extends Se{constructor(e,r){super();f(this,a);f(this,y);f(this,n);f(this,N);f(this,m);f(this,T);f(this,P);f(this,Q);f(this,S);f(this,V);f(this,_);f(this,L);f(this,F);f(this,U);f(this,w);f(this,B,new Set);this.options=r,u(this,y,e),u(this,S,null),u(this,Q,le()),this.options.experimental_prefetchInRender||s(this,Q).reject(new Error("experimental_prefetchInRender feature flag is not enabled")),this.bindMethods(),this.setOptions(r)}bindMethods(){this.refetch=this.refetch.bind(this)}onSubscribe(){this.listeners.size===1&&(s(this,n).addObserver(this),be(s(this,n),this.options)?l(this,a,z).call(this):this.updateResult(),l(this,a,te).call(this))}onUnsubscribe(){this.hasListeners()||this.destroy()}shouldFetchOnReconnect(){return ne(s(this,n),this.options,this.options.refetchOnReconnect)}shouldFetchOnWindowFocus(){return ne(s(this,n),this.options,this.options.refetchOnWindowFocus)}destroy(){this.listeners=new Set,l(this,a,se).call(this),l(this,a,re).call(this),s(this,n).removeObserver(this)}setOptions(e,r){const i=this.options,d=s(this,n);if(this.options=s(this,y).defaultQueryOptions(e),this.options.enabled!==void 0&&typeof this.options.enabled!="boolean"&&typeof this.options.enabled!="function"&&typeof O(this.options.enabled,s(this,n))!="boolean")throw new Error("Expected enabled to be a boolean or a callback that returns a boolean");l(this,a,ie).call(this),s(this,n).setOptions(this.options),i._defaulted&&!J(this.options,i)&&s(this,y).getQueryCache().notify({type:"observerOptionsUpdated",query:s(this,n),observer:this});const c=this.hasListeners();c&&ge(s(this,n),d,this.options,i)&&l(this,a,z).call(this),this.updateResult(r),c&&(s(this,n)!==d||O(this.options.enabled,s(this,n))!==O(i.enabled,s(this,n))||q(this.options.staleTime,s(this,n))!==q(i.staleTime,s(this,n)))&&l(this,a,Y).call(this);const h=l(this,a,Z).call(this);c&&(s(this,n)!==d||O(this.options.enabled,s(this,n))!==O(i.enabled,s(this,n))||h!==s(this,w))&&l(this,a,ee).call(this,h)}getOptimisticResult(e){const r=s(this,y).getQueryCache().build(s(this,y),e),i=this.createResult(r,e);return Ue(this,i)&&(u(this,m,i),u(this,P,this.options),u(this,T,s(this,n).state)),i}getCurrentResult(){return s(this,m)}trackResult(e,r){const i={};return Object.keys(e).forEach(d=>{Object.defineProperty(i,d,{configurable:!1,enumerable:!0,get:()=>(this.trackProp(d),r==null||r(d),e[d])})}),i}trackProp(e){s(this,B).add(e)}getCurrentQuery(){return s(this,n)}refetch({...e}={}){return this.fetch({...e})}fetchOptimistic(e){const r=s(this,y).defaultQueryOptions(e),i=s(this,y).getQueryCache().build(s(this,y),r);return i.fetch().then(()=>this.createResult(i,r))}fetch(e){return l(this,a,z).call(this,{...e,cancelRefetch:e.cancelRefetch??!0}).then(()=>(this.updateResult(),s(this,m)))}createResult(e,r){var ue;const i=s(this,n),d=this.options,c=s(this,m),h=s(this,T),E=s(this,P),v=e!==i?e.state:s(this,N),{state:x}=e;let o={...x},j=!1,b;if(r._optimisticResults){const g=this.hasListeners(),D=!g&&be(e,r),M=g&&ge(e,i,r,d);(D||M)&&(o={...o,...Ie(x.data,e.options)}),r._optimisticResults==="isRestoring"&&(o.fetchStatus="idle")}let{error:k,errorUpdatedAt:A,status:R}=o;if(r.select&&o.data!==void 0)if(c&&o.data===(h==null?void 0:h.data)&&r.select===s(this,V))b=s(this,_);else try{u(this,V,r.select),b=r.select(o.data),b=fe(c==null?void 0:c.data,b,r),u(this,_,b),u(this,S,null)}catch(g){u(this,S,g)}else b=o.data;if(r.placeholderData!==void 0&&b===void 0&&R==="pending"){let g;if(c!=null&&c.isPlaceholderData&&r.placeholderData===(E==null?void 0:E.placeholderData))g=c.data;else if(g=typeof r.placeholderData=="function"?r.placeholderData((ue=s(this,L))==null?void 0:ue.state.data,s(this,L)):r.placeholderData,r.select&&g!==void 0)try{g=r.select(g),u(this,S,null)}catch(D){u(this,S,D)}g!==void 0&&(R="success",b=fe(c==null?void 0:c.data,g,r),j=!0)}s(this,S)&&(k=s(this,S),b=s(this,_),A=Date.now(),R="error");const H=o.fetchStatus==="fetching",K=R==="pending",$=R==="error",he=K&&H,oe=b!==void 0,C={status:R,fetchStatus:o.fetchStatus,isPending:K,isSuccess:R==="success",isError:$,isInitialLoading:he,isLoading:he,data:b,dataUpdatedAt:o.dataUpdatedAt,error:k,errorUpdatedAt:A,failureCount:o.fetchFailureCount,failureReason:o.fetchFailureReason,errorUpdateCount:o.errorUpdateCount,isFetched:o.dataUpdateCount>0||o.errorUpdateCount>0,isFetchedAfterMount:o.dataUpdateCount>v.dataUpdateCount||o.errorUpdateCount>v.errorUpdateCount,isFetching:H,isRefetching:H&&!K,isLoadingError:$&&!oe,isPaused:o.fetchStatus==="paused",isPlaceholderData:j,isRefetchError:$&&oe,isStale:ae(e,r),refetch:this.refetch,promise:s(this,Q)};if(this.options.experimental_prefetchInRender){const g=W=>{C.status==="error"?W.reject(C.error):C.data!==void 0&&W.resolve(C.data)},D=()=>{const W=u(this,Q,C.promise=le());g(W)},M=s(this,Q);switch(M.status){case"pending":e.queryHash===i.queryHash&&g(M);break;case"fulfilled":(C.status==="error"||C.data!==M.value)&&D();break;case"rejected":(C.status!=="error"||C.error!==M.reason)&&D();break}}return C}updateResult(e){const r=s(this,m),i=this.createResult(s(this,n),this.options);if(u(this,T,s(this,n).state),u(this,P,this.options),s(this,T).data!==void 0&&u(this,L,s(this,n)),J(i,r))return;u(this,m,i);const d={},c=()=>{if(!r)return!0;const{notifyOnChangeProps:h}=this.options,E=typeof h=="function"?h():h;if(E==="all"||!E&&!s(this,B).size)return!0;const p=new Set(E??s(this,B));return this.options.throwOnError&&p.add("error"),Object.keys(s(this,m)).some(v=>{const x=v;return s(this,m)[x]!==r[x]&&p.has(x)})};(e==null?void 0:e.listeners)!==!1&&c()&&(d.listeners=!0),l(this,a,ve).call(this,{...d,...e})}onQueryUpdate(){this.updateResult(),this.hasListeners()&&l(this,a,te).call(this)}},y=new WeakMap,n=new WeakMap,N=new WeakMap,m=new WeakMap,T=new WeakMap,P=new WeakMap,Q=new WeakMap,S=new WeakMap,V=new WeakMap,_=new WeakMap,L=new WeakMap,F=new WeakMap,U=new WeakMap,w=new WeakMap,B=new WeakMap,a=new WeakSet,z=function(e){l(this,a,ie).call(this);let r=s(this,n).fetch(this.options,e);return e!=null&&e.throwOnError||(r=r.catch(Oe)),r},Y=function(){l(this,a,se).call(this);const e=q(this.options.staleTime,s(this,n));if(X||s(this,m).isStale||!de(e))return;const i=Ee(s(this,m).dataUpdatedAt,e)+1;u(this,F,setTimeout(()=>{s(this,m).isStale||this.updateResult()},i))},Z=function(){return(typeof this.options.refetchInterval=="function"?this.options.refetchInterval(s(this,n)):this.options.refetchInterval)??!1},ee=function(e){l(this,a,re).call(this),u(this,w,e),!(X||O(this.options.enabled,s(this,n))===!1||!de(s(this,w))||s(this,w)===0)&&u(this,U,setInterval(()=>{(this.options.refetchIntervalInBackground||xe.isFocused())&&l(this,a,z).call(this)},s(this,w)))},te=function(){l(this,a,Y).call(this),l(this,a,ee).call(this,l(this,a,Z).call(this))},se=function(){s(this,F)&&(clearTimeout(s(this,F)),u(this,F,void 0))},re=function(){s(this,U)&&(clearInterval(s(this,U)),u(this,U,void 0))},ie=function(){const e=s(this,y).getQueryCache().build(s(this,y),this.options);if(e===s(this,n))return;const r=s(this,n);u(this,n,e),u(this,N,e.state),this.hasListeners()&&(r==null||r.removeObserver(this),e.addObserver(this))},ve=function(e){Re.batch(()=>{e.listeners&&this.listeners.forEach(r=>{r(s(this,m))}),s(this,y).getQueryCache().notify({query:s(this,n),type:"observerResultsUpdated"})})},ye);function Fe(t,e){return O(e.enabled,t)!==!1&&t.state.data===void 0&&!(t.state.status==="error"&&e.retryOnMount===!1)}function be(t,e){return Fe(t,e)||t.state.data!==void 0&&ne(t,e,e.refetchOnMount)}function ne(t,e,r){if(O(e.enabled,t)!==!1){const i=typeof r=="function"?r(t):r;return i==="always"||i!==!1&&ae(t,e)}return!1}function ge(t,e,r,i){return(t!==e||O(i.enabled,t)===!1)&&(!r.suspense||t.state.status!=="error")&&ae(t,r)}function ae(t,e){return O(e.enabled,t)!==!1&&t.isStaleByTime(q(e.staleTime,t))}function Ue(t,e){return!J(t.getCurrentResult(),e)}var Ce=I.createContext(!1),De=()=>I.useContext(Ce);Ce.Provider;function Me(){let t=!1;return{clearReset:()=>{t=!1},reset:()=>{t=!0},isReset:()=>t}}var Pe=I.createContext(Me()),_e=()=>I.useContext(Pe),Le=(t,e)=>{(t.suspense||t.throwOnError||t.experimental_prefetchInRender)&&(e.isReset()||(t.retryOnMount=!1))},Be=t=>{I.useEffect(()=>{t.clearReset()},[t])},je=({result:t,errorResetBoundary:e,throwOnError:r,query:i})=>t.isError&&!e.isReset()&&!t.isFetching&&i&&we(r,[t.error,i]),ke=t=>{const e=t.staleTime;t.suspense&&(t.staleTime=typeof e=="function"?(...r)=>Math.max(e(...r),1e3):Math.max(e??1e3,1e3),typeof t.gcTime=="number"&&(t.gcTime=Math.max(t.gcTime,1e3)))},Ae=(t,e)=>t.isLoading&&t.isFetching&&!e,He=(t,e)=>(t==null?void 0:t.suspense)&&e.isPending,me=(t,e,r)=>e.fetchOptimistic(t).catch(()=>{r.clearReset()});function ze(t,e,r){var o,j,b,k,A;const i=Qe(),d=De(),c=_e(),h=i.defaultQueryOptions(t);(j=(o=i.getDefaultOptions().queries)==null?void 0:o._experimental_beforeQuery)==null||j.call(o,h),h._optimisticResults=d?"isRestoring":"optimistic",ke(h),Le(h,c),Be(c);const E=!i.getQueryCache().get(h.queryHash),[p]=I.useState(()=>new e(i,h)),v=p.getOptimisticResult(h),x=!d&&t.subscribed!==!1;if(I.useSyncExternalStore(I.useCallback(R=>{const H=x?p.subscribe(Re.batchCalls(R)):pe;return p.updateResult(),H},[p,x]),()=>p.getCurrentResult(),()=>p.getCurrentResult()),I.useEffect(()=>{p.setOptions(h,{listeners:!1})},[h,p]),He(h,v))throw me(h,p,c);if(je({result:v,errorResetBoundary:c,throwOnError:h.throwOnError,query:i.getQueryCache().get(h.queryHash)}))throw v.error;if((k=(b=i.getDefaultOptions().queries)==null?void 0:b._experimental_afterQuery)==null||k.call(b,h,v),h.experimental_prefetchInRender&&!X&&Ae(v,d)){const R=E?me(h,p,c):(A=i.getQueryCache().get(h.queryHash))==null?void 0:A.promise;R==null||R.catch(pe).finally(()=>{p.updateResult()})}return h.notifyOnChangeProps?v:p.trackResult(v)}function Ke(t,e){return ze(t,Te)}export{Ke as u};
