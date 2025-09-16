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

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

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
    const [organizadorId, setOrganizadorId] = React.useState("");
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
    const [categoriaTipo, setCategoriaTipo] = React.useState("");

    const [etiquetasCSV, setEtiquetasCSV] = React.useState("");

    const [submitting, setSubmitting] = React.useState(false);
    const [errorMsg, setErrorMsg] = React.useState<string | null>(null);
    const [successMsg, setSuccessMsg] = React.useState<string | null>(null);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setErrorMsg(null);
        setSuccessMsg(null);

        if (!organizadorId || !titulo || !fecha || !horaInicio) {
            setErrorMsg("Completá organizador, título, fecha y hora de inicio.");
            return;
        }
        if (!categoriaTipo) {
            setErrorMsg("Ingresá la categoría (campo 'tipo').");
            return;
        }

        const fechaISO = `${fecha}T${horaInicio}:00`;

        const payload: SolicitudEventoDto = {
            organizadorId: organizadorId.trim(),
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
            categoria: { tipo: categoriaTipo.trim() },
            etiquetas: etiquetasCSV.split(",").map(s => s.trim()).filter(Boolean),
        };

        try {
            setSubmitting(true);

            const res = await fetch(`${API_BASE_URL}/eventos`, {
                method: "POST",
                headers: authService.getAuthHeaders(),
                body: JSON.stringify(payload),
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
            setCategoriaTipo("");
            setEtiquetasCSV("");
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
                <Grid item xs={12} sm={6}>
                    <TextField
                        label="Organizador ID"
                        placeholder="1"
                        value={organizadorId}
                        onChange={(e) => setOrganizadorId(e.target.value)}
                        fullWidth
                        required
                        disabled={submitting}
                    />
                </Grid>

                <Grid item xs={12} sm={6}>
                    <TextField
                        label="Título"
                        value={titulo}
                        onChange={(e) => setTitulo(e.target.value)}
                        fullWidth
                        required
                        disabled={submitting}
                    />
                </Grid>

                <Grid item xs={12}>
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

                <Grid item xs={12} sm={6}>
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

                <Grid item xs={12} sm={6}>
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

                <Grid item xs={12} sm={4}>
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

                <Grid item xs={12} sm={8}>
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
                <Grid item xs={12} sm={6}>
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

                <Grid item xs={12} sm={6}>
                    <TextField
                        label="Categoría (categoria.tipo)"
                        value={categoriaTipo}
                        onChange={(e) => setCategoriaTipo(e.target.value)}
                        fullWidth
                        required
                        disabled={submitting}
                        helperText="Se enviará como { tipo: '...' }"
                    />
                </Grid>

                {/* Cupos */}
                <Grid item xs={12} sm={6}>
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

                <Grid item xs={12} sm={6}>
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
                <Grid item xs={12} sm={3}>
                    <TextField
                        label="Latitud"
                        value={latitud}
                        onChange={(e) => setLatitud(e.target.value)}
                        fullWidth
                        disabled={submitting}
                        placeholder="-34.6037"
                    />
                </Grid>
                <Grid item xs={12} sm={3}>
                    <TextField
                        label="Longitud"
                        value={longitud}
                        onChange={(e) => setLongitud(e.target.value)}
                        fullWidth
                        disabled={submitting}
                        placeholder="-58.3816"
                    />
                </Grid>
                <Grid item xs={12} sm={3}>
                    <TextField
                        label="Localidad"
                        value={localidad}
                        onChange={(e) => setLocalidad(e.target.value)}
                        fullWidth
                        disabled={submitting}
                        placeholder="CABA"
                    />
                </Grid>
                <Grid item xs={12} sm={3}>
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
                <Grid item xs={12} sm={4}>
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
                <Grid item xs={12} sm={8}>
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
            </Grid>

            <Box sx={{ mt: 2 }}>
                <Button type="submit" variant="contained" disabled={submitting}>
                    {submitting ? "Creando..." : "Crear evento"}
                </Button>
            </Box>

        </Box>
    );
}
