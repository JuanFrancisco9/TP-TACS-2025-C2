import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'leaflet/dist/leaflet.css'
import './index.css'
import App from './App.tsx'
import { ThemeProvider, CssBaseline, createTheme } from '@mui/material'

const theme = createTheme({
  typography: {
    fontFamily: 'Poppins, "Segoe UI", Roboto, Helvetica, Arial, sans-serif',
    fontWeightRegular: 400,
    fontWeightMedium: 600,
    fontWeightBold: 700,
    button: { textTransform: 'none', fontWeight: 600 },
    subtitle1: { fontWeight: 600 },
    subtitle2: { fontWeight: 700 },
  },
  palette: {
    mode: 'light',
    text: { primary: '#2F1D4A' },
    background: { default: '#FDF3E0', paper: '#FDF3E0' },
    primary: { main: '#2F1D4A' },
  },
  components: {
    MuiButton: { styleOverrides: { root: { textTransform: 'none' } } },
  },
});

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <App />
    </ThemeProvider>
  </StrictMode>,
)
