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
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import HowToRegIcon from '@mui/icons-material/HowToReg';
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
    } catch (error) {
      const message = error instanceof Error && error.message
        ? error.message
        : 'Error al inscribirse. Intenta nuevamente.';
      setSnackbarMsg(message);
    }
    setShowSnackbar(true);
    setShowDialog(false);
    setLoading(false);
  };

  return (
    <>
      <Paper
        variant="outlined"
        sx={{
          overflow: 'hidden',
          borderRadius: 4,
          boxShadow: 4,
          border: '1.5px solid #e0e0e0',
          transition: 'box-shadow 0.3s, transform 0.3s',
          ':hover': {
            boxShadow: 12,
            transform: 'translateY(-4px) scale(1.03)'
          }
        }}
      >
        <Box
          component="img"
          src={item.imagen}
          alt={item.titulo}
          sx={{ width: '100%', height: 160, objectFit: 'cover', transition: 'filter 0.3s', cursor: 'pointer', ':hover': { filter: 'brightness(0.95)' } }}
          onError={(e: any) => { e.currentTarget.src = `/logo.PNG`; }}
          onClick={onVerDetalle}
          tabIndex={0}
          role="button"
          aria-label={`Ver detalles de ${item.titulo}`}
        />
        <Box sx={{ p: 1.5 }}>
          <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 0.5 }}>
            {item.etiquetas?.map((b) => (
              <Chip key={b} size="small" color="error" label={b} />
            ))}
          </Stack>
          <Typography
            variant="subtitle1"
            sx={{ fontWeight: 600, cursor: 'pointer' }}
            onClick={onVerDetalle}
            tabIndex={0}
            role="button"
            aria-label={`Ver detalles de ${item.titulo}`}
          >
            {item.titulo}
          </Typography>
          {item.precio && (
            <Typography
              variant="body2"
              color="text.secondary"
              sx={{ mt: 0.5, cursor: 'pointer' }}
              onClick={onVerDetalle}
              tabIndex={0}
              role="button"
              aria-label={`Ver detalles de ${item.titulo}`}
            >
              {item.precio.cantidad} {item.precio.moneda}
            </Typography>
          )}
          <Stack direction="row" spacing={1} sx={{ mt: 1.5 }}>
            <Button
              size="small"
              variant="outlined"
              color="secondary"
              endIcon={<ArrowForwardIcon />}
              onClick={onVerDetalle}
              sx={{
                borderRadius: 2,
                boxShadow: 1,
                textTransform: 'none',
                fontWeight: 500,
                transition: 'box-shadow 0.3s, transform 0.3s',
                ':hover': {
                  boxShadow: 6,
                  transform: 'translateY(-2px) scale(1.04)'
                }
              }}
            >
              Ver más
            </Button>
            <Button
              size="small"
              variant="contained"
              color="primary"
              startIcon={<HowToRegIcon />}
              onClick={e => { e.stopPropagation(); handleInscribirme(); }}
              sx={{
                borderRadius: 2,
                boxShadow: 1,
                textTransform: 'none',
                fontWeight: 500,
                transition: 'box-shadow 0.3s, transform 0.3s',
                ':hover': {
                  boxShadow: 8,
                  transform: 'translateY(-2px) scale(1.04)'
                }
              }}
            >
              Inscribirme
            </Button>
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
    </>
  );
};

export default EventCard;
