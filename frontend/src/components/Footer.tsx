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
        backgroundColor: 'grey.50',
        borderTop: 1,
        borderColor: 'grey.300',
        width: '100%',
        py: 4
      }}
    >
      <Container maxWidth="lg">
        <Grid container spacing={4}>
          <Grid size={{ xs: 12, md: 4 }}>
            <Typography variant="h6" color="primary" gutterBottom>
              TP TACS
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Sistema de gestión de eventos desarrollado para el curso de Técnicas Avanzadas de Construcción de Software.
            </Typography>
          </Grid>
          
          <Grid size={{ xs: 12, md: 4 }}>
            <Typography variant="h6" color="primary" gutterBottom>
              Enlaces Rápidos
            </Typography>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
              <Link href="#" color="text.primary" underline="hover">
                Eventos
              </Link>
              <Link href="#" color="text.primary" underline="hover">
                Inscripciones
              </Link>
              <Link href="#" color="text.primary" underline="hover">
                Estadísticas
              </Link>
              <Link href="#" color="text.primary" underline="hover">
                Contacto
              </Link>
            </Box>
          </Grid>
          
          <Grid size={{ xs: 12, md: 4 }}>
            <Typography variant="h6" color="primary" gutterBottom>
              Información
            </Typography>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
              <Typography variant="body2" color="text.secondary">
                <strong>Universidad:</strong> UTN FRBA
              </Typography>
              <Typography variant="body2" color="text.secondary">
                <strong>Materia:</strong> TACS
              </Typography>
              <Typography variant="body2" color="text.secondary">
                <strong>Año:</strong> 2025
              </Typography>
            </Box>
          </Grid>
        </Grid>
        
        <Divider sx={{ my: 3 }} />
        
        <Box sx={{ textAlign: 'center' }}>
          <Typography variant="body2" color="text.secondary">
            © 2025 TP TACS. Todos los derechos reservados.
          </Typography>
        </Box>
      </Container>
    </Paper>
  );
};

export default Footer;