import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
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
    const location = useLocation();
    const from = `${location.pathname}${location.search}`;

    // Si requiere autenticación y no hay usuario, redirigir al login
    if (requireAuth && !currentUser) {
        const fallbackTarget = authService.getLastAccessibleRoute() ?? from;
        authService.rememberUnauthorizedOrigin(fallbackTarget);
        return <Navigate to={redirectTo} replace state={{ from }} />;
    }

    // Si hay roles específicos permitidos y el usuario no tiene el rol correcto
    if (allowedRoles.length > 0 && currentUser) {
        const hasAllowedRole = allowedRoles.includes(currentUser.rol);
        if (!hasAllowedRole) {
            const params = new URLSearchParams({ reason: 'forbidden' });
            const fallbackTarget = authService.getLastAccessibleRoute() ?? from;
            if (fallbackTarget) {
                params.set('from', fallbackTarget);
            }
            authService.rememberUnauthorizedOrigin(fallbackTarget);
            return <Navigate to={`/unauthorized?${params.toString()}`} replace />;
        }
    }

    return <>{children}</>;
};

export default ProtectedRoute;
