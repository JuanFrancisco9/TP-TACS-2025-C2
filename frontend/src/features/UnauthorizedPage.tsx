import React, { useMemo } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import authService, { type UnauthorizedReason, UNAUTHORIZED_RETURN_KEY } from '../services/authService';

type ReasonContent = {
    title: string;
    subtitle: string;
    message: string;
    actionLabel: string;
};

const REASON_COPY: Record<UnauthorizedReason, ReasonContent> = {
    'session-expired': {
        title: 'Tu sesión expiró',
        subtitle: 'Por seguridad te desconectamos automáticamente.',
        message: 'Necesitás iniciar sesión nuevamente para continuar viendo esta sección.',
        actionLabel: 'Iniciar sesión otra vez'
    },
    forbidden: {
        title: 'No tenés permisos para ver esto',
        subtitle: 'Tu cuenta no cuenta con los permisos requeridos para acceder.',
        message: 'Si debés acceder con otro rol, cerrá la sesión actual y volvé a ingresar con la cuenta correcta.',
        actionLabel: 'Cambiar de cuenta'
    },
    unknown: {
        title: 'Acceso restringido',
        subtitle: 'Ocurrió un problema al validar tus permisos.',
        message: 'Intentá iniciar sesión nuevamente o volvé a la página anterior.',
        actionLabel: 'Ir al inicio de sesión'
    }
};

const UnauthorizedPage: React.FC = () => {
    const location = useLocation();
    const navigate = useNavigate();

    const searchParams = useMemo(() => new URLSearchParams(location.search), [location.search]);
    const reason = (searchParams.get('reason') as UnauthorizedReason) ?? 'unknown';
    const from = searchParams.get('from');

    const lastAccessibleRoute = useMemo(() => authService.getLastAccessibleRoute(), [location.key]);
    const storedUnauthorizedFrom = useMemo(() => authService.getStoredUnauthorizedOrigin(), [location.key]);

    const effectiveFrom = useMemo(() => {
        if (lastAccessibleRoute && lastAccessibleRoute !== '/unauthorized') {
            return lastAccessibleRoute;
        }
        if (storedUnauthorizedFrom && storedUnauthorizedFrom !== '/unauthorized') {
            return storedUnauthorizedFrom;
        }
        return null;
    }, [from, lastAccessibleRoute, storedUnauthorizedFrom]);

    const copy = REASON_COPY[reason] ?? REASON_COPY.unknown;
    const canGoBack = Boolean(effectiveFrom);

    const clearStoredFrom = React.useCallback(() => {
        try {
            localStorage.removeItem(UNAUTHORIZED_RETURN_KEY);
        } catch {
            // no-op
        }
    }, []);

    const handleLoginClick = () => {
        const target = effectiveFrom;
        authService.logout();
        clearStoredFrom();
        const navigateState = target ? { from: target } : undefined;
        navigate('/login', { state: navigateState });
    };

    const handleHomeClick = () => {
        clearStoredFrom();
        navigate('/');
    };

    const handleBackClick = () => {
        if (effectiveFrom) {
            const target = effectiveFrom;
            clearStoredFrom();
            navigate(target);
        } else {
            navigate(-1);
        }
    };


    return (
        <div className="min-vh-100 d-flex align-items-center justify-content-center bg-light">
            <div className="container py-5">
                <div className="row justify-content-center">
                    <div className="col-md-9 col-lg-6">
                        <div className="card border-0 shadow-lg" style={{ borderRadius: '18px' }}>
                            <div className="card-body text-center p-5">
                                <h2 className="fw-bold text-dark mb-2">{copy.title}</h2>
                                <p className="text-muted mb-3">{copy.subtitle}</p>
                                <p className="text-secondary mb-4">{copy.message}</p>

                                <div className="d-flex flex-column flex-md-row gap-2 justify-content-center">
                                    <button
                                        className="btn btn-primary px-4"
                                        onClick={handleLoginClick}
                                    >
                                        {copy.actionLabel}
                                    </button>
                                    {canGoBack && (
                                        <button
                                            className="btn btn-outline-secondary px-4"
                                            onClick={handleBackClick}
                                        >
                                            Volver a la página anterior
                                        </button>
                                    )}
                                    <button
                                        className="btn btn-link text-decoration-none"
                                        onClick={handleHomeClick}
                                    >
                                        Ir al inicio
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default UnauthorizedPage;
