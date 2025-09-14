import * as React from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, Snackbar, Alert, Button } from '@mui/material';

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
        <Button onClick={onClose} disabled={loading}>Cancelar</Button>
        <Button variant="contained" onClick={onConfirm} disabled={loading}>
          Confirmar Inscripción
        </Button>
      </DialogActions>
    </Dialog>
    <Snackbar open={showSnackbar} autoHideDuration={4000} onClose={onSnackbarClose} anchorOrigin={{ vertical: 'top', horizontal: 'center' }}>
      <Alert onClose={onSnackbarClose} severity={snackbarMsg.startsWith('Error') ? 'error' : 'success'} sx={{ width: '100%' }}>
        {snackbarMsg}
      </Alert>
    </Snackbar>
  </>
);

export default InscripcionDialog;
