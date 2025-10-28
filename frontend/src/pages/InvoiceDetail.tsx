import { useParams } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { api } from '@/lib/api'

export function InvoiceDetailPage() {
  const { id } = useParams()
  const { data } = useQuery({
    queryKey: ['invoice', id],
    queryFn: async () => (await api.get(`/invoices/${id}`)).data,
  })
  if (!data) return null
  return (
    <div className="p-6">
      <h1 className="text-xl font-semibold">Fatura Detayı</h1>
      <pre className="bg-gray-900 text-gray-100 p-4 rounded mt-4 overflow-auto">{JSON.stringify(data, null, 2)}</pre>
    </div>
  )
}


