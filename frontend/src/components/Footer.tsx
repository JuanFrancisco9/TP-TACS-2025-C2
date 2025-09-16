import React from 'react';
import {
  Container,
  Grid,
  Typography,
  Link,
  Box,
  Divider,
  Paper
} from '@mui/material';

const Footer: React.FC = () => {
  return (
    <Paper
      elevation={0}
      sx={{
        bgcolor: '#FDF3E0',
        color: '#2F1D4A',
        borderTop: '1px solid',
        borderColor: 'divider',
        width: '100%',
        py: 2
      }}
    >
      <Container maxWidth="lg" sx={{ px: { xs: 2, sm: 3 } }}>
        <Grid container spacing={2}>
          <Grid size={{ xs: 12, md: 4 }}>
            <Typography variant="subtitle2" color="inherit" gutterBottom sx={{ fontWeight: 700, mb: 1 }}>
              TP TACS
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ m: 0 }}>
              Sistema de gestión de eventos desarrollado para el curso de Técnicas Avanzadas de Construcción de Software.
            </Typography>
          </Grid>

          <Grid size={{ xs: 12, md: 4 }}>
            <Typography variant="subtitle2" color="inherit" gutterBottom sx={{ fontWeight: 700, mb: 1 }}>
              Enlaces Rápidos
            </Typography>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
              <Link href="https://wiki.archlinux.org/title/Desktop_environment_(Espa%C3%B1ol)" color="inherit" underline="hover">
                Eventos
              </Link>
              <Link href="#" color="inherit" underline="hover">
                Inscripciones
              </Link>
              <Link href="#" color="inherit" underline="hover">
                Estadísticas
              </Link>
              <Link href="#" color="inherit" underline="hover">
                Contacto
              </Link>
            </Box>
          </Grid>

          <Grid size={{ xs: 12, md: 4 }}>
            <Typography variant="subtitle2" color="inherit" gutterBottom sx={{ fontWeight: 700, mb: 1 }}>
              Información
            </Typography>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
              <Typography variant="body2" color="text.secondary" sx={{ m: 0 }}>
                <strong>Universidad:</strong> UTN FRBA
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ m: 0 }}>
                <strong>Materia:</strong> TACS
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ m: 0 }}>
                <strong>Año:</strong> 2025
              </Typography>
            </Box>
          </Grid>
        </Grid>
        <Divider sx={{ my: 2 }} />
        <Box sx={{ textAlign: 'center' }}>
          <Typography variant="body2" color="text.secondary" sx={{ m: 0 }}>
            © 2025 TP TACS. Todos los derechos reservados.
          </Typography>
        </Box>
      </Container>
    </Paper>
  );
};

export default Footer;
