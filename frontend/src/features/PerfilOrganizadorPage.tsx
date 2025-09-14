import {useEffect, useState} from "react";
import {
    Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
    Paper, Button, Dialog, DialogTitle, DialogContent, DialogActions,
    TextField, Typography, Select, MenuItem, InputLabel
} from "@mui/material";
import VisibilityIcon from "@mui/icons-material/Visibility";
import EditIcon from "@mui/icons-material/Edit";
import ListAltIcon from "@mui/icons-material/ListAlt";

import type {Event} from '../services/participanteApiService'
import FormControl from "@mui/material/FormControl";

interface EditEvent {
    event: Event | null;
    onClose: () => void;
    onSave: (updated: Event) => void | null;
}

interface ViewModal {
    event: Event | null;
    onClose: () => void;
}

export default function PerfilOrganizador() {
    const [events, setEvents] = useState<Event[]>([
        {
            id: '1',
            titulo: 'Seminario de Mocks',
            descripcion: 'Introducción a Mocks',
            fecha: '2025-09-10',
            horaInicio: '19:00',
            duracion: 2.5,
            ubicacion: { provincia: 'Buenos Aires', ciudad: 'CABA', direccion: 'Av. Siempre Viva 123' },
            cupoMaximo: 30,
            cupoMinimo: 10,
            precio: { moneda: 'ARS', monto: 1000 },
            organizador: { id: '1', nombre: 'Juan', apellido: 'Pérez',dni: '22515565', usuario: null },
            estado: { tipoEstado: 'CONFIRMADO', fechaCambio: '2025-08-27' },
            categoria: { tipo: 'TECNOLOGIA' },
            etiquetas: ['mocks', 'testing', 'java']
        }
    ]);

    const [user] = useState("Juan");

    // ---- estados de modal ----
    const [editEvent, setEditEvent] = useState<Event | null>(null);
    const [viewEvent, setViewEvent] = useState<Event | null>(null);
    const [waitlistEvent, setWaitlistEvent] = useState<Event | null>(null);

    // ---- handlers ----
    const handleSaveEdit = (updatedEvent: Event) => {
        setEvents(prev =>
            prev.map(ev => ev.id === updatedEvent.id ? updatedEvent : ev)
        );
        setEditEvent(null);
    };

    return (
        <div style={{ padding: "2rem" }}>
            <Typography variant="h4" gutterBottom>Bienvenido: {user}</Typography>

            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Título</TableCell>
                            <TableCell>Fecha</TableCell>
                            <TableCell>Ubicación</TableCell>
                            <TableCell>Acciones</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {events.map((e) => (
                            <TableRow key={e.id}>
                                <TableCell>{e.titulo}</TableCell>
                                <TableCell>{e.fecha}</TableCell>
                                <TableCell>{e.ubicacion.ciudad}, {e.ubicacion.direccion}</TableCell>
                                <TableCell>
                                    <Button onClick={() => setEditEvent(e)}><EditIcon /></Button>
                                    <Button onClick={() => setViewEvent(e)}><VisibilityIcon /></Button>
                                    <Button onClick={() => setWaitlistEvent(e)}><ListAltIcon /></Button>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>

            <EditEvent event={editEvent} onClose={() => setEditEvent(null)} onSave={handleSaveEdit} />
            <VerInscriptos event={viewEvent} onClose={() => setViewEvent(null)}/>
            <WaitList event={waitlistEvent} onClose={() => setWaitlistEvent(null)} />
        </div>
    );
}

const EditEvent = ({ event, onClose, onSave }: EditEvent) => {
    const [localEvent, setLocalEvent] = useState<Event | null>(null);

    useEffect(() => {
        setLocalEvent(event ? { ...event } : null);
    }, [event]);

    if (!localEvent) return null;

    const handleChange = (field: keyof Event, value: any) => {
        setLocalEvent(prev => prev ? { ...prev, [field]: value } : prev);
    };

    const handleNestedChange = <K extends keyof Event>(
        parent: K,
        subfield: keyof Event[K],
        value: any
    ) => {
        setLocalEvent(prev =>
            prev
                ? {
                    ...prev,
                    [parent]: { ...(prev[parent] as any), [subfield]: value } as any
                }
                : prev
        );
    };

    const handleSubmit = () => {
        if (localEvent) {
            onSave(localEvent);
        }
        onClose();
    };

    return (
        <Dialog open={!!event} onClose={onClose} maxWidth="sm" fullWidth>
            <DialogTitle>Editar Evento</DialogTitle>
            <DialogContent>
                <TextField
                    label="Título"
                    fullWidth
                    margin="normal"
                    value={localEvent.titulo}
                    onChange={e => handleChange("titulo", e.target.value)}
                />
                <TextField
                    label="Descripción"
                    fullWidth
                    margin="normal"
                    multiline
                    value={localEvent.descripcion}
                    onChange={e => handleChange("descripcion", e.target.value)}
                />
                <TextField
                    type="date"
                    fullWidth
                    margin="normal"
                    value={localEvent.fecha}
                    onChange={e => handleChange("fecha", e.target.value)}
                />
                <TextField
                    label="Hora de inicio"
                    type="time"
                    fullWidth
                    margin="normal"
                    value={localEvent.horaInicio}
                    onChange={e => handleChange("horaInicio", e.target.value)}
                />
                <TextField
                    label="Duración (horas)"
                    type="number"
                    fullWidth
                    margin="normal"
                    value={localEvent.duracion}
                    onChange={e => handleChange("duracion", parseFloat(e.target.value))}
                />

                {/* Ubicación */}
                <TextField
                    label="Provincia"
                    fullWidth
                    margin="normal"
                    value={localEvent.ubicacion.provincia}
                    onChange={e => handleNestedChange("ubicacion", "provincia", e.target.value)}
                />
                <TextField
                    label="Ciudad"
                    fullWidth
                    margin="normal"
                    value={localEvent.ubicacion.ciudad}
                    onChange={e => handleNestedChange("ubicacion", "ciudad", e.target.value)}
                />
                <TextField
                    label="Dirección"
                    fullWidth
                    margin="normal"
                    value={localEvent.ubicacion.direccion}
                    onChange={e => handleNestedChange("ubicacion", "direccion", e.target.value)}
                />

                <TextField
                    label="Cupo máximo"
                    type="number"
                    fullWidth
                    margin="normal"
                    value={localEvent.cupoMaximo}
                    onChange={e => handleChange("cupoMaximo", parseInt(e.target.value))}
                />
                <TextField
                    label="Cupo mínimo"
                    type="number"
                    fullWidth
                    margin="normal"
                    value={localEvent.cupoMinimo}
                    onChange={e => handleChange("cupoMinimo", parseInt(e.target.value))}
                />

                {/* Precio */}
                <TextField
                    label="Moneda"
                    fullWidth
                    margin="normal"
                    value={localEvent.precio.moneda}
                    onChange={e => handleNestedChange("precio", "moneda", e.target.value)}
                />
                <TextField
                    label="Monto"
                    type="number"
                    fullWidth
                    margin="normal"
                    value={localEvent.precio.monto}
                    onChange={e => handleNestedChange("precio", "monto", parseFloat(e.target.value))}
                />

                {/* Estado */}
                <FormControl fullWidth margin="normal">
                    <InputLabel>Estado</InputLabel>
                    <Select
                        value={localEvent.estado.tipoEstado}
                        label="Estado"
                        onChange={e => handleNestedChange("estado", "tipoEstado", e.target.value)}
                    >
                        <MenuItem value="CONFIRMADO">CONFIRMADO</MenuItem>
                        <MenuItem value="PENDENTE">PENDENTE</MenuItem>
                        <MenuItem value="CANCELADO">CANCELADO</MenuItem>
                    </Select>
                </FormControl>

                {/* Categoría */}
                <TextField
                    label="Categoría"
                    fullWidth
                    margin="normal"
                    value={localEvent.categoria.tipo}
                    onChange={e => handleNestedChange("categoria", "tipo", e.target.value)}
                />

                {/* Etiquetas */}
                <TextField
                    label="Etiquetas (separadas por coma)"
                    fullWidth
                    margin="normal"
                    value={localEvent.etiquetas.join(", ")}
                    onChange={e =>
                        handleChange(
                            "etiquetas",
                            e.target.value.split(",").map(t => t.trim())
                        )
                    }
                />
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose}>Cancelar</Button>
                <Button onClick={handleSubmit} variant="contained">
                    Guardar
                </Button>
            </DialogActions>
        </Dialog>
    );
};

const VerInscriptos = ({event, onClose}:ViewModal) => {
    if (!event) return null;

    return (
        <Dialog open={!!event} onClose={onClose} maxWidth="sm" fullWidth>
            <DialogTitle>Inscriptos en {event.titulo}</DialogTitle>
            <DialogContent>
                <ul>
                    <li>Usuario 1</li>
                    <li>Usuario 2</li>
                </ul>
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose}>Cerrar</Button>
            </DialogActions>
        </Dialog>
    );

}

const WaitList = ({event, onClose}:ViewModal) =>{
    if (!event) return null;

    return (
        <Dialog open={!!event} onClose={onClose} maxWidth="sm" fullWidth>
            <DialogTitle>Lista de espera de {event.titulo}</DialogTitle>
            <DialogContent>
                <ul>
                    <li>Usuario 3</li>
                    <li>Usuario 4</li>
                </ul>
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose}>Cerrar</Button>
            </DialogActions>
        </Dialog>
    );
}