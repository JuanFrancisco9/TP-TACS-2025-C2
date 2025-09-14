import React, { useEffect, useState } from 'react';
import {
  Box,
  Stack,
  Paper,
  Typography,
  TextField,
  Divider,
  FormGroup,
  FormControlLabel,
  Checkbox,
} from '@mui/material';
import Grid from '@mui/material/Grid'; 
import type { MapPoint } from '../components/MapView';
import MapView from '../components/MapView';
import EventCard from '../components/EventCard';
import DetallesEvento from '../components/EventDetails';
import { EventoService, type Evento } from '../services/eventoService';
import { useNavigate, useSearchParams } from 'react-router-dom';



// const MOCK_EVENTS: EventItem[] = [
//   { id: '1', title: 'Graphic Design Meetup', image: 'https://picsum.photos/id/1062/600/400', price: 'Gratis', rating: 4.8, badges: ['Sale'] },
//   { id: '2', title: 'React Buenos Aires', image: 'https://picsum.photos/id/1050/600/400', price: '$ 5.000', rating: 4.9 },
// ];

const MOCK_POINTS: MapPoint[] = [
  { id: 'p1', title: 'Villa Crespo', position: [-34.5975, -58.4385] },
  { id: 'p2', title: 'Palermo', position: [-34.583, -58.42] },
  { id: 'p3', title: 'Recoleta', position: [-34.588, -58.396] },
  { id: 'p4', title: 'San Telmo', position: [-34.621, -58.371] },
];

const EventOverview: React.FC = () => {
  const [eventos, setEventos] = useState<Evento[]>([]);
  const [eventoSeleccionado, setEventoSeleccionado] = useState<Evento | null>(null);
  useEffect(() => {
    EventoService.obtenerEventos()
      .then((res) => setEventos(res.eventos))
      .catch(() => setEventos([]));
  }, []);

  return eventoSeleccionado ? (
    <DetallesEvento
      evento={eventoSeleccionado}
      onVolver={() => setEventoSeleccionado(null)}
    />
  ) : (
    <Box sx={{ display: 'flex', height: '100vh', width: '100vw' }}>
      {/* Columna izquierda (50%) */}
      <Box
        sx={{
          width: '70vw',       
          height: '100vh',
          p: 2,
          overflowY: 'auto',
          bgcolor: '#FDF3E0',
        }}
      >
        <Typography variant="h5" sx={{ fontWeight: 700, mb: 2 }}>
          Explorá eventos
        </Typography>

        <Grid container spacing={2} alignItems="stretch">
          {/* Filtros */}
          <Grid size={{ xs: 12, md: 4 }}>
            <Paper variant="outlined" sx={{ p: 2 }}>

              <Stack spacing={1}>
                <Typography variant="subtitle2">Filtro</Typography>
                <TextField size="small" placeholder="Buscar" fullWidth defaultValue={searchParams.get('q') || ''} />
              </Stack>
              <Divider sx={{ my: 2 }} />
              <Typography variant="subtitle2" sx={{ mb: 1 }}>Categorías</Typography>
              <FormGroup>
                {['Música', 'Gastronomía', 'Vida nocturna', 'Artes', 'Negocios'].map((n) => (
                  <FormControlLabel key={n} control={<Checkbox size="small" />} label={n} />
                ))}
              </FormGroup>
              <Divider sx={{ my: 2 }} />
              <Typography variant="subtitle2" sx={{ mb: 1 }}>Precio</Typography>
              <FormGroup>
                {['Gratis', 'Pagos'].map((n) => (
                  <FormControlLabel key={n} control={<Checkbox size="small" />} label={n} />
                ))}
              </FormGroup>
            </Paper>
          </Grid>

          {/* Cards list */}
          <Grid size={{ xs: 12, md: 8 }}>
            <Grid container spacing={2}>
              {eventos.map((ev) => (
                <Grid key={ev.id} size={{ xs: 12, sm: 6 }}>
                  <EventCard
                    item={ev}
                    onVerDetalle={() => setEventoSeleccionado(ev)}
                    // Inscripción ahora se maneja en EventCard
                  />
                </Grid>
              ))}
            </Grid>
          </Grid>
        </Grid>
      </Box>

      {/* Columna derecha (50%): Mapa */}
      <Paper
        variant="outlined"
        sx={{
          width: '50vw',        
          height: '100vh',
          display: 'flex',
          overflow: 'hidden',
        }}
      >
        <MapView center={[-34.6037, -58.3816]} zoom={12} points={MOCK_POINTS} />
      </Paper>
    </Box>
  );
};

export default EventOverview;
