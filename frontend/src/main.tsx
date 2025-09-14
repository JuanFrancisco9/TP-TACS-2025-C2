import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
<<<<<<< HEAD
import { ThemeProvider, createTheme, CssBaseline } from '@mui/material'
import App from './App'
import './index.css' // si querés mantener tus estilos globales propios

// Podés customizar el tema acá
const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2', // azul default de MUI
    },
    secondary: {
      main: '#9c27b0', // violeta
    },
  },
  shape: {
    borderRadius: 12,
  },
})
=======
import 'bootstrap/dist/css/bootstrap.min.css'
import 'leaflet/dist/leaflet.css'
import './index.css'
import App from './App.tsx'
>>>>>>> origin/feature/frontend

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <App />
    </ThemeProvider>
  </StrictMode>,
)
