export const Rol = {
    ROLE_ADMIN: 'ROLE_ADMIN',
    ROLE_ORGANIZER: 'ROLE_ORGANIZER',
    ROLE_USER: 'ROLE_USER'
} as const;

export type Rol = typeof Rol[keyof typeof Rol];

export interface Usuario {
    id: string;
    username: string;
    rol: Rol;
}

export interface LoginRequest {
    username: string;
    password: string;
}

export interface Participante {
    id: string;
    nombre: string;
    apellido: string;
    dni: string;
    usuario: Usuario | null //TODO: Sacar el o null cuando se deshardcode el participantesApiService
}

export interface Organizador {
    id: string;
    nombre: string;
    apellido: string;
    dni: string;
    usuario: Usuario;
}