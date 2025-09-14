import type { Inscripcion } from '../types/inscripciones';

interface EventCardProps {
    inscripcion: Inscripcion;
}

function EventCard({ inscripcion }: EventCardProps) {
    const getEstadoBadgeClass = (estado: string) => {
        switch (estado) {
            case 'ACEPTADA':
                return 'badge bg-success';
            case 'WAITLIST':
                return 'badge bg-warning text-dark';
            case 'RECHAZADA':
                return 'badge bg-danger';
            default:
                return 'badge bg-secondary';
        }
    };

    const formatFecha = (fechaString: string) => {
        try {
            return new Date(fechaString).toLocaleDateString('es-AR');
        } catch {
            return fechaString;
        }
    };

    return (
        <div className="card border-0 shadow-sm h-100" style={{ borderRadius: '12px' }}>
            <div className="card-body p-4">
                <div className="d-flex justify-content-between align-items-start mb-3">
                    <h5 className="card-title mb-0 fw-bold text-dark">
                        {inscripcion.evento.titulo}
                    </h5>
                    <span className={getEstadoBadgeClass(inscripcion.estado.tipoEstado)}>
                        {inscripcion.estado.tipoEstado}
                    </span>
                </div>

                <p className="card-text text-muted mb-3">
                    {inscripcion.evento.descripcion}
                </p>

                <div className="row g-2 text-sm">
                    <div className="col-6">
                        <div className="d-flex align-items-center">
                            <span className="me-2">📅</span>
                            <small className="text-muted">
                                Inscrito: {formatFecha(inscripcion.fechaRegistro)}
                            </small>
                        </div>
                    </div>
                    <div className="col-6">
                        <div className="d-flex align-items-center">
                            <span className="me-2">🔄</span>
                            <small className="text-muted">
                                Estado: {formatFecha(inscripcion.estado.fechaCambio)}
                            </small>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default EventCard;