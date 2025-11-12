
import React, {useState} from 'react'
import api from '../api'

export default function Register(){
  const [username,setUsername] = useState('')
  const [email,setEmail] = useState('')
  const [password,setPassword] = useState('')

  async function submit(e){
    e.preventDefault()
    try{
      await api.post('/auth/signup',{username,email,password})
      alert('Registered, please login')
    }catch(e){ alert('Register failed') }
  }

  return (
    <form onSubmit={submit}>
      <h3>Register</h3>
      <input value={username} onChange={e=>setUsername(e.target.value)} placeholder='username'/><br/>
      <input value={email} onChange={e=>setEmail(e.target.value)} placeholder='email'/><br/>
      <input type='password' value={password} onChange={e=>setPassword(e.target.value)} placeholder='password'/><br/>
      <button>Sign up</button>
    </form>
  )
}
