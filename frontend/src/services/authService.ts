import type { Usuario, LoginRequest } from '../types/auth';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export type UnauthorizedReason = 'session-expired' | 'forbidden' | 'unknown';

export const UNAUTHORIZED_RETURN_KEY = 'auth:lastUnauthorizedFrom';
export const LAST_ACCESSIBLE_ROUTE_KEY = 'auth:lastAccessibleRoute';
export const CURRENT_ROUTE_KEY = 'auth:currentRoute';

type HandleUnauthorizedOptions = {
    logout?: boolean;
    redirectTo?: string | null;
};

const isValidFallback = (value: unknown): value is string =>
    typeof value === 'string' &&
    value.trim().length > 0 &&
    !value.startsWith('/unauthorized');

class AuthService {
    private currentUser: Usuario | null = null;
    private credentials: string | null = null;


    async login(loginData: LoginRequest): Promise<Usuario> {
        const response = await fetch(`${API_BASE_URL}/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: loginData.username,
                password: loginData.password
            })
        });

        if (!response.ok) {
            throw new Error('Credenciales inválidas');
        }

        // El backend retorna los datos del usuario
        const userData = await response.json() as Usuario;

        // Crear credenciales Basic Auth para futuras requests
        const credentials = btoa(`${loginData.username}:${loginData.password}`);

        this.currentUser = userData;
        this.credentials = credentials;

        // Guardar en localStorage
        localStorage.setItem('currentUser', JSON.stringify(userData));
        localStorage.setItem('credentials', credentials);


        window.dispatchEvent(new CustomEvent('authStateChanged', { detail: userData }));

        return userData;
    }


    logout(): void {
        this.currentUser = null;
        this.credentials = null;
        localStorage.removeItem('currentUser');
        localStorage.removeItem('credentials');

        
        window.dispatchEvent(new CustomEvent('authStateChanged', { detail: null }));
    }

    getLastAccessibleRoute(): string | null {
        try {
            const stored = localStorage.getItem(LAST_ACCESSIBLE_ROUTE_KEY);
            if (isValidFallback(stored)) {
                return stored;
            }

            const current = localStorage.getItem(CURRENT_ROUTE_KEY);
            return isValidFallback(current) ? current : null;
        } catch {
            return null;
        }
    }

    updateNavigationHistory(path: string): void {
        if (!isValidFallback(path)) {
            return;
        }

        try {
            const current = localStorage.getItem(CURRENT_ROUTE_KEY);
            if (isValidFallback(current) && current !== path) {
                localStorage.setItem(LAST_ACCESSIBLE_ROUTE_KEY, current);
            }
            localStorage.setItem(CURRENT_ROUTE_KEY, path);
        } catch {
            // ignore storage errors (private mode, etc.)
        }
    }

    getStoredUnauthorizedOrigin(): string | null {
        try {
            const stored = localStorage.getItem(UNAUTHORIZED_RETURN_KEY);
            return isValidFallback(stored) ? stored : null;
        } catch {
            return null;
        }
    }

    rememberUnauthorizedOrigin(path: string | null | undefined): void {
        const candidate = isValidFallback(path) ? path : this.getLastAccessibleRoute();

        if (!isValidFallback(candidate)) {
            try {
                localStorage.removeItem(UNAUTHORIZED_RETURN_KEY);
            } catch {
                // ignore storage errors
            }
            return;
        }

        try {
            localStorage.setItem(UNAUTHORIZED_RETURN_KEY, candidate);
        } catch {
            // ignore storage errors
        }
    }

    handleUnauthorized(
        reason: UnauthorizedReason = 'unknown',
        options: HandleUnauthorizedOptions = {}
    ): void {
        const { logout = true, redirectTo = null } = options;

        if (logout) {
            this.logout();
        }

        const params = new URLSearchParams({ reason });
        const fallback = redirectTo ?? `${window.location.pathname}${window.location.search}`;

        this.rememberUnauthorizedOrigin(fallback);

        const storedFallback = this.getStoredUnauthorizedOrigin();
        if (storedFallback) {
            params.set('from', storedFallback);
        }

        window.location.href = `/unauthorized?${params.toString()}`;
    }

    getCurrentUser(): Usuario | null {
        if (this.currentUser) {
            return this.currentUser;
        }

        // Intentar recuperar del localStorage
        const storedUser = localStorage.getItem('currentUser');
        const storedCredentials = localStorage.getItem('credentials');

        if (storedUser && storedCredentials) {
            this.currentUser = JSON.parse(storedUser);
            this.credentials = storedCredentials;
            return this.currentUser;
        }

        return null;
    }

    getActorId(): string | null {
        const user = this.getCurrentUser();
        return user?.actorId || null;
    }

    getAuthHeaders(options?: { contentType?: string | null }): Record<string, string> {
        // Asegurar que las credenciales estén cargadas
        this.getCurrentUser();

        const headers: Record<string, string> = {};
        const contentType = options?.contentType;

        if (contentType !== null) {
            headers['Content-Type'] = contentType ?? 'application/json';
        }

        if (this.credentials) {
            headers['Authorization'] = `Basic ${this.credentials}`;
        }

        return headers;
    }

}

export default new AuthService();
