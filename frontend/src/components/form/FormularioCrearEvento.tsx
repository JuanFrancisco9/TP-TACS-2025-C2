import * as React from "react";
import {
    Box,
    Grid,
    TextField,
    Button,
    Alert,
    Typography,
    MenuItem,
    FormControl,
    FormLabel,
    RadioGroup,
    FormControlLabel,
    Radio,
} from "@mui/material";
import Autocomplete from "@mui/material/Autocomplete";
import authService from "../../services/authService";
import { EventoService } from "../../services/eventoService";
import { getCategoryIconComponent, inferIconName, normalizeKey } from "../../utils/categoryIcons";
import LocationPickerMap from "./LocationPickerMap";
import { PROVINCIAS, getDefaultCoordenadas, getLocalidades } from "../../utils/locationData";
import type { CategoriaDTO, CategoriaIconRule } from "../../types/evento";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

const createEmptyCategoria = (): CategoriaDTO => ({ tipo: "", icono: undefined });

const formatForDisplay = (value: string) =>
    value
        .trim()
        .replace(/\s+/g, " ")
        .split(" ")
        .filter(Boolean)
        .map((word) => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
        .join(" ");

type Ubicacion = {
    latitud: string | null;
    longitud: string | null;
    provincia: string | null;
    localidad: string | null;
    direccion: string | null;
    esVirtual: boolean;
    enlaceVirtual: string | null;
};

type Precio = {
    moneda: string;   // p.ej. "ARS" | "USD"
    cantidad: number; // Float en backend
};

type TipoEstadoEvento =
    | "CONFIRMADO"
    | "PENDIENTE"
    | "CANCELADO"
    | "NO_ACEPTA_INSCRIPCIONES";

type SolicitudEventoDto = {
    organizadorId: string;
    titulo: string;
    descripcion: string;
    fecha: string;              // "YYYY-MM-DDTHH:mm:ss"
    horaInicio: string;         // "HH:mm"
    duracion: number;           // Float
    ubicacion: Ubicacion;
    cupoMaximo: number | null;
    cupoMinimo: number | null;
    precio: Precio | null;
    estado: TipoEstadoEvento;
    categoria: CategoriaDTO;
    etiquetas: string[];
};

const MONEDAS = ["ARS", "USD", "EUR"];
type ModalidadEvento = "PRESENCIAL" | "VIRTUAL";

const validarEnlaceVirtual = (enlace: string): { ok: boolean; mensaje: string | null } => {
    if (!enlace) {
        return { ok: false, mensaje: "Ingresá el enlace para la reunión." };
    }

    let url: URL;
    try {
        url = new URL(enlace);
    } catch (err) {
        return { ok: false, mensaje: "Enlace invalido." };
    }

    if (url.protocol !== "https:") {
        return { ok: false, mensaje: "Enlace invalido." };
    }

    return { ok: true, mensaje: null };
};

export default function FormularioCrearEvento() {
    const [titulo, setTitulo] = React.useState("");
    const [descripcion, setDescripcion] = React.useState("");

    const [fecha, setFecha] = React.useState("");       // YYYY-MM-DD
    const [horaInicio, setHoraInicio] = React.useState(""); // HH:mm

    const [duracion, setDuracion] = React.useState<string>("");
    const [cupoMaximo, setCupoMaximo] = React.useState<string>("");
    const [cupoMinimo, setCupoMinimo] = React.useState<string>("");

    const [modalidad, setModalidad] = React.useState<ModalidadEvento>("PRESENCIAL");
    const [enlaceVirtual, setEnlaceVirtual] = React.useState("");
    const [enlaceVirtualError, setEnlaceVirtualError] = React.useState<string | null>(null);
    const [provincia, setProvincia] = React.useState("");
    const [latitud, setLatitud] = React.useState("");
    const [longitud, setLongitud] = React.useState("");
    const [localidad, setLocalidad] = React.useState("");
    const [direccion, setDireccion] = React.useState("");
    const [coordsAjustadasManualmente, setCoordsAjustadasManualmente] = React.useState(false);

    const [precioMoneda, setPrecioMoneda] = React.useState("ARS");
    const [precioCantidad, setPrecioCantidad] = React.useState<string>("");

    const estado: TipoEstadoEvento = "CONFIRMADO";
    const [categoriasDisponibles, setCategoriasDisponibles] = React.useState<CategoriaDTO[]>([]);
    const [iconRules, setIconRules] = React.useState<CategoriaIconRule[]>([]);
    const [categoriaSeleccionada, setCategoriaSeleccionada] = React.useState<CategoriaDTO>(createEmptyCategoria);
    const [categoriaInputValue, setCategoriaInputValue] = React.useState("");

    const [etiquetasCSV, setEtiquetasCSV] = React.useState("");
    const [imagen, setImagen] = React.useState<File | null>(null);

    const [submitting, setSubmitting] = React.useState(false);
    const [errorMsg, setErrorMsg] = React.useState<string | null>(null);
    const [successMsg, setSuccessMsg] = React.useState<string | null>(null);

    const localidadesDisponibles = React.useMemo(
        () => (modalidad === "PRESENCIAL" ? getLocalidades(provincia) : []),
        [modalidad, provincia]
    );

    React.useEffect(() => {
        let active = true;
        (async () => {
            try {
                const [categorias, reglas] = await Promise.all([
                    EventoService.obtenerCategorias(),
                    EventoService.obtenerReglasIcono(),
                ]);
                if (!active) return;
                setCategoriasDisponibles(categorias);
                if (reglas.length > 0) {
                    setIconRules(reglas);
                }
                if (categorias.length > 0) {
                    setCategoriaSeleccionada((prev) => {
                        if (prev.tipo) {
                            return prev;
                        }
                        const primera = categorias[0];
                        setCategoriaInputValue(primera.tipo);
                        return primera;
                    });
                }
            } catch (error) {
                console.error("Error cargando categorías o reglas de iconos", error);
            }
        })();
        return () => {
            active = false;
        };
    }, []);

    React.useEffect(() => {
        if (modalidad === "PRESENCIAL") {
            if (!provincia && PROVINCIAS.length > 0) {
                setProvincia(PROVINCIAS[0]);
            }
        } else {
            setProvincia("");
            setLocalidad("");
            setDireccion("");
            setLatitud("");
            setLongitud("");
            setCoordsAjustadasManualmente(false);
        }
    }, [modalidad, provincia]);

    React.useEffect(() => {
        if (modalidad !== "PRESENCIAL" || !provincia) {
            return;
        }
        const localidades = getLocalidades(provincia);
        if (localidades.length === 0) {
            setLocalidad("");
            setLatitud("");
            setLongitud("");
            setCoordsAjustadasManualmente(false);
            return;
        }
        const existente = localidades.find((loc) => loc.nombre === localidad);
        const objetivo = existente ?? localidades[0];
        if (!existente) {
            setLocalidad(objetivo.nombre);
        }
        if (!coordsAjustadasManualmente || !existente) {
            setLatitud(objetivo.latitud.toFixed(6));
            setLongitud(objetivo.longitud.toFixed(6));
        }
    }, [provincia, modalidad, localidad, coordsAjustadasManualmente]);

    React.useEffect(() => {
        if (modalidad !== "PRESENCIAL") {
            return;
        }
        const direccionLimpia = direccion.trim();
        if (!provincia || !localidad || !direccionLimpia) {
            return;
        }

        const controller = new AbortController();
        const timeoutId = window.setTimeout(async () => {
            try {
                const query = `${direccionLimpia}, ${localidad}, ${provincia}, Argentina`;
                const response = await fetch(`https://nominatim.openstreetmap.org/search?format=json&limit=1&q=${encodeURIComponent(query)}`, {
                    headers: {
                        'Accept-Language': 'es',
                        'User-Agent': 'tptacsg2-event-form'
                    },
                    signal: controller.signal,
                });

                if (!response.ok) {
                    return;
                }

                const data: Array<{ lat: string; lon: string }> = await response.json();
                if (Array.isArray(data) && data.length > 0) {
                    const match = data[0];
                    setLatitud(Number(match.lat).toFixed(6));
                    setLongitud(Number(match.lon).toFixed(6));
                    setCoordsAjustadasManualmente(true);
                }
            } catch (err) {
                if (!(err instanceof DOMException && err.name === 'AbortError')) {
                    console.warn('No se pudo geocodificar la dirección', err);
                }
            }
        }, 600);

        return () => {
            controller.abort();
            window.clearTimeout(timeoutId);
        };
    }, [modalidad, direccion, localidad, provincia]);

    const syncCategoria = React.useCallback((rawValue: string) => {
        const trimmed = rawValue.trim();
        if (!trimmed) {
            setCategoriaSeleccionada(createEmptyCategoria());
            setCategoriaInputValue("");
            return;
        }
        const display = formatForDisplay(trimmed);
        const normalizedTarget = normalizeKey(display);
        setCategoriasDisponibles((prev) => {
            const existente = prev.find((cat) => normalizeKey(cat.tipo) === normalizedTarget);
            if (existente) {
                setCategoriaSeleccionada(existente);
                setCategoriaInputValue(existente.tipo);
                return prev;
            }
            const iconoInferido = inferIconName(iconRules, display);
            const nuevaCategoria: CategoriaDTO = { tipo: display, icono: iconoInferido };
            setCategoriaSeleccionada(nuevaCategoria);
            setCategoriaInputValue(display);
            return [...prev, nuevaCategoria].sort((a, b) => a.tipo.localeCompare(b.tipo, "es", { sensitivity: "base" }));
        });
    }, [iconRules]);

    const handleModalidadChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const value = event.target.value as ModalidadEvento;
        setModalidad(value);
        if (value === "VIRTUAL") {
            setEnlaceVirtual("");
        }
    };

    const handleProvinciaSelect = (value: string) => {
        setProvincia(value);
        setCoordsAjustadasManualmente(false);
        const localidades = getLocalidades(value);
        if (localidades.length > 0) {
            const primera = localidades[0];
            setLocalidad(primera.nombre);
            setLatitud(primera.latitud.toFixed(6));
            setLongitud(primera.longitud.toFixed(6));
        } else {
            setLocalidad("");
            setLatitud("");
            setLongitud("");
        }
    };

    const handleLocalidadSelect = (value: string) => {
        setLocalidad(value);
        setCoordsAjustadasManualmente(false);
        const coords = getDefaultCoordenadas(provincia, value);
        if (coords) {
            setLatitud(coords.latitud.toFixed(6));
            setLongitud(coords.longitud.toFixed(6));
        }
    };

    const handleMapaChange = (lat: string, lon: string) => {
        setCoordsAjustadasManualmente(true);
        setLatitud(lat);
        setLongitud(lon);
    };

    const handleImageChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const file = event.target.files?.[0];
        setImagen(file ?? null);
    };

    const handleCategoriaSelection = (_event: React.SyntheticEvent, newValue: string | null) => {
        syncCategoria(newValue ?? "");
    };

    const handleCategoriaInputChange = (_event: React.SyntheticEvent, newInputValue: string) => {
        setCategoriaInputValue(newInputValue);
        if (!newInputValue) {
            setCategoriaSeleccionada(createEmptyCategoria());
        }
    };

    const ensureCategoriaSincronizada = React.useCallback(() => {
        syncCategoria(categoriaInputValue);
    }, [categoriaInputValue, syncCategoria]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setErrorMsg(null);
        setSuccessMsg(null);

        if (!titulo || !fecha || !horaInicio) {
            setErrorMsg("Completá título, fecha y hora de inicio.");
            return;
        }
        if (modalidad === "VIRTUAL") {
            const enlaceLimpio = enlaceVirtual.trim();
            const validation = validarEnlaceVirtual(enlaceLimpio);
            if (!validation.ok) {
                setEnlaceVirtualError(validation.mensaje);
                setErrorMsg(validation.mensaje ?? "El enlace del evento virtual no es válido.");
                return;
            }
            setEnlaceVirtualError(null);
        } else {
            if (!provincia) {
                setErrorMsg("Seleccioná una provincia.");
                return;
            }
            if (!localidad) {
                setErrorMsg("Seleccioná una localidad.");
                return;
            }
            if (!direccion.trim()) {
                setErrorMsg("Ingresá la dirección del evento.");
                return;
            }
            if (!latitud || !longitud) {
                setErrorMsg("Confirmá la ubicación en el mapa (latitud y longitud válidas).");
                return;
            }
        }
        const categoriaTexto = (categoriaInputValue || categoriaSeleccionada.tipo).trim();
        const categoriaDisplay = categoriaTexto ? formatForDisplay(categoriaTexto) : "";

        let categoriaPayload: CategoriaDTO | null = null;
        if (categoriaDisplay) {
            const normalized = normalizeKey(categoriaDisplay);
            const existente = categoriasDisponibles.find((cat) => normalizeKey(cat.tipo) === normalized);
            categoriaPayload = existente ?? { tipo: categoriaDisplay, icono: inferIconName(iconRules, categoriaDisplay) };
        }

        if (!categoriaPayload || !categoriaPayload.tipo) {
            setErrorMsg("Debés seleccionar una categoría.");
            return;
        }

        const categoriaFinal: CategoriaDTO = {
            tipo: categoriaPayload.tipo,
            icono: categoriaPayload.icono ?? inferIconName(iconRules, categoriaPayload.tipo),
        };

        setCategoriaSeleccionada(categoriaFinal);
        setCategoriaInputValue(categoriaFinal.tipo);
        setCategoriasDisponibles((prev) => {
            const objetivo = normalizeKey(categoriaFinal.tipo);
            if (prev.some((cat) => normalizeKey(cat.tipo) === objetivo)) {
                return prev;
            }
            return [...prev, categoriaFinal].sort((a, b) => a.tipo.localeCompare(b.tipo, "es", { sensitivity: "base" }));
        });

        const enlaceLimpio = enlaceVirtual.trim();
        const validacionEnlace = modalidad === "VIRTUAL" ? validarEnlaceVirtual(enlaceLimpio) : { ok: true, mensaje: null };
        if (modalidad === "VIRTUAL" && !validacionEnlace.ok) {
            setEnlaceVirtualError(validacionEnlace.mensaje);
            setErrorMsg(validacionEnlace.mensaje ?? "El enlace del evento virtual no es válido.");
            return;
        }
        setEnlaceVirtualError(null);
        const fechaISO = `${fecha}T${horaInicio}:00`;

        const currentUser = authService.getCurrentUser();
        if (!currentUser?.id) {
            setErrorMsg("Usuario no encontrado. Por favor, iniciá sesión.");
            return;
        }

        const payload: SolicitudEventoDto = {
            organizadorId: String(currentUser.actorId),
            titulo: titulo.trim(),
            descripcion: descripcion.trim(),
            fecha: fechaISO,
            horaInicio,
            duracion: duracion ? parseFloat(duracion) : 0,
            ubicacion: modalidad === "VIRTUAL"
                ? {
                    latitud: null,
                    longitud: null,
                    provincia: null,
                    localidad: null,
                    direccion: null,
                    esVirtual: true,
                    enlaceVirtual: enlaceLimpio,
                }
                : {
                    latitud: latitud.trim(),
                    longitud: longitud.trim(),
                    provincia: provincia.trim(),
                    localidad: localidad.trim(),
                    direccion: direccion.trim(),
                    esVirtual: false,
                    enlaceVirtual: null,
                },
            cupoMaximo: cupoMaximo ? parseInt(cupoMaximo, 10) : null,
            cupoMinimo: cupoMinimo ? parseInt(cupoMinimo, 10) : null,
            precio: precioCantidad
                ? { moneda: (precioMoneda || "ARS").toUpperCase(), cantidad: parseFloat(precioCantidad) }
                : null,
            estado,
            categoria: {
                tipo: categoriaFinal.tipo,
                icono: categoriaFinal.icono,
            },
            etiquetas: etiquetasCSV.split(",").map(s => s.trim()).filter(Boolean),
        };

        try {
            setSubmitting(true);

            const formData = new FormData();
            formData.append(
                "evento",
                new Blob([JSON.stringify(payload)], { type: "application/json" })
            );

            if (imagen) {
                formData.append("imagen", imagen);
            }

            const res = await fetch(`${API_BASE_URL}/eventos`, {
                method: "POST",
                headers: authService.getAuthHeaders({ contentType: null }),
                body: formData,
            });

            if (!res.ok) {
                let msg = `Error al crear evento (HTTP ${res.status}).`;
                try {
                    const txt = await res.text();
                    if (txt) msg = txt;
                } catch {}
                setErrorMsg(msg);
                return;
            }

            setSuccessMsg("¡Evento creado con éxito!");
            // Reset rápido
            setTitulo("");
            setDescripcion("");
            setFecha("");
            setHoraInicio("");
            setDuracion("");
            setLatitud("");
            setLongitud("");
            setLocalidad("");
            setDireccion("");
            setCupoMaximo("");
            setCupoMinimo("");
            setPrecioCantidad("");
            setPrecioMoneda("ARS");
            setModalidad("PRESENCIAL");
            setEnlaceVirtual("");
            setProvincia("");
            setLocalidad("");
            setDireccion("");
            setLatitud("");
            setLongitud("");
            setCoordsAjustadasManualmente(false);
            setCategoriaSeleccionada(createEmptyCategoria());
            setCategoriaInputValue("");
            setEtiquetasCSV("");
            setImagen(null);
            void (async () => {
                try {
                    const [categoriasActualizadas, reglasActualizadas] = await Promise.all([
                        EventoService.obtenerCategorias(),
                        EventoService.obtenerReglasIcono(),
                    ]);
                    setCategoriasDisponibles(categoriasActualizadas);
                    if (reglasActualizadas.length > 0) {
                        setIconRules(reglasActualizadas);
                    }
                    if (categoriasActualizadas.length > 0) {
                        setCategoriaSeleccionada(categoriasActualizadas[0]);
                        setCategoriaInputValue(categoriasActualizadas[0].tipo);
                    }
                } catch (fetchError) {
                    console.error("No se pudieron refrescar las categorías/iconos", fetchError);
                }
            })();
        } catch (err: any) {
            setErrorMsg(err?.message ?? "No se pudo conectar con el servidor.");
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <Box component="form" onSubmit={handleSubmit} sx={{ p: 2, maxWidth: 800 }}>
            <Typography variant="h6" sx={{ mb: 2 }}>Crear evento</Typography>

            {errorMsg && <Alert severity="error" sx={{ mb: 2 }}>{errorMsg}</Alert>}
            {successMsg && <Alert severity="success" sx={{ mb: 2 }}>{successMsg}</Alert>}

            <Grid container spacing={2}>
                <Grid  size={{xs:12}}>
                    <TextField
                        label="Título"
                        value={titulo}
                        onChange={(e) => setTitulo(e.target.value)}
                        fullWidth
                        required
                        disabled={submitting}
                    />
                </Grid>

                <Grid size={{xs:12}}>
                    <TextField
                        label="Descripción"
                        value={descripcion}
                        onChange={(e) => setDescripcion(e.target.value)}
                        fullWidth
                        multiline
                        minRows={3}
                        disabled={submitting}
                    />
                </Grid>

                <Grid size={{xs:12,sm:6}}>
                    <TextField
                        type="date"
                        label="Fecha"
                        value={fecha}
                        onChange={(e) => setFecha(e.target.value)}
                        fullWidth
                        required
                        disabled={submitting}
                        InputLabelProps={{ shrink: true }}
                    />
                </Grid>

                <Grid size={{xs:12,sm:6}}>
                    <TextField
                        type="time"
                        label="Hora de inicio"
                        value={horaInicio}
                        onChange={(e) => setHoraInicio(e.target.value)}
                        fullWidth
                        required
                        disabled={submitting}
                        InputLabelProps={{ shrink: true }}
                    />
                </Grid>

                <Grid size={{xs:12,sm:6}}>
                    <TextField
                        label="Duración (horas)"
                        type="number"
                        inputProps={{ step: "0.25", min: "0" }}
                        value={duracion}
                        onChange={(e) => setDuracion(e.target.value)}
                        fullWidth
                        disabled={submitting}
                    />
                </Grid>

                <Grid size={{xs:12,sm:6}}>
                    <TextField
                        label="Etiquetas (separadas por coma)"
                        placeholder="networking, frontend, free"
                        value={etiquetasCSV}
                        onChange={(e) => setEtiquetasCSV(e.target.value)}
                        fullWidth
                        disabled={submitting}
                    />
                </Grid>

                {/* Categoría */}
                <Grid size={{xs:12,sm:6}}>
                    <Autocomplete
                        freeSolo
                        options={categoriasDisponibles.map((cat) => cat.tipo)}
                        value={categoriaSeleccionada.tipo || null}
                        inputValue={categoriaInputValue}
                        onChange={handleCategoriaSelection}
                        onInputChange={handleCategoriaInputChange}
                        disabled={submitting}
                        renderInput={(params) => {
                            const iconNamePreview = categoriaSeleccionada.icono
                                ?? inferIconName(iconRules, categoriaInputValue || categoriaSeleccionada.tipo);
                            const IconPreview = getCategoryIconComponent(iconNamePreview);
                            return (
                                <TextField
                                    {...params}
                                    label="Categoría"
                                    required
                                    onBlur={ensureCategoriaSincronizada}
                                    InputProps={{
                                        ...params.InputProps,
                                        endAdornment: (
                                            <>
                                                <Box sx={{ display: "flex", alignItems: "center", pr: 1 }}>
                                                    <IconPreview fontSize="small" />
                                                </Box>
                                                {params.InputProps.endAdornment}
                                            </>
                                        ),
                                    }}
                                />
                            );
                        }}
                    />
                </Grid>

                <Grid size={{xs:12}}>
                    <FormControl component="fieldset" disabled={submitting}>
                        <FormLabel component="legend">Modalidad</FormLabel>
                        <RadioGroup
                            row
                            value={modalidad}
                            onChange={handleModalidadChange}
                            name="modalidad-evento"
                        >
                            <FormControlLabel value="PRESENCIAL" control={<Radio />} label="Presencial" />
                            <FormControlLabel value="VIRTUAL" control={<Radio />} label="Virtual" />
                        </RadioGroup>
                    </FormControl>
                </Grid>

                {modalidad === "VIRTUAL" ? (
                    <Grid size={{ xs: 12 }}>
                        <TextField
                            label="Enlace de la reunión (Meet, Zoom, etc.)"
                            value={enlaceVirtual}
                            onChange={(e) => {
                                const value = e.target.value;
                                setEnlaceVirtual(value);
                                const cleaned = value.trim();
                                if (!cleaned) {
                                    setEnlaceVirtualError(null);
                                    return;
                                }
                                const validation = validarEnlaceVirtual(cleaned);
                                setEnlaceVirtualError(validation.ok ? null : validation.mensaje);
                            }}
                            fullWidth
                            required
                            type="url"
                            placeholder="https://..."
                            disabled={submitting}
                            error={Boolean(enlaceVirtualError)}
                        />
                    </Grid>
                ) : (
                    <>
                        <Grid size={{ xs: 12, md: 4 }}>
                            <TextField
                                select
                                label="Provincia"
                                value={provincia}
                                onChange={(e) => handleProvinciaSelect(e.target.value)}
                                fullWidth
                                required
                                disabled={submitting || modalidad !== "PRESENCIAL"}
                                SelectProps={{
                                    MenuProps: {
                                        PaperProps: {
                                            style: { maxHeight: 240 }
                                        }
                                    }
                                }}
                            >
                                {PROVINCIAS.map((prov) => (
                                    <MenuItem key={prov} value={prov}>{prov}</MenuItem>
                                ))}
                            </TextField>
                        </Grid>
                        <Grid size={{ xs: 12, md: 4 }}>
                            <TextField
                                select
                                label="Localidad"
                                value={localidad}
                                onChange={(e) => handleLocalidadSelect(e.target.value)}
                                fullWidth
                                required
                                disabled={submitting || modalidad !== "PRESENCIAL" || localidadesDisponibles.length === 0}
                                SelectProps={{
                                    MenuProps: {
                                        PaperProps: {
                                            style: { maxHeight: 240 }
                                        }
                                    }
                                }}
                            >
                                {localidadesDisponibles.map((loc) => (
                                    <MenuItem key={loc.nombre} value={loc.nombre}>{loc.nombre}</MenuItem>
                                ))}
                            </TextField>
                        </Grid>
                        <Grid size={{ xs: 12, md: 4 }}>
                            <TextField
                                label="Dirección"
                                value={direccion}
                                onChange={(e) => setDireccion(e.target.value)}
                                fullWidth
                                required
                                disabled={submitting || modalidad !== "PRESENCIAL"}
                                placeholder="Calle y número"
                            />
                        </Grid>
                        <Grid size={{ xs: 12 }}>
                            <LocationPickerMap
                                latitud={latitud}
                                longitud={longitud}
                                onChange={handleMapaChange}
                                disabled={submitting || modalidad !== "PRESENCIAL"}
                            />
                        </Grid>
                    </>
                )}

                {/* Cupos */}
                <Grid size={{xs:12,sm:6}}>
                    <TextField
                        label="Cupo máximo"
                        type="number"
                        value={cupoMaximo}
                        onChange={(e) => setCupoMaximo(e.target.value)}
                        fullWidth
                        disabled={submitting}
                        inputProps={{ min: "0" }}
                    />
                </Grid>

                <Grid size={{xs:12,sm:6}}>
                    <TextField
                        label="Cupo mínimo"
                        type="number"
                        value={cupoMinimo}
                        onChange={(e) => setCupoMinimo(e.target.value)}
                        fullWidth
                        disabled={submitting}
                        inputProps={{ min: "0" }}
                    />
                </Grid>

                {/* Precio */}
                <Grid size={{xs:12,sm:4}}>
                    <TextField
                        select
                        label="Moneda"
                        value={precioMoneda}
                        onChange={(e) => setPrecioMoneda(e.target.value)}
                        fullWidth
                        disabled={submitting}
                    >
                        {MONEDAS.map((m) => (
                            <MenuItem key={m} value={m}>{m}</MenuItem>
                        ))}
                    </TextField>
                </Grid>
                <Grid size={{xs:12,sm:8}}>
                    <TextField
                        label="Precio (cantidad)"
                        type="number"
                        inputProps={{ step: "0.01", min: "0" }}
                        value={precioCantidad}
                        onChange={(e) => setPrecioCantidad(e.target.value)}
                        fullWidth
                        disabled={submitting}
                    />
                </Grid>

                <Grid size={{xs:12}}>
                    <Button
                        variant="outlined"
                        component="label"
                        disabled={submitting}
                    >
                        {imagen ? "Cambiar imagen" : "Subir imagen"}
                        <input
                            type="file"
                            accept="image/*"
                            hidden
                            onChange={handleImageChange}
                        />
                    </Button>
                    {imagen && (
                        <Typography variant="body2" sx={{ mt: 1 }}>
                            {imagen.name}
                        </Typography>
                    )}
                </Grid>
            </Grid>

            <Box sx={{ mt: 2 }}>
                <Button type="submit" variant="contained" disabled={submitting}>
                    {submitting ? "Creando..." : "Crear evento"}
                </Button>
            </Box>

        </Box>
    );
}
