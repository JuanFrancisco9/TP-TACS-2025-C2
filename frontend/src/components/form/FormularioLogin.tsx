import * as React from "react";
import {Box, Button, TextField, Tabs, Tab, Alert, FormControl, InputLabel, Select, MenuItem} from "@mui/material";
import { useNavigate } from 'react-router-dom';
import authService from '../../services/authService';
import type { LoginRequest } from '../../types/auth';
import { Rol } from '../../types/auth';


type InputRegistroDto = {
    id?: number | null
    username: string
    password: string
    rol: string | null
    nombre: string
    apellido: string
    dni: string
}

export default function FormularioLogin() {
    const navigate = useNavigate();
    const [tabValue, setTabValue] = React.useState(0);

    // Estados para Login
    const [loginUsername, setLoginUsername] = React.useState("");
    const [loginPassword, setLoginPassword] = React.useState("");

    // Estados para Registro
    const [nombre, setNombre] = React.useState("");
    const [apellido, setApellido] = React.useState("");
    const [dni, setDNI] = React.useState("");
    const [username, setUsername] = React.useState("");
    const [contrasenia, setContrasenia] = React.useState("");
    const [comprobarContrasenia, setComprobarContrasenia] = React.useState("");
    const [selectedRol, setSelectedRol] = React.useState("");

    const [submitting, setSubmitting] = React.useState(false);
    const [errorMsg, setErrorMsg] = React.useState<string | null>(null);
    const [successMsg, setSuccessMsg] = React.useState<string | null>(null);

    const handleTabChange = (_event: React.SyntheticEvent, newValue: number) => {
        setTabValue(newValue);
        setErrorMsg(null);
        setSuccessMsg(null);
    };

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        setErrorMsg(null);
        setSuccessMsg(null);

        if (!loginUsername || !loginPassword) {
            setErrorMsg("Por favor completa todos los campos");
            return;
        }

        try {
            setSubmitting(true);
            const loginData: LoginRequest = {
                username: loginUsername,
                password: loginPassword
            };

            const usuario = await authService.login(loginData);

            // Redirigir según el rol
            if (usuario.rol === 'ROLE_ADMIN') {
                navigate('/estadisticas');
            }
            if (usuario.rol === 'ROLE_ORGANIZER') {
                navigate('/crear-evento')
            }
            if (usuario.rol === 'ROLE_USER') {
                navigate('/mis-eventos')
            }else {
                navigate('/');
            }
        } catch (err: any) {
            setErrorMsg(err?.message ?? "Error al iniciar sesión");
        } finally {
            setSubmitting(false);
        }
    };

    const handleRegister = async (e: React.FormEvent) => {
        e.preventDefault();
        setErrorMsg(null);
        setSuccessMsg(null);

        if (contrasenia !== comprobarContrasenia) {
            setErrorMsg("Las contraseñas no coinciden");
            return;
        }

        if (!selectedRol) {
            setErrorMsg("Por favor selecciona un tipo de usuario");
            return;
        }

        const payload: InputRegistroDto = {
            username: username,
            password: contrasenia,
            rol: selectedRol,
            nombre: nombre,
            apellido: apellido,
            dni: dni
        };

        try {
            setSubmitting(true);
            const res = await fetch(`${import.meta.env.VITE_API_BASE_URL}/user`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload),
            });

            if (!res.ok) {
                let msg = "Error al registrar usuario.";
                try {
                    const text = await res.text();
                    if (text) msg = text;
                } catch {}
                setErrorMsg(msg);
                return;
            }

            setSuccessMsg("¡Usuario registrado con éxito!");
            setNombre("");
            setApellido("");
            setDNI("");
            setUsername("");
            setContrasenia("");
            setComprobarContrasenia("");
            setSelectedRol("");
        } catch (err: any) {
            setErrorMsg(err?.message ?? "No se pudo conectar con el servidor.");
        } finally {
            setSubmitting(false);
        }
    };


    return (
        <Box sx={{ width: '100%' }}>
            <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
                <Tabs value={tabValue} onChange={handleTabChange}>
                    <Tab label="Iniciar Sesión" />
                    <Tab label="Registrarse" />
                </Tabs>
            </Box>

            {/* Mostrar errores y éxitos */}
            {errorMsg && (
                <Alert severity="error" sx={{ mt: 2 }}>
                    {errorMsg}
                </Alert>
            )}
            {successMsg && (
                <Alert severity="success" sx={{ mt: 2 }}>
                    {successMsg}
                </Alert>
            )}

            {/* Tab de Login */}
            {tabValue === 0 && (
                <Box
                    component="form"
                    onSubmit={handleLogin}
                    sx={{ display: "grid", gap: 2, maxWidth: 420, p: 2 }}
                >
                    <TextField
                        label="Usuario"
                        value={loginUsername}
                        onChange={(e) => setLoginUsername(e.target.value)}
                        fullWidth
                        required
                    />

                    <TextField
                        label="Contraseña"
                        type="password"
                        value={loginPassword}
                        onChange={(e) => setLoginPassword(e.target.value)}
                        fullWidth
                        required
                    />

                    <Button
                        type="submit"
                        variant="contained"
                        disabled={submitting}
                        fullWidth
                    >
                        {submitting ? "Iniciando..." : "Iniciar Sesión"}
                    </Button>
                </Box>
            )}

            {/* Tab de Registro */}
            {tabValue === 1 && (
                <Box
                    component="form"
                    onSubmit={handleRegister}
                    sx={{ display: "grid", gap: 2, maxWidth: 420, p: 2 }}
                >
                    <TextField
                        label="Nombre de usuario"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        fullWidth
                        required
                    />

                    <TextField
                        label="Nombre"
                        value={nombre}
                        onChange={(e) => setNombre(e.target.value)}
                        fullWidth
                        required
                    />

                    <TextField
                        label="Apellido"
                        value={apellido}
                        onChange={(e) => setApellido(e.target.value)}
                        fullWidth
                        required
                    />

                    <TextField
                        label="DNI"
                        value={dni}
                        onChange={(e) => setDNI(e.target.value)}
                        fullWidth
                        required
                    />

                    <FormControl fullWidth required>
                        <InputLabel>Tipo de Usuario</InputLabel>
                        <Select
                            value={selectedRol}
                            label="Tipo de Usuario"
                            onChange={(e) => setSelectedRol(e.target.value)}
                        >
                            <MenuItem value={Rol.ROLE_USER}>Participante</MenuItem>
                            <MenuItem value={Rol.ROLE_ORGANIZER}>Organizador</MenuItem>
                        </Select>
                    </FormControl>

                    <TextField
                        label="Contraseña"
                        type="password"
                        value={contrasenia}
                        onChange={(e) => setContrasenia(e.target.value)}
                        fullWidth
                        required
                    />

                    <TextField
                        label="Reescriba su contraseña"
                        type="password"
                        value={comprobarContrasenia}
                        onChange={(e) => setComprobarContrasenia(e.target.value)}
                        fullWidth
                        required
                    />

                    <Button
                        type="submit"
                        variant="contained"
                        disabled={submitting}
                        fullWidth
                    >
                        {submitting ? "Registrando..." : "Registrarse"}
                    </Button>
                </Box>
            )}
        </Box>
    );
}