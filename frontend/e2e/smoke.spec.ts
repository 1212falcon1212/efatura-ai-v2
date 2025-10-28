import { test, expect } from '@playwright/test';

test('login → invoices → create → sign → send shows toasts', async ({ page }) => {
  // Mock backend API calls
  const API = 'http://localhost:8080';
  let createdId = '00000000-0000-0000-0000-000000000abc';

  await page.route(`${API}/auth/login`, async (route) => {
    await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify({ accessToken: 'fake-token' }) });
  });
  await page.route(`${API}/invoices`, async (route) => {
    if (route.request().method() === 'GET') {
      return route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify([]) });
    }
    if (route.request().method() === 'POST') {
      return route.fulfill({ status: 201, contentType: 'application/json', body: JSON.stringify({ id: createdId }) });
    }
    return route.fallback();
  });
  await page.route(new RegExp(`${API}/invoices/.+`), async (route) => {
    const url = route.request().url();
    if (url.endsWith(`/invoices/${createdId}`) && route.request().method() === 'GET') {
      return route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify({ id: createdId, status: 'DRAFT' }) });
    }
    if (url.endsWith(`/invoices/${createdId}/sign`) && route.request().method() === 'POST') {
      return route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify({ status: 'SIGNED' }) });
    }
    if (url.endsWith(`/invoices/${createdId}/send`) && route.request().method() === 'POST') {
      return route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify({ status: 'SENT' }) });
    }
    return route.fallback();
  });

  await page.goto('http://localhost:5173/login');
  await page.getByPlaceholder('Tenant UUID').fill('00000000-0000-0000-0000-000000000001');
  await page.getByPlaceholder('Kullanıcı adı').fill('admin');
  await page.getByPlaceholder('Şifre').fill('password');
  await page.getByRole('button', { name: 'Giriş Yap' }).click();

  await page.getByRole('heading', { name: 'Faturalar' }).waitFor();
  await page.getByRole('link', { name: 'Yeni Fatura' }).click();

  await page.getByPlaceholder('Müşteri').fill('Acme AŞ');
  await page.getByPlaceholder('Brüt Tutar').fill('118');
  await page.getByRole('button', { name: 'Kaydet' }).click();

  await page.getByRole('heading', { name: 'Fatura Detayı' }).waitFor();
  await page.getByTestId('sign-btn').click();
  await expect(page.getByTestId('toast')).toHaveText(/imzalandı/i);

  await page.getByTestId('send-btn').click();
  await expect(page.getByTestId('toast')).toHaveText(/gönderildi/i);
});


