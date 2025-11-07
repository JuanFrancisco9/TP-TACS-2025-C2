// @ts-nocheck
import React, { useState, useEffect } from 'react';
import {
    Container,
    Grid,
    Card,
    CardContent,
    CardHeader,
    Typography,
    Button,
    TextField,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    CircularProgress,
    Alert,
    Box
} from "@mui/material";
import { Edit as EditIcon, Save as SaveIcon } from "@mui/icons-material";
import { participanteApiService } from '../services/participanteApiService';
import {type Participante} from '../types/auth.ts';
import authService from "../services/authService.ts";

const UserPage: React.FC = () => {
  const [participante, setParticipante] = useState<Participante | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [currentUser] = useState(authService.getCurrentUser());
  const [participanteId, setParticipanteId] = useState<string>(authService.getActorId()?.toString() || '');


  useEffect(() => {
    if (currentUser?.actorId) {
        setParticipanteId(currentUser.actorId.toString());
    }
  }, [currentUser]);

  useEffect(() => {
    const fetchParticipante = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await participanteApiService.getParticipante(participanteId);
        setParticipante(data);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Error desconocido');
      } finally {
        setLoading(false);
      }
    };

    fetchParticipante();
  }, [participanteId]);

    const [openEdit, setOpenEdit] = useState(false);
    const [editData, setEditData] = useState({
        nombre: participante?.nombre || "",
        apellido: participante?.apellido || "",
        dni: participante?.dni || ""
    });
    const [saving, setSaving] = useState(false);

    if (loading) {
        return (
            <Box sx={{ display: "flex", justifyContent: "center", alignItems: "center", height: "50vh" }}>
                <CircularProgress />
                <Typography sx={{ ml: 2 }}>Cargando información del participante...</Typography>
            </Box>
        );
    }

    if (error) {
        return (
            <Container maxWidth="sm" sx={{ mt: 4 }}>
                <Alert severity="error">{error}</Alert>
            </Container>
        );
    }

    if (!participante) {
        return (
            <Container maxWidth="sm" sx={{ mt: 4 }}>
                <Alert severity="warning">No se encontró información del participante.</Alert>
            </Container>
        );
    }

    const handleOpenEdit = () => {
        setEditData({
            nombre: participante?.nombre,
            apellido: participante?.apellido,
            dni: participante?.dni
        })
        setOpenEdit(true);
    }
    const handleCloseEdit = () => setOpenEdit(false);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setEditData((prev) => ({ ...prev, [name]: value }));
    };

    const handleSave = async () => {
        setSaving(true);
        try {
            const actualizado = await participanteApiService.updateParticipante(participanteId, editData);

            // Actualizamos el estado del participante en pantalla
            setParticipante(actualizado);

            handleCloseEdit();
        } catch (e) {
            console.error("Error al guardar cambios:", e);
            alert("No se pudo actualizar la información del participante");
        } finally {
            setSaving(false);
        }
    };

    return (
        <Container maxWidth="md" sx={{ py: 4 }}>
            <Box textAlign="center" mb={4}>
                <Typography variant="h4" color="primary" fontWeight={600}>
                    Bienvenido a tu perfil
                </Typography>
                <Typography variant="subtitle1" color="text.secondary">
                    Aca podes ver y modificar tu información personal
                </Typography>
            </Box>

            <Card sx={{ boxShadow: 4, borderRadius: 3 }}>
                <CardHeader
                    title={
                        <Typography variant="h6" fontWeight={600}>
                            Información del Usuario
                        </Typography>
                    }
                    action={
                        <Button
                            variant="contained"
                            color="secondary"
                            startIcon={<EditIcon />}
                            onClick={handleOpenEdit}
                            sx={{ borderRadius: 2, textTransform: "none" }}
                        >
                            Editar
                        </Button>
                    }
                    sx={{
                        background: "linear-gradient(135deg, #6a0572 0%, #0f8b8d 100%)",
                        color: "#fff",
                        borderTopLeftRadius: 12,
                        borderTopRightRadius: 12
                    }}
                />
                <CardContent>
                    <Grid container spacing={2}>
                        <Grid item xs={12} sm={6}>
                            <Typography variant="body2" color="text.secondary">
                                Nombre
                            </Typography>
                            <Typography variant="h6">{participante.nombre}</Typography>
                        </Grid>

                        <Grid item xs={12} sm={6}>
                            <Typography variant="body2" color="text.secondary">
                                Apellido
                            </Typography>
                            <Typography variant="h6">{participante.apellido}</Typography>
                        </Grid>

                        <Grid item xs={12} sm={6}>
                            <Typography variant="body2" color="text.secondary">
                                Tipo de Documento
                            </Typography>
                            <Typography variant="h6">DNI</Typography>
                        </Grid>

                        <Grid item xs={12} sm={6}>
                            <Typography variant="body2" color="text.secondary">
                                Número de Documento
                            </Typography>
                            <Typography variant="h6">{participante.dni}</Typography>
                        </Grid>
                    </Grid>
                </CardContent>
            </Card>

            <Dialog open={openEdit} onClose={handleCloseEdit} fullWidth maxWidth="sm">
                <DialogTitle>Editar información</DialogTitle>
                <DialogContent sx={{ pt: 2 }}>
                    <TextField
                        fullWidth
                        label="Nombre"
                        name="nombre"
                        value={editData.nombre}
                        onChange={handleChange}
                        margin="dense"
                    />
                    <TextField
                        fullWidth
                        label="Apellido"
                        name="apellido"
                        value={editData.apellido}
                        onChange={handleChange}
                        margin="dense"
                    />
                    <TextField
                        fullWidth
                        label="DNI"
                        name="dni"
                        value={editData.dni}
                        onChange={handleChange}
                        margin="dense"
                    />
                </DialogContent>
                <DialogActions sx={{ px: 3, pb: 2 }}>
                    <Button onClick={handleCloseEdit} color="inherit">
                        Cancelar
                    </Button>
                    <Button
                        variant="contained"
                        color="primary"
                        startIcon={saving ? <CircularProgress size={18} /> : <SaveIcon />}
                        onClick={handleSave}
                        disabled={saving}
                    >
                        {saving ? "Guardando..." : "Guardar"}
                    </Button>
                </DialogActions>
            </Dialog>
        </Container>
    );
};

export default UserPage;
