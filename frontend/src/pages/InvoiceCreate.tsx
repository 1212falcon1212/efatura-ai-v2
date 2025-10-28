import { useState } from 'react'
import { api } from '@/lib/api'
import { useNavigate } from 'react-router-dom'

export function InvoiceCreatePage() {
  const [customerName, setCustomerName] = useState('')
  const [totalGross, setTotalGross] = useState('')
  const nav = useNavigate()

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault()
    const res = await api.post('/invoices', { customerName, totalGross: Number(totalGross) })
    nav(`/invoices/${res.data.id}`)
  }

  return (
    <div className="p-6">
      <h1 className="text-xl font-semibold mb-4">Yeni Fatura</h1>
      <form onSubmit={onSubmit} className="space-y-3 max-w-md">
        <input className="input" placeholder="Müşteri" value={customerName} onChange={(e) => setCustomerName(e.target.value)} />
        <input className="input" placeholder="Brüt Tutar" value={totalGross} onChange={(e) => setTotalGross(e.target.value)} />
        <button className="btn" type="submit">Kaydet</button>
      </form>
    </div>
  )
}


