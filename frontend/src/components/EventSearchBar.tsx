import React, { useState } from 'react';
import {
  TextField,
  Button,
  Card,
  CardContent,
  CircularProgress,
  Stack
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import ClearIcon from '@mui/icons-material/Clear';
import { eventoApiService } from '../services/eventoApiService';
import type { ResultadoBusquedaEvento } from '../services/eventoApiService';

interface EventSearchBarProps {
  onSearchResults: (resultados: ResultadoBusquedaEvento) => void;
  onLoading: (loading: boolean) => void;
  onError: (error: string | null) => void;
}

const EventSearchBar: React.FC<EventSearchBarProps> = ({
  onSearchResults,
  onLoading,
  onError
}) => {
  const [palabrasClave, setPalabrasClave] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!palabrasClave.trim()) {
      onError('Las palabras clave son obligatorias');
      return;
    }

    try {
      setLoading(true);
      onLoading(true);
      onError(null);

      const resultados = await eventoApiService.buscarEventos({
        palabrasClave: palabrasClave.trim(),
      });

      onSearchResults(resultados);
    } catch (error) {
      onError(error instanceof Error ? error.message : 'Error desconocido');
    } finally {
      setLoading(false);
      onLoading(false);
    }
  };

  const handleClear = () => {
    setPalabrasClave('');
    onError(null);
  };

  return (
    <Card sx={{ mb: 4 }}>
      <CardContent>
        <form onSubmit={handleSearch}>
          <Stack direction="row" spacing={2}>
            <TextField
              fullWidth
              variant="outlined"
              size="medium"
              placeholder="Ingrese palabras clave para buscar eventos..."
              value={palabrasClave}
              onChange={(e) => setPalabrasClave(e.target.value)}
              disabled={loading}
            />
            <Button
              type="submit"
              variant="contained"
              color="primary"
              disabled={loading || !palabrasClave.trim()}
              startIcon={!loading ? <SearchIcon /> : undefined}
            >
              {loading ? <CircularProgress size={20} color="inherit" /> : 'Buscar'}
            </Button>
            <Button
              type="button"
              variant="outlined"
              color="secondary"
              onClick={handleClear}
              disabled={loading}
              startIcon={<ClearIcon />}
            >
              Limpiar
            </Button>
          </Stack>
        </form>
      </CardContent>
    </Card>
  );
};

export default EventSearchBar;
