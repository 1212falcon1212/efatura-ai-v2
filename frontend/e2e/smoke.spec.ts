import { test, expect } from '@playwright/test';

test('login → invoices → create → sign → send shows toasts', async ({ page }) => {
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


