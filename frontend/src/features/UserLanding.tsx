import { useState, useEffect } from 'react';
import EventCard from '../components/EventCard';
import inscripcionesService from '../services/inscripcionesParticipanteService.ts';
import type { Inscripcion } from '../types/inscripciones';

function UserLanding() {
    const [inscripciones, setInscripciones] = useState<Inscripcion[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [participanteId, setParticipanteId] = useState<string>('1'); // Por ahora hardcodeado

    const fetchInscripciones = async () => {
        try {
            setLoading(true);
            setError(null);

            const data = await inscripcionesService.obtenerInscripcionesDeParticipante(participanteId);
            setInscripciones(data);
        } catch (err) {
            setError('No se pudieron cargar las inscripciones');
            console.error('Error:', err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchInscripciones();
    }, [participanteId]);

    const inscripcionesActivas = inscripciones.filter(i =>
        i.estado.tipoEstado === 'ACEPTADA' || i.estado.tipoEstado === 'WAITLIST'
    );

    return (
        <div className="min-vh-100">
            <div className="container py-5">

                {/* HEADER */}
                <div className="row mb-5">
                    <div className="col-lg-8">
                        <h1 className="display-4 fw-bold text-dark mb-3">
                            👋 Bienvenido
                        </h1>
                        <p className="lead text-muted">
                            Aquí puedes ver todos los eventos donde estás inscrito
                        </p>
                    </div>

                    <div className="col-lg-4 d-flex align-items-center justify-content-lg-end">
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
                                    {inscripciones.filter(i => i.estado.tipoEstado === 'WAITLIST').length}
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
                                <EventCard inscripcion={inscripcion} />
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