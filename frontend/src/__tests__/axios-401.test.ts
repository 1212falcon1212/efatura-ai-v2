import { describe, it, expect, beforeEach, vi } from 'vitest'
import { api } from '../lib/api'

describe('axios 401 interceptor', () => {
  beforeEach(() => {
    localStorage.setItem('token', 'x')
    // stub window.location
    // @ts-expect-error override for test
    delete window.location
    // @ts-expect-error minimal shape
    window.location = { href: '' }
  })

  it('clears token and redirects to /login on 401', async () => {
    const handler = (api.interceptors.response as any).handlers.find((h: any) => h.rejected)
    expect(handler).toBeTruthy()
    try {
      await handler.rejected({ response: { status: 401 } })
    } catch {}
    expect(localStorage.getItem('token')).toBeNull()
    expect(window.location.href).toContain('/login')
  })
})


