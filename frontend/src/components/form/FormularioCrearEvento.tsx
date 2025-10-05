import * as React from "react";
import {
    Box,
    Grid,
    TextField,
    Button,
    Alert,
    Typography,
    MenuItem,
} from "@mui/material";
import authService from "../../services/authService";
import { CATEGORY_PRESETS } from "../../utils/categoryIcons";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

const buildDefaultCategoria = () => (CATEGORY_PRESETS[0] ? { ...CATEGORY_PRESETS[0] } : { tipo: "", icono: undefined });

type Ubicacion = {
    latitud: string;
    longitud: string;
    localidad: string;
    direccion: string;
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

type Categoria = {
    tipo: string; // clase con un único campo
    icono?: string;
};

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
    categoria: Categoria;
    etiquetas: string[];
};

const ESTADOS: TipoEstadoEvento[] = [
    "CONFIRMADO",
    "PENDIENTE",
    "CANCELADO",
    "NO_ACEPTA_INSCRIPCIONES",
];

const MONEDAS = ["ARS", "USD", "EUR"];

export default function FormularioCrearEvento() {
    const [titulo, setTitulo] = React.useState("");
    const [descripcion, setDescripcion] = React.useState("");

    const [fecha, setFecha] = React.useState("");       // YYYY-MM-DD
    const [horaInicio, setHoraInicio] = React.useState(""); // HH:mm

    const [duracion, setDuracion] = React.useState<string>("");
    const [cupoMaximo, setCupoMaximo] = React.useState<string>("");
    const [cupoMinimo, setCupoMinimo] = React.useState<string>("");

    const [latitud, setLatitud] = React.useState("");
    const [longitud, setLongitud] = React.useState("");
    const [localidad, setLocalidad] = React.useState("");
    const [direccion, setDireccion] = React.useState("");

    const [precioMoneda, setPrecioMoneda] = React.useState("ARS");
    const [precioCantidad, setPrecioCantidad] = React.useState<string>("");

    const [estado, setEstado] = React.useState<TipoEstadoEvento>("PENDIENTE");
    const [categoriaSeleccionada, setCategoriaSeleccionada] = React.useState<Categoria>(buildDefaultCategoria);

    const [etiquetasCSV, setEtiquetasCSV] = React.useState("");
    const [imagen, setImagen] = React.useState<File | null>(null);

    const [submitting, setSubmitting] = React.useState(false);
    const [errorMsg, setErrorMsg] = React.useState<string | null>(null);
    const [successMsg, setSuccessMsg] = React.useState<string | null>(null);

    const handleImageChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const file = event.target.files?.[0];
        setImagen(file ?? null);
    };

    const handleCategoriaChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const value = event.target.value;
        const preset = CATEGORY_PRESETS.find((categoria) => categoria.tipo === value);
        setCategoriaSeleccionada(preset ? { ...preset } : buildDefaultCategoria());
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setErrorMsg(null);
        setSuccessMsg(null);

        if (!titulo || !fecha || !horaInicio) {
            setErrorMsg("Completá título, fecha y hora de inicio.");
            return;
        }
        if (!categoriaSeleccionada || !categoriaSeleccionada.tipo) {
            setErrorMsg("Debés seleccionar una categoría.");
            return;
        }

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
            ubicacion: {
                latitud: latitud.trim(),
                longitud: longitud.trim(),
                localidad: localidad.trim(),
                direccion: direccion.trim(),
            },
            cupoMaximo: cupoMaximo ? parseInt(cupoMaximo, 10) : null,
            cupoMinimo: cupoMinimo ? parseInt(cupoMinimo, 10) : null,
            precio: precioCantidad
                ? { moneda: (precioMoneda || "ARS").toUpperCase(), cantidad: parseFloat(precioCantidad) }
                : null,
            estado,
            categoria: {
                tipo: categoriaSeleccionada.tipo,
                icono: categoriaSeleccionada.icono,
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
            setEstado("PENDIENTE");
            setCategoriaSeleccionada(buildDefaultCategoria());
            setEtiquetasCSV("");
            setImagen(null);
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

                {/* Estado / Categoría */}
                <Grid size={{xs:12,sm:6}}>
                    <TextField
                        select
                        label="Estado"
                        value={estado}
                        onChange={(e) => setEstado(e.target.value as TipoEstadoEvento)}
                        fullWidth
                        required
                        disabled={submitting}
                        helperText="TipoEstadoEvento"
                    >
                        {ESTADOS.map((op) => (
                            <MenuItem key={op} value={op}>{op}</MenuItem>
                        ))}
                    </TextField>
                </Grid>

                <Grid size={{xs:12,sm:6}}>
                    <TextField
                        select
                        label="Categoría"
                        value={categoriaSeleccionada.tipo}
                        onChange={handleCategoriaChange}
                        fullWidth
                        required
                        disabled={submitting}
                        helperText="Se enviará el icono asociado automáticamente"
                    >
                        {CATEGORY_PRESETS.map((cat) => (
                            <MenuItem key={cat.tipo} value={cat.tipo}>
                                {cat.tipo}
                            </MenuItem>
                        ))}
                    </TextField>
                </Grid>

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

                {/* Ubicación */}
                <Grid size={{xs:12,sm:3}}>
                    <TextField
                        label="Latitud"
                        value={latitud}
                        onChange={(e) => setLatitud(e.target.value)}
                        fullWidth
                        disabled={submitting}
                        placeholder="-34.6037"
                    />
                </Grid>
                <Grid size={{xs:12,sm:3}}>
                    <TextField
                        label="Longitud"
                        value={longitud}
                        onChange={(e) => setLongitud(e.target.value)}
                        fullWidth
                        disabled={submitting}
                        placeholder="-58.3816"
                    />
                </Grid>
                <Grid size={{xs:12,sm:3}}>
                    <TextField
                        label="Localidad"
                        value={localidad}
                        onChange={(e) => setLocalidad(e.target.value)}
                        fullWidth
                        disabled={submitting}
                        placeholder="CABA"
                    />
                </Grid>
                <Grid size={{xs:12,sm:3}}>
                    <TextField
                        label="Dirección"
                        value={direccion}
                        onChange={(e) => setDireccion(e.target.value)}
                        fullWidth
                        disabled={submitting}
                        placeholder="Av. Corrientes 1234"
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
