import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import InscripcionCard from '../components/InscripcionCard';
import inscripcionesService from '../services/inscripcionesParticipanteService.ts';
import authService from '../services/authService.ts';
import type { Inscripcion } from '../types/inscripciones';
import { Rol } from '../types/auth';
import DetallesEvento from '../components/EventDetails';
import { EventoService } from '../services/eventoService';
import type { Evento } from '../types/evento';
import { isEventInPast } from '../utils/eventDate';

function UserLanding() {
    const navigate = useNavigate();
    const [inscripciones, setInscripciones] = useState<Inscripcion[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [currentUser] = useState(authService.getCurrentUser());
    const [participanteId, setParticipanteId] = useState<string>(authService.getActorId()?.toString() || '');
    const [detalleEvento, setDetalleEvento] = useState<Evento | null>(null);
    const [detalleCargando, setDetalleCargando] = useState(false);



    const fetchInscripciones = async () => {
        try {
            setLoading(true);
            setError(null);

            const data = await inscripcionesService.obtenerInscripcionesDeParticipante(participanteId);

            // Filtrar duplicados por ID, quedándose con la más reciente
            const inscripcionesFiltradas = data.reduce((acc: Inscripcion[], current: Inscripcion) => {
                const existingIndex = acc.findIndex(item => item.id === current.id);

                if (existingIndex === -1) {
                    acc.push(current);
                } else {
                    const existing = acc[existingIndex];
                    const currentDate = new Date(current.estado.fechaDeCambio);
                    const existingDate = new Date(existing.estado.fechaDeCambio);

                    if (currentDate > existingDate) {
                        acc[existingIndex] = current;
                    }
                }

                return acc;
            }, []);

            // Definir orden de estados
            const ordenEstados: Record<string, number> = {
                'ACEPTADA': 1,
                'PENDIENTE': 2,
                'CANCELADA': 3
            };

            // Ordenar inscripciones según el estado
            const inscripcionesOrdenadas = [...inscripcionesFiltradas].sort((a, b) => {
                const estadoA = ordenEstados[a.estado.tipoEstado] || 99;
                const estadoB = ordenEstados[b.estado.tipoEstado] || 99;
                return estadoA - estadoB;
            });

            setInscripciones(inscripcionesOrdenadas);

        } catch (err) {
            setError('No se pudieron cargar las inscripciones');
            console.error('Error:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleVerDetalle = async (eventoId: string) => {
        if (!eventoId) {
            setError('No se encontró el evento seleccionado.');
            return;
        }

        setDetalleCargando(true);
        setError(null);

        try {
            const eventoCompleto = await EventoService.obtenerEventoPorId(eventoId);

            if (!eventoCompleto) {
                setError('No se pudieron cargar los detalles del evento.');
                return;
            }

            setDetalleEvento(eventoCompleto);
        } catch (err) {
            console.error('Error al cargar detalle de evento:', err);
            setError('Ocurrió un error al cargar los detalles. Intenta nuevamente.');
        } finally {
            setDetalleCargando(false);
        }
    };

    useEffect(() => {
        // Si es organizador, redirigir a crear eventos
        if (currentUser?.rol === Rol.ROLE_ORGANIZER) {
            navigate('/crear-evento');
            return;
        }

        // Actualizar participanteId cuando cambie el usuario
        if (currentUser?.actorId) {
            setParticipanteId(currentUser.actorId.toString());
        }
    }, [currentUser, navigate]);

    useEffect(() => {
        if (participanteId && currentUser?.rol !== Rol.ROLE_ORGANIZER) {
            fetchInscripciones();
        }
    }, [participanteId]);

    const esEventoFinalizado = (inscripcion: Inscripcion) => isEventInPast(inscripcion.evento);

    const inscripcionesActivas = inscripciones.filter(i =>
        (i.estado.tipoEstado === 'ACEPTADA' || i.estado.tipoEstado === 'PENDIENTE') && !esEventoFinalizado(i)
    );

    if (detalleCargando) {
        return (
            <div className="min-vh-100 d-flex flex-column justify-content-center align-items-center">
                <div className="spinner-border text-primary mb-3" role="status">
                    <span className="visually-hidden">Cargando detalles del evento...</span>
                </div>
                <p className="text-muted">Cargando detalles del evento...</p>
            </div>
        );
    }

    if (detalleEvento) {
        return (
            <DetallesEvento
                evento={detalleEvento}
                onVolver={() => setDetalleEvento(null)}
            />
        );
    }

    return (
        <div className="min-vh-100">
            <div className="container py-5">

                {/* HEADER */}
                <div className="row mb-5">
                    <div className="col-lg-6">
                        <h1 className="display-4 fw-bold text-dark mb-3">
                            👋 Bienvenido {currentUser?.username}
                        </h1>
                        <p className="lead text-muted">
                            Aquí puedes ver todos los eventos donde estás inscrito
                        </p>
                    </div>

                    <div className="col-lg-6 d-flex align-items-center justify-content-lg-end gap-2">
                        {/* Botón de Estadísticas solo para Admin */}
                        {currentUser?.rol === Rol.ROLE_ADMIN && (
                            <button
                                className="btn btn-success px-4 py-2 d-flex align-items-center"
                                onClick={() => navigate('/statistics')}
                                style={{ borderRadius: '12px' }}
                            >
                                📊 Estadísticas
                            </button>
                        )}

                        <button
                            className="btn btn-primary px-4 py-2 d-flex align-items-center"
                            onClick={fetchInscripciones}
                            disabled={loading}
                            style={{ borderRadius: '12px' }}
                        >
                            {loading ? (
                                <>
                                    <span className="spinner-border spinner-border-sm me-2"></span>
                                    Actualizando...
                                </>
                            ) : (
                                <>
                                    🔄 Actualizar
                                </>
                            )}
                        </button>
                    </div>
                </div>

                {/* ERROR HANDLING */}
                {error && (
                    <div className="row mb-4">
                        <div className="col-12">
                            <div className="alert alert-danger border-0 shadow-sm" style={{ borderRadius: '12px' }}>
                                <div className="d-flex align-items-center">
                                    <span className="me-3">⚠️</span>
                                    <div>
                                        <strong>Error de conexión</strong>
                                        <div className="small mt-1">{error}</div>
                                    </div>
                                    <button
                                        type="button"
                                        className="btn-close ms-auto"
                                        onClick={() => setError(null)}
                                    ></button>
                                </div>
                            </div>
                        </div>
                    </div>
                )}

                {/* ESTADÍSTICAS RÁPIDAS */}
                <div className="row mb-5">
                    <div className="col-md-4">
                        <div className="card border-0 bg-primary text-white" style={{ borderRadius: '12px' }}>
                            <div className="card-body text-center">
                                <h3 className="fw-bold mb-1">{inscripciones.length}</h3>
                                <p className="mb-0">Total Inscripciones</p>
                            </div>
                        </div>
                    </div>
                    <div className="col-md-4">
                        <div className="card border-0 bg-success text-white" style={{ borderRadius: '12px' }}>
                            <div className="card-body text-center">
                                <h3 className="fw-bold mb-1">
                                    {inscripciones.filter(i => i.estado.tipoEstado === 'ACEPTADA').length}
                                </h3>
                                <p className="mb-0">Confirmadas</p>
                            </div>
                        </div>
                    </div>
                    <div className="col-md-4">
                        <div className="card border-0 bg-warning text-dark" style={{ borderRadius: '12px' }}>
                            <div className="card-body text-center">
                                <h3 className="fw-bold mb-1">
                                    {inscripciones.filter(i => i.estado.tipoEstado === 'PENDIENTE').length}
                                </h3>
                                <p className="mb-0">En Espera</p>
                            </div>
                        </div>
                    </div>
                </div>

                {/* SECCIÓN DE EVENTOS */}
                <div className="row mb-4">
                    <div className="col-12">
                        <h2 className="h3 fw-bold text-dark mb-0">
                            📅 Mis Eventos
                        </h2>
                        <p className="text-muted">
                            {inscripcionesActivas.length > 0
                                ? `${inscripcionesActivas.length} eventos activos`
                                : 'No tienes eventos activos'
                            }
                        </p>
                    </div>
                </div>

                {/* GRID DE EVENTOS */}
                {loading ? (
                    <div className="text-center py-5">
                        <div className="spinner-border text-primary" role="status">
                            <span className="visually-hidden">Cargando...</span>
                        </div>
                    </div>
                ) : inscripciones.length === 0 ? (
                    <div className="text-center py-5">
                        <div className="mb-3" style={{ fontSize: '3rem' }}>📝</div>
                        <h4 className="text-muted">No tienes inscripciones aún</h4>
                        <p className="text-muted">¡Explora eventos disponibles y regístrate!</p>
                    </div>
                ) : (
                    <div className="row g-4">
                        {inscripciones.map((inscripcion) => (
                            <div key={inscripcion.id} className="col-lg-6 col-xl-4">
                                <InscripcionCard
                                    inscripcion={inscripcion}
                                    onVerDetalle={() => handleVerDetalle(inscripcion.evento.id)}
                                    onInscripcionCancelada={fetchInscripciones}
                                />
                            </div>
                        ))}
                    </div>
                )}

                {/* FOOTER INFO */}
                <div className="row mt-5">
                    <div className="col-12">
                        <div className="text-center">
                            <p className="text-muted">
                                <small>
                                    📡 Datos sincronizados •
                                    Última actualización: {inscripciones.length > 0 ? new Date().toLocaleTimeString('es-AR') : 'Pendiente'}
                                </small>
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default UserLanding;
