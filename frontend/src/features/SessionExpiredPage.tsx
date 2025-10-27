import { Box, Button, Container, Paper, Stack, Typography } from '@mui/material';
import ReportProblemIcon from '@mui/icons-material/ReportProblem';

const SessionExpiredPage = () => (
  <Box sx={{ minHeight: '100vh', bgcolor: 'background.default', py: 6 }}>
    <Container maxWidth="sm">
      <Paper
        elevation={6}
        sx={{
          p: 4,
          textAlign: 'center',
          borderRadius: 4,
          bgcolor: 'background.paper',
        }}
      >
        <Stack spacing={3} alignItems="center">
          <ReportProblemIcon color="warning" sx={{ fontSize: 56 }} />
          <Typography variant="h4" fontWeight={700}>
            Sesión expirada
          </Typography>
          <Typography color="text.secondary">
            Tu sesión ya no es válida o no tienes permisos para continuar. Inicia sesión nuevamente
            para seguir usando la plataforma.
          </Typography>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
            <Button variant="contained" color="primary" onClick={() => (window.location.href = '/login')}>
              Ir al login
            </Button>
            <Button variant="outlined" color="primary" onClick={() => (window.location.href = '/')}>
              Volver al inicio
            </Button>
          </Stack>
        </Stack>
      </Paper>
    </Container>
  </Box>
);

export default SessionExpiredPage;
