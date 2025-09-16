import * as React from 'react';
import inscripcionesService from '../services/inscripcionesParticipanteService';
import {
  Typography,
  Button,
  Stack,
  Box,
  Paper,
  Chip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  CircularProgress,
  Snackbar,
  Alert
} from '@mui/material';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import CancelIcon from '@mui/icons-material/Cancel';
import type { Inscripcion } from '../types/inscripciones';

interface InscripcionCardProps {
  inscripcion: Inscripcion;
  onVerDetalle?: () => void;
  onInscripcionCancelada?: () => void;
}

const InscripcionCard: React.FC<InscripcionCardProps> = ({
  inscripcion,
  onVerDetalle,
  onInscripcionCancelada
}) => {
  const [showCancelDialog, setShowCancelDialog] = React.useState(false);
  const [showSnackbar, setShowSnackbar] = React.useState(false);
  const [snackbarMsg, setSnackbarMsg] = React.useState('');
  const [snackbarSeverity, setSnackbarSeverity] = React.useState<'success' | 'error'>('success');
  const [loading, setLoading] = React.useState(false);

  const handleCancelarInscripcion = () => setShowCancelDialog(true);

  const handleConfirmarCancelacion = async () => {
    setLoading(true);
    try {
      await inscripcionesService.cancelarInscripcion(inscripcion.id);
      setSnackbarMsg(`Inscripción cancelada: ${inscripcion.evento.titulo}`);
      setSnackbarSeverity('success');
      onInscripcionCancelada?.();
    } catch (e) {
      setSnackbarMsg('Error al cancelar inscripción. Intenta nuevamente.');
      setSnackbarSeverity('error');
    }
    setShowSnackbar(true);
    setShowCancelDialog(false);
    setLoading(false);
  };

  const getEstadoColor = (estado: string) => {
    switch (estado) {
      case 'ACEPTADA': return 'success';
      case 'WAITLIST': return 'warning';
      case 'CANCELADA': return 'error';
      default: return 'default';
    }
  };

  const getEstadoLabel = (estado: string) => {
    switch (estado) {
      case 'ACEPTADA': return 'Confirmada';
      case 'WAITLIST': return 'En espera';
      case 'CANCELADA': return 'Cancelada';
      default: return estado;
    }
  };

  return (
    <>
      <Paper
        variant="outlined"
        sx={{
          overflow: 'hidden',
          borderRadius: 4,
          boxShadow: 4,
          border: '1.5px solid #e0e0e0',
          transition: 'box-shadow 0.3s, transform 0.3s',
          ':hover': {
            boxShadow: 12,
            transform: 'translateY(-4px) scale(1.03)'
          }
        }}
      >
        <Box
          sx={{
            width: '100%',
            height: 160,
            backgroundColor: '#f5f5f5',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            cursor: 'pointer',
            transition: 'background-color 0.3s',
            ':hover': { backgroundColor: '#eeeeee' }
          }}
          onClick={onVerDetalle}
          tabIndex={0}
          role="button"
          aria-label={`Ver detalles de ${inscripcion.evento.titulo}`}
        >
          <Typography variant="h4" color="text.secondary">
            📅
          </Typography>
        </Box>

        <Box sx={{ p: 1.5 }}>
          <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 0.5 }}>
            <Chip
              size="small"
              color={getEstadoColor(inscripcion.estado.tipoEstado)}
              label={getEstadoLabel(inscripcion.estado.tipoEstado)}
            />
          </Stack>

          <Typography
            variant="subtitle1"
            sx={{ fontWeight: 600, cursor: 'pointer' }}
            onClick={onVerDetalle}
            tabIndex={0}
            role="button"
            aria-label={`Ver detalles de ${inscripcion.evento.titulo}`}
          >
            {inscripcion.evento.titulo}
          </Typography>

          <Typography
            variant="body2"
            color="text.secondary"
            sx={{ mt: 0.5, cursor: 'pointer' }}
            onClick={onVerDetalle}
            tabIndex={0}
            role="button"
            aria-label={`Ver detalles de ${inscripcion.evento.titulo}`}
          >
            {inscripcion.evento.descripcion}
          </Typography>

          <Typography
            variant="caption"
            color="text.secondary"
            sx={{ mt: 0.5, display: 'block' }}
          >
            Inscrito: {new Date(inscripcion.fechaRegistro).toLocaleDateString('es-AR')}
          </Typography>

          <Stack direction="row" spacing={1} sx={{ mt: 1.5 }}>
            <Button
              size="small"
              variant="outlined"
              color="secondary"
              endIcon={<ArrowForwardIcon />}
              onClick={onVerDetalle}
              sx={{
                borderRadius: 2,
                boxShadow: 1,
                textTransform: 'none',
                fontWeight: 500,
                transition: 'box-shadow 0.3s, transform 0.3s',
                ':hover': {
                  boxShadow: 6,
                  transform: 'translateY(-2px) scale(1.04)'
                }
              }}
            >
              Ver más
            </Button>

            {(inscripcion.estado.tipoEstado === 'ACEPTADA' || inscripcion.estado.tipoEstado === 'WAITLIST') && (
              <Button
                size="small"
                variant="outlined"
                color="error"
                startIcon={<CancelIcon />}
                onClick={e => { e.stopPropagation(); handleCancelarInscripcion(); }}
                sx={{
                  borderRadius: 2,
                  boxShadow: 1,
                  textTransform: 'none',
                  fontWeight: 500,
                  transition: 'box-shadow 0.3s, transform 0.3s',
                  ':hover': {
                    boxShadow: 6,
                    transform: 'translateY(-2px) scale(1.04)'
                  }
                }}
              >
                Cancelar
              </Button>
            )}
          </Stack>
        </Box>
      </Paper>

      <Dialog
        open={showCancelDialog}
        onClose={() => setShowCancelDialog(false)}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>Cancelar Inscripción</DialogTitle>
        <DialogContent>
          <Typography>
            ¿Estás seguro de que quieres cancelar tu inscripción a "{inscripcion.evento.titulo}"?
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
            Esta acción no se puede deshacer.
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button
            onClick={() => setShowCancelDialog(false)}
            disabled={loading}
          >
            Cancelar
          </Button>
          <Button
            onClick={handleConfirmarCancelacion}
            color="error"
            variant="contained"
            disabled={loading}
            startIcon={loading && <CircularProgress size={16} />}
          >
            {loading ? 'Cancelando...' : 'Confirmar cancelación'}
          </Button>
        </DialogActions>
      </Dialog>

      <Snackbar
        open={showSnackbar}
        autoHideDuration={4000}
        onClose={() => setShowSnackbar(false)}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert
          onClose={() => setShowSnackbar(false)}
          severity={snackbarSeverity}
          sx={{ width: '100%' }}
        >
          {snackbarMsg}
        </Alert>
      </Snackbar>
    </>
  );
};

export default InscripcionCard;