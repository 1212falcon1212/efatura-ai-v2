import { useParams } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { api } from '@/lib/api'
import { useState } from 'react'

export function InvoiceDetailPage() {
  const { id } = useParams()
  const { data } = useQuery({
    queryKey: ['invoice', id],
    queryFn: async () => (await api.get(`/invoices/${id}`)).data,
  })
  const [msg, setMsg] = useState('')
  if (!data) return null
  return (
    <div className="p-6">
      <h1 className="text-xl font-semibold">Fatura Detayı</h1>
      {msg && <div data-testid="toast" className="mt-2 text-green-700">{msg}</div>}
      <div className="mt-3 space-x-2">
        <button
          data-testid="sign-btn"
          className="btn"
          onClick={async () => {
            await api.post(`/invoices/${id}/sign`)
            setMsg('Fatura imzalandı')
          }}>
          İmzala
        </button>
        <button
          data-testid="send-btn"
          className="btn"
          onClick={async () => {
            await api.post(`/invoices/${id}/send`)
            setMsg('Fatura gönderildi')
          }}>
          Gönder
        </button>
      </div>
      <pre className="bg-gray-900 text-gray-100 p-4 rounded mt-4 overflow-auto">{JSON.stringify(data, null, 2)}</pre>
    </div>
  )
}


