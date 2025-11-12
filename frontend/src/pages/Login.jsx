
import React, {useState} from 'react'
import api from '../api'

export default function Login(){
  const [username,setUsername] = useState('')
  const [password,setPassword] = useState('')

  async function submit(e){
    e.preventDefault()
    try{
      const r = await api.post('/auth/login',{username,password})
      localStorage.setItem('token', r.data.token)
      alert('Logged in')
    }catch(e){
      alert('Login failed')
    }
  }

  return (
    <form onSubmit={submit}>
      <h3>Login</h3>
      <div><input value={username} onChange={e=>setUsername(e.target.value)} placeholder='username'/></div>
      <div><input type='password' value={password} onChange={e=>setPassword(e.target.value)} placeholder='password'/></div>
      <button>Login</button>
    </form>
  )
}
