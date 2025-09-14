import * as React from 'react';
import {
  Typography,
  Button,
  Stack,
  Box,
  Paper,
  Chip
} from '@mui/material';
import type { Evento } from '../services/eventoService';


interface EventCardProps {
  item: Evento;
  onVerDetalle?: () => void;
}

const EventCard: React.FC<EventCardProps> = ({ item, onVerDetalle }) => (
  <Paper variant="outlined" sx={{ overflow: 'hidden' }}>
    <Box component="img" src={item.imagen} alt={item.titulo} sx={{ width: '100%', height: 160, objectFit: 'cover' }} />
    <Box sx={{ p: 1.5 }}>
      <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 0.5 }}>
        {item.etiquetas?.map((b) => (
          <Chip key={b} size="small" color="error" label={b} />
        ))}
        {/* {item. && (
          <Chip size="small" label={`★ ${item.rating.toFixed(1)}`} />
        )} */}
      </Stack>
      <Typography variant="subtitle1" sx={{ fontWeight: 600 }}>{item.titulo}</Typography>
      {item.precio && (
        <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>{item.precio.cantidad} {item.precio.moneda}</Typography>
      )}
      <Stack direction="row" spacing={1} sx={{ mt: 1.5 }}>
        <Button size="small" variant="outlined" onClick={onVerDetalle}>Ver más</Button>
        <Button size="small" variant="contained">Inscribirme</Button>
      </Stack>
    </Box>
  </Paper>
);

export default EventCard;
