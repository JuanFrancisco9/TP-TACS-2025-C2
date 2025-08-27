# TP-TACS-2025-C2


Base URL por defecto: `http://localhost:8080`

## Endpoints

### 1. Crear Evento
POST /evento

Request JSON:
```json
{
  "titulo": "Seminario de Mocks",
  "descripcion": "Introducción a Mocks",
  "fecha": "2025-09-10T19:00:00",
  "horaInicio": "19:00",
  "duracion": 2.5,
  "ubicacion": { "provincia": "Buenos Aires", "ciudad": "CABA", "direccion": "Av. Siempre Viva 123" },
  "cupoMaximo": 30,
  "precio": { "moneda": "ARS", "monto": 1000 },
  "organizadorId": "1"
}
```
Response 201:
```json
{
  "id": "<id>",
  "titulo": "Seminario de Mocks",
  "descripcion": "Introducción a Mocks",
  "fecha": "2025-09-10T19:00:00",
  "horaInicio": "19:00",
  "duracion": 2.5,
  "ubicacion": { "provincia": "Buenos Aires", "ciudad": "CABA", "direccion": "Av. Siempre Viva 123" },
  "cupoMaximo": 30,
  "precio": { "moneda": "ARS", "monto": 1000 },
  "organizador": { "id": "1", "nombre": "Juan", "apellido": "Pérez", "dni": "12345678" },
  "estado": { "tipoEstado": "CONFIRMADO", "fechaCambio": "2025-08-27T12:00:00" }
}
```

### 2. Crear Inscripción
POST /inscripcion

Request JSON:
```json
{
  "participanteId": "1",
  "eventoId": "0"
}
```
Response 200:
```json
{
  "id": "<id>",
  "participante": { "id": "1", "nombre": "Carlos", "apellido": "López", "dni": "11111111" },
  "fechaRegistro": "2025-08-27T12:05:00",
  "estado": { "tipoEstado": "ACEPTADA", "fechaCambio": "2025-08-27T12:05:00" },
  "evento": { "id": "0", "titulo": "Seminario de Mocks" }
}
```

### 3. Listar Eventos de un Organizador
GET /organizadores/eventos/{id_organizador}

Response 200:
```json
[
  {
    "id": "0",
    "titulo": "Seminario de Mocks",
    "descripcion": "Mocks",
    "fecha": "2025-08-27T19:00:00",
    "horaInicio": "19:00",
    "duracion": 5.0,
    "ubicacion": { "provincia": "", "ciudad": "", "direccion": "" },
    "cupoMaximo": 10,
    "precio": { "moneda": "Pesos", "monto": 100.0 },
    "organizador": { "id": "1", "nombre": "Juan", "apellido": "Pérez", "dni": "12345678" },
    "estado": { "tipoEstado": "CONFIRMADO", "fechaCambio": "2025-08-27T12:00:00" }
  }
]
```
Response 204: lista vacía.

### 4. Listar Inscripciones de un Participante
GET /participantes/inscripciones/{id_usuario}

Response 200:
```json
[
  {
    "id": "1",
    "participante": { "id": "1", "nombre": "Carlos", "apellido": "López", "dni": "11111111" },
    "fechaRegistro": "2025-08-27T12:05:00",
    "estado": { "tipoEstado": "ACEPTADA", "fechaCambio": "2025-08-27T12:05:00" },
    "evento": { "id": "0", "titulo": "Seminario de Mocks", "descripcion": "Mocks" }
  }
]
```
Response 204: lista vacía.

### 5. Estadísticas de Uso
GET /estadisticas

Response 200 (ejemplo):
```json
{
  "cantidad_eventos": 1,
  "cantidad_eventos_activos": 1,
  "cantidad_inscripciones_totales": 2,
  "cantidad_inscripciones_confirmadas": 1,
  "cantidad_inscripciones_waitlist": 1,
  "tasa_conversion_waitlist": 50.0,
  "evento_mas_popular": "Seminario de Mocks",
  "promedio_inscripciones_por_evento": 2.0
}
```

