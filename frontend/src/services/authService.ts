import type { Usuario, LoginRequest } from '../types/auth';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

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

    getAuthHeaders(): Record<string, string> {
        // Asegurar que las credenciales estén cargadas
        this.getCurrentUser();

        const headers: Record<string, string> = {
            'Content-Type': 'application/json'
        };

        if (this.credentials) {
            headers['Authorization'] = `Basic ${this.credentials}`;
        }

        return headers;
    }

}

export default new AuthService();