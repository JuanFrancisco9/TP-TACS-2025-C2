import React from 'react';
import { 
  Card, 
  Button, 
  Row, 
  Col, 
  Container, 
  Badge, 
  ListGroup, 
  Alert,
  Modal
} from 'react-bootstrap';
import type { Evento } from '../services/eventoService';

interface DetallesEventoProps {
  evento: Evento;
  onVolver: () => void;
  onInscribirse?: () => void;
}

const DetallesEvento: React.FC<DetallesEventoProps> = ({
  evento,
  onVolver,
  onInscribirse
}) => {
  const [showModal, setShowModal] = React.useState(false);

  const handleInscribirse = () => {
    if (onInscribirse) {
      onInscribirse();
    } else {
      setShowModal(true);
    }
  };

  return (
    <Container className="py-4">
      {/* Botón de volver */}
      <Row className="mb-4">
        <Col>
          <Button 
            variant="outline-secondary" 
            onClick={onVolver}
            className="mb-3"
          >
            ← Volver al listado
          </Button>
        </Col>
      </Row>

      <Row>
        {/* Imagen principal */}
        <Col lg={6} className="mb-4">
          {evento.imagen && (
            <Card>
              <Card.Img 
                variant="top" 
                src={evento.imagen} 
                alt={evento.titulo}
                style={{ height: '400px', objectFit: 'cover' }}
              />
            </Card>
          )}
        </Col>

        {/* Información principal */}
        <Col lg={6}>
          <Card className="h-100">
            <Card.Body>
              <div className="d-flex justify-content-between align-items-start mb-3">
                <Card.Title className="h2">{evento.titulo}</Card.Title>
                {evento.categoria && (
                  <Badge bg="primary" className="ms-2">
                    {evento.categoria}
                  </Badge>
                )}
              </div>

              <Card.Text className="lead mb-4">
                {evento.descripcion}
              </Card.Text>

              {/* Información básica */}
              <ListGroup className="mb-4">
                <ListGroup.Item className="d-flex align-items-center">
                  <i className="bi bi-calendar-event me-3 text-primary"></i>
                  <div>
                    <strong>Fecha:</strong> {evento.fecha}
                  </div>
                </ListGroup.Item>

                <ListGroup.Item className="d-flex align-items-center">
                  <i className="bi bi-geo-alt me-3 text-primary"></i>
                  <div>
                    <strong>Ubicación:</strong> {evento.ubicacion.localidad} - {evento.ubicacion.direccion}
                  </div>
                </ListGroup.Item>

                {evento.duracion && (
                  <ListGroup.Item className="d-flex align-items-center">
                    <i className="bi bi-clock me-3 text-primary"></i>
                    <div>
                      <strong>Duración:</strong> {evento.duracion}
                    </div>
                  </ListGroup.Item>
                )}

                {evento.organizador && (
                  <ListGroup.Item className="d-flex align-items-center">
                    <i className="bi bi-person me-3 text-primary"></i>
                    <div>
                      <strong>Organizador:</strong> {evento.organizador}
                    </div>
                  </ListGroup.Item>
                )}

                {evento.precio && (
                  <ListGroup.Item className="d-flex align-items-center">
                    <i className="bi bi-currency-dollar me-3 text-success"></i>
                    <div>
                      <strong>Precio:</strong> 
                      <span className="text-success fw-bold ms-2">
                        {evento.precio.cantidad} {evento.precio.moneda}
                      </span>
                    </div>
                  </ListGroup.Item>
                )}
              </ListGroup>

              {/* Cupos */}
              {(evento.cupoMinimo || evento.cupoMaximo) && (
                <Alert variant="info" className="mb-4">
                  <div className="d-flex justify-content-between">
                    {evento.cupoMinimo && (
                      <span>Cupo Mínimo: <strong>{evento.cupoMinimo}</strong></span>
                    )}
                    {evento.cupoMaximo && (
                      <span>Cupo Máximo: <strong>{evento.cupoMaximo}</strong></span>
                    )}
                  </div>
                </Alert>
              )}

              {/* Botón de acción */}
              <Button 
                variant="primary" 
                size="lg"
                onClick={handleInscribirse}
                className="w-100"
              >
                Inscribirse al Evento
              </Button>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Información adicional */}
      <Row className="mt-4">
        <Col lg={6}>
          {/* Etiquetas */}
          {evento.etiquetas && evento.etiquetas.length > 0 && (
            <Card className="mb-4">
              <Card.Header>
                <h5 className="mb-0">
                  <i className="bi bi-tags me-2"></i>
                  Etiquetas
                </h5>
              </Card.Header>
              <Card.Body>
                <div className="d-flex flex-wrap gap-2">
                  {evento.etiquetas.map((etiqueta, index) => (
                    <Badge key={index} bg="secondary" className="fs-6">
                      {etiqueta}
                    </Badge>
                  ))}
                </div>
              </Card.Body>
            </Card>
          )}
        </Col>

        <Col lg={6}>
          {/* Estado del evento */}
          <Card className="mb-4">
            <Card.Header>
              <h5 className="mb-0">
                <i className="bi bi-info-circle me-2"></i>
                Estado del Evento
              </h5>
            </Card.Header>
            <Card.Body>
              <div className="d-flex align-items-center">
                <Badge 
                  bg={evento.estado === 'ACTIVO' ? 'success' : evento.estado === 'CANCELADO' ? 'danger' : 'warning'}
                  className="fs-6"
                >
                  {evento.estado}
                </Badge>
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Modal de confirmación */}
      <Modal show={showModal} onHide={() => setShowModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Confirmar Inscripción</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          ¿Estás seguro de que quieres inscribirte a "{evento.titulo}"?
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowModal(false)}>
            Cancelar
          </Button>
          <Button variant="primary" onClick={() => {
            setShowModal(false);
            alert(`Te has inscrito a: ${evento.titulo}`);
          }}>
            Confirmar Inscripción
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  );
};

export default DetallesEvento;
