import { useState } from 'react'
import { api } from '@/lib/api'

export function LoginPage() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [tenant, setTenant] = useState('')
  const [error, setError] = useState('')

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault()
    setError('')
    try {
      const res = await api.post('/auth/login', { username, password, tenant })
      localStorage.setItem('token', res.data.accessToken)
      window.location.href = '/invoices'
    } catch (e: any) {
      setError(e?.response?.data?.detail || 'Giriş başarısız')
    }
  }

  return (
    <div className="min-h-screen grid place-items-center">
      <form onSubmit={onSubmit} className="w-full max-w-sm space-y-3 p-6 bg-white shadow rounded">
        <h1 className="text-xl font-semibold">Giriş</h1>
        <input className="input" placeholder="Tenant UUID" value={tenant} onChange={(e) => setTenant(e.target.value)} />
        <input className="input" placeholder="Kullanıcı adı" value={username} onChange={(e) => setUsername(e.target.value)} />
        <input className="input" type="password" placeholder="Şifre" value={password} onChange={(e) => setPassword(e.target.value)} />
        {error && <div className="text-red-600 text-sm">{error}</div>}
        <button className="btn w-full" type="submit">Giriş Yap</button>
      </form>
    </div>
  )
}


