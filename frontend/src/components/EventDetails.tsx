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
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Stack,
  Divider,
  Box,
} from '@mui/material';
import Grid from '@mui/material/Grid';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import EventIcon from '@mui/icons-material/Event';
import PlaceIcon from '@mui/icons-material/Place';
import ScheduleIcon from '@mui/icons-material/Schedule';
import PersonIcon from '@mui/icons-material/Person';
import AttachMoneyIcon from '@mui/icons-material/AttachMoney';
import InfoOutlinedIcon from '@mui/icons-material/InfoOutlined';
import LocalOfferIcon from '@mui/icons-material/LocalOffer';
import type { Evento } from '../services/eventoService';

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
  const [showModal, setShowModal] = React.useState(false);

  const handleInscribirse = () => (onInscribirse ? onInscribirse() : setShowModal(true));

  const estadoColor: 'success' | 'error' | 'warning' =
    evento.estado?.tipoEstado === 'ACTIVO' ? 'success' : evento.estado?.tipoEstado === 'CANCELADO' ? 'error' : 'warning';

  return (
    <Box sx={{ minHeight: '100vh', py: 4 }}>
      <Container maxWidth="md">
        <Box sx={{ bgcolor: 'white', borderRadius: 3, boxShadow: 2, p: 3 }}>
      <Box sx={{ mb: 3 }}>
        <Button variant="outlined" startIcon={<ArrowBackIcon />} onClick={onVolver}>
          Volver al listado
        </Button>
      </Box>

      <Grid container spacing={3}>
        {/* Imagen principal */}
        <Grid size={{ xs: 12, lg: 6 }}>
          {evento.imagen && (
            <Card>
              <CardMedia
                component="img"
                image={evento.imagen}
                alt={evento.titulo}
                sx={{ height: 400, objectFit: 'cover' }}
              />
            </Card>
          )}
        </Grid>

        {/* Información principal */}
        <Grid size={{ xs: 12, lg: 6 }}>
          <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column', boxShadow: 4, border: '1px solid #e0e0e0', borderRadius: 3 }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', mb: 2, gap: 2 }}>
                <Typography variant="h4" component="h1">
                  {evento.titulo}
                </Typography>
                {evento.categoria?.tipo && <Chip label={evento.categoria.tipo} color="primary" />}
              </Box>

              <Typography variant="subtitle1" sx={{ mb: 3 }}>
                {evento.descripcion}
              </Typography>

              <Stack spacing={1.5} divider={<Divider flexItem />} sx={{ mb: 3 }}>
                <Stack direction="row" spacing={1} alignItems="center">
                  <EventIcon color="action" fontSize="small" />
                  <Typography variant="body2">
                    <strong>Fecha:</strong> {evento.fecha}
                  </Typography>
                </Stack>

                <Stack direction="row" spacing={1} alignItems="center">
                  <PlaceIcon color="action" fontSize="small" />
                  <Typography variant="body2">
                    <strong>Ubicación:</strong> {evento.ubicacion.localidad} - {evento.ubicacion.direccion}
                  </Typography>
                </Stack>

                {evento.duracion && (
                  <Stack direction="row" spacing={1} alignItems="center">
                    <ScheduleIcon color="action" fontSize="small" />
                    <Typography variant="body2">
                      <strong>Duración:</strong> {evento.duracion} hs
                    </Typography>
                  </Stack>
                )}

                {evento.organizador && (
                  <Stack direction="row" spacing={1} alignItems="center">
                    <PersonIcon color="action" fontSize="small" />
                    <Typography variant="body2">
                      <strong>Organizador:</strong> {evento.organizador.nombre} {evento.organizador.apellido}
                    </Typography>
                  </Stack>
                )}

                {evento.precio && (
                  <Stack direction="row" spacing={1} alignItems="center">
                    <AttachMoneyIcon color="success" fontSize="small" />
                    <Typography variant="body2" color="success.main" fontWeight="bold">
                      <strong>Precio:</strong> {renderPrecio(evento.precio)}
                    </Typography>
                  </Stack>
                )}
              </Stack>

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

            <Box sx={{ flexGrow: 1 }} />

            <CardActions sx={{ p: 2, pt: 0 }}>
              <Button variant="contained" size="large" fullWidth onClick={handleInscribirse}>
                Inscribirse al Evento
              </Button>
            </CardActions>
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
                  {evento.etiquetas.map((etiqueta, i) => (
                    <Chip key={`${etiqueta}-${i}`} label={etiqueta} variant="outlined" />
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

      <Dialog open={showModal} onClose={() => setShowModal(false)}>
        <DialogTitle>Confirmar Inscripción</DialogTitle>
        <DialogContent>
          <Typography>¿Estás seguro de que quieres inscribirte a “{evento.titulo}”?</Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setShowModal(false)}>Cancelar</Button>
          <Button
            variant="contained"
            onClick={() => {
              setShowModal(false);
              alert(`Te has inscrito a: ${evento.titulo}`);
            }}
          >
            Confirmar Inscripción
          </Button>
        </DialogActions>
      </Dialog>
        </Box>
      </Container>
    </Box>
  );
};

export default DetallesEvento;
