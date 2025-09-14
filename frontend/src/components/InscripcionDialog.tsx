import * as React from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, Snackbar, Alert, Button } from '@mui/material';
import HowToRegIcon from '@mui/icons-material/HowToReg';
import CloseIcon from '@mui/icons-material/Close';

interface InscripcionDialogProps {
  open: boolean;
  onClose: () => void;
  onConfirm: () => Promise<void>;
  loading?: boolean;
  snackbarMsg: string;
  showSnackbar: boolean;
  onSnackbarClose: () => void;
  titulo: string;
}

const InscripcionDialog: React.FC<InscripcionDialogProps> = ({
  open,
  onClose,
  onConfirm,
  loading = false,
  snackbarMsg,
  showSnackbar,
  onSnackbarClose,
  titulo,
}) => (
  <>
    <Dialog open={open} onClose={onClose}>
      <DialogTitle>Confirmar Inscripción</DialogTitle>
      <DialogContent>
        <span>¿Estás seguro de que quieres inscribirte a “{titulo}”?</span>
      </DialogContent>
      <DialogActions>
        <Button
          onClick={e => { e.stopPropagation(); onClose(); }}
          color="secondary"
          startIcon={<CloseIcon />}
          disabled={loading}
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
        <Button
          variant="contained"
          color="primary"
          startIcon={<HowToRegIcon />}
          onClick={e => { e.stopPropagation(); onConfirm(); }}
          disabled={loading}
          sx={{
            borderRadius: 2,
            boxShadow: 1,
            textTransform: 'none',
            fontWeight: 500,
            transition: 'box-shadow 0.3s, transform 0.3s',
            ':hover': {
              boxShadow: 8,
              transform: 'translateY(-2px) scale(1.04)'
            }
          }}
        >
          Confirmar Inscripción
        </Button>
      </DialogActions>
    </Dialog>
    {showSnackbar && (
      <Snackbar open={showSnackbar} autoHideDuration={4000} onClose={onSnackbarClose} anchorOrigin={{ vertical: 'top', horizontal: 'center' }}>
        <Alert onClose={onSnackbarClose} severity={snackbarMsg.startsWith('Error') ? 'error' : 'success'} sx={{ width: '100%' }}>
          {snackbarMsg}
        </Alert>
      </Snackbar>
    )}
  </>
);

export default InscripcionDialog;
