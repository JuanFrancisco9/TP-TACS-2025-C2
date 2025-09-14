import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import LandingPage from './features/LandingPage'
import EventOverview from './features/EventOverview'
import AppLayout from './components/AppLayout'
import UserPage from './features/UserPage'
import LoginPage from './features/LoginPage'
import InscripcionPage from './features/InscripcionPage'
import CreateEventPage from './features/CreateEventPage'

function App() {
    return (
    <BrowserRouter>
      <Routes>
        <Route element={<AppLayout />}>
          <Route path="/" element={<LandingPage />} />
          <Route path="/eventos" element={<EventOverview />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/inscripcion" element={<InscripcionPage />} />
          <Route path="/inscripcion/:id" element={<InscripcionPage />} />
          <Route path="/crear-evento" element={<CreateEventPage />} />
          <Route path="/perfil" element={<UserPage />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}

export default App;