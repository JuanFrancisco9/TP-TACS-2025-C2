import React from 'react';
import { Card, Row, Col, Badge } from 'react-bootstrap';

interface UserInfoProps {
  name: string;
  lastName: string;
  documentType: string;
  documentNumber: string;
  userName: string;
}

const UserInfo: React.FC<UserInfoProps> = ({
  name,
  lastName,
  documentType,
  documentNumber,
  userName
}) => {
  return (
    <Card className="shadow-sm">
      <Card.Header className="bg-primary text-white">
        <h4 className="mb-0">
          <i className="bi bi-person-circle me-2"></i>
          Información del Usuario
        </h4>
      </Card.Header>
      <Card.Body>
        <Row className="g-3">
          <Col md={6}>
            <div className="d-flex align-items-center">
              <strong className="text-muted me-2">Nombre:</strong>
              <span className="fs-5">{name}</span>
            </div>
          </Col>
          <Col md={6}>
            <div className="d-flex align-items-center">
              <strong className="text-muted me-2">Apellido:</strong>
              <span className="fs-5">{lastName}</span>
            </div>
          </Col>
          <Col md={6}>
            <div className="d-flex align-items-center">
              <strong className="text-muted me-2">Tipo de Documento:</strong>
              <Badge bg="secondary" className="fs-6">{documentType}</Badge>
            </div>
          </Col>
          <Col md={6}>
            <div className="d-flex align-items-center">
              <strong className="text-muted me-2">Número de Documento:</strong>
              <span className="fs-5">{documentNumber}</span>
            </div>
          </Col>
          <Col md={12}>
            <hr />
            <div className="d-flex align-items-center">
              <strong className="text-muted me-2">Nombre de Usuario:</strong>
              <Badge bg="info" className="fs-6">{userName}</Badge>
            </div>
          </Col>
        </Row>
      </Card.Body>
    </Card>
  );
};

export default UserInfo;
