import {useEffect, useState} from "react";
import {
    Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
    Paper, Button, Dialog, DialogTitle, DialogContent, DialogActions,
    TextField, Typography, Select, MenuItem, InputLabel
} from "@mui/material";
import VisibilityIcon from "@mui/icons-material/Visibility";
import EditIcon from "@mui/icons-material/Edit";
import ListAltIcon from "@mui/icons-material/ListAlt";
import type {Evento} from '../services/eventoService.ts'
import type {Inscripcion} from "../types/inscripciones.ts";
import FormControl from "@mui/material/FormControl";
import { EventoService } from '../services/eventoService';
import { formatDateForInput } from "../utils/formatFecha.ts";

interface EditEvent {
    event: Evento | null;
    onClose: () => void;
    onSave: (updated: Evento) => Promise<void> | null;
}

interface ViewModal {
    event: Evento | null;
    onClose: () => void;
}

export default function PerfilOrganizador() {
    const [events, setEvents] = useState<Evento[]>([]);

    const [user] = useState("Juan");

    // ---- estados de modal ----
    const [editEvent, setEditEvent] = useState<Evento | null>(null);
    const [viewEvent, setViewEvent] = useState<Evento | null>(null);
    const [waitlistEvent, setWaitlistEvent] = useState<Evento | null>(null);

    // ---- handlers ----
    const handleSaveEdit = async (updatedEvent: Evento) => {
        try{
            const eventoAcutalizado = await EventoService.actualizarEvento(updatedEvent.id,updatedEvent)
            setEvents(prev =>
                prev.map(ev => ev.id === eventoAcutalizado.id ? updatedEvent : ev)
            );
            setEditEvent(null);
        }catch (error){
            console.error(error)
        }
    };

    useEffect(() => {
        const cargarEventos = async () => {
            try {
                const { eventos } = await EventoService.obtenerEventos();
                setEvents(eventos);
            } catch (error) {
                console.error("Error al cargar los eventos:", error);
            }
        };
        cargarEventos();
    }, []);


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
                                <TableCell>{e.ubicacion.localidad}, {e.ubicacion.direccion}</TableCell>
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
    const [localEvent, setLocalEvent] = useState<Evento | null>(null);

    useEffect(() => {
        setLocalEvent(event ? { ...event } : null);
    }, [event]);

    if (!localEvent) return null;

    const handleChange = (field: keyof Evento, value: any) => {
        setLocalEvent(prev => prev ? { ...prev, [field]: value } : prev);
    };

    const handleNestedChange = <K extends keyof Evento>(
        parent: K,
        subfield: keyof Evento[K],
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
            if(localEvent.estado.tipoEstado === event?.estado.tipoEstado){
                onSave(localEvent);
            }else{
                console.log("Se cambio el estado del evento")
                const eventWithNewState = {
                    ...localEvent,
                    estado:{
                        id: localEvent.id,
                        tipoEstado: localEvent.estado.tipoEstado,
                        fechaCambio: new Date().toISOString()
                    }
                }
                onSave(eventWithNewState)
            }
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
                    type="datetime-local"
                    fullWidth
                    margin="normal"
                    value={formatDateForInput(localEvent.fecha)}
                    onChange={e => handleChange("fecha", e.target.value)}
                />
                <TextField
                    label="Hora de inicio"
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
                    label="Latitud"
                    fullWidth
                    margin="normal"
                    value={localEvent.ubicacion.latitud}
                    onChange={e => handleNestedChange("ubicacion", "latitud", e.target.value)}
                />
                <TextField
                    label="Longitud"
                    fullWidth
                    margin="normal"
                    value={localEvent.ubicacion.longitud}
                    onChange={e => handleNestedChange("ubicacion", "longitud", e.target.value)}
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
                    label="Precio"
                    type="number"
                    fullWidth
                    margin="normal"
                    value={localEvent.precio.cantidad}
                    onChange={e => handleNestedChange("precio", "cantidad", parseFloat(e.target.value))}
                />

                {/* Estado */}
                <FormControl fullWidth margin="normal">
                    <InputLabel>Estado</InputLabel>
                    <Select
                        value={localEvent.estado.tipoEstado}
                        label="Estado"
                        onChange={e => handleNestedChange("estado", "tipoEstado", e.target.value)}
                    >
                        <MenuItem value="CONFIRMADO">Confirmado</MenuItem>
                        <MenuItem value="PENDENTE">Pendiente</MenuItem>
                        <MenuItem value="CANCELADO">Cancelado</MenuItem>
                        <MenuItem value="NO_ACEPTA_INSCRIPCIONES">Incripciones cerradas</MenuItem>
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
    const [inscripciones, setInscripciones] = useState<Inscripcion[] | null>(null)
    useEffect(() => {
        const getWaitlist = async ()=>{
            try{
                const inscripciones = await EventoService.obtenerWaitlistDeEvento(event)
                setInscripciones(inscripciones)
            }catch (e){
                console.log(e)
            }
        }
        if(event){
            getWaitlist()
        }
    }, [event]);

    if (!event) return null;

    return (
        <Dialog open={!!event} onClose={onClose} maxWidth="sm" fullWidth>
            <DialogTitle>Lista de espera de {event.titulo}</DialogTitle>
            <DialogContent>
                {inscripciones === null ? (
                    <p>Cargando...</p>
                ) : inscripciones.length === 0 ? (
                    <p>No hay inscripciones en lista de espera.</p>
                ) : (
                    <ul>
                        {inscripciones.map((inscripcion) => (
                            <li key={inscripcion.id}>
                                {inscripcion.participante.nombre} {inscripcion.participante.apellido} —
                                Registrado: {new Date(inscripcion.fechaRegistro).toLocaleDateString()}
                            </li>
                        ))}
                    </ul>
                )}
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose}>Cerrar</Button>
            </DialogActions>
        </Dialog>
    );
}