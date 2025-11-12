
import React, {useEffect, useState} from 'react'
import api from '../api'

export default function DiaryList(){
  const [list,setList]=useState([])
  useEffect(()=>{
    async function load(){
      try{
        const r = await api.get('/diaries')
        setList(r.data)
      }catch(e){ console.error(e) }
    }
    load()
  },[])
  return (
    <div>
      <h3>Your Diaries</h3>
      {list.length===0 && <div>No diaries (or unauthenticated in template)</div>}
      <ul>
        {list.map(d => (<li key={d.id}>{d.title} — {d.emotionLabel} ({d.emotionScore})</li>))}
      </ul>
    </div>
  )
}
