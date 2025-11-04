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
      estadisticas: '/estadisticas/completas',
      usuarios: '/user'
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
    estadisticas: '/estadisticas'
  },
  
  // Messages
  messages: {
    welcome: '¡Hola! Soy el bot de TP-TACS. Puedo ayudarte a gestionar eventos y consultar información.',
    help: `Comandos disponibles:
/start - Iniciar el bot
/help - Mostrar esta ayuda
/login - Iniciar sesión
/logout - Cerrar sesión
/eventos - Ver eventos disponibles
/miseventos - Ver mis eventos
/inscripciones - Ver todas las inscripciones
/estadisticas - Ver estadísticas`,
    error: 'Lo siento, ocurrió un error. Por favor intenta de nuevo.',
    noData: 'No hay datos disponibles en este momento.',
    loginPrompt: '🔐 Para iniciar sesión, envía tu usuario y contraseña en el formato:\n`usuario:contraseña`\n\nEjemplo: `admin:admin123`',
    loginSuccess: '✅ ¡Inicio de sesión exitoso! Bienvenido/a',
    loginError: '❌ Usuario o contraseña incorrectos. Intenta de nuevo.',
    logoutSuccess: '👋 Sesión cerrada exitosamente.',
    notLoggedIn: '🔒 Debes iniciar sesión primero. Usa /login',
    alreadyLoggedIn: 'ℹ️ Ya tienes una sesión activa. Usa /logout para cerrar sesión.',
    userAlreadyLoggedIn: '⚠️ Este usuario ya tiene una sesión activa en otro chat.'
  }
};

module.exports = config;
