import * as React from 'react';
import InscripcionDialog from './InscripcionDialog';
import { EventoService } from '../services/eventoService';
import {
  Typography,
  Button,
  Stack,
  Box,
  Paper,
  Chip
} from '@mui/material';
import type { Evento } from '../services/eventoService';


interface EventCardProps {
  item: Evento;
  onVerDetalle?: () => void;
  onInscribirse?: () => void;
}


const EventCard: React.FC<EventCardProps> = ({ item, onVerDetalle }) => {
  const [showDialog, setShowDialog] = React.useState(false);
  const [showSnackbar, setShowSnackbar] = React.useState(false);
  const [snackbarMsg, setSnackbarMsg] = React.useState('');
  const [loading, setLoading] = React.useState(false);

  const handleInscribirme = () => setShowDialog(true);

  const handleConfirmar = async () => {
    setLoading(true);
    try {
      await EventoService.inscribirseAEvento(item.id);
      setSnackbarMsg(`Inscripción confirmada a: ${item.titulo}`);
    } catch (e) {
      setSnackbarMsg('Error al inscribirse. Intenta nuevamente.');
    }
    setShowSnackbar(true);
    setShowDialog(false);
    setLoading(false);
  };

  return (
    <Paper variant="outlined" sx={{ overflow: 'hidden' }}>
      <Box component="img" src={item.imagen} alt={item.titulo} sx={{ width: '100%', height: 160, objectFit: 'cover' }} />
      <Box sx={{ p: 1.5 }}>
        <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 0.5 }}>
          {item.etiquetas?.map((b) => (
            <Chip key={b} size="small" color="error" label={b} />
          ))}
        </Stack>
        <Typography variant="subtitle1" sx={{ fontWeight: 600 }}>{item.titulo}</Typography>
        {item.precio && (
          <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>{item.precio.cantidad} {item.precio.moneda}</Typography>
        )}
        <Stack direction="row" spacing={1} sx={{ mt: 1.5 }}>
          <Button size="small" variant="outlined" onClick={onVerDetalle}>Ver más</Button>
          <Button size="small" variant="contained" onClick={handleInscribirme}>Inscribirme</Button>
        </Stack>
      </Box>
      <InscripcionDialog
        open={showDialog}
        onClose={() => setShowDialog(false)}
        onConfirm={handleConfirmar}
        loading={loading}
        snackbarMsg={snackbarMsg}
        showSnackbar={showSnackbar}
        onSnackbarClose={() => setShowSnackbar(false)}
        titulo={item.titulo}
      />
    </Paper>
  );
};

export default EventCard;
