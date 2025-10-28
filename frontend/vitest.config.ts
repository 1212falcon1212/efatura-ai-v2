import { defineConfig } from 'vitest/config';
import { fileURLToPath } from 'node:url';
import { dirname, resolve } from 'node:path';

const __dirname = dirname(fileURLToPath(import.meta.url));

export default defineConfig({
  test: {
    include: ['src/**/*.{test,spec}.?(c|m)[jt]s?(x)'],
    exclude: ['e2e/**', 'node_modules/**', 'dist/**'],
    environment: 'jsdom',
  },
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  }
});


