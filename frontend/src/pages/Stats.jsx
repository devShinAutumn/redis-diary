
import React, {useEffect, useState} from 'react'
import api from '../api'
export default function Stats(){
  const [stats,setStats]=useState({})
  useEffect(()=>{ async function f(){ try{ const r=await api.get('/stats/weekly'); setStats(r.data)}catch(e){console.error(e)} } f() },[])
  return (
    <div>
      <h3>Weekly Stats</h3>
      <pre>{JSON.stringify(stats, null, 2)}</pre>
    </div>
  )
}
