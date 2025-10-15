import {BrowserRouter, Routes, Route, Navigate} from 'react-router-dom'
import LandingPage from './features/LandingPage'
import EventOverview from './features/EventOverview'
import AppLayout from './components/AppLayout'
import UserPage from './features/UserPage'
import LoginPage from './features/LoginPage'
import InscripcionPage from './features/InscripcionPage'
import CreateEventPage from './features/CreateEventPage'
import Statistics from "./features/Statistics.tsx";
import UserLanding from "./features/UserLanding.tsx";
import ProtectedRoute from './components/ProtectedRoute';
import { Rol } from './types/auth';
import PerfilOrganizadorPage from './features/PerfilOrganizadorPage'
import UnauthorizedPage from './features/UnauthorizedPage';

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route element={<AppLayout/>}>
                    <Route path="/" element={<LandingPage/>}/>
                    <Route path="/eventos" element={<EventOverview/>}/>
                    <Route path="/login" element={<LoginPage/>}/>
                    <Route path="/unauthorized" element={<UnauthorizedPage />} />

                    {/* Rutas que requieren autenticación */}
                    <Route path="/perfil" element={
                        <ProtectedRoute>
                            <UserPage/>
                        </ProtectedRoute>
                    }/>

                    {/* Rutas específicas por rol */}
                    <Route path="/inscripcion" element={
                        <ProtectedRoute allowedRoles={[Rol.ROLE_USER]}>
                            <InscripcionPage/>
                        </ProtectedRoute>
                    }/>
                    <Route path="/inscripcion/:id" element={
                        <ProtectedRoute allowedRoles={[Rol.ROLE_USER]}>
                            <InscripcionPage/>
                        </ProtectedRoute>
                    }/>

                    <Route path="/crear-evento" element={
                        <ProtectedRoute allowedRoles={[Rol.ROLE_ORGANIZER]}>
                            <CreateEventPage/>
                        </ProtectedRoute>
                    }/>
                    <Route path="/organizador/eventos" element={
                        <ProtectedRoute allowedRoles={[Rol.ROLE_ORGANIZER]}>
                            <PerfilOrganizadorPage/>
                        </ProtectedRoute>
                    }/>
                    <Route path="/mis-eventos" element={
                        <ProtectedRoute allowedRoles={[Rol.ROLE_USER]}>
                            <UserLanding/>
                        </ProtectedRoute>
                    }/>
                    <Route path="/estadisticas" element={
                        <ProtectedRoute allowedRoles={[Rol.ROLE_ADMIN]}>
                            <Statistics/>
                        </ProtectedRoute>
                    }/>
                    <Route path="*" element={<Navigate to="/" replace/>}/>
                </Route>
            </Routes>
        </BrowserRouter>
    )
}

export default App;
