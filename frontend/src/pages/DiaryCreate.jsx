
import React, {useState} from 'react'
import api from '../api'

export default function DiaryCreate(){
  const [title,setTitle]=useState('')
  const [content,setContent]=useState('')
  async function submit(e){
    e.preventDefault()
    try{
      await api.post('/diaries',{title,content})
      alert('Created')
    }catch(e){ alert('Failed') }
  }
  return (
    <form onSubmit={submit}>
      <h3>New Diary</h3>
      <input value={title} onChange={e=>setTitle(e.target.value)} placeholder='title'/><br/>
      <textarea value={content} onChange={e=>setContent(e.target.value)} placeholder='content' rows={6} cols={60}/><br/>
      <button>Create</button>
    </form>
  )
}
