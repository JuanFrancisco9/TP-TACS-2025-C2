import React from 'react';
import { Container, Paper, Typography } from '@mui/material';
import FormularioLogin from '../components/form/FormularioLogin';

const LoginPage: React.FC = () => {
  return (
    <Container sx={{ py: 4 }}>
      <Paper variant="outlined" sx={{ p: 3, maxWidth: 560, mx: 'auto', bgcolor: '#FDF3E0' }}>
        <Typography variant="h5" sx={{ mb: 2, fontWeight: 700 }}>
          Iniciar sesión / Registrarse
        </Typography>
        <FormularioLogin />
      </Paper>
    </Container>
  );
};

export default LoginPage;

