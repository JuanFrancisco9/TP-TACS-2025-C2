import React from 'react';
import { Card, Button, ListGroup, Row, Col } from 'react-bootstrap';

interface CardEventoProps {
  titulo: string;
  descripcion: string;
  fecha: string;
  ubicacion: string;
  precio?: string;
  imagen?: string;
  onVerDetalle?: () => void;
  onInscribirse?: () => void;
}

const CardEvento: React.FC<CardEventoProps> = ({
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
    <Card className="h-100 shadow-sm" style={{ minHeight: '400px' }}>
      {imagen && (
        <Card.Img 
          variant="top" 
          src={imagen} 
          alt={titulo}
          style={{ height: '200px', objectFit: 'cover' }}
        />
      )}
      
      <Card.Body className="d-flex flex-column">
        <Card.Title>{titulo}</Card.Title>
        
        <Card.Text className="text-muted flex-grow-1">
          {descripcion}
        </Card.Text>
        
        <ListGroup className="list-group-flush mb-3">
          <ListGroup.Item className="d-flex align-items-center">
            <i className="bi bi-calendar-event me-2"></i>
            <small className="text-muted">{fecha}</small>
          </ListGroup.Item>
          
          <ListGroup.Item className="d-flex align-items-center">
            <i className="bi bi-geo-alt me-2"></i>
            <small className="text-muted">{ubicacion}</small>
          </ListGroup.Item>
          
          {precio && (
            <ListGroup.Item className="d-flex align-items-center">
              <i className="bi bi-currency-dollar me-2"></i>
              <small className="text-success fw-bold">{precio}</small>
            </ListGroup.Item>
          )}
        </ListGroup>
        
        <Card.Body className="pt-0">
          <Row className="g-2">
            {onVerDetalle && (
              <Col>
                <Button 
                  variant="outline-primary" 
                  size="sm" 
                  className="w-100"
                  onClick={onVerDetalle}
                >
                  Ver Detalle
                </Button>
              </Col>
            )}
            
            {onInscribirse && (
              <Col>
                <Button 
                  variant="primary" 
                  size="sm" 
                  className="w-100"
                  onClick={onInscribirse}
                >
                  Inscribirse
                </Button>
              </Col>
            )}
          </Row>
        </Card.Body>
      </Card.Body>
    </Card>
  );
};

export default CardEvento;
