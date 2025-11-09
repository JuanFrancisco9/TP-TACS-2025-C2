# Telegram Bot - TP-TACS

Bot de Telegram para la gestión de eventos del sistema TP-TACS con integración completa al backend API.

## Características

- 📅 Consulta de eventos disponibles desde el backend
- 📊 Visualización de estadísticas del sistema en tiempo real
- 🔐 Autenticación completa con el backend API
- 🔗 Integración con la API del backend Spring Boot
- ⚙️ Configuración parametrizada (JSON o API)
- 👤 Gestión de sesiones de usuario

## Instalación

1. Instalar dependencias:
```bash
npm install
```

2. Configurar variables de entorno:
```bash
cp env.example .env
```

3. Editar el archivo `.env` con tus configuraciones:
```env
TELEGRAM_BOT_TOKEN=tu_token_del_bot
API_BASE_URL=http://localhost:8080
USE_JSON_FILE=false
BOT_USERNAME=tu_bot_username
```

## Configuración

### Variables de Entorno

- `TELEGRAM_BOT_TOKEN`: Token del bot de Telegram (obtenido de @BotFather)
- `API_BASE_URL`: URL base de la API del backend (default: http://localhost:8080)
- `USE_JSON_FILE`: Usar archivo JSON en lugar de API (default: false)
- `API_TIMEOUT`: Timeout para las peticiones HTTP (default: 5000ms)
- `BOT_USERNAME`: Nombre de usuario del bot
- `BOT_DESCRIPTION`: Descripción del bot

### Archivo de Configuración

El archivo `config.js` centraliza toda la configuración del bot, incluyendo:
- Configuración de Telegram
- URLs de la API y endpoints
- Comandos disponibles
- Mensajes del bot

## Uso

### Iniciar el bot

```bash
npm start
```

### Modo desarrollo (con auto-reload)

```bash
npm run dev
```

## Comandos Disponibles

- `/start` - Iniciar el bot
- `/help` - Mostrar ayuda
- `/login` - Iniciar sesión con el backend
- `/logout` - Cerrar sesión
- `/eventos` - Ver eventos disponibles desde el backend
- `/miseventos` - Ver mis eventos (requiere autenticación)
- `/inscripciones` - Ver todas las inscripciones
- `/estadisticas` - Ver estadísticas del sistema desde el backend

## Estructura del Proyecto

```
bot_telegram/
├── index.js          # Archivo principal del bot
├── config.js         # Configuración centralizada
├── package.json      # Dependencias y scripts
├── env.example       # Ejemplo de variables de entorno
└── README.md         # Este archivo
```

## Integración con la API

El bot se conecta a la API del backend Spring Boot usando la URL base configurada. Los endpoints utilizados son:

- `POST /login` - Autenticación de usuarios
- `GET /eventos` - Obtener lista de eventos con filtros
- `GET /eventos/{id}` - Obtener evento específico
- `GET /estadisticas/completas` - Obtener estadísticas completas del sistema
- `POST /inscripciones` - Crear inscripciones
- `GET /user` - Gestión de usuarios (solo ADMIN)

### Autenticación

El bot implementa autenticación completa con el backend:
- Login mediante username/password
- Gestión de sesiones activas
- Comandos protegidos que requieren autenticación

## Desarrollo

### Agregar nuevos comandos

1. Agregar el comando en `config.js`
2. Implementar el handler en `index.js`
3. Actualizar el mensaje de ayuda

### Modificar la URL base

La URL base se puede cambiar modificando la variable `API_BASE_URL` en el archivo `.env` o directamente en `config.js`.

## Troubleshooting

### Error de conexión con la API

- Verificar que `API_BASE_URL` esté correctamente configurada
- Asegurarse de que el backend esté ejecutándose
- Revisar los logs del bot para errores específicos

### Bot no responde

- Verificar que `TELEGRAM_BOT_TOKEN` sea válido
- Comprobar que el bot esté iniciado correctamente
- Revisar los logs para errores de polling

## Licencia

MIT

