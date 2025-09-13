import React, { useState } from 'react';
import { Form, InputGroup, Button, Card, Spinner } from 'react-bootstrap';
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
    
    // TODO: Hacer un redirect a la página de ver eventos con el filtro de nombre pre-asignado 
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
    <Card className="mb-4">
      <Card.Body>
        <Form onSubmit={handleSearch}>
          <InputGroup size="lg">
            <Form.Control
              type="text"
              placeholder="Ingrese palabras clave para buscar eventos..."
              value={palabrasClave}
              onChange={(e) => setPalabrasClave(e.target.value)}
              disabled={loading}
            />
            <Button 
              type="submit" 
              variant="primary"
              disabled={loading || !palabrasClave.trim()}
            >
              {loading ? (
                <Spinner size="sm" animation="border" role="status">
                  <span className="visually-hidden">Buscando...</span>
                </Spinner>
              ) : (
                <>
                  <i className="bi bi-search me-1"></i>
                  Buscar
                </>
              )}
            </Button>
            <Button 
              type="button" 
              variant="outline-secondary"
              onClick={handleClear}
              disabled={loading}
            >
              <i className="bi bi-x-circle"> X </i>
            </Button>
          </InputGroup>
        </Form>
      </Card.Body>
    </Card>
  );
};

export default EventSearchBar;