import * as React from 'react';
import {
  Card,
  CardMedia,
  CardContent,
  CardActions,
  Typography,
  Button,
  Stack,
  Divider,
  Box
} from '@mui/material';
import EventIcon from '@mui/icons-material/Event';
import PlaceIcon from '@mui/icons-material/Place';
import AttachMoneyIcon from '@mui/icons-material/AttachMoney';

interface CardEvento {
  titulo: string;
  descripcion: string;
  fecha: string;
  ubicacion: string;
  precio?: string;
  imagen?: string;
  onVerDetalle?: () => void;
  onInscribirse?: () => void;
}

const CardEvento: React.FC<CardEvento> = ({
  titulo,
  descripcion,
  fecha,
  ubicacion,
  precio,
  imagen,
  onVerDetalle,
  onInscribirse
}) => {
  return (
    <Card
      sx={{
        height: '100%',
        minHeight: 400,
        display: 'flex',
        flexDirection: 'column',
        borderRadius: 2,
        boxShadow: 3
      }}
    >
      {imagen && (
        <CardMedia
          component="img"
          height="200"
          image={imagen}
          alt={titulo}
          sx={{ objectFit: 'cover' }}
        />
      )}

      <CardContent sx={{ flexGrow: 1 }}>
        <Typography variant="h6" gutterBottom>
          {titulo}
        </Typography>

        <Typography
          variant="body2"
          color="text.secondary"
          sx={{
            mb: 2,
            display: '-webkit-box',
            WebkitLineClamp: 3,
            WebkitBoxOrient: 'vertical',
            overflow: 'hidden'
          }}
        >
          {descripcion}
        </Typography>

        <Stack spacing={1} divider={<Divider flexItem />}>
          <Stack direction="row" spacing={1} alignItems="center">
            <EventIcon fontSize="small" color="action" />
            <Typography variant="body2" color="text.secondary">
              {fecha}
            </Typography>
          </Stack>

          <Stack direction="row" spacing={1} alignItems="center">
            <PlaceIcon fontSize="small" color="action" />
            <Typography variant="body2" color="text.secondary">
              {ubicacion}
            </Typography>
          </Stack>

          {precio && (
            <Stack direction="row" spacing={1} alignItems="center">
              <AttachMoneyIcon fontSize="small" color="success" />
              <Typography variant="body2" color="success.main" fontWeight="bold">
                {precio}
              </Typography>
            </Stack>
          )}
        </Stack>
      </CardContent>

      <Box sx={{ flexGrow: 1 }} />

      <CardActions sx={{ p: 2, pt: 0 }}>
        {onVerDetalle && (
          <Button
            variant="outlined"
            size="small"
            fullWidth
            onClick={onVerDetalle}
          >
            Ver Detalle
          </Button>
        )}

        {onInscribirse && (
          <Button
            variant="contained"
            size="small"
            fullWidth
            onClick={onInscribirse}
          >
            Inscribirse
          </Button>
        )}
      </CardActions>
    </Card>
  );
};

export default CardEvento;
