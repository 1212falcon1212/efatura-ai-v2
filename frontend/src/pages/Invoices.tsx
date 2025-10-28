import { useQuery } from '@tanstack/react-query'
import { api } from '@/lib/api'
import { Link } from 'react-router-dom'

type Invoice = { id: string; invoiceNo: string; customerName: string; totalGross: number }

export function InvoicesPage() {
  const { data } = useQuery({
    queryKey: ['invoices'],
    queryFn: async () => (await api.get('/invoices')).data as Invoice[],
  })

  return (
    <div className="p-6">
      <div className="flex items-center justify-between">
        <h1 className="text-xl font-semibold">Faturalar</h1>
        <Link to="/invoices/new" className="btn">Yeni Fatura</Link>
      </div>
      <div className="mt-4 bg-white shadow rounded">
        <table className="w-full">
          <thead>
            <tr className="text-left">
              <th className="p-2">No</th>
              <th className="p-2">Müşteri</th>
              <th className="p-2">Tutar</th>
            </tr>
          </thead>
          <tbody>
            {data?.map((inv) => (
              <tr key={inv.id} className="border-t">
                <td className="p-2"><Link className="text-blue-600" to={`/invoices/${inv.id}`}>{inv.invoiceNo}</Link></td>
                <td className="p-2">{inv.customerName}</td>
                <td className="p-2">{inv.totalGross.toFixed(2)} TL</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}


