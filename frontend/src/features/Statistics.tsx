import { useState } from 'react'
import DatePicker from 'react-datepicker'
import 'react-datepicker/dist/react-datepicker.css'
import StatCard from '../components/StatCard.tsx'
import estadisticasService from '../services/estadisticasService.ts'
import {type EstadisticasUsoDTO, TipoEstadistica, type EstadisticasParams } from '../types/estadisticas.ts'

function Statistics() {
    const [stats, setStats] = useState<EstadisticasUsoDTO | null>(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [fechaDesde, setFechaDesde] = useState<Date | null>(null);
    const [fechaHasta, setFechaHasta] = useState<Date | null>(null);
    const [estadisticasSeleccionadas, setEstadisticasSeleccionadas] = useState<TipoEstadistica[]>([]);
    const [mostrarFiltros, setMostrarFiltros] = useState(true);

    const fetchStats = async () => {
        try {
            setLoading(true);
            setError(null);

            const params: EstadisticasParams = {
                ...(fechaDesde && { fechaDesde: fechaDesde.toISOString().split('T')[0] }),
                ...(fechaHasta && { fechaHasta: fechaHasta.toISOString().split('T')[0] }),
                ...(estadisticasSeleccionadas.length > 0 && { estadisticas: estadisticasSeleccionadas })
            };

            const data = estadisticasSeleccionadas.length > 0
                ? await estadisticasService.obtenerEstadisticasPersonalizadas(params)
                : await estadisticasService.obtenerEstadisticasCompletas(params);

            setStats(data);

        } catch (err) {
            setError('No se pudieron cargar las estadísticas');
            console.error('Error:', err);
        } finally {
            setLoading(false);
        }
    };


    const handleEstadisticaChange = (estadistica: TipoEstadistica) => {
        setEstadisticasSeleccionadas(prev =>
            prev.includes(estadistica)
                ? prev.filter(e => e !== estadistica)
                : [...prev, estadistica]
        );
    };

    const limpiarFiltros = () => {
        setFechaDesde(null);
        setFechaHasta(null);
        setEstadisticasSeleccionadas([]);
        fetchStats();
    };

    const formatNumber = (value: number | null) => {
        if (value === null) return 'N/A';
        return value.toLocaleString('es-AR');
    };

    const formatPercentage = (value: number | null) => {
        if (value === null) return 'N/A';
        return `${value.toFixed(1)}%`;
    };

    return (
        <div className="min-vh-100">
            <div className="container py-5">

                {/* HEADER LIMPIO */}
                <div className="row mb-5">
                    <div className="col-lg-8">
                        <h1 className="display-4 fw-bold text-dark mb-3">
                            📊 Dashboard Analytics
                        </h1>
                        <p className="lead text-muted">
                            Métricas clave de tu plataforma actualizadas en tiempo real
                        </p>
                    </div>

                    <div className="col-lg-4 d-flex align-items-center justify-content-lg-end gap-2">
                        <button
                            className="btn btn-outline-secondary px-3 py-2"
                            onClick={() => setMostrarFiltros(!mostrarFiltros)}
                            style={{ borderRadius: '12px' }}
                        >
                            ⚙️ Filtros
                        </button>
                        <button
                            className="btn btn-primary px-4 py-2 d-flex align-items-center"
                            onClick={fetchStats}
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

                {/* PANEL DE FILTROS */}
                {mostrarFiltros && (
                    <div className="row mb-4">
                        <div className="col-12">
                            <div className="card border-0 shadow-sm" style={{ borderRadius: '12px' }}>
                                <div className="card-body p-4">
                                    <h5 className="card-title mb-3">🔍 Filtros de Estadísticas</h5>

                                    {/* Filtros de fecha */}
                                    <div className="row mb-3">
                                        <div className="col-md-6 mb-2">
                                            <label className="form-label fw-semibold">📅 Fecha desde:</label>
                                            <DatePicker
                                                selected={fechaDesde}
                                                onChange={(date: Date | null) => setFechaDesde(date)}
                                                dateFormat="dd/MM/yyyy"
                                                placeholderText="Seleccionar fecha de inicio"
                                                className="form-control"
                                                maxDate={fechaHasta || undefined}
                                                showMonthDropdown
                                                showYearDropdown
                                                dropdownMode="select"
                                                isClearable
                                            />
                                        </div>
                                        <div className="col-md-6 mb-2">
                                            <label className="form-label fw-semibold">📅 Fecha hasta:</label>
                                            <DatePicker
                                                selected={fechaHasta}
                                                onChange={(date: Date | null) => setFechaHasta(date)}
                                                dateFormat="dd/MM/yyyy"
                                                placeholderText="Seleccionar fecha de fin"
                                                className="form-control"
                                                minDate={fechaDesde || undefined}
                                                maxDate={new Date()}
                                                showMonthDropdown
                                                showYearDropdown
                                                dropdownMode="select"
                                                isClearable
                                            />
                                        </div>
                                    </div>

                                    {/* Selector de estadísticas */}
                                    <div className="mb-3">
                                        <label className="form-label fw-semibold">Estadísticas a mostrar (dejar vacío para todas):</label>
                                        <div className="row">
                                            {Object.values(TipoEstadistica).map((estadistica) => (
                                                <div key={estadistica} className="col-lg-3 col-md-4 col-sm-6 mb-2">
                                                    <div className="form-check">
                                                        <input
                                                            className="form-check-input check-filtro"
                                                            type="checkbox"
                                                            id={estadistica}
                                                            checked={estadisticasSeleccionadas.includes(estadistica)}
                                                            onChange={() => handleEstadisticaChange(estadistica)}
                                                        />
                                                        <label className="form-check-label" htmlFor={estadistica}>
                                                            {estadistica.replace(/_/g, ' ').toLowerCase()}
                                                        </label>
                                                    </div>
                                                </div>
                                            ))}
                                        </div>
                                    </div>

                                    {/* Botones de acción */}
                                    <div className="d-flex gap-2">
                                        <button
                                            className="btn btn-success px-3"
                                            onClick={fetchStats}
                                            disabled={loading}
                                            style={{ borderRadius: '8px' }}
                                        >
                                            ✅ Consultar
                                        </button>
                                        <button
                                            className="btn btn-outline-secondary px-3"
                                            onClick={limpiarFiltros}
                                            style={{ borderRadius: '8px' }}
                                        >
                                            🗑️ Limpiar
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                )}

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

                {/* GRID DE CARDS LIMPIAS */}
                <div className="row g-4">

                    {(!estadisticasSeleccionadas.length || estadisticasSeleccionadas.includes(TipoEstadistica.CANTIDAD_EVENTOS)) && (
                        <div className="col-xl-3 col-lg-6 col-md-6">
                            <StatCard
                                title="Total Eventos"
                                value={formatNumber(stats?.cantidad_eventos || null)}
                                description="Cantidad total de eventos registrados en el sistema durante el período seleccionado"
                                emoji="📅"
                                color="blue"
                                loading={loading}
                            />
                        </div>
                    )}

                    {(!estadisticasSeleccionadas.length || estadisticasSeleccionadas.includes(TipoEstadistica.CANTIDAD_EVENTOS_ACTIVOS)) && (
                        <div className="col-xl-3 col-lg-6 col-md-6">
                            <StatCard
                                title="Eventos Activos"
                                value={formatNumber(stats?.cantidad_eventos_activos || null)}
                                description="Eventos confirmados y disponibles para inscripciones durante el período"
                                emoji="⚡"
                                color="green"
                                loading={loading}
                            />
                        </div>
                    )}

                    {(!estadisticasSeleccionadas.length || estadisticasSeleccionadas.includes(TipoEstadistica.CANTIDAD_INSCRIPCIONES_TOTALES)) && (
                        <div className="col-xl-3 col-lg-6 col-md-6">
                            <StatCard
                                title="Inscripciones Totales"
                                value={formatNumber(stats?.cantidad_inscripciones_totales || null)}
                                description="Total de inscripciones realizadas en todos los eventos durante el período"
                                emoji="📝"
                                color="purple"
                                loading={loading}
                            />
                        </div>
                    )}

                    {(!estadisticasSeleccionadas.length || estadisticasSeleccionadas.includes(TipoEstadistica.CANTIDAD_INSCRIPCIONES_CONFIRMADAS)) && (
                        <div className="col-xl-3 col-lg-6 col-md-6">
                            <StatCard
                                title="Inscripciones Confirmadas"
                                value={formatNumber(stats?.cantidad_inscripciones_confirmadas || null)}
                                description="Inscripciones que han sido aceptadas y confirmadas exitosamente"
                                emoji="✅"
                                color="green"
                                loading={loading}
                            />
                        </div>
                    )}

                    {(!estadisticasSeleccionadas.length || estadisticasSeleccionadas.includes(TipoEstadistica.CANTIDAD_INSCRIPCIONES_WAITLIST)) && (
                        <div className="col-xl-3 col-lg-6 col-md-6">
                            <StatCard
                                title="En Lista de Espera"
                                value={formatNumber(stats?.cantidad_inscripciones_waitlist || null)}
                                description="Inscripciones que están actualmente en lista de espera"
                                emoji="⏳"
                                color="orange"
                                loading={loading}
                            />
                        </div>
                    )}

                    {(!estadisticasSeleccionadas.length || estadisticasSeleccionadas.includes(TipoEstadistica.TASA_CONVERSION_WAITLIST)) && (
                        <div className="col-xl-3 col-lg-6 col-md-6">
                            <StatCard
                                title="Tasa de Conversión"
                                value={formatPercentage(stats?.tasa_conversion_waitlist || null)}
                                description="Porcentaje de inscripciones confirmadas respecto al total"
                                emoji="📊"
                                color="blue"
                                loading={loading}
                            />
                        </div>
                    )}

                    {(!estadisticasSeleccionadas.length || estadisticasSeleccionadas.includes(TipoEstadistica.EVENTO_MAS_POPULAR)) && (
                        <div className="col-xl-3 col-lg-6 col-md-6">
                            <StatCard
                                title="Evento Más Popular"
                                value={stats?.evento_mas_popular || 'N/A'}
                                description="El evento con mayor cantidad de inscripciones en el período"
                                emoji="🏆"
                                color="red"
                                loading={loading}
                            />
                        </div>
                    )}

                    {(!estadisticasSeleccionadas.length || estadisticasSeleccionadas.includes(TipoEstadistica.PROMEDIO_INSCRIPCIONES_POR_EVENTO)) && (
                        <div className="col-xl-3 col-lg-6 col-md-6">
                            <StatCard
                                title="Promedio Inscripciones"
                                value={formatNumber(stats?.promedio_inscripciones_por_evento || null)}
                                description="Promedio de inscripciones por evento durante el período seleccionado"
                                emoji="📈"
                                color="purple"
                                loading={loading}
                            />
                        </div>
                    )}
                </div>

                {/* FOOTER INFO */}
                <div className="row mt-5">
                    <div className="col-12">
                        <div className="text-center">
                            <p className="text-muted">
                                <small>
                                    📡 Datos sincronizados •
                                    Última actualización: {stats ? new Date().toLocaleTimeString('es-AR') : 'Pendiente'}
                                </small>
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Statistics;