const TelegramBot = require('node-telegram-bot-api');
const fs = require('fs').promises;
const axios = require('axios');
const config = require('./config');

// Initialize bot
const bot = new TelegramBot(config.telegram.token, { polling: true });

// Store active sessions (chatId -> user)
const activeSessions = new Map();

// API client configuration
function getAuthToken(chatId) {
    const session = activeSessions.get(chatId);
    return session ? session.authToken : null;
}

const apiClient = axios.create({
  baseURL: config.api.baseUrl,
  timeout: config.api.timeout,
  headers: {
    'Content-Type': 'application/json'
  }
});

apiClient.interceptors.request.use(
    (config) => {
        const chatId = config.chatId; // propiedad personalizada

        if (chatId) {
            const token = getAuthToken(chatId);
            if (token) {
                config.headers = config.headers || {};
                config.headers['Authorization'] = `Basic ${token}`;
            }
        }

        return config;
    },
    (error) => Promise.reject(error)
);

function makeBasicAuthHeader(username, password) {
    return btoa(`${username}:${password}`);
}

// Helper function to read JSON data
async function readJsonData() {
  try {
    const data = await fs.readFile(config.data.jsonFile, 'utf8');
    return JSON.parse(data);
  } catch (error) {
    throw error;
  }
}

// Helper function to get data (JSON or API)
async function getData(type) {
  if (config.data.useJsonFile) {
    const jsonData = await readJsonData();
    return jsonData[type];
  } else {
    // API implementation
    try {
      let endpoint;
      switch (type) {
        case 'eventos':
          endpoint = config.api.endpoints.eventos;
          break;
        case 'estadisticas':
          endpoint = config.api.endpoints.estadisticas;
          break;
        case 'inscripciones':
          endpoint = config.api.endpoints.inscripciones;
          break;
        default:
          throw new Error(`Unknown data type: ${type}`);
      }
      
      const response = await apiClient.get(endpoint);
      return response.data;
    } catch (error) {
      throw error;
    }
  }
}

// Helper function to write JSON data
async function writeJsonData(data) {
  try {
    await fs.writeFile(config.data.jsonFile, JSON.stringify(data, null, 2), 'utf8');
  } catch (error) {
    throw error;
  }
}

// Helper function to authenticate user
async function authenticateUser(username, password) {
  if (config.data.useJsonFile) {
    const data = await readJsonData();
    const user = data.usuarios.find(u => u.username === username && u.password === password);
    return user;
  } else {
    // API authentication
    try {
      const response = await apiClient.post(config.api.endpoints.login, {
        username: username,
        password: password
      });
      const authToken = makeBasicAuthHeader(username,password)
      return {
        id: response.data.id,
        username: response.data.username,
        nombre: response.data.username, // API doesn't return nombre, using username
        tipo: response.data.rol,
        actorId: response.data.actorId,
        authToken
      };
    } catch (error) {
      if (error.response && error.response.status === 401) {
        return null; // Invalid credentials
      }
      throw error;
    }
  }
}

// Helper function to check if user is logged in
function isUserLoggedIn(chatId) {
  return activeSessions.has(chatId);
}

// Helper function to get current user
function getCurrentUser(chatId) {
  return activeSessions.get(chatId);
}

// Helper function to login user
async function loginUser(chatId, username, password) {
  const user = await authenticateUser(username, password);
  if (!user) {
    return false;
  }
  
  if (config.data.useJsonFile) {
    // Check if user is already logged in elsewhere
    const data = await readJsonData();
    const existingUser = data.usuarios.find(u => u.id === user.id && u.telegramId);
    if (existingUser && existingUser.telegramId !== chatId.toString()) {
      return 'already_logged_in';
    }
    
    // Update telegramId in data
    const userIndex = data.usuarios.findIndex(u => u.id === user.id);
    if (userIndex !== -1) {
      data.usuarios[userIndex].telegramId = chatId.toString();
      await writeJsonData(data);
    }
  }
  
  // Store session
  activeSessions.set(chatId, user);
  return true;
}

// Helper function to logout user
async function logoutUser(chatId) {
  const user = getCurrentUser(chatId);
  if (user) {
    if (config.data.useJsonFile) {
      // Clear telegramId in data
      const data = await readJsonData();
      const userIndex = data.usuarios.findIndex(u => u.id === user.id);
      if (userIndex !== -1) {
        data.usuarios[userIndex].telegramId = null;
        await writeJsonData(data);
      }
    }
    
    // Remove session
    activeSessions.delete(chatId);
    return true;
  }
  return false;
}

// Helper function to format event data
function formatEvent(event) {
  const fechaInicio = new Date(event.fecha).toLocaleDateString('es-ES', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
  
  return `🎯 *${event.titulo}*
📅 Fecha: ${fechaInicio}
📍 Ubicación: ${event.ubicacion.esVirtual? event.ubicacion.enlaceVirtual : event.ubicacion.direccion}
👥 Participantes: ${event.cupoMaximo || 'N/A'}
📝 Descripción: ${event.descripcion || 'Sin descripción'}
🏷️ Categoría: ${event.categoria?.nombre || 'Sin categoría'}
✅ Estado: ${event.estado.tipoEstado}
💰 Precio: ${event.precio?.monto || 'Gratis'} ${event.precio?.moneda || ''}`;
}

// Helper function to format statistics
function formatStatistics(stats) {
  return `📊 *Estadísticas del Sistema*

🎯 Total Eventos: ${stats.cantidadEventos || 0}
✅ Eventos Activos: ${stats.cantidadEventosActivos || 0}
📝 Total Inscripciones: ${stats.cantidadInscripcionesTotales || 0}
✅ Inscripciones Confirmadas: ${stats.cantidadInscripcionesConfirmadas || 0}
⏳ Inscripciones en Waitlist: ${stats.cantidadInscripcionesWaitlist || 0}
📈 Tasa Conversión Waitlist: ${(stats.tasaConversionWaitlist || 0).toFixed(2)}%
🏆 Evento Más Popular: ${stats.eventoMasPopular || 'N/A'}
📊 Promedio Inscripciones/Evento: ${(stats.promedioInscripcionesPorEvento || 0).toFixed(2)}`;
}

// Helper function to format inscription data
function formatInscription(inscription, evento, participante) {
  const fechaInscripcion = new Date(inscription.fechaRegistro).toLocaleDateString('es-ES', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });

  const fechaUltimaModificacion =  new Date(inscription.estado.fechaDeCambio).toLocaleDateString('es-ES', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
  });
  return `📝 *Inscripción*
🎯 Evento: ${evento.titulo}
👤 Participante: ${participante.nombre}
📅 Fecha de Inscripción: ${fechaInscripcion}
✅ Estado: ${inscription.estado.tipoEstado}
📍 Ubicación: ${evento.ubicacion.esVirtual? evento.ubicacion.enlaceVirtual : evento.ubicacion.direccion}

Ultima Modificación: ${fechaUltimaModificacion}`;
}

// Start command
bot.onText(/\/start/, (msg) => {
  const chatId = msg.chat.id;
  bot.sendMessage(chatId, config.messages.welcome);
});

// Help command
bot.onText(/\/help/, (msg) => {
  const chatId = msg.chat.id;
  const user = activeSessions.get(chatId);

  if(user){
      switch (user.tipo) {
          case 'ROLE_USER' :
              bot.sendMessage(chatId, `${config.messages.help}\n\n${config.messages.helpUser}`);
              break
          case 'ROLE_ORGANIZER':
              bot.sendMessage(chatId, `${config.messages.help}\n\n${config.messages.helpOrg}`);
              break
          case 'ROLE_ADMIN':
              bot.sendMessage(chatId, `${config.messages.help}\n\n${config.messages.helpAdmin}`);
              break
          default:
              bot.sendMessage(chatId, config.messages.help);
              break
      }
  }else{
      bot.sendMessage(chatId, config.messages.help);
  }
});

// Login command
bot.onText(/\/login/, async (msg) => {
  const chatId = msg.chat.id;
  
  if (isUserLoggedIn(chatId)) {
    bot.sendMessage(chatId, config.messages.alreadyLoggedIn);
    return;
  }
  
  bot.sendMessage(chatId, config.messages.loginPrompt, { parse_mode: 'Markdown' });
});

// Logout command
bot.onText(/\/logout/, async (msg) => {
  const chatId = msg.chat.id;
  
  try {
    const success = await logoutUser(chatId);
    if (success) {
      bot.sendMessage(chatId, config.messages.logoutSuccess);
    } else {
      bot.sendMessage(chatId, config.messages.notLoggedIn);
    }
  } catch (error) {
    bot.sendMessage(chatId, config.messages.error);
  }
});

// Events command - Get all events
bot.onText(/\/eventos/, async (msg) => {
  const chatId = msg.chat.id;
  
  try {
    bot.sendMessage(chatId, '🔍 Buscando eventos disponibles...');
    
    const data = await getData('eventos');
    const eventos = data.eventos
    
    if (!eventos || eventos.length === 0) {
      bot.sendMessage(chatId, config.messages.noData);
      return;
    }
    
    // Send all events (since we only have 1 for now)
    eventos.forEach((evento, index) => {
      setTimeout(() => {
        bot.sendMessage(chatId, formatEvent(evento), { parse_mode: 'Markdown' });
      }, index * 1000); // Delay between messages
    });
    
  } catch (error) {
      console.log(error)
      bot.sendMessage(chatId, config.messages.error);
  }
});

//Get user's inscriptions
bot.onText(/\/inscripciones/, async (msg) => {
  const chatId = msg.chat.id;

  if (!isUserLoggedIn(chatId)) {
    bot.sendMessage(chatId, config.messages.notLoggedIn);
    return;
  }
  const user = activeSessions.get(chatId);
  //console.log(user)
  try {
    bot.sendMessage(chatId, '🔍 Buscando tus inscripciones...');

      // API mode - for now, show a message that this feature needs API enhancement
      endpoint = `${config.api.endpoints.participantes}/inscripciones/${user.actorId}`
      const response = await apiClient.get(endpoint, {chatId})
      const inscripciones = response.data
      if(!inscripciones){
          bot.sendMessage(chatId, config.messages.noData);
          return;
      }
      inscripciones.forEach((inscripcion, index) => {
          setTimeout(() => {
              bot.sendMessage(chatId, formatInscription(inscripcion, inscripcion.evento, inscripcion.participante), { parse_mode: 'Markdown' });
          }, index * 1000); // Delay between messages
      });
    
  } catch (error) {
    console.log(error)
    bot.sendMessage(chatId, config.messages.error);
  }
});

bot.onText(/\/confirmadas/, async (msg) => {
    const chatId = msg.chat.id;

    if (!isUserLoggedIn(chatId)) {
        bot.sendMessage(chatId, config.messages.notLoggedIn);
        return;
    }
    const user = activeSessions.get(chatId);
    //console.log(user)
    try {
        bot.sendMessage(chatId, '🔍 Buscando tus inscripciones confirmadas...');

        // API mode - for now, show a message that this feature needs API enhancement
        endpoint = `${config.api.endpoints.participantes}/inscripciones/${user.actorId}`
        const response = await apiClient.get(endpoint, {chatId})
        const inscripciones = response.data
        if(!inscripciones){
            bot.sendMessage(chatId, config.messages.noData);
            return;
        }
        const inscripcionesConfirmadas = inscripciones.filter(i => i.estado.tipoEstado === 'ACEPTADA')
        if(inscripcionesConfirmadas.length === 0){
            bot.sendMessage(chatId, config.messages.noData);
            return;
        }
        inscripcionesConfirmadas.forEach((inscripcion, index) => {
            setTimeout(() => {
                bot.sendMessage(chatId, formatInscription(inscripcion, inscripcion.evento, inscripcion.participante), { parse_mode: 'Markdown' });
            }, index * 1000); // Delay between messages
        });

    } catch (error) {
        console.log(error)
        bot.sendMessage(chatId, config.messages.error);
    }
});
bot.onText(/\/pendientes/, async (msg) => {
    const chatId = msg.chat.id;

    if (!isUserLoggedIn(chatId)) {
        bot.sendMessage(chatId, config.messages.notLoggedIn);
        return;
    }
    const user = activeSessions.get(chatId);
    //console.log(user)
    try {
        bot.sendMessage(chatId, '🔍 Buscando tus inscripciones pendientes...');

        // API mode - for now, show a message that this feature needs API enhancement
        endpoint = `${config.api.endpoints.participantes}/inscripciones/${user.actorId}`
        const response = await apiClient.get(endpoint, {chatId})
        const inscripciones = response.data
        if(!inscripciones){
            bot.sendMessage(chatId, config.messages.noData);
            return;
        }
        const inscripcionesPendientes = inscripciones.filter(i => i.estado.tipoEstado === 'PENDIENTE')
        if(inscripcionesPendientes.length === 0){
            bot.sendMessage(chatId, config.messages.noData);
            return;
        }
        inscripcionesPendientes.forEach((inscripcion, index) => {
            setTimeout(() => {
                bot.sendMessage(chatId, formatInscription(inscripcion, inscripcion.evento, inscripcion.participante), { parse_mode: 'Markdown' });
            }, index * 1000); // Delay between messages
        });

    } catch (error) {
        console.log(error)
        bot.sendMessage(chatId, config.messages.error);
    }
});

bot.onText(/\/miseventos/, async (msg) => {
    const chatId = msg.chat.id;

    if (!isUserLoggedIn(chatId)) {
        bot.sendMessage(chatId, config.messages.notLoggedIn);
        return;
    }
    const user = activeSessions.get(chatId);
    //console.log(user)
    try {
        bot.sendMessage(chatId, '🔍 Buscando tus eventos...');

        // API mode - for now, show a message that this feature needs API enhancement
        endpoint = `${config.api.endpoints.organizadores}/eventos/${user.actorId}`
        const response = await apiClient.get(endpoint, {chatId})
        const eventos = response.data
        if(!eventos){
            bot.sendMessage(chatId, config.messages.noData);
            return;
        }
        eventos.forEach((evento, index) => {
            setTimeout(() => {
                bot.sendMessage(chatId, formatEvent(evento), { parse_mode: 'Markdown' });
            }, index * 1000); // Delay between messages
        });

    } catch (error) {
        console.log(error)
        bot.sendMessage(chatId, config.messages.error);
    }
});


// Statistics command
bot.onText(/\/estadisticas/, async (msg) => {
  const chatId = msg.chat.id;
  
  try {
    bot.sendMessage(chatId, '📊 Obteniendo estadísticas...');
    
    const estadisticas = await getData('estadisticas');
    
    if (!estadisticas) {
      bot.sendMessage(chatId, config.messages.noData);
      return;
    }
    
    bot.sendMessage(chatId, formatStatistics(estadisticas), { parse_mode: 'Markdown' });
    
  } catch (error) {
    bot.sendMessage(chatId, config.messages.error);
  }
});

// Handle any other text messages
bot.on('message', async (msg) => {
  const chatId = msg.chat.id;
  const text = msg.text;
  
  // Only respond to text messages that aren't commands
  if (text && !text.startsWith('/')) {
    // Check if this looks like login credentials (username:password)
    if (text.includes(':') && text.split(':').length === 2) {
      if (isUserLoggedIn(chatId)) {
        bot.sendMessage(chatId, config.messages.alreadyLoggedIn);
        return;
      }
      
      try {
        const [username, password] = text.split(':');
        const result = await loginUser(chatId, username.trim(), password.trim());
        
        if (result === true) {
          const user = getCurrentUser(chatId);
          bot.sendMessage(chatId, `${config.messages.loginSuccess} ${user.nombre}!`);
        } else if (result === 'already_logged_in') {
          bot.sendMessage(chatId, config.messages.userAlreadyLoggedIn);
        } else {
          bot.sendMessage(chatId, config.messages.loginError);
        }
      } catch (error) {
        bot.sendMessage(chatId, config.messages.error);
      }
    } else {
      bot.sendMessage(chatId, 'No entiendo ese mensaje. Usa /help para ver los comandos disponibles.');
    }
  }
});

// Error handling
bot.on('polling_error', (error) => {
  // Silent error handling
});

bot.on('error', (error) => {
  // Silent error handling
});

// Graceful shutdown
process.on('SIGINT', () => {
  bot.stopPolling();
  process.exit(0);
});

process.on('SIGTERM', () => {
  bot.stopPolling();
  process.exit(0);
});

console.log(`🤖 Bot iniciado: ${config.telegram.username}`);
console.log(`🔗 Modo: ${config.data.useJsonFile ? 'JSON' : 'API'} - ${config.data.useJsonFile ? config.data.jsonFile : config.api.baseUrl}`);
