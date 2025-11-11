import * as React from 'react';
import {
  Container,
  Card,
  CardMedia,
  CardContent,
  CardActions,
  Typography,
  Button,
  Chip,
  Alert,
  Stack,
  Divider,
  Box,
} from '@mui/material';
import InscripcionDialog from './InscripcionDialog';
import { EventoService } from '../services/eventoService';
import Grid from '@mui/material/Grid';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import HowToRegIcon from '@mui/icons-material/HowToReg';
import EventIcon from '@mui/icons-material/Event';
import PlaceIcon from '@mui/icons-material/Place';
import LanguageIcon from '@mui/icons-material/Language';
import ScheduleIcon from '@mui/icons-material/Schedule';
import PersonIcon from '@mui/icons-material/Person';
import GroupsIcon from '@mui/icons-material/Groups';
import AttachMoneyIcon from '@mui/icons-material/AttachMoney';
import InfoOutlinedIcon from '@mui/icons-material/InfoOutlined';
import LocalOfferIcon from '@mui/icons-material/LocalOffer';
import type { AlertColor } from '@mui/material/Alert';
import type { Evento } from '../types/evento.ts';
import { formatFecha } from '../utils/formatFecha';
import authService from "../services/authService.ts";
import { useLocation, useNavigate } from 'react-router-dom';
import { Rol, type Usuario } from '../types/auth';
import { isEventInPast } from '../utils/eventDate';

interface DetallesEventoProps {
  evento: Evento;
  onVolver: () => void;
  onInscribirse?: () => void;
}

function renderPrecio(precio?: any) {
  if (!precio) return null;
  if (typeof precio === 'string') return precio;
  if (precio?.cantidad != null && precio?.moneda) return `${precio.cantidad} ${precio.moneda}`;
  return String(precio);
}

const DetallesEvento: React.FC<DetallesEventoProps> = ({ evento, onVolver, onInscribirse }) => {
  const [showDialog, setShowDialog] = React.useState(false);
  const [showSnackbar, setShowSnackbar] = React.useState(false);
  const [snackbarMsg, setSnackbarMsg] = React.useState('');
  const [snackbarSeverity, setSnackbarSeverity] = React.useState<AlertColor>('success');
  const [loading, setLoading] = React.useState(false);
  const [cuposDisponibles, setCuposDisponibles] = React.useState<number | null>(null);
  const [calculandoCupo, setCalculandoCupo] = React.useState(false);
  const [cuposError, setCuposError] = React.useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const [currentUser, setCurrentUser] = React.useState<Usuario | null>(authService.getCurrentUser());

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

  React.useEffect(() => {
    let isActive = true;

    const cargarCupoDisponible = async () => {
      if (!evento.id || evento.cupoMaximo == null) {
        if (isActive) {
          setCuposDisponibles(null);
          setCalculandoCupo(false);
          setCuposError(false);
        }
        return;
      }

      if (isActive) {
        setCalculandoCupo(true);
        setCuposError(false);
      }

      try {
        const cupos = await EventoService.obtenerCuposDisponibles(evento.id);
        if (!isActive) return;

        setCuposDisponibles(cupos != null ? Math.max(cupos, 0) : null);
      } catch (error) {
        if (!isActive) return;
        console.error('Error obteniendo cupos disponibles del evento:', error);
        setCuposDisponibles(null);
        setCuposError(true);
      } finally {
        if (isActive) {
          setCalculandoCupo(false);
        }
      }
    };

    cargarCupoDisponible();

    return () => {
      isActive = false;
    };
  }, [evento.id, evento.cupoMaximo]);

  const isOrganizer = currentUser?.rol === Rol.ROLE_ORGANIZER;
  const eventoFinalizado = React.useMemo(
    () => isEventInPast(evento),
    [evento.fecha, evento.horaInicio]
  );
  const imageSrc = evento.imagenUrl ?? evento.imagen ?? `https://picsum.photos/seed/${encodeURIComponent(evento.id)}/1200/600`;

  const handleInscribirse = () => {
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

    if (onInscribirse) {
      onInscribirse();
    } else {
      setShowDialog(true);
    }
  };

  const handleConfirmar = async () => {
    setLoading(true);
    try {
      const inscripcion = await EventoService.inscribirseAEvento(evento.id);
      const estado = inscripcion?.estado?.tipoEstado?.toUpperCase?.() ?? '';
      const esWaitlist = estado === 'PENDIENTE';
      setSnackbarSeverity(esWaitlist ? 'info' : 'success');
      setSnackbarMsg(esWaitlist ? `Inscripción en waitlist: ${evento.titulo}` : `Inscripción confirmada a: ${evento.titulo}`);
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

  const estadoColor: 'success' | 'error' | 'warning' =
    evento.estado?.tipoEstado === 'ACTIVO' ? 'success' : evento.estado?.tipoEstado === 'CANCELADO' ? 'error' : 'warning';

  return (
    <Box sx={{ minHeight: '100vh', py: 4, bgcolor: 'white' }}>
      <Container maxWidth={false} sx={{ width: '98vw', maxWidth: '1800px', mx: 'auto' }}>
        <Box sx={{ bgcolor: 'white', borderRadius: 3, boxShadow: 2, p: 3 }}>
      <Box sx={{ mb: 3 }}>
        <Button
          variant="outlined"
          color="secondary"
          startIcon={<ArrowBackIcon />}
          onClick={onVolver}
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
          Volver al listado
        </Button>
      </Box>

      <Grid container spacing={3}>
        {/* Imagen principal */}
        <Grid size={{ xs: 12, lg: 6 }}>
          {imageSrc && (
            <Card>
              <CardMedia
                component="img"
                image={imageSrc}
                alt={evento.titulo}
                sx={{ height: 400, objectFit: 'cover' }}
              />
            </Card>
          )}
        </Grid>

          {/* Información principal */}
          <Grid size={{ xs: 12, lg: 6 }}>
              <Card
                  sx={{
                      height: '100%',
                      display: 'flex',
                      flexDirection: 'column',
                      boxShadow: 4,
                      border: '1px solid #e0e0e0',
                      borderRadius: 3,
                  }}
              >
                  <CardContent>
                      {/* Título y categoría */}
                      <Box
                          sx={{
                              display: 'flex',
                              alignItems: 'flex-start',
                              justifyContent: 'space-between',
                              mb: 2,
                              gap: 2,
                          }}
                      >
                          <Typography variant="h4" component="h1">
                              {evento.titulo}
                          </Typography>
                          {evento.categoria?.tipo && <Chip label={evento.categoria.tipo} color="primary" />}
                      </Box>

                      {/* Descripción */}
                      <Typography variant="subtitle1" sx={{ mb: 3 }}>
                          {evento.descripcion}
                      </Typography>

                      {/* Datos principales */}
                      <Stack spacing={1.5} divider={<Divider flexItem />} sx={{ mb: 3 }}>
                          {/* Fecha */}
                          <Stack direction="row" spacing={1} alignItems="center">
                              <EventIcon color="action" fontSize="small" />
                              <Typography variant="body2">
                                  <strong>Fecha:</strong> {formatFecha(evento.fecha)}
                              </Typography>
                          </Stack>

                          {/* Ubicación o modalidad */}
                          <Stack direction="row" spacing={1} alignItems="center">
                              {evento.ubicacion.esVirtual ? (
                                  <>
                                      <LanguageIcon color="action" fontSize="small" />
                                      <Typography variant="body2">
                                          <strong>Modalidad:</strong> Virtual
                                          {evento.ubicacion.enlaceVirtual && (
                                              <>
                                                  {' – '}
                                                  <a
                                                      href={evento.ubicacion.enlaceVirtual}
                                                      target="_blank"
                                                      rel="noopener noreferrer"
                                                  >
                                                      Ingresar al enlace
                                                  </a>
                                              </>
                                          )}
                                      </Typography>
                                  </>
                              ) : (
                                  <>
                                      <PlaceIcon color="action" fontSize="small" />
                                      <Typography variant="body2">
                                          <strong>Ubicación:</strong>{' '}
                                          {[evento.ubicacion.provincia, evento.ubicacion.localidad, evento.ubicacion.direccion]
                                              .filter(Boolean)
                                              .join(', ')}
                                      </Typography>
                                  </>
                              )}
                          </Stack>

                          {/* Duración */}
                          {evento.duracion && (
                              <Stack direction="row" spacing={1} alignItems="center">
                                  <ScheduleIcon color="action" fontSize="small" />
                                  <Typography variant="body2">
                                      <strong>Duración:</strong> {evento.duracion} hs
                                  </Typography>
                              </Stack>
                          )}

                          {/* Organizador */}
                          {evento.organizador && (
                              <Stack direction="row" spacing={1} alignItems="center">
                                  <PersonIcon color="action" fontSize="small" />
                                  <Typography variant="body2">
                                      <strong>Organizador:</strong> {evento.organizador.nombre}{' '}
                                      {evento.organizador.apellido}
                                  </Typography>
                              </Stack>
                          )}

                          {/* Fecha de creación - solo visible para admin u organizador */}
                          {(currentUser?.rol === "ROLE_ADMIN" || currentUser?.rol === "ROLE_ORGANIZER") && (
                              <Stack direction="row" spacing={1} alignItems="center">
                                  <EventIcon color="action" fontSize="small" />
                                  <Typography variant="body2">
                                      <strong>Fecha Creación:</strong> {formatFecha(evento.fechaCreacion)}
                                  </Typography>
                              </Stack>
                          )}

                          {/* Cupos disponibles */}
                          {evento.cupoMaximo != null && (
                              <Stack direction="row" spacing={1} alignItems="center">
                                  <GroupsIcon color="action" fontSize="small" />
                                  <Typography variant="body2">
                                      <strong>Cupos disponibles:</strong>{' '}
                                      {calculandoCupo
                                          ? 'Calculando...'
                                          : cuposError
                                              ? 'No disponible'
                                              : cuposDisponibles}
                                  </Typography>
                              </Stack>
                          )}

                          {/* Precio */}
                          {evento.precio && (
                              <Stack direction="row" spacing={1} alignItems="center">
                                  <AttachMoneyIcon color="success" fontSize="small" />
                                  <Typography variant="body2" color="success.main" fontWeight="bold">
                                      <strong>Precio:</strong> {renderPrecio(evento.precio)}
                                  </Typography>
                              </Stack>
                          )}
                      </Stack>

                      {/* Info cupos mínimos/máximos */}
                      {(evento.cupoMinimo || evento.cupoMaximo) && (
                          <Alert icon={<InfoOutlinedIcon />} severity="info" sx={{ mb: 3 }}>
                              <Stack direction="row" justifyContent="space-between" flexWrap="wrap" gap={2}>
                                  {evento.cupoMinimo != null && (
                                      <span>
                Cupo Mínimo: <strong>{evento.cupoMinimo}</strong>
              </span>
                                  )}
                                  {evento.cupoMaximo != null && (
                                      <span>
                Cupo Máximo: <strong>{evento.cupoMaximo}</strong>
              </span>
                                  )}
                              </Stack>
                          </Alert>
                      )}
                  </CardContent>

                  {/* Botón de inscripción (solo para no organizadores) */}
                  <Box sx={{ flexGrow: 1 }} />
                  {!isOrganizer && (
                      <CardActions sx={{ p: 2, pt: 0 }}>
                          {eventoFinalizado ? (
                              <Chip
                                  label="Evento finalizado"
                                  color="default"
                                  variant="outlined"
                                  sx={{ borderRadius: 2, fontWeight: 600, width: '100%', justifyContent: 'center' }}
                              />
                          ) : (
                              <Button
                                  variant="contained"
                                  color="primary"
                                  size="large"
                                  disabled={evento.estado?.tipoEstado !== 'CONFIRMADO'}
                                  fullWidth
                                  startIcon={<HowToRegIcon />}
                                  onClick={handleInscribirse}
                                  sx={{
                                      borderRadius: 2,
                                      boxShadow: 1,
                                      textTransform: 'none',
                                      fontWeight: 500,
                                      transition: 'box-shadow 0.3s, transform 0.3s',
                                      ':hover': {
                                          boxShadow: 8,
                                          transform: 'translateY(-2px) scale(1.04)',
                                      },
                                  }}
                              >
                                  Inscribirse al Evento
                              </Button>
                          )}
                      </CardActions>
                  )}
              </Card>
          </Grid>
      </Grid>

      <Grid container spacing={3} sx={{ mt: 1 }}>
        <Grid size={{ xs: 12, lg: 6 }}>
          {evento.etiquetas && evento.etiquetas.length > 0 && (
            <Card sx={{ mb: 3 }}>
              <CardContent>
                <Stack direction="row" alignItems="center" spacing={1} sx={{ mb: 1.5 }}>
                  <LocalOfferIcon />
                  <Typography variant="h6" component="h2" sx={{ m: 0 }}>
                    Etiquetas
                  </Typography>
                </Stack>
                <Stack direction="row" spacing={1} useFlexGap flexWrap="wrap">
                  {evento.etiquetas?.map((etiqueta: string, index: number) => (
                    <Chip key={`${etiqueta}-${index}`} label={etiqueta} variant="outlined" />
                  ))}
                </Stack>
              </CardContent>
            </Card>
          )}
        </Grid>

        <Grid size={{ xs: 12, lg: 6 }}>
          <Card sx={{ mb: 3, boxShadow: 4, border: '1px solid #e0e0e0', borderRadius: 3 }}>
            <CardContent>
              <Stack direction="row" alignItems="center" spacing={1} sx={{ mb: 1.5 }}>
                <InfoOutlinedIcon />
                <Typography variant="h6" component="h2" sx={{ m: 0 }}>
                  Estado del Evento
                </Typography>
              </Stack>
              <Chip label={evento.estado?.tipoEstado} color={estadoColor} sx={{ fontWeight: 600 }} />
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <InscripcionDialog
        open={showDialog}
        onClose={() => setShowDialog(false)}
        onConfirm={handleConfirmar}
        loading={loading}
        snackbarMsg={snackbarMsg}
        showSnackbar={showSnackbar}
        onSnackbarClose={() => setShowSnackbar(false)}
        snackbarSeverity={snackbarSeverity}
        titulo={evento.titulo}
      />
        </Box>
      </Container>
    </Box>
  );
};

export default DetallesEvento;
