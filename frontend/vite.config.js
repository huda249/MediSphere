
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api/scheduling': {
        target: 'http://localhost:8082',
        changeOrigin: true,
      },
      '/api/patients': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/api/emergency': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/api/admissions': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/api/donors': {
        target: 'http://localhost:8083',
        changeOrigin: true,
      },
      '/api/blood-bank': {
        target: 'http://localhost:8083',
        changeOrigin: true,
      },
      '/api/organs': {
        target: 'http://localhost:8083',
        changeOrigin: true,
      },
      '/api/match': {
        target: 'http://localhost:8083',
        changeOrigin: true,
      },
      '/api/reserve': {
        target: 'http://localhost:8083',
        changeOrigin: true,
      },
    },
  },
})
