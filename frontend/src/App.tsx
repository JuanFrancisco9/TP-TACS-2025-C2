<<<<<<< HEAD
import { useState } from 'react';
import ListaEventos from './components/ListaEventos';
import DetallesEvento from './components/detalles-evento';
import type { Evento } from './services/eventoService';
import './App.css';

function App() {
  const [eventoSeleccionado, setEventoSeleccionado] = useState<Evento | null>(null);

  const handleVerDetalle = (evento: Evento) => {
    setEventoSeleccionado(evento);
  };

  const handleVolver = () => {
    setEventoSeleccionado(null);
  };

  const handleInscribirse = (titulo: string) => {
    alert(`Inscribirse a: ${titulo}`);
  };

  // Si hay un evento seleccionado, mostrar la página de detalle
  if (eventoSeleccionado) {
    return (
      <DetallesEvento
        evento={eventoSeleccionado}
        onVolver={handleVolver}
        onInscribirse={() => handleInscribirse(eventoSeleccionado.titulo)}
      />
    );
  }

  // Si no hay evento seleccionado, mostrar el listado
  return (
    <ListaEventos
      onVerDetalle={handleVerDetalle}
      onInscribirse={handleInscribirse}
    />
  );
=======
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import LandingPage from './features/LandingPage'
import EventOverview from './features/EventOverview'
import AppLayout from './components/AppLayout'
import UserPage from './features/UserPage'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<AppLayout />}>
          <Route path="/" element={<LandingPage />} />
          <Route path="/eventos" element={<EventOverview />} />
          <Route path="/perfil" element={<UserPage />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
>>>>>>> origin/feature/frontend
}

export default App;