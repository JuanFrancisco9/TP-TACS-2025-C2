# Integración de Imágenes con Cloudflare R2

## Resumen
Se ha completado la integración de imágenes con Cloudflare R2 para el sistema de eventos. Las imágenes se almacenan en R2 y la metadata se persiste en MongoDB.

## Componentes Implementados

### 1. Modelo de Datos
- **Imagen**: Modelo para almacenar metadata de imágenes en MongoDB
- **Evento**: Actualizado con campo `imagenKey` para asociar imágenes

### 2. Servicios
- **R2StorageService**: Maneja la subida, descarga y eliminación de imágenes en R2
- **ImagenRepository**: Repositorio para persistir metadata en MongoDB

### 3. Controlador
- **ImagenController**: Endpoints REST para manejar imágenes

## Endpoints Disponibles

### Subir Imagen
```
POST /api/imagenes/upload
Content-Type: multipart/form-data

Parámetros:
- file: Archivo de imagen (máximo 5MB)
- Requiere autenticación

Respuesta:
{
  "id": "uuid-de-la-imagen",
  "key": "imagenes/2025/01/uuid.jpg",
  "url": "https://images/imagenes/2025/01/uuid.jpg",
  "originalName": "foto.jpg",
  "contentType": "image/jpeg",
  "sizeBytes": 123456
}
```

### Obtener Imagen
```
GET /api/imagenes/{key}
Respuesta: Archivo binario de la imagen
```

### Obtener Información de Imagen
```
GET /api/imagenes/{key}/info
Respuesta:
{
  "id": "uuid-de-la-imagen",
  "key": "imagenes/2025/01/uuid.jpg",
  "url": "https://images/imagenes/2025/01/uuid.jpg",
  "originalName": "foto.jpg",
  "contentType": "image/jpeg",
  "sizeBytes": 123456,
  "etag": "etag-del-archivo",
  "ownerUserId": 123
}
```

### Eliminar Imagen
```
DELETE /api/imagenes/{key}
Requiere autenticación y ser propietario de la imagen
```

## Configuración

### Variables de entorno requeridas
Defin� los siguientes valores (por ejemplo en un archivo `.env` cargado por docker compose):
```env
CLOUDFLARE_R2_ENDPOINT=https://<tu-cuenta>.r2.cloudflarestorage.com/images
CLOUDFLARE_R2_BUCKET=images
CLOUDFLARE_R2_ACCESS_KEY=<tu-access-key>
CLOUDFLARE_R2_SECRET_KEY=<tu-secret-key>
CLOUDFLARE_R2_PUBLIC_BASE_URL=https://<tu-cuenta>.r2.dev/images
```

### application.properties
```properties
# Cloudflare R2
cloudflare.r2.endpoint=${CLOUDFLARE_R2_ENDPOINT}
cloudflare.r2.bucket=${CLOUDFLARE_R2_BUCKET:images}
cloudflare.r2.access-key=${CLOUDFLARE_R2_ACCESS_KEY}
cloudflare.r2.secret-key=${CLOUDFLARE_R2_SECRET_KEY}
cloudflare.r2.public-base-url=${CLOUDFLARE_R2_PUBLIC_BASE_URL}
```

## Uso en el Frontend

### Subir Imagen
```javascript
const uploadImage = async (file) => {
  const formData = new FormData();
  formData.append('file', file);
  
  const response = await fetch('/api/imagenes/upload', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`
    },
    body: formData
  });
  
  return await response.json();
};
```

### Mostrar Imagen
```javascript
const getImageUrl = (imageKey) => {
  return `/api/imagenes/${imageKey}`;
};

// En JSX
<img src={getImageUrl(evento.imagenKey)} alt="Imagen del evento" />
```

## Estructura de Almacenamiento

### En R2
```
imagenes/
  └── 2025/
      └── 01/
          └── uuid-archivo.jpg
```

### En MongoDB
```json
{
  "_id": "uuid-de-la-imagen",
  "bucket": "images",
  "key": "imagenes/2025/01/uuid-archivo.jpg",
  "contentType": "image/jpeg",
  "sizeBytes": 123456,
  "originalName": "foto.jpg",
  "etag": "etag-del-archivo",
  "ownerUserId": 123
}
```

## Validaciones Implementadas

1. **Tipo de archivo**: Solo imágenes (content-type que empiece con "image/")
2. **Tamaño**: Máximo 5MB
3. **Autenticación**: Requerida para subir y eliminar
4. **Autorización**: Solo el propietario puede eliminar sus imágenes

## Próximos Pasos

1. Actualizar el EventoController para manejar el campo imagenKey
2. Modificar el formulario de creación de eventos para incluir subida de imagen
3. Actualizar la visualización de eventos para mostrar las imágenes
4. Implementar redimensionamiento de imágenes si es necesario
