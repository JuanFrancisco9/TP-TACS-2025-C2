require('dotenv').config();
const path = require('path');

const config = {
  // Telegram Bot Configuration
  telegram: {
    token: process.env.TELEGRAM_BOT_TOKEN,
    username: process.env.BOT_USERNAME || 'tptacs_bot',
    description: process.env.BOT_DESCRIPTION || 'Bot para gestión de eventos TP-TACS'
  },
  
  // Data Configuration
  data: {
    jsonFile: path.join(__dirname, 'data.json'),
    useJsonFile: process.env.USE_JSON_FILE === 'true' || false // Default to API
  },
  
  // API Configuration
  api: {
    baseUrl: process.env.API_BASE_URL || 'http://localhost:8080',
    timeout: parseInt(process.env.API_TIMEOUT) || 5000,
    endpoints: {
      login: '/login',
      eventos: '/eventos',
      inscripciones: '/inscripciones',
      estadisticas: '/estadisticas',
      usuarios: '/user',
      participantes: '/participantes',
      organizadores: '/organizadores',
      waitlist: '/waitlist'
    }
  },
  
  // Bot Commands
  commands: {
    start: '/start',
    help: '/help',
    login: '/login',
    logout: '/logout',
    eventos: '/eventos',
    mis_eventos: '/miseventos',
    inscripciones: '/inscripciones',
    estadisticas: '/estadisticas',
  },
  
  // Messages
  messages: {
    welcome: '¡Hola! Soy el bot del grupo 2 del TP-TACS. Puedo ayudarte a consultar información sobre eventos e inscripciones.\n\n Para ver los comandos disponibles utiliza /help',
    help: `Comandos basicos:
/start - Iniciar el bot
/help - Mostrar esta ayuda
/login - Iniciar sesión
/logout - Cerrar sesión
/eventos - Ver eventos disponibles`,
    helpUser: `Comandos adicionales:
/inscripciones - Ver todas tus inscripciones
/confirmadas - Ver las inscripciones confirmadas
/pendientes - Ver las inscripciones pendientes`,
    helpOrg: `Comandos adicionales: 
/mis_eventos - Ver todos tus eventos
/publicar_evento`,
    helpAdmin: `Comandos adicionales:
/estadisticas - Ver estadísticas completas`,
    error: 'Lo siento, ocurrió un error. Por favor intenta de nuevo.',
    noData: 'No hay datos disponibles en este momento.',
    loginPrompt: `🔐 Para iniciar sesión, enviá tu usuario y contraseña en el formato:
<code>usuario:contraseña</code>

Ejemplo: <code>pepe:casa123</code>

Si todavía no te registraste, podés hacerlo acá 👉 <a href="${process.env.FRONT_URL}">Registrarme</a>`,
    loginSuccess: '✅ ¡Inicio de sesión exitoso! Bienvenido/a',
    loginError: '❌ Usuario o contraseña incorrectos. Intenta de nuevo.',
    logoutSuccess: '👋 Sesión cerrada exitosamente.',
    notLoggedIn: '🔒 Debes iniciar sesión primero. Usa /login',
    alreadyLoggedIn: 'ℹ️ Ya tienes una sesión activa. Usa /logout para cerrar sesión.',
    userAlreadyLoggedIn: '⚠️ Este usuario ya tiene una sesión activa en otro chat.',
    noPermission: '⚠️ No tienes permisos para ejecutar ese comando'
  }
};

module.exports = config;
