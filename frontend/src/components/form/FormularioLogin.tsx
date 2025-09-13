import FormControl from '@mui/material/FormControl';
import * as React from "react";
import {Box, Button, circularProgressClasses, TextField} from "@mui/material";

export default function FormularioLogin() {
    const [nombre, setNombre] = React.useState("");
    const [apellido, setApellido] = React.useState("");
    const [dni, setDNI] = React.useState("");
    const [constrasenia, setContrasenia] = React.useState("");
    const [comprobarContrasenia, setComprobarContrasenia] = React.useState("");

    const handleSubmit = ( e : React.FormEvent) => {
        e.preventDefault();
        const payload = {

        }
        // llamada al back con el payload necesario para registrar al usuario
    }

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
                value={constrasenia}
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