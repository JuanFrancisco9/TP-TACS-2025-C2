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
  CircularProgress,
  Radio,
  Slider,
} from '@mui/material';
import Grid from '@mui/material/Grid'; 
import type { MapPoint } from '../components/MapView';
import MapView from '../components/MapView';
import EventCard from '../components/EventCard';
import DetallesEvento from '../components/EventDetails';
import { useLocation } from 'react-router-dom';
import { EventoService } from '../services/eventoService';
import type { Evento, CategoriaDTO } from '../types/evento';
import { getCategoryIconFor } from '../utils/categoryIcons';

// ---------- helpers ----------
const eventosToPoints = (eventos: Evento[]): MapPoint[] =>
  eventos
    .map((ev) => {
      if (ev.ubicacion?.esVirtual) return null;
      const lat = Number(ev.ubicacion?.latitud);
      const lon = Number(ev.ubicacion?.longitud);
      if (!Number.isFinite(lat) || !Number.isFinite(lon)) return null;
      return { id: ev.id, title: ev.titulo, position: [lat, lon] as [number, number] };
    })
    .filter((p): p is MapPoint => Boolean(p));

// Formatea Date a YYYY-MM-DD en zona local (evita corrimiento por timezone)
const toLocalISODate = (d: Date) => {
  const off = d.getTimezoneOffset();
  const local = new Date(d.getTime() - off * 60_000);
  return local.toISOString().slice(0, 10);
};

const EventOverview: React.FC = () => {
  const location = useLocation() as any;

  // estado
  const [eventos, setEventos] = useState<Evento[]>([]);
  const [eventoSeleccionado, setEventoSeleccionado] = useState<Evento | null>(null);
  const [loading, setLoading] = useState<boolean>(false);

  const [categories, setCategories] = useState<CategoriaDTO[]>([]);
  const [loadingCats, setLoadingCats] = useState<boolean>(false);
  const [selectedCategory, setSelectedCategory] = useState<string>('');

  const [priceRange, setPriceRange] = useState<[number, number]>([0, 0]); // 0,0 => sin filtro
  const [fechaDesde, setFechaDesde] = useState<Date | undefined>(undefined);
  const [fechaHasta, setFechaHasta] = useState<Date | undefined>(undefined);

  // buscador (simple: toma q de la URL, actualiza onBlur)
  const qs = new URLSearchParams(window.location.search);
  const initialQ = qs.get('q') || '';
  const [searchText, setSearchText] = useState<string>(initialQ);

  // ---------- carga inicial ----------
  useEffect(() => {
    const stateFilters = location?.state?.filtros;
    const qs = new URLSearchParams(window.location.search);
    const urlQ = qs.get('q') || '';
    const urlLoc = qs.get('loc') || '';
    const categoria = qs.get('categoria') || '';
    const palabrasClave = stateFilters?.palabrasClave ?? urlQ ?? '';
    const ubicacion = stateFilters?.ubicacion ?? urlLoc ?? '';

    // backend espera 0-based
    const pagina = Math.max(0, (stateFilters?.nroPagina ?? 1) - 1);

    setLoading(true);
    EventoService.buscarEventosConFiltros({ palabrasClave, pagina, ubicacion, categoria })
      .then((res) => setEventos(res.eventos))
      .catch(() => setEventos([]))
      .finally(() => setLoading(false));
  }, [location?.state]);

  // ---------- categorías ----------
  useEffect(() => {
    setLoadingCats(true);
    EventoService.obtenerCategorias()
      .then((cats) => setCategories(cats))
      .finally(() => setLoadingCats(false));
  }, []);

  // ---------- refetch central ----------
  const refetch = () => {
    const stateFilters = location?.state?.filtros;
    const qs = new URLSearchParams(window.location.search);
    const urlQ = qs.get('q') || '';
    const urlLoc = qs.get('loc') || '';

    const palabrasClave = (stateFilters?.palabrasClave ?? urlQ ?? '').trim();
    const ubicacion = (stateFilters?.ubicacion ?? urlLoc ?? '').trim();
    const pagina = Math.max(1, (stateFilters?.nroPagina ?? 1) - 1);

    const filtros: {
      palabrasClave?: string;
      ubicacion?: string;
      categoria?: string;
      fechaInicio?: Date;
      fechaFin?: Date;
      precioMin?: number;
      precioMax?: number;
      pagina?: number;
    } = { pagina };

    if (palabrasClave) filtros.palabrasClave = palabrasClave;
    if (ubicacion) filtros.ubicacion = ubicacion;
    if (selectedCategory) filtros.categoria = selectedCategory;
    // precio solo si aplica
    const [pmin, pmax] = priceRange;
    if (Number.isFinite(pmin) && pmin > 0) filtros.precioMin = pmin;
    if (Number.isFinite(pmax) && pmax > 0 && pmax >= pmin) filtros.precioMax = pmax;

    if (fechaDesde) filtros.fechaInicio = fechaDesde;
    if (fechaHasta) filtros.fechaFin = fechaHasta;

    setLoading(true);
    EventoService.buscarEventosConFiltros(filtros)
      .then((res) => setEventos(res.eventos))
      .catch(() => setEventos([]))
      .finally(() => setLoading(false));
  };

  // ---------- handlers ----------
  const handleToggleCategory = (tipo: string) => {
    const updated = selectedCategory === tipo ? '' : tipo;
    setSelectedCategory(updated);
    refetch();
  };

  const handlePriceChange = (_: Event, v: number | number[]) => {
    setPriceRange(v as [number, number]);
  };
  const handlePriceCommitted = () => {
    refetch();
  };

  const handleFechaDesde = (value: string) => {
    setFechaDesde(value ? new Date(value + 'T00:00:00') : undefined);
  };
  const handleFechaHasta = (value: string) => {
    setFechaHasta(value ? new Date(value + 'T00:00:00') : undefined);
  };

  const handleSearchBlur = () => {
    const url = new URL(window.location.href);
    if (searchText) url.searchParams.set('q', searchText);
    else url.searchParams.delete('q');
    window.history.replaceState({}, '', url.toString());
    refetch();
  };

  // ---------- loading ----------
  if (loading) {
    return (
      <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100vh', width: '100vw', bgcolor: '#FDF3E0' }}>
        <CircularProgress size={32} sx={{ mr: 1 }} />
        <Typography variant="body2" color="text.secondary">Cargando eventos...</Typography>
      </Box>
    );
  }

  // ---------- mapa ----------
  const points: MapPoint[] = eventosToPoints(eventos);
  const defaultCenter: [number, number] = [-34.6037, -58.3816]; // Obelisco
  const mapCenter: [number, number] = points.length ? points[0].position : defaultCenter;

  return eventoSeleccionado ? (
    <DetallesEvento evento={eventoSeleccionado} onVolver={() => setEventoSeleccionado(null)} />
  ) : (
    <Box sx={{ display: 'flex', height: '100vh', width: '100vw' }}>
      {/* Columna izquierda */}
      <Box sx={{ width: '70vw', height: '100vh', p: 2, overflowY: 'auto', bgcolor: '#FDF3E0' }}>
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
                  value={searchText}
                  onChange={(e) => setSearchText(e.target.value)}
                  onBlur={handleSearchBlur}
                />
              </Stack>

              <Divider sx={{ my: 2 }} />

              <Typography variant="subtitle2" sx={{ mb: 1 }}>Fecha (rango)</Typography>
              <Stack direction="row" spacing={1}>
                <TextField
                  type="date"
                  label="Desde"
                  size="small"
                  InputLabelProps={{ shrink: true }}
                  value={fechaDesde ? toLocalISODate(fechaDesde) : ''}
                  onChange={(e) => handleFechaDesde(e.target.value)}
                  onBlur={refetch}
                />
                <TextField
                  type="date"
                  label="Hasta"
                  size="small"
                  InputLabelProps={{ shrink: true }}
                  value={fechaHasta ? toLocalISODate(fechaHasta) : ''}
                  onChange={(e) => handleFechaHasta(e.target.value)}
                  onBlur={refetch}
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
                          <Radio
                            size="small"
                            checked={selectedCategory === tipo}
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
              <Slider
                value={priceRange}
                onChange={handlePriceChange}
                onChangeCommitted={handlePriceCommitted}
                valueLabelDisplay="auto"
                min={0}
                max={100000}   // ajustá al techo real de tu backend
                step={100}
              />
              <Typography variant="caption">
                {priceRange[0] === 0 && priceRange[1] === 0
                  ? 'Sin filtro de precio'
                  : `Entre ${priceRange[0]} y ${priceRange[1]}`}
              </Typography>
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
                  />
                </Grid>
              ))}
            </Grid>
          </Grid>
        </Grid>
      </Box>

      {/* Columna derecha: Mapa */}
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

