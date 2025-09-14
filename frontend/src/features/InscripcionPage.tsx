import React from 'react';
import { Container, Paper, Typography } from '@mui/material';
import { useParams } from 'react-router-dom';
import FormularioInscribirseAEvento from '../components/form/FormularioInscribirseAEvento';

const InscripcionPage: React.FC = () => {
  const { id } = useParams<{ id?: string }>();
  const evento = id ? { id } : null;

  return (
    <Container sx={{ py: 4 }}>
      <Paper variant="outlined" sx={{ p: 3, maxWidth: 560, mx: 'auto', bgcolor: '#FDF3E0' }}>
        <Typography variant="h5" sx={{ mb: 2, fontWeight: 700 }}>
          Inscripción a evento
        </Typography>
        <FormularioInscribirseAEvento evento={evento} />
      </Paper>
    </Container>
  );
};

export default InscripcionPage;
