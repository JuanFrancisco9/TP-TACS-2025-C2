import React from 'react';
import {
  Box,
  Container,
  Grid,
  Stack,
  Paper,
  Typography,
  TextField,
  Divider,
  Chip,
  Button,
  FormGroup,
  FormControlLabel,
  Checkbox,
} from '@mui/material';
import type { MapPoint } from '../components/MapView';
import MapView from '../components/MapView';
import { useNavigate, useSearchParams } from 'react-router-dom';

type EventItem = {
  id: string;
  title: string;
  image: string;
  price?: string;
  rating?: number;
  badges?: string[];
};

const MOCK_EVENTS: EventItem[] = [
  { id: '1', title: 'Graphic Design Meetup', image: 'https://picsum.photos/id/1062/600/400', price: 'Gratis', rating: 4.8, badges: ['Sale'] },
  { id: '2', title: 'React Buenos Aires', image: 'https://picsum.photos/id/1050/600/400', price: '$ 5.000', rating: 4.9 },
];

const MOCK_POINTS: MapPoint[] = [
  { id: 'p1', title: 'Villa Crespo', position: [-34.5975, -58.4385] },
  { id: 'p2', title: 'Palermo', position: [-34.583, -58.42] },
  { id: 'p3', title: 'Recoleta', position: [-34.588, -58.396] },
  { id: 'p4', title: 'San Telmo', position: [-34.621, -58.371] },
];

const EventCard: React.FC<{ item: EventItem; onInscribirse: (id: string) => void }> = ({ item, onInscribirse }) => (
  <Paper variant="outlined" sx={{ overflow: 'hidden' }}>
    <Box component="img" src={item.image} alt={item.title} sx={{ width: '100%', height: 160, objectFit: 'cover' }} />
    <Box sx={{ p: 1.5 }}>
      <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 0.5 }}>
        {item.badges?.map((b) => (
          <Chip key={b} size="small" color="error" label={b} />
        ))}
        {item.rating && (
          <Chip size="small" label={`★ ${item.rating.toFixed(1)}`} />
        )}
      </Stack>
      <Typography variant="subtitle1" sx={{ fontWeight: 600 }}>{item.title}</Typography>
      {item.price && (
        <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>{item.price}</Typography>
      )}
      <Stack direction="row" spacing={1} sx={{ mt: 1.5 }}>
        <Button size="small" variant="outlined">Ver más</Button>
        <Button size="small" variant="contained" onClick={() => onInscribirse(item.id)}>Inscribirme</Button>
      </Stack>
    </Box>
  </Paper>
);

const EventOverview: React.FC = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const q = (searchParams.get('q') || '').toLowerCase();
  const filtered = q
    ? MOCK_EVENTS.filter((ev) => ev.title.toLowerCase().includes(q))
    : MOCK_EVENTS;

  const handleInscribirse = (id: string) => {
    navigate(`/inscripcion/${id}`);
  };

  return (
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
          <Grid item xs={12} md={4}>
            <Paper variant="outlined" sx={{ p: 2, bgcolor: '#FDF3E0' }}>
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
          <Grid item xs={12} md={8}>
            <Grid container spacing={2}>
              {filtered.map((ev) => (
                <Grid key={ev.id} item xs={12} sm={6}>
                  <EventCard item={ev} onInscribirse={handleInscribirse} />
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
