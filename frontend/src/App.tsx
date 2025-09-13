import { 
  ThemeProvider, 
  createTheme, 
  CssBaseline,
  Container,
  Typography,
  Box
} from '@mui/material';
import Footer from './components/Footer';

const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
  },
});

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Box sx={{ 
        minHeight: '100vh', 
        minWidth: '100vw',
        display: 'flex', 
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center'
      }}>
        <Container maxWidth="lg" sx={{ py: 4, flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <Box sx={{ textAlign: 'center', mb: 4 }}>
            <Typography variant="h3" component="h1" color="primary" gutterBottom>
              TP TACS
            </Typography>
            <Typography variant="h6" color="text.secondary">
              Sistema de gestión de eventos
            </Typography>
          </Box>
        </Container>
        <Footer />
      </Box>
    </ThemeProvider>
  );
}

export default App;
