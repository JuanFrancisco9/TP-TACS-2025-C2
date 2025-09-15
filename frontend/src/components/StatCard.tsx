interface StatCardProps {
    title: string;
    value: string | number;
    description?: string;
    emoji: string;
    color?: 'red' | 'blue' | 'green' | 'orange' | 'purple';
    loading?: boolean;
}

function StatCard({
                      title,
                      value,
                      description,
                      emoji,
                      color = 'red',
                      loading = false
                  }: StatCardProps) {

    // Colores corregidos para cada tipo
    const colorClasses = {
        red: {
            border: 'border-danger',
            accent: 'bg-danger',
            text: 'text-danger'
        },
        blue: {
            border: 'border-primary',
            accent: 'bg-primary',
            text: 'text-primary'
        },
        green: {
            border: 'border-success',
            accent: 'bg-success',
            text: 'text-success'
        },
        orange: {
            border: 'border-warning',
            accent: 'bg-warning',
            text: 'text-warning'
        },
        purple: {
            border: 'border-info',
            accent: 'bg-info',
            text: 'text-info'
        }
    };

    const currentColor = colorClasses[color];

    return (
        <div
            className="card h-100 shadow-sm border-0 position-relative overflow-hidden"
            style={{
                borderRadius: '16px',
                transition: 'transform 0.2s ease, box-shadow 0.2s ease'
            }}
            onMouseEnter={(e) => {
                e.currentTarget.style.transform = 'translateY(-2px)';
                e.currentTarget.style.boxShadow = '0 8px 25px rgba(0,0,0,0.15)';
            }}
            onMouseLeave={(e) => {
                e.currentTarget.style.transform = 'translateY(0)';
                e.currentTarget.style.boxShadow = '';
            }}
        >
            {/* Acento de color en la parte superior */}
            <div
                className={`${currentColor.accent}`}
                style={{ height: '4px', width: '100%' }}
            ></div>

            <div className="card-body p-4 text-center d-flex flex-column h-100">

                {/* EMOJI */}
                <div className="mb-3">
                    <span style={{
                        fontSize: '2.5rem',
                        display: 'block',
                        lineHeight: '1'
                    }}>
                        {emoji}
                    </span>
                </div>

                {/* TÍTULO */}
                <h6 className="fw-semibold text-uppercase text-muted mb-2"
                    style={{
                        fontSize: '0.75rem',
                        letterSpacing: '0.5px'
                    }}>
                    {title}
                </h6>

                {/* VALOR PRINCIPAL */}
                <div className="mb-3">
                    {loading ? (
                        <div className="d-flex justify-content-center align-items-center" style={{ height: '60px' }}>
                            <div className={`spinner-border ${currentColor.text}`} style={{ width: '2rem', height: '2rem' }}></div>
                        </div>
                    ) : (
                        <div
                            className="fw-bold text-dark"
                            style={{
                                fontSize: typeof value === 'string' && value.length > 20 ? '1.4rem' : '2.2rem',
                                lineHeight: '1.1',
                                whiteSpace: typeof value === 'string' && value.length > 15 ? 'normal' : 'nowrap',
                                fontFamily: 'system-ui, -apple-system, sans-serif',
                                wordBreak: 'break-word',
                                hyphens: 'auto',
                                textAlign: 'center',
                                maxHeight: '80px',
                                overflow: 'hidden',
                                display: '-webkit-box',
                                WebkitLineClamp: 3,
                                WebkitBoxOrient: 'vertical'
                            }}
                            title={typeof value === 'string' ? value : undefined}
                        >
                            {typeof value === 'number' ? value.toLocaleString('es-AR') : value}
                        </div>
                    )}
                </div>

                {/* DESCRIPCIÓN - Ocupará el espacio restante */}
                {description && (
                    <div className="mt-auto">
                        <p className="text-muted mb-0"
                           style={{
                               fontSize: '0.85rem',
                               lineHeight: '1.4',
                               opacity: '0.8'
                           }}>
                            {description}
                        </p>
                    </div>
                )}
            </div>
        </div>
    );
}

export default StatCard;