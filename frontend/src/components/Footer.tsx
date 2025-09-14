import React from 'react';
import { Container, Row, Col, Nav } from 'react-bootstrap';

const Footer: React.FC = () => {
  return (
    <footer className="bg-dark text-light mt-5 py-4">
      <Container>
        <Row>
          <Col md={4}>
            <h5>TP TACS</h5>
            <p className="text-muted">
              Sistema de gestión de eventos desarrollado para el curso de Técnicas Avanzadas de Construcción de Software.
            </p>
          </Col>
          <Col md={4}>
            <h5>Enlaces Rápidos</h5>
            <Nav className="flex-column">
              <Nav.Link href="#" className="text-light p-0 mb-1">Eventos</Nav.Link>
              <Nav.Link href="#" className="text-light p-0 mb-1">Inscripciones</Nav.Link>
              <Nav.Link href="#" className="text-light p-0 mb-1">Estadísticas</Nav.Link>
              <Nav.Link href="#" className="text-light p-0 mb-1">Contacto</Nav.Link>
            </Nav>
          </Col>
          <Col md={4}>
            <h5>Información</h5>
            <p className="text-muted mb-1">
              <strong>Universidad:</strong> UTN FRBA
            </p>
            <p className="text-muted mb-1">
              <strong>Materia:</strong> TACS
            </p>
            <p className="text-muted">
              <strong>Año:</strong> 2025
            </p>
          </Col>
        </Row>
        <hr className="my-3" />
        <Row>
          <Col className="text-center text-muted">
            <p className="mb-0">
              © 2025 TP TACS. Todos los derechos reservados.
            </p>
          </Col>
        </Row>
      </Container>
    </footer>
  );
};

export default Footer;
