import React, { useState, useEffect } from 'react';
import { Row, Col, Container, Pagination } from 'react-bootstrap';
import CardEvento from './card-evento';
import { EventoService } from '../services/eventoService';
import type { Evento } from '../services/eventoService';

interface ListaEventosProps {
  onVerDetalle: (evento: Evento) => void;
  onInscribirse: (titulo: string) => void;
}

const ListaEventos: React.FC<ListaEventosProps> = ({
  onVerDetalle,
  onInscribirse
}) => {
  const [eventos, setEventos] = useState<Evento[]>([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [paginaActual, setPaginaActual] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);
  const [totalElementos, setTotalElementos] = useState(0);

  useEffect(() => {
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
      console.log('�� Eventos recibidos:', resultado.eventos);
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
      console.log('�� ListaEventos.cargarEventos - Finalizando carga');
      setCargando(false);
    }
  };

  if (cargando) {
    return (
      <Container className="py-4">
        <div className="text-center">
          <div className="spinner-border text-primary" role="status">
            <span className="visually-hidden">Cargando...</span>
          </div>
          <p className="mt-2">Cargando eventos...</p>
        </div>
      </Container>
    );
  }

  if (error) {
    return (
      <Container className="py-4">
        <div className="alert alert-danger" role="alert">
          <h4 className="alert-heading">Error</h4>
          <p>{error}</p>
          <button 
            className="btn btn-outline-danger" 
            onClick={cargarEventos}
          >
            Reintentar
          </button>
        </div>
      </Container>
    );
  }

  if (eventos.length === 0) {
    console.log('🔍 ListaEventos - No hay eventos para mostrar');
    console.log('📊 Estado actual:', { eventos, cargando, error, totalElementos });
    return (
      <Container className="py-4">
        <div className="text-center">
          <h3>No hay eventos disponibles</h3>
          <p className="text-muted">Vuelve más tarde para ver nuevos eventos.</p>
        </div>
      </Container>
    );
  }

  const handlePageChange = (page: number) => {
    setPaginaActual(page - 1); // Pagination es 1-based, pero nuestra API es 0-based
  };

  return (
    <Container className="py-5">
      <Row className="mb-5">
        <Col>
          <h1 className="text-center mb-4">Listado de Eventos</h1>
          <p className="text-center text-muted">
            Mostrando {eventos.length} de {totalElementos} eventos
          </p>
        </Col>
      </Row>
      
      <Row className="g-4 mb-5 justify-content-center">
        {eventos.map((evento, index) => (
            <Col key={evento.id || index} lg={4} md={6} sm={12} className="mb-4 d-flex justify-content-center">
              <div style={{ width: '100%', maxWidth: '350px' }}>
                <CardEvento
                  titulo={evento.titulo}
                  descripcion={evento.descripcion}
                  fecha={evento.fecha}
                  ubicacion={evento.ubicacion.localidad} // Usar localidad de la ubicación
                  precio={evento.precio ? `${evento.precio.cantidad} ${evento.precio.moneda}` : undefined}
                  imagen={evento.imagen}
                  onVerDetalle={() => onVerDetalle(evento)}
                  onInscribirse={() => onInscribirse(evento.titulo)}
                />
              </div>
            </Col>
        ))}
      </Row>

      {/* Paginación */}
      {totalPaginas > 1 && (
        <Row className="mt-4">
          <Col className="d-flex justify-content-center">
            <Pagination>
              <Pagination.First 
                onClick={() => handlePageChange(1)} 
                disabled={paginaActual === 0}
              />
              <Pagination.Prev 
                onClick={() => handlePageChange(paginaActual)} 
                disabled={paginaActual === 0}
              />
              
              {/* Mostrar páginas */}
              {Array.from({ length: Math.min(5, totalPaginas) }, (_, i) => {
                const pageNumber = i + 1;
                return (
                  <Pagination.Item
                    key={pageNumber}
                    active={pageNumber === paginaActual + 1}
                    onClick={() => handlePageChange(pageNumber)}
                  >
                    {pageNumber}
                  </Pagination.Item>
                );
              })}
              
              <Pagination.Next 
                onClick={() => handlePageChange(paginaActual + 2)} 
                disabled={paginaActual >= totalPaginas - 1}
              />
              <Pagination.Last 
                onClick={() => handlePageChange(totalPaginas)} 
                disabled={paginaActual >= totalPaginas - 1}
              />
            </Pagination>
          </Col>
        </Row>
      )}
    </Container>
  );
};

export default ListaEventos;
