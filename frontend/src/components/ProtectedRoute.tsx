import React from 'react';
import { Navigate } from 'react-router-dom';
import authService from '../services/authService';

interface ProtectedRouteProps {
    children: React.ReactNode;
    requireAuth?: boolean;
    allowedRoles?: string[];
    redirectTo?: string;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
    children,
    requireAuth = true,
    allowedRoles = [],
    redirectTo = '/login'
}) => {
    const currentUser = authService.getCurrentUser();

    // Si requiere autenticación y no hay usuario, redirigir al login
    if (requireAuth && !currentUser) {
        return <Navigate to={redirectTo} replace />;
    }

    // Si hay roles específicos permitidos y el usuario no tiene el rol correcto
    if (allowedRoles.length > 0 && currentUser) {
        const hasAllowedRole = allowedRoles.includes(currentUser.rol);
        if (!hasAllowedRole) {
            return (
                <div className="min-vh-100 d-flex align-items-center justify-content-center bg-light">
                    <div className="container">
                        <div className="row justify-content-center">
                            <div className="col-md-8 col-lg-6">
                                <div className="card border-0 shadow-lg" style={{ borderRadius: '16px' }}>
                                    <div className="card-body text-center p-5">
                                        <div className="mb-4" style={{ fontSize: '4rem' }}>🚫</div>
                                        <h2 className="fw-bold text-dark mb-3">Acceso Denegado</h2>
                                        <p className="text-muted mb-4">
                                            No tienes permisos para acceder a esta página.
                                            Tu rol actual es: <strong>{currentUser.rol}</strong>
                                        </p>
                                        <div className="d-flex gap-2 justify-content-center">
                                            <button
                                                className="btn btn-primary px-4"
                                                onClick={() => window.history.back()}
                                                style={{ borderRadius: '8px' }}
                                            >
                                                ← Volver
                                            </button>
                                            <button
                                                className="btn btn-outline-secondary px-4"
                                                onClick={() => window.location.href = '/'}
                                                style={{ borderRadius: '8px' }}
                                            >
                                                🏠 Inicio
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            );
        }
    }

    return <>{children}</>;
};

export default ProtectedRoute;
