import { describe, it, expect, beforeEach, vi } from 'vitest'
import { api } from '../lib/api'

describe('axios 401 interceptor', () => {
  beforeEach(() => {
    const store: Record<string, string> = {}
    const storageMock = {
      getItem: vi.fn((k: string) => (Object.prototype.hasOwnProperty.call(store, k) ? store[k] : null)),
      setItem: vi.fn((k: string, v: string) => {
        store[k] = String(v)
      }),
      removeItem: vi.fn((k: string) => {
        delete store[k]
      }),
      clear: vi.fn(() => {
        for (const k of Object.keys(store)) delete store[k]
      }),
    } as unknown as Storage
    Object.defineProperty(window, 'localStorage', { value: storageMock, configurable: true })
    window.localStorage.setItem('token', 'x')
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


