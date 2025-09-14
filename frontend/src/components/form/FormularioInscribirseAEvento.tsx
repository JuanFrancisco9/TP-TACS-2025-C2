import * as React from "react";
import { Box, Button, Alert, Typography } from "@mui/material";


type Inscripcion = any;

type Props = {
    evento?: any | null;
}

export default function FormularioInscribirseAEvento( { evento }: Props) {
    const [submitting, setSubmitting] = React.useState(false);
    const [errorMsg, setErrorMsg] = React.useState<string | null>(null);
    const [successMsg, setSuccessMsg] = React.useState<string | null>(null);
    const [inscripcion, setInscripcion] = React.useState<Inscripcion | null>(null);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setErrorMsg(null);
        setSuccessMsg(null);
        setInscripcion(null);

        const payload = {
            participante: "", // Aca habria que pasarle el usuario y el ID del evento, no se como podemos hacerlo
            evento_id: evento?.id,
        };

        try {
            setSubmitting(true);

            const res = await fetch("/api/inscripciones", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    payload
                })
            });

            if (!res.ok) {
                const text = await res.text();
                throw new Error(text || "No se pudo completar la inscripción.");
            }

            const data: Inscripcion = await res.json();
            setInscripcion(data);
            setSuccessMsg("¡Inscripción realizada con éxito!");
        } catch (err: any) {
            setErrorMsg(err?.message ?? "Error de red al inscribirse.");
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
            <Typography variant="h6">Inscribirse</Typography>

            {errorMsg && <Alert severity="error">{errorMsg}</Alert>}
            {successMsg && <Alert severity="success">{successMsg}</Alert>}
            {inscripcion && (
                <Alert severity="info">
                    Inscripción creada: <code>{JSON.stringify(inscripcion)}</code>
                </Alert>
            )}

            <Button type="submit" variant="contained" disabled={submitting}>
                {submitting ? "Enviando…" : "Confirmar inscripción"}
            </Button>
        </Box>
    );
}
