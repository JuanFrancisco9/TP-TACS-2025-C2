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
}

export default App;