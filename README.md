# TP TACS 2025-2C  Grupo 2

El objetivo del TP es desarrollar una aplicación que permita a los usuarios publicar eventos y que otros usuarios se registren respetando el cupo disponible, gestionando automáticamente una lista de espera (waitlist) cuando se llena el cupo.

[Enunciado](https://docs.google.com/document/d/e/2PACX-1vRKgz7eEA1fIByKMtXKxA6-Vs1rSst8cwUeTkMnZyYrDPkzkUECyK7WXqXWFSh5jwnxJMdanffdyWzB/pub)

## Arquitectura

<img width="1064" height="574" alt="image" src="https://github.com/user-attachments/assets/b5709a62-aea6-432e-a342-6513ae6aac21" />

## URL: 

## Stack Tecnológico
- Backend: Java 21, SpringBoot 3.5.4, Maven
- Frontend: React, TypeScript Vite
- Base de datos: MongoDB y Redis


## Ejecutar en Local

``` bash
docker compose up
```

Levanta la aplicación en el puerto 8080

Se puede comprobar que se levantó correctamente haciendo:

```bash
curl http://localhost:8080/actuator/health
```

Y viendo que el status devuelto sea UP.

Para incializar la DB con datos: ``` docker compose -f compose.yaml -f compose-init.yaml up ```

## Entrega 2 - Interfaz de Usuario (UI)

### Configuración del Frontend

Para ejecutar la interfaz de usuario, es necesario:

1. **Crear archivo de configuración .env**:
   - Copiar el archivo `frontend/.env.example` como `frontend/.env`
   - Este archivo contiene la configuración para conectar con el backend:

   ```bash
   # Desde la carpeta raíz del proyecto
   cp frontend/.env.example frontend/.env
   ```

2. **Instalar dependencias y ejecutar**:
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

   La aplicación estará disponible en: http://localhost:5173

### Usuarios Precargados para Pruebas

El sistema incluye usuarios de prueba con diferentes roles:

| Rol | Username | Password |
|-----|----------|----------|
| Usuario | `usuario` | `usuario` |
| Organizador | `organizador` | `organizador` |
| Administrador | `admin` | `admin` |



## Endpoints

### 1. Crear Evento
POST /evento

Request JSON:
```json
{
  "organizadorId": "1",
  "titulo": "Seminario de Mocks",
  "descripcion": "Introducción a Mocks",
  "fecha": "2025-09-10T19:00:00",
  "horaInicio": "19:00",
  "duracion": 2.5,
  "ubicacion": {
    "provincia": "Buenos Aires",
    "ciudad": "CABA",
    "direccion": "Av. Siempre Viva 123"
  },
  "cupoMaximo": 30,
  "cupoMinimo": 10,
  "precio": {
    "moneda": "ARS",
    "monto": 1000
  },
  "estado": "CONFIRMADO",
  "categoria": {
    "tipo": "TECNOLOGIA"
  },
  "etiquetas": ["mocks", "testing", "java"]
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
  "estado": { "tipoEstado": "CONFIRMADO", "fechaCambio": "2025-08-27T12:00:00" },
  "categoria":{"tipo": "TECNOLOGIA"},
  "etiquetas": ["mocks", "testing", "java"]
}
```

### 2. Crear Inscripción
POST /inscripcion

Request JSON:
```json
{
  "participante": {
    "id": "1",
    "nombre": "Carlos",
    "apellido": "López",
    "dni": "11111111"
  },
  "evento_id": "1"
}
```
Response 200:
```json
{
  "id": "<id>",
  "participante": {
    "id": "1",
    "nombre": "Carlos",
    "apellido": "López",
    "dni": "11111111"
  },
  "fechaRegistro": "2025-08-27T12:05:00",
  "estado": { "tipoEstado": "ACEPTADA", "fechaCambio": "2025-08-27T12:05:00" },
  "evento": {
    "id": "1",
    "titulo": "Seminario de Mocks",
    "descripcion": "Mocks"
  }
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

#### 5.1. Estadísticas Generales (Personalizables)
GET /estadisticas

Parámetros de consulta opcionales:
- `fechaDesde` (LocalDate): Fecha desde para filtrar (formato: YYYY-MM-DD)
- `fechaHasta` (LocalDate): Fecha hasta para filtrar (formato: YYYY-MM-DD)  
- `estadisticas` (Set<TipoEstadistica>): Estadísticas específicas a calcular

Ejemplo con filtros:
```
GET /estadisticas?fechaDesde=2025-01-01&fechaHasta=2025-12-31&estadisticas=CANTIDAD_EVENTOS,CANTIDAD_EVENTOS_ACTIVOS
```

Response 200 (ejemplo):
```json
{
  "cantidad_eventos": 1,
  "cantidad_eventos_activos": 1,
  "cantidad_inscripciones_totales": null,
  "cantidad_inscripciones_confirmadas": null,
  "cantidad_inscripciones_waitlist": null,
  "tasa_conversion_waitlist": null,
  "evento_mas_popular": null,
  "promedio_inscripciones_por_evento": null
}
```

#### 5.2. Estadísticas Completas
GET /estadisticas/completas

Parámetros de consulta opcionales:
- `fechaDesde` (LocalDate): Fecha desde para filtrar
- `fechaHasta` (LocalDate): Fecha hasta para filtrar

Response 200: Mismo formato que estadísticas generales con todas las estadísticas calculadas.

#### 5.3. Estadísticas Individuales

##### Cantidad de Eventos
GET /estadisticas/eventos/cantidad
Parámetros: `fechaDesde`, `fechaHasta` (opcionales)
Response 200: `15` (Integer)

##### Cantidad de Eventos Activos  
GET /estadisticas/eventos/activos
Parámetros: `fechaDesde`, `fechaHasta` (opcionales)
Response 200: `8` (Integer)

##### Total de Inscripciones
GET /estadisticas/inscripciones/totales
Parámetros: `fechaDesde`, `fechaHasta` (opcionales)
Response 200: `120` (Integer)

##### Inscripciones Confirmadas
GET /estadisticas/inscripciones/confirmadas
Parámetros: `fechaDesde`, `fechaHasta` (opcionales)
Response 200: `95` (Integer)

##### Inscripciones en Waitlist
GET /estadisticas/inscripciones/waitlist
Parámetros: `fechaDesde`, `fechaHasta` (opcionales)
Response 200: `25` (Integer)

##### Tasa de Conversión Waitlist
GET /estadisticas/conversion/waitlist
Parámetros: `fechaDesde`, `fechaHasta` (opcionales)
Response 200: `60.0` (Double - porcentaje)

##### Evento Más Popular
GET /estadisticas/eventos/mas-popular
Parámetros: `fechaDesde`, `fechaHasta` (opcionales)
Response 200: `"Seminario de Mocks"` (String)

##### Promedio de Inscripciones por Evento
GET /estadisticas/inscripciones/promedio-por-evento
Parámetros: `fechaDesde`, `fechaHasta` (opcionales)
Response 200: `8.0` (Double)

### 6. Registrar Usuario
POST /user

Request JSON:
```json
{
  "username": "nuevoUser",
  "password": "secreta",
  "rol": "ROLE_USER"
}
```
Response 201:
```json
"Usuario registrado correctamente"
```

### 7. Listar Usuarios (solo ADMIN)
GET /user

Response 200:
```json
[
  { "username": "admin", "rol": "ROLE_ADMIN" },
  { "username": "organizador", "rol": "ROLE_ORGANIZER" },
  { "username": "usuario", "rol": "ROLE_USER" }
]
```
Response 403 si no tiene rol adecuado (si seguridad activa).

### 8. Login
POST /login

Request JSON:
```json
{
  "username": "admin",
  "password": "admin123"
}
```
Response 200: sin body.

Response 401:
```text
Unauthorized
```
