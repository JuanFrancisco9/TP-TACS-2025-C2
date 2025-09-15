export const Rol = {
    ROLE_ADMIN: 'ROLE_ADMIN',
    ROLE_ORGANIZER: 'ROLE_ORGANIZER',
    ROLE_USER: 'ROLE_USER'
} as const;

export type Rol = typeof Rol[keyof typeof Rol];

export interface Usuario {
    id: number;
    username: string;
    rol: Rol;
}

export interface LoginRequest {
    username: string;
    password: string;
}