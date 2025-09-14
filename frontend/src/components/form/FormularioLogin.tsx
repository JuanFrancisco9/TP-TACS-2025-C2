import FormControl from '@mui/material/FormControl';
import * as React from "react";
import {Box, Button, circularProgressClasses, TextField} from "@mui/material";

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
    const [nombre, setNombre] = React.useState("");
    const [apellido, setApellido] = React.useState("");
    const [dni, setDNI] = React.useState("");
    const [contrasenia, setContrasenia] = React.useState("");
    const [comprobarContrasenia, setComprobarContrasenia] = React.useState("");

    const [submitting, setSubmitting] = React.useState(false);
    const [errorMsg, setErrorMsg] = React.useState<string | null>(null);
    const [successMsg, setSuccessMsg] = React.useState<string | null>(null);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setErrorMsg(null);
        setSuccessMsg(null);

        if (contrasenia !== comprobarContrasenia) {
            setErrorMsg("Las contraseñas no coinciden");
            return;
        }

        const payload: InputRegistroDto = {
            id: null,
            username: null,
            password: contrasenia,
            rol: null,
            nombre,
            apellido,
            dni,
        };

        try {
            setSubmitting(true);
            const res = await fetch("/api/user", {
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
            setContrasenia("");
            setComprobarContrasenia("");
        } catch (err: any) {
            setErrorMsg(err?.message ?? "No se pudo conectar con el servidor.");
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <Box
            component="form"
            onSubmit={handleSubmit}
            sx={{ display: "grid", gap: 2, maxWidth: 420, p: 2 }}
        >
            <TextField
                label="Nombre"
                value={nombre}
                onChange={(e) => setNombre(e.target.value)}
                fullWidth
            />

            <TextField
                label="Apellido"
                value={apellido}
                onChange={(e) => setApellido(e.target.value)}
                fullWidth
            />

            <TextField
                label="DNI"
                value={dni}
                onChange={(e) => setDNI(e.target.value)}
                fullWidth
            />

            <TextField
                label="Contraseña"
                value={contrasenia}
                onChange={(e) => setContrasenia(e.target.value)}
                fullWidth
            />

            <TextField
                label="Reescriba su contraseña"
                value={comprobarContrasenia}
                onChange={(e) => setComprobarContrasenia(e.target.value)}
                fullWidth
            />

            <Button type="submit" variant="contained">Registrarse</Button>
        </Box>
    );
}