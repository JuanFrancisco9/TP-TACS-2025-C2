import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import LandingPage from './features/LandingPage'
import EventOverview from './features/EventOverview'
import AppLayout from './components/AppLayout'
import UserPage from './features/UserPage'
import Statistics from "./pages/Statistics.tsx";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<AppLayout />}>
          <Route path="/" element={<LandingPage />} />
          <Route path="/eventos" element={<EventOverview />} />
          <Route path="/perfil" element={<UserPage />} />
          <Route path="*" element={<Navigate to="/" replace />} />
            <Route path="/estadisticas" element={<Statistics />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}

export default App
