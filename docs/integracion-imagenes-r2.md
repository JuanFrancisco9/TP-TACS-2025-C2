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

### application.properties
```properties
# Cloudflare R2
cloudflare.r2.endpoint=https://cf3e92705ed9dff7747f837a3fcc0d82.r2.cloudflarestorage.com/images
cloudflare.r2.bucket=images
cloudflare.r2.access-key=c036b5dfa1ef00c140c54bd2bbb65ee0
cloudflare.r2.secret-key=8985cc297a7e9bf330a38259959bad265a12f27ab195432baec10b3c97a6b534
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
