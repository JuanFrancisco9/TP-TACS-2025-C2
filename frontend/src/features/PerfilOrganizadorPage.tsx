import {useEffect, useState} from "react";
import {
    Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
    Paper, Button, Dialog, DialogTitle, DialogContent, DialogActions,
    TextField, Typography, Select, MenuItem, InputLabel, CircularProgress,
    Tooltip
} from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import ListAltIcon from "@mui/icons-material/ListAlt";
import BlockIcon from '@mui/icons-material/Block';
import VisibilityIcon from "@mui/icons-material/Visibility";
import GroupIcon from "@mui/icons-material/Group";
import type {Inscripcion} from "../types/inscripciones.ts";
import FormControl from "@mui/material/FormControl";
import { EventoService } from '../services/eventoService';
import { formatDateForInput } from "../utils/formatFecha.ts";
import authService from "../services/authService.ts";
import type { Evento } from "../types/evento.ts";
import type {Participante, Usuario} from "../types/auth.ts";
import DetallesEvento from '../components/EventDetails';
import { isEventInPast } from '../utils/eventDate';

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
    const [user, setUser] = useState<Usuario | null>(null);
    const [loading, setLoading] = useState(true);

    // ---- estados de modal ----
    const [editEvent, setEditEvent] = useState<Evento | null>(null);
    const [viewEvent, setViewEvent] = useState<Evento | null>(null);
    const [waitlistEvent, setWaitlistEvent] = useState<Evento | null>(null);
    const [detailEvent, setDetailEvent] = useState<Evento | null>(null);

    // ---- handlers ----
    const handleSaveEdit = async (updatedEvent: Evento) => {
        try {
            const eventoActualizado = await EventoService.actualizarEvento(updatedEvent.id, updatedEvent);
            setEvents(prev =>
                prev.map(ev => ev.id === eventoActualizado.id ? eventoActualizado : ev)
            );
            setEditEvent(null);
        } catch (error) {
            console.error(error);
        }
    };

    const handelCloseInscriptions = async (updatedEvent: Evento) => {
        try {
            const eventoActualizado = await EventoService.actualizarEstadoEvento(updatedEvent, "NO_ACEPTA_INSCRIPCIONES");
            setEvents(prev =>
                prev.map(ev => ev.id === eventoActualizado.id ? eventoActualizado : ev)
            );
            setEditEvent(null);
        } catch (error) {
            console.error(error);
        }
    };

    // ---- cargar perfil y eventos ----
    useEffect(() => {
        const loadData = async () => {
            try {
                const currentUser = authService.getCurrentUser()
                setUser(currentUser);

                if (currentUser?.id) {
                    const eventos = await EventoService.obtenerEventosParaOrganizador(currentUser.actorId);
                    setEvents(eventos);
                }
            } catch (error) {
                console.error("Error al cargar los eventos:", error);
            } finally {
                setLoading(false);
            }
        };

        loadData();
    }, []);

    // ---- loading ----
    if (loading) {
        return (
            <div style={{ padding: "2rem", textAlign: "center" }}>
                <CircularProgress />
                <Typography>Cargando perfil...</Typography>
            </div>
        );
    }

    if (detailEvent) {
        return (
            <DetallesEvento
                evento={detailEvent}
                onVolver={() => setDetailEvent(null)}
            />
        );
    }

    // ---- UI principal ----
    return (
        <div style={{ padding: "2rem" }}>
            <Typography variant="h4" gutterBottom>
                Bienvenido: {user?.username}
            </Typography>

            {events.length === 0 ? (
                <Typography variant="h6" color="textSecondary" align="center" sx={{ mt: 4 }}>
                    No tenés ningún evento por el momento
                </Typography>
            ) : (
                <TableContainer component={Paper}>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell>Imagen</TableCell>
                                <TableCell>Título</TableCell>
                                <TableCell>Fecha</TableCell>
                                <TableCell>Ubicación</TableCell>
                                <TableCell>Estado</TableCell>
                                <TableCell>Acciones</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {events.map((e) => {
                                const imageSrc = e.imagenUrl ?? e.imagen ?? `/logo.PNG`;
                                const eventoFinalizado = isEventInPast(e) ||
                                    e.estado?.tipoEstado === 'NO_ACEPTA_INSCRIPCIONES' ||
                                    e.estado?.tipoEstado === 'CANCELADO';
                                return (
                                <TableRow
                                    key={e.id}
                                    hover
                                    sx={{ cursor: 'pointer' }}
                                >
                                    <TableCell>
                                        <img
                                            src={imageSrc}
                                            alt={e.titulo}
                                            style={{ width: 96, height: 54, objectFit: 'cover', borderRadius: 8 }}
                                            onError={(event) => {
                                                (event.currentTarget as HTMLImageElement).src = '/logo.PNG';
                                            }}
                                        />
                                    </TableCell>
                                    <TableCell>{e.titulo}</TableCell>
                                    <TableCell>
                                        {new Date(e.fecha).toLocaleString('es-AR', {
                                            day: '2-digit',
                                            month: '2-digit',
                                            year: 'numeric',
                                            hour: '2-digit',
                                            minute: '2-digit',
                                        }).replace(',', ' -')}
                                    </TableCell>
                                    <TableCell>
                                        {e.ubicacion.esVirtual
                                            ? 'Virtual'
                                            : [e.ubicacion.provincia, e.ubicacion.localidad, e.ubicacion.direccion]
                                                .filter(Boolean)
                                                .join(', ')}
                                    </TableCell>
                                    <TableCell>
                                        {e.estado.tipoEstado === "NO_ACEPTA_INSCRIPCIONES"
                                            ? "INSCRIPCIONES CERRADAS"
                                            : e.estado.tipoEstado}
                                    </TableCell>
                                    <TableCell>
                                        {!eventoFinalizado && (
                                            <Tooltip title="Editar" arrow>
                                                <Button onClick={(ev) => { ev.stopPropagation(); setEditEvent(e); }} aria-label="Editar evento">
                                                    <EditIcon />
                                                </Button>
                                            </Tooltip>
                                        )}
                                        <Tooltip title="Ver detalle" arrow>
                                            <Button onClick={(ev) => { ev.stopPropagation(); setDetailEvent(e); }} aria-label="Ver detalle">
                                                <VisibilityIcon />
                                            </Button>
                                        </Tooltip>
                                        <Tooltip title="Ver lista de inscriptos" arrow>
                                            <Button onClick={(ev) => { ev.stopPropagation(); setViewEvent(e); }} aria-label="Ver lista de inscriptos">
                                                <GroupIcon />
                                            </Button>
                                        </Tooltip>
                                        <Tooltip title="Ver waitlist" arrow>
                                            <Button onClick={(ev) => { ev.stopPropagation(); setWaitlistEvent(e); }} aria-label="Ver waitlist">
                                                <ListAltIcon />
                                            </Button>
                                        </Tooltip>
                                        <Tooltip title="Cerrar inscripciones" arrow>
                                            <Button onClick={(ev) => { ev.stopPropagation(); handelCloseInscriptions(e); }} aria-label="Cerrar inscripciones">
                                                <BlockIcon />
                                            </Button>
                                        </Tooltip>
                                    </TableCell>
                                </TableRow>
                            );})}
                        </TableBody>
                    </Table>
                </TableContainer>
            )}

            <EditEvent event={editEvent} onClose={() => setEditEvent(null)} onSave={handleSaveEdit} />
            <VerInscriptos event={viewEvent} onClose={() => setViewEvent(null)} />
            <WaitList event={waitlistEvent} onClose={() => setWaitlistEvent(null)} />
        </div>

    );
}

const EditEvent = ({ event, onClose, onSave }: EditEvent) => {
    const [localEvent, setLocalEvent] = useState<Evento | null>(null);

    useEffect(() => {
        if (!event) {
            setLocalEvent(null);
            return;
        }

        setLocalEvent({
            ...event,
            etiquetas: event.etiquetas ?? []
        });
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
                    value={localEvent.ubicacion.latitud ?? ''}
                    onChange={e => handleNestedChange("ubicacion", "latitud", e.target.value)}
                />
                <TextField
                    label="Longitud"
                    fullWidth
                    margin="normal"
                    value={localEvent.ubicacion.longitud ?? ''}
                    onChange={e => handleNestedChange("ubicacion", "longitud", e.target.value)}
                />
                <TextField
                    label="Dirección"
                    fullWidth
                    margin="normal"
                    value={localEvent.ubicacion.direccion ?? ''}
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
                        <MenuItem value="PENDIENTE">Pendiente</MenuItem>
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
                    value={(localEvent.etiquetas ?? []).join(", ")}
                    onChange={e =>
                        handleChange(
                            "etiquetas",
                            e.target.value
                                .split(",")
                                .map(t => t.trim())
                                .filter(Boolean)
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
    const [participantes, setParticipantes] = useState<Participante[] | null>(null)
    useEffect(() => {
        const getParticipantes = async ()=>{
            try{
                const partcipantes = await EventoService.obtenerParticipantesDeEvento(event)
                setParticipantes(partcipantes)
            }catch (e){
                console.error(e)
            }
        }
        if(event){
            getParticipantes()
        }
    }, [event]);

    if (!event) return null;

    return (
        <Dialog open={!!event} onClose={onClose} maxWidth="sm" fullWidth>
            <DialogTitle>Inscriptos en {event.titulo}</DialogTitle>
            <DialogContent>
                {participantes === null ? (
                    <p>Cargando...</p>
                ) : participantes.length === 0 ? (
                    <p>No hay participantes inscriptos.</p>
                ) : (
                    <ul>
                        {participantes.map((p) => (
                            <li key={p.dni}>
                                {p.nombre} {p.apellido}
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

const WaitList = ({event, onClose}:ViewModal) =>{
    const [inscripciones, setInscripciones] = useState<Inscripcion[] | null>(null)
    useEffect(() => {
        const getWaitlist = async ()=>{
            try{
                const inscripciones = await EventoService.obtenerWaitlistDeEvento(event)
                setInscripciones(inscripciones)
            }catch (e){
                console.error(e)
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
