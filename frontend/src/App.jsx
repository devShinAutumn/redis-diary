
import React from 'react'
import { Routes, Route, Link } from 'react-router-dom'
import Login from './pages/Login'
import Register from './pages/Register'
import DiaryList from './pages/DiaryList'
import DiaryCreate from './pages/DiaryCreate'
import Stats from './pages/Stats'

export default function App() {
  return (
    <div style={{padding:20}}>
      <nav style={{marginBottom:20}}>
        <Link to="/">Home</Link> | <Link to="/login">Login</Link> | <Link to="/diaries">Diaries</Link> | <Link to="/create">New</Link> | <Link to="/stats">Stats</Link>
      </nav>
      <Routes>
        <Route path='/' element={<div>Welcome to Redis Diary</div>} />
        <Route path='/login' element={<Login/>} />
        <Route path='/register' element={<Register/>} />
        <Route path='/diaries' element={<DiaryList/>} />
        <Route path='/create' element={<DiaryCreate/>} />
        <Route path='/stats' element={<Stats/>} />
      </Routes>
    </div>
  )
}
