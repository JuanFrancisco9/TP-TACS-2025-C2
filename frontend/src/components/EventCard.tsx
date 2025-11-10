import * as React from 'react';
import InscripcionDialog from './InscripcionDialog';
import { EventoService } from '../services/eventoService';
import authService from '../services/authService';
import type { Evento } from '../types/evento';
import { Rol, type Usuario } from '../types/auth';
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
import type { AlertColor } from '@mui/material/Alert';
import { useLocation, useNavigate } from 'react-router-dom';
import { isEventInPast } from '../utils/eventDate';

interface EventCardProps {
  item: Evento;
  onVerDetalle?: () => void;
  onInscribirse?: () => void;
}


const EventCard: React.FC<EventCardProps> = ({ item, onVerDetalle }) => {
  const [showDialog, setShowDialog] = React.useState(false);
  const [showSnackbar, setShowSnackbar] = React.useState(false);
  const [snackbarMsg, setSnackbarMsg] = React.useState('');
  const [snackbarSeverity, setSnackbarSeverity] = React.useState<AlertColor>('success');
  const [loading, setLoading] = React.useState(false);
  const imageSrc = item.imagenUrl ?? item.imagen ?? `https://picsum.photos/seed/${encodeURIComponent(item.id)}/800/400`;
  const navigate = useNavigate();
  const location = useLocation();
  const [currentUser, setCurrentUser] = React.useState<Usuario | null>(authService.getCurrentUser());

  const eventoFinalizado = React.useMemo(
    () => isEventInPast(item),
    [item.fecha, item.horaInicio]
  );


    React.useEffect(() => {
    const handleAuthChange = (event: Event) => {
      const detail = (event as CustomEvent<Usuario | null>).detail;
      setCurrentUser(detail ?? authService.getCurrentUser());
    };

    window.addEventListener('authStateChanged', handleAuthChange as EventListener);
    return () => {
      window.removeEventListener('authStateChanged', handleAuthChange as EventListener);
    };
  }, []);

  const isOrganizer = currentUser?.rol === Rol.ROLE_ORGANIZER;

  const handleInscribirme = () => {
    const user = authService.getCurrentUser();
    if (!user) {
      const currentPath = `${location.pathname}${location.search}`;
      authService.rememberUnauthorizedOrigin(currentPath);
      navigate('/login', { state: { from: currentPath } });
      return;
    }

    if (user.rol === Rol.ROLE_ORGANIZER) {
      return;
    }

    setShowDialog(true);
  };

  const handleConfirmar = async () => {
    setLoading(true);
    try {
      const inscripcion = await EventoService.inscribirseAEvento(item.id);
      const estado = inscripcion?.estado?.tipoEstado?.toUpperCase?.() ?? '';
      const esWaitlist = estado === 'PENDIENTE';
      setSnackbarSeverity(esWaitlist ? 'info' : 'success');
      setSnackbarMsg(esWaitlist ? `Inscripción en waitlist: ${item.titulo}` : `Inscripción confirmada a: ${item.titulo}`);
    } catch (error) {
      const message = error instanceof Error && error.message
        ? error.message
        : 'Error al inscribirse. Intenta nuevamente.';
      setSnackbarSeverity('warning');
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
          src={imageSrc}
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
            {item.etiquetas?.map((etiqueta: string) => (
              <Chip key={etiqueta} size="small" color="error" label={etiqueta} />
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
            {!isOrganizer && (
              eventoFinalizado ? (
                <Chip
                  label="Evento finalizado"
                  color="default"
                  variant="outlined"
                  sx={{
                    borderRadius: 2,
                    fontWeight: 600,
                    alignSelf: 'center'
                  }}
                />
              ) : (
                <Button
                  size="small"
                  variant="contained"
                  disabled={item.estado?.tipoEstado !== 'CONFIRMADO'}
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
              )
            )}
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
          snackbarSeverity={snackbarSeverity}
          titulo={item.titulo}
        />
      </Paper>
    </>
  );
};

export default EventCard;
