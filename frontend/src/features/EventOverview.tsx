import React, { useEffect, useMemo, useRef, useState } from 'react';
import {
    Box,
    Stack,
    Paper,
    Typography,
    TextField,
    Divider,
    FormControlLabel,
    CircularProgress,
    Radio,
    Slider,
    IconButton,
    Tooltip,
    RadioGroup,
} from '@mui/material';
import Grid from '@mui/material/Grid';
import RestartAltIcon from '@mui/icons-material/RestartAlt';

import type { MapPoint } from '../components/MapView';
import MapView from '../components/MapView';
import EventCard from '../components/EventCard';
import DetallesEvento from '../components/EventDetails';
import { useLocation, useSearchParams } from 'react-router-dom';
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

// Formatea Date a YYYY-MM-DD en zona local
const toLocalISODate = (d: Date) => {
    const off = d.getTimezoneOffset();
    const local = new Date(d.getTime() - off * 60_000);
    return local.toISOString().slice(0, 10);
};

type FiltrosState = {
    palabrasClave?: string;
    ubicacion?: string;
    categoria?: string;
    nroPagina?: number; // 1-based desde state externo
};

type LocationState = {
    filtros?: FiltrosState;
} | null;

const EventOverview: React.FC = () => {
    const location = useLocation();
    const locState = (location.state as LocationState) || null;

    // URL params
    const [searchParams, setSearchParams] = useSearchParams();

    // ---------- estado principal ----------
    const [eventos, setEventos] = useState<Evento[]>([]);
    const [eventoSeleccionado, setEventoSeleccionado] = useState<Evento | null>(null);
    const [loading, setLoading] = useState<boolean>(false);

    // categorías
    const [categories, setCategories] = useState<CategoriaDTO[]>([]);
    const [loadingCats, setLoadingCats] = useState<boolean>(false);

    // Filtros (UI vs committed)
    const urlQ = searchParams.get('q') || '';
    const urlCategoria = searchParams.get('categoria') || '';

    const initialQRef = useRef(urlQ);
    const initialCategoriaRef = useRef(urlCategoria);

    // --- búsqueda
    const [committedSearch, setCommittedSearch] = useState<string>(urlQ);

    // --- categoría
    const [selectedCategory, setSelectedCategory] = useState<string>(urlCategoria);

    // --- precio
    const [priceRange, setPriceRange] = useState<[number, number]>([0, 0]);
    const [committedPriceRange, setCommittedPriceRange] = useState<[number, number]>([0, 0]);

    // --- fechas
    const [fechaDesde, setFechaDesde] = useState<Date | undefined>(undefined);
    const [fechaHasta, setFechaHasta] = useState<Date | undefined>(undefined);
    const [committedFechaDesde, setCommittedFechaDesde] = useState<Date | undefined>(undefined);
    const [committedFechaHasta, setCommittedFechaHasta] = useState<Date | undefined>(undefined);

    // ---------- categorías ----------
    useEffect(() => {
        setLoadingCats(true);
        EventoService.obtenerCategorias()
            .then((cats) => setCategories(cats))
            .finally(() => setLoadingCats(false));
    }, []);

    // ---------- init desde location.state + URL (una sola vez) ----------
    const didInitRef = useRef(false);
    useEffect(() => {
        if (didInitRef.current) return;
        didInitRef.current = true;

        const stateFilters = locState?.filtros;
        if (stateFilters) {
            if (stateFilters.palabrasClave != null) {
                setCommittedSearch(stateFilters.palabrasClave);
                setSearchParams((prev) => {
                    const p = new URLSearchParams(prev);
                    if (stateFilters.palabrasClave) p.set('q', stateFilters.palabrasClave);
                    else p.delete('q');
                    return p;
                });
            }
            if (stateFilters.categoria != null) {
                setSelectedCategory(stateFilters.categoria);
                setSearchParams((prev) => {
                    const p = new URLSearchParams(prev);
                    if (stateFilters.categoria) p.set('categoria', stateFilters.categoria);
                    else p.delete('categoria');
                    return p;
                });
            }
        } else {
            setCommittedSearch(initialQRef.current);
            setSelectedCategory(initialCategoriaRef.current);
        }
    }, [locState, setSearchParams]);

    // ---------- filtros efectivos para fetch ----------
    const effectiveFilters = useMemo(() => {
        const sf = locState?.filtros;
        const pagina = Math.max(0, (sf?.nroPagina ?? 1) - 1);

        const base: Record<string, unknown> = { pagina };

        const palabrasClave = (sf?.palabrasClave ?? committedSearch ?? '').trim();
        if (palabrasClave) base.palabrasClave = palabrasClave;

        const ubicacion = (sf?.ubicacion ?? '').trim();
        if (ubicacion) base.ubicacion = ubicacion;

        if (selectedCategory) base.categoria = selectedCategory;

        const [pmin, pmax] = committedPriceRange;
        if (Number.isFinite(pmin) && pmin > 0) base.precioMin = pmin;
        if (Number.isFinite(pmax) && pmax > 0 && pmax >= pmin) base.precioMax = pmax;

        if (committedFechaDesde) base.fechaInicio = toLocalISODate(committedFechaDesde);
        if (committedFechaHasta) base.fechaFin = toLocalISODate(committedFechaHasta);

        return base;
    }, [
        locState?.filtros,
        committedSearch,
        selectedCategory,
        committedPriceRange,
        committedFechaDesde,
        committedFechaHasta,
    ]);

    // ---------- fetch central ----------
    useEffect(() => {
        setLoading(true);
        EventoService.buscarEventosConFiltros(effectiveFilters)
            .then((res) => setEventos(res.eventos))
            .catch(() => setEventos([]))
            .finally(() => setLoading(false));
    }, [effectiveFilters]);

    // ---------- handlers ----------
    const handleToggleCategory = (tipo: string) => {
        const updated = selectedCategory === tipo ? '' : tipo;
        setSelectedCategory(updated);
        setSearchParams((prev) => {
            const p = new URLSearchParams(prev);
            if (updated) p.set('categoria', updated);
            else p.delete('categoria');
            return p;
        });
    };

    const handlePriceChange = (_e: unknown, v: number | number[]) => {
        setPriceRange(v as [number, number]);
    };
    const handlePriceCommitted = (_e: unknown, v: number | number[]) => {
        setCommittedPriceRange(v as [number, number]);
    };

    const commitFechas = () => {
        setCommittedFechaDesde(fechaDesde);
        setCommittedFechaHasta(fechaHasta);
    };

    const handleFechaDesde = (value: string) => {
        setFechaDesde(value ? new Date(value + 'T00:00:00') : undefined);
    };
    const handleFechaHasta = (value: string) => {
        setFechaHasta(value ? new Date(value + 'T00:00:00') : undefined);
    };

    const handleClearFilters = () => {
        setCommittedSearch('');
        setSelectedCategory('');
        setPriceRange([0, 0]);
        setCommittedPriceRange([0, 0]);
        setFechaDesde(undefined);
        setFechaHasta(undefined);
        setCommittedFechaDesde(undefined);
        setCommittedFechaHasta(undefined);

        setSearchParams((prev) => {
            const p = new URLSearchParams(prev);
            ['q', 'categoria', 'loc', 'desde', 'hasta', 'pmin', 'pmax'].forEach((k) => p.delete(k));
            return p;
        });
    };

    // ---------- loading fullscreen (evita early return) ----------
    const showFullLoader = loading && !eventos.length;

    // ---------- mapa ----------
    const points: MapPoint[] = useMemo(() => eventosToPoints(eventos), [eventos]);
    const defaultCenter: [number, number] = [-34.6037, -58.3816]; // Obelisco
    const mapCenter: [number, number] = points.length ? points[0].position : defaultCenter;

    // ---------- render único ----------
    return (
        <>
            {showFullLoader ? (
                <Box
                    sx={{
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        height: '100vh',
                        width: '100vw',
                        bgcolor: '#FDF3E0',
                    }}
                >
                    <CircularProgress size={32} sx={{ mr: 1 }} />
                    <Typography variant="body2" color="text.secondary">
                        Cargando eventos...
                    </Typography>
                </Box>
            ) : eventoSeleccionado ? (
                <DetallesEvento evento={eventoSeleccionado} onVolver={() => setEventoSeleccionado(null)} />
            ) : (
                <Box sx={{ display: 'flex', width: '100vw', height: '100vh' }}>
                    {/* Columna izquierda */}
                    <Box sx={{ flex: 1, minWidth: 0, p: 2, overflowY: 'auto', bgcolor: '#FDF3E0' }}>
                        <Typography variant="h5" sx={{ fontWeight: 700, mb: 2 }}>
                            Explorá eventos
                        </Typography>

                        <Grid container spacing={2} alignItems="stretch">
                            {/* Filtros */}
                            <Grid>
                                <Paper variant="outlined" sx={{ p: 2 }}>
                                    <Stack direction="row" alignItems="center" justifyContent="space-between" sx={{ mb: 1 }}>
                                        <Typography variant="subtitle2">Filtros</Typography>
                                        <Tooltip title="Limpiar filtros">
                      <span>
                        <IconButton
                            onClick={handleClearFilters}
                            disabled={loading}
                            size="small"
                            sx={{ color: 'text.secondary' }}
                        >
                          <RestartAltIcon fontSize="small" />
                        </IconButton>
                      </span>
                                        </Tooltip>
                                    </Stack>

                                    <Typography variant="subtitle2" sx={{ mb: 1 }}>
                                        Fecha (rango)
                                    </Typography>
                                    <Stack direction="row" spacing={1}>
                                        <TextField
                                            type="date"
                                            label="Desde"
                                            size="small"
                                            InputLabelProps={{ shrink: true }}
                                            value={fechaDesde ? toLocalISODate(fechaDesde) : ''}
                                            onChange={(e) => handleFechaDesde(e.target.value)}
                                            onBlur={commitFechas}
                                            inputProps={{ max: fechaHasta ? toLocalISODate(fechaHasta) : undefined }}
                                        />
                                        <TextField
                                            type="date"
                                            label="Hasta"
                                            size="small"
                                            InputLabelProps={{ shrink: true }}
                                            value={fechaHasta ? toLocalISODate(fechaHasta) : ''}
                                            onChange={(e) => handleFechaHasta(e.target.value)}
                                            onBlur={commitFechas}
                                            inputProps={{ min: fechaDesde ? toLocalISODate(fechaDesde) : undefined }}
                                        />
                                    </Stack>

                                    <Divider sx={{ my: 2 }} />

                                    <Typography variant="subtitle2" sx={{ mb: 1 }}>
                                        Categorías
                                    </Typography>
                                    {loadingCats ? (
                                        <Box sx={{ display: 'flex', justifyContent: 'center', py: 1 }}>
                                            <CircularProgress size={20} />
                                        </Box>
                                    ) : (
                                        <RadioGroup
                                            value={selectedCategory}
                                            onChange={(_, v) => handleToggleCategory(v)}
                                            name="categorias"
                                        >
                                            <FormControlLabel value="" control={<Radio size="small" />} label="Todas" />
                                            {categories.map((categoria) => {
                                                const tipo = categoria.tipo;
                                                const IconComponent = getCategoryIconFor(undefined, categoria.icono, tipo);
                                                return (
                                                    <FormControlLabel
                                                        key={tipo}
                                                        value={tipo}
                                                        control={<Radio size="small" />}
                                                        label={
                                                            <Stack direction="row" spacing={1} alignItems="center">
                                                                <IconComponent fontSize="small" />
                                                                <span>{tipo}</span>
                                                            </Stack>
                                                        }
                                                    />
                                                );
                                            })}
                                        </RadioGroup>
                                    )}

                                    <Divider sx={{ my: 2 }} />

                                    <Typography variant="subtitle2" sx={{ mb: 1 }}>
                                        Precio
                                    </Typography>
                                    <Slider
                                        value={priceRange}
                                        onChange={handlePriceChange}
                                        onChangeCommitted={handlePriceCommitted}
                                        valueLabelDisplay="auto"
                                        min={0}
                                        max={100000}
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
                            <Grid size = {6}>
                                {!loading && eventos.length === 0 ? (
                                    <Paper
                                        variant="outlined"
                                        sx={{ p: 3, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}
                                    >
                                        <Box>
                                            <Typography variant="h6" sx={{ fontWeight: 700, mb: 0.5 }}>
                                                No encontramos eventos
                                            </Typography>
                                            <Typography variant="body2" color="text.secondary">
                                                No hay resultados que coincidan con los filtros seleccionados.
                                            </Typography>
                                        </Box>
                                        <IconButton title="Limpiar filtros" aria-label="Limpiar filtros" onClick={handleClearFilters}>
                                            <RestartAltIcon />
                                        </IconButton>
                                    </Paper>
                                ) : (
                                    <Grid container spacing={2} alignItems="stretch">
                                        {eventos.map((ev) => (
                                            <Grid size = {6} key={ev.id} sx={{ display: 'flex' }}>
                                                <EventCard item={ev} onVerDetalle={() => setEventoSeleccionado(ev)} />
                                            </Grid>
                                        ))}
                                    </Grid>
                                )}
                            </Grid>
                        </Grid>
                    </Box>

                    {/* Columna derecha: Mapa */}
                    <Paper
                        variant="outlined"
                        sx={{
                            width: { xs: '100%', md: '35%' },
                            height: '100vh',
                            display: 'flex',
                            overflow: 'hidden',
                        }}
                    >
                        <MapView center={mapCenter} zoom={12} points={points} />
                    </Paper>
                </Box>
            )}
        </>
    );
};

export default EventOverview;
