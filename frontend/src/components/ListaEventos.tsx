import * as React from 'react';
import {
  Container,
  Typography,
  Pagination,
  CircularProgress,
  Alert,
  Button,
  Box,
  Stack,
} from '@mui/material';
import Grid from '@mui/material/Grid'; // ✅ Grid v2 en MUI 7
import CardEvento from './card-evento';
import { EventoService } from '../services/eventoService';
import type { Evento } from '../services/eventoService';

interface ListaEventosProps {
  onVerDetalle: (evento: Evento) => void;
  onInscribirse: (titulo: string) => void;
}

const ListaEventos: React.FC<ListaEventosProps> = ({
  onVerDetalle,
  onInscribirse,
}) => {
  const [eventos, setEventos] = React.useState<Evento[]>([]);
  const [cargando, setCargando] = React.useState(true);
  const [error, setError] = React.useState<string | null>(null);
  const [paginaActual, setPaginaActual] = React.useState(0); // 0-based
  const [totalPaginas, setTotalPaginas] = React.useState(0);
  const [totalElementos, setTotalElementos] = React.useState(0);

  React.useEffect(() => {
    console.log('useEffect montó / paginaActual=', paginaActual);
    cargarEventos();
  }, [paginaActual]);

  const cargarEventos = async () => {
    try {
      console.log('🔄 ListaEventos.cargarEventos - Iniciando carga de eventos');
      console.log('📄 Página actual:', paginaActual);

      setCargando(true);
      setError(null);

      const resultado = await EventoService.obtenerEventos(paginaActual);

      console.log('✅ ListaEventos.cargarEventos - Eventos cargados exitosamente:');
      console.log('📦 Eventos recibidos:', resultado.eventos);
      console.log('📄 Total páginas:', resultado.totalPaginas);
      console.log('🔢 Total elementos:', resultado.totalElementos);

      setEventos(resultado.eventos);
      setTotalPaginas(resultado.totalPaginas);
      setTotalElementos(resultado.totalElementos);
    } catch (error) {
      console.error('❌ ListaEventos.cargarEventos - Error cargando eventos:');
      console.error('🚨 Error completo:', error);
      setError('Error al cargar los eventos');
    } finally {
      console.log('✅ ListaEventos.cargarEventos - Finalizando carga');
      setCargando(false);
    }
  };

  const handlePageChange = (_: React.ChangeEvent<unknown>, page1Based: number) => {
    // MUI Pagination es 1-based, nuestro estado es 0-based
    setPaginaActual(page1Based - 1);
  };

  // Loading
  if (cargando) {
    return (
      <Container sx={{ py: 4 }}>
        <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 2 }}>
          <CircularProgress />
          <Typography>Cargando eventos...</Typography>
        </Box>
      </Container>
    );
  }

  // Error
  if (error) {
    return (
      <Container sx={{ py: 4 }}>
        <Alert
          severity="error"
          action={
            <Button color="inherit" size="small" onClick={cargarEventos}>
              Reintentar
            </Button>
          }
        >
          {error}
        </Alert>
      </Container>
    );
  }

  // Vacío
  if (eventos.length === 0) {
    console.log('🔍 ListaEventos - No hay eventos para mostrar');
    console.log('📊 Estado actual:', { eventos, cargando, error, totalElementos });
    return (
      <Container sx={{ py: 4 }}>
        <Box sx={{ textAlign: 'center' }}>
          <Typography variant="h5">No hay eventos disponibles</Typography>
          <Typography variant="body2" color="text.secondary">
            Vuelve más tarde para ver nuevos eventos.
          </Typography>
        </Box>
      </Container>
    );
  }

  return (
    <Container sx={{ py: 5 }}>
      <Stack spacing={1} sx={{ mb: 4, textAlign: 'center' }}>
        <Typography variant="h4">Listado de Eventos</Typography>
        <Typography variant="body2" color="text.secondary">
          Mostrando {eventos.length} de {totalElementos} eventos
        </Typography>
      </Stack>

      <Grid container spacing={3} sx={{ mb: 5, justifyContent: 'center' }}>
        {eventos.map((evento) => (
          <Grid
            key={evento.id}
            size={{ xs: 12, sm: 6, md: 4, lg: 4 }} // ✅ sin `item`, usando `size`
            sx={{ display: 'flex', justifyContent: 'center' }}
          >
            <Box sx={{ width: '100%', maxWidth: 350 }}>
              <CardEvento
                titulo={evento.titulo}
                descripcion={evento.descripcion}
                fecha={evento.fecha}
                ubicacion={evento.ubicacion.localidad}
                // soporta string o { cantidad, moneda }
                precio={
                  (evento as any).precio?.cantidad != null
                    ? `${(evento as any).precio.cantidad} ${(evento as any).precio.moneda}`
                    : (evento as any).precio
                }
                imagen={evento.imagen}
                onVerDetalle={() => onVerDetalle(evento)}
                onInscribirse={() => onInscribirse(evento.titulo)}
              />
            </Box>
          </Grid>
        ))}
      </Grid>

      {/* Paginación */}
      {totalPaginas > 1 && (
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2 }}>
          <Pagination
            count={totalPaginas}
            page={paginaActual + 1}
            onChange={handlePageChange}
            color="primary"
            shape="rounded"
            siblingCount={1}
            boundaryCount={1}
            showFirstButton
            showLastButton
          />
        </Box>
      )}
    </Container>
  );
};

export default ListaEventos;
