import { createBrowserRouter } from 'react-router-dom'
import { LoginPage } from './pages/Login'
import { InvoicesPage } from './pages/Invoices'
import { InvoiceDetailPage } from './pages/InvoiceDetail'
import { InvoiceCreatePage } from './pages/InvoiceCreate'

export const router = createBrowserRouter([
  { path: '/', element: <InvoicesPage /> },
  { path: '/login', element: <LoginPage /> },
  { path: '/invoices', element: <InvoicesPage /> },
  { path: '/invoices/:id', element: <InvoiceDetailPage /> },
  { path: '/invoices/new', element: <InvoiceCreatePage /> },
])


