import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Alert, Spinner } from 'react-bootstrap';
import UserInfo from './UserInfo';
import { participanteApiService } from '../services/participanteApiService';
import type { Participante } from '../services/participanteApiService';

const UserPage: React.FC = () => {
  const [participante, setParticipante] = useState<Participante | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // For demo purposes, we'll fetch participant with ID "1" (Carlos López)
  // In a real app, this ID would come from authentication context or props
  const participanteId = "1";

  useEffect(() => {
    const fetchParticipante = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await participanteApiService.getParticipante(participanteId);
        setParticipante(data);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Error desconocido');
      } finally {
        setLoading(false);
      }
    };

    fetchParticipante();
  }, [participanteId]);

  if (loading) {
    return (
      <Container className="py-4">
        <Row className="justify-content-center">
          <Col className="text-center">
            <Spinner animation="border" role="status" className="mb-3">
              <span className="visually-hidden">Cargando...</span>
            </Spinner>
            <p className="text-muted">Cargando información del participante...</p>
          </Col>
        </Row>
      </Container>
    );
  }

  if (error) {
    return (
      <Container className="py-4">
        <Row className="justify-content-center">
          <Col lg={8} xl={6}>
            <Alert variant="danger">
              <Alert.Heading>
                <i className="bi bi-exclamation-triangle me-2"></i>
                Error
              </Alert.Heading>
              <p className="mb-0">{error}</p>
            </Alert>
          </Col>
        </Row>
      </Container>
    );
  }

  if (!participante) {
    return (
      <Container className="py-4">
        <Row className="justify-content-center">
          <Col lg={8} xl={6}>
            <Alert variant="warning">
              <Alert.Heading>
                <i className="bi bi-info-circle me-2"></i>
                No encontrado
              </Alert.Heading>
              <p className="mb-0">No se encontró información del participante.</p>
            </Alert>
          </Col>
        </Row>
      </Container>
    );
  }

  return (
    <Container className="py-4">
      <Row className="justify-content-center">
        <Col lg={8} xl={6}>
          <div className="text-center mb-4">
            <h1 className="display-6 text-primary">Perfil de Participante</h1>
            <p className="lead text-muted">
              Información personal del participante registrado
            </p>
          </div>
          
          <UserInfo
            name={participante.nombre}
            lastName={participante.apellido}
            documentType="DNI"
            documentNumber={participante.dni}
            userName={participante.usuario?.username || 'Sin usuario'}
          />
          
          <Alert variant="info" className="mt-4">
            <Alert.Heading>
              <i className="bi bi-info-circle me-2"></i>
              Información
            </Alert.Heading>
            <p className="mb-0">
              Esta información es de solo lectura. Para modificar sus datos personales, 
              contacte con el administrador del sistema.
            </p>
          </Alert>
        </Col>
      </Row>
    </Container>
  );
};

export default UserPage;
