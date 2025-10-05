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
  CircularProgress,
} from '@mui/material';
import Grid from '@mui/material/Grid'; 
import type { MapPoint } from '../components/MapView';
import MapView from '../components/MapView';
import EventCard from '../components/EventCard';
import DetallesEvento from '../components/EventDetails';
import { useLocation } from 'react-router-dom';
import { EventoService } from '../services/eventoService';
import type { Evento, CategoriaDTO } from '../types/evento.ts';
import { getCategoryIconFor } from '../utils/categoryIcons';


// Helper para convertir eventos -> puntos del mapa
const eventosToPoints = (eventos: Evento[]): MapPoint[] => {
  return eventos
    .map((ev) => {
      const lat = Number((ev as any)?.ubicacion?.latitud);
      const lon = Number((ev as any)?.ubicacion?.longitud);
      if (!Number.isFinite(lat) || !Number.isFinite(lon)) return null;
      return {
        id: ev.id,
        title: ev.titulo,
        position: [lat, lon] as [number, number],
      } as MapPoint;
    })
    .filter((p): p is MapPoint => Boolean(p));
};

const EventOverview: React.FC = () => {
  const location = useLocation() as any;
  const [eventos, setEventos] = useState<Evento[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [categories, setCategories] = useState<CategoriaDTO[]>([]);
  const [loadingCats, setLoadingCats] = useState<boolean>(false);
  const [selectedCategories, setSelectedCategories] = useState<Record<string, boolean>>({});
  const [eventoSeleccionado, setEventoSeleccionado] = useState<Evento | null>(null);
  useEffect(() => {
    const stateFilters = location?.state?.filtros;
    const qs = new URLSearchParams(window.location.search);
    const urlQ = qs.get('q') || '';
    const urlLoc = qs.get('loc') || '';
    const palabrasClave = stateFilters?.palabrasClave ?? urlQ ?? '';
    const ubicacion = stateFilters?.ubicacion ?? urlLoc ?? '';
    const pagina = Math.max(0, (stateFilters?.nroPagina ?? 1) - 1);

    setLoading(true);
    EventoService
      .buscarEventos(palabrasClave, pagina, ubicacion)
      .then((res) => {
        setEventos(res.eventos);
      })
      .catch(() => setEventos([]))
      .finally(() => setLoading(false));
  }, [location?.state]);

  // Cargar categorías para filtros
  useEffect(() => {
    setLoadingCats(true);
    EventoService.obtenerCategorias()
      .then((cats) => {
        setCategories(cats);
        const map: Record<string, boolean> = {};
        cats.forEach((c) => (map[c.tipo] = true));
        setSelectedCategories(map);
      })
      .finally(() => setLoadingCats(false));
  }, []);
  
  // Buscar con filtros cuando cambian categorías
  const refetchWithCategory = (updated: Record<string, boolean>) => {
    const stateFilters = location?.state?.filtros;
    const qs = new URLSearchParams(window.location.search);
    const urlQ = qs.get('q') || '';
    const urlLoc = qs.get('loc') || '';
    const palabrasClave = stateFilters?.palabrasClave ?? urlQ ?? '';
    const ubicacion = stateFilters?.ubicacion ?? urlLoc ?? '';
    const pagina = Math.max(0, (stateFilters?.nroPagina ?? 1) - 1);

    const active = Object.entries(updated)
      .filter(([, v]) => v)
      .map(([k]) => k);
    const categoria = active.length === 1 ? active[0] : undefined;

    setLoading(true);
    EventoService
      .buscarEventosConFiltros({ palabrasClave, ubicacion, categoria, pagina })
      .then((res) => {
        setEventos(res.eventos);
      })
      .catch(() => setEventos([]))
      .finally(() => setLoading(false));
  };

  const handleToggleCategory = (name: string) => {
    const updated = { ...selectedCategories, [name]: !selectedCategories[name] };
    setSelectedCategories(updated);
    refetchWithCategory(updated);
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100vh', width: '100vw', bgcolor: '#FDF3E0' }}>
        <CircularProgress size={32} sx={{ mr: 1 }} />
        <Typography variant="body2" color="text.secondary">Cargando eventos...</Typography>
      </Box>
    );
  }

  const points: MapPoint[] = eventosToPoints(eventos);
  const defaultCenter: [number, number] = [-34.6037, -58.3816]; // Obelisco
  const mapCenter: [number, number] = points.length ? points[0].position : defaultCenter;

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
                <TextField
                  size="small"
                  placeholder="Buscar"
                  fullWidth
                  defaultValue={new URLSearchParams(window.location.search).get('q') || ''}
                />
              </Stack>
              <Divider sx={{ my: 2 }} />
              <Typography variant="subtitle2" sx={{ mb: 1 }}>Categorías</Typography>
              {loadingCats ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', py: 1 }}>
                  <CircularProgress size={20} />
                </Box>
              ) : (
                <FormGroup>
                  {categories.map((categoria) => {
                    const tipo = categoria.tipo;
                    const IconComponent = getCategoryIconFor(undefined, categoria.icono, tipo);
                    return (
                      <FormControlLabel
                        key={tipo}
                        control={
                          <Checkbox
                            size="small"
                            checked={selectedCategories[tipo] ?? true}
                            onChange={() => handleToggleCategory(tipo)}
                          />
                        }
                        label={
                          <Stack direction="row" spacing={1} alignItems="center">
                            <IconComponent fontSize="small" />
                            <span>{tipo}</span>
                          </Stack>
                        }
                      />
                    );
                  })}
                </FormGroup>
              )}
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
        <MapView center={mapCenter} zoom={12} points={points} />
      </Paper>
    </Box>
  );
};

export default EventOverview;
