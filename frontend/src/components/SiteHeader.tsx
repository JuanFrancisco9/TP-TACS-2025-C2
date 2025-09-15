import React, { useState, useEffect } from 'react';
import {
  AppBar, Toolbar, Container as MuiContainer, Box, Stack, TextField,
  Button, IconButton, useMediaQuery, Menu, MenuItem
} from '@mui/material';
import { useTheme } from '@mui/material/styles';
import { useNavigate } from 'react-router-dom';
import {
  PersonOutline as PersonOutlineIcon,
  EventAvailable as EventAvailableIcon,
  Search as SearchIcon,
  Assessment as AssessmentIcon,
  Assignment as AssignmentIcon,
  Logout as LogoutIcon,
  ArrowDropDown as ArrowDropDownIcon
} from '@mui/icons-material';
import authService from '../services/authService';
import { Rol } from '../types/auth';
import type { Usuario } from '../types/auth'; 

const SiteHeader: React.FC = () => {
  const theme = useTheme();
  const mdUp = useMediaQuery(theme.breakpoints.up('md'));
  const navigate = useNavigate();

  const [query, setQuery] = useState('');
  const [currentUser, setCurrentUser] = useState<Usuario | null>(null);
  const [userMenuAnchor, setUserMenuAnchor] = useState<null | HTMLElement>(null);

  useEffect(() => {
    // Verificar el estado de autenticación al cargar el componente
    setCurrentUser(authService.getCurrentUser());

    // Escuchar cambios en el estado de autenticación
    const handleAuthStateChange = (event: any) => {
      setCurrentUser(event.detail);
    };

    window.addEventListener('authStateChanged', handleAuthStateChange);

    // Limpiar el event listener al desmontar el componente
    return () => {
      window.removeEventListener('authStateChanged', handleAuthStateChange);
    };
  }, []);

  const handleSearch = () => {
    const params = new URLSearchParams();
    if (query.trim()) params.set('q', query.trim());
    navigate({ pathname: '/eventos', search: params.toString() ? `?${params}` : '' });
  };

  const handleUserMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setUserMenuAnchor(event.currentTarget);
  };

  const handleUserMenuClose = () => {
    setUserMenuAnchor(null);
  };

  const handleLogout = () => {
    authService.logout();
    setCurrentUser(null);
    handleUserMenuClose();
    navigate('/');
  };

  const handleNavigation = (path: string) => {
    navigate(path);
    handleUserMenuClose();
  };

  return (
    <AppBar position="static" elevation={0}
      sx={{ bgcolor: '#FDF3E0', color: '#2F1D4A', borderBottom: '1px solid', borderColor: 'divider' }}>
      <MuiContainer maxWidth={false}>
        <Toolbar disableGutters sx={{ py: 1 }}>
          <Box
            sx={{ display: 'flex', alignItems: 'center', mr: 2, cursor: 'pointer' }}
            onClick={() => navigate('/')}
            title="Ir al inicio"
            aria-label="Ir al inicio"
          >
            <Box component="img" src="/logo.PNG" alt="Gatherly"
                 sx={{ height: 36, mr: 1, objectFit: 'contain' }}
                 onError={(e: any) => { e.currentTarget.src = '/favicon.svg'; }} />
          </Box>

          {mdUp && (
            <Stack direction="row" spacing={1} sx={{ flexGrow: 1, px: 3 }}>
              <TextField
                size="small"
                label="Buscar"
                fullWidth
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                onKeyDown={(e) => { if (e.key === 'Enter') handleSearch(); }}
              />
              <TextField size="small" label="Ubicación" sx={{ width: 160 }} />
              <IconButton aria-label="Buscar" title="Buscar" onClick={handleSearch}
                sx={{ bgcolor: '#2F1D4A', color: '#fff', '&:hover': { bgcolor: '#26173d' } }}>
                <SearchIcon />
              </IconButton>
            </Stack>
          )}

          <Stack direction="row" spacing={1} sx={{ ml: 'auto' }}>
            {currentUser ? (
              // Usuario autenticado
              <>
                {/* Botón de Mis Inscripciones solo para participantes */}
                {currentUser.rol === Rol.ROLE_USER && (
                  <Button
                    startIcon={<AssignmentIcon />}
                    color="inherit"
                    onClick={() => handleNavigation('/mis-eventos')}
                  >
                    Mis Inscripciones
                  </Button>
                )}

                {/* Botón de Estadísticas solo para Admin */}
                {currentUser.rol === Rol.ROLE_ADMIN && (
                  <Button
                    startIcon={<AssessmentIcon />}
                    color="inherit"
                    onClick={() => handleNavigation('/estadisticas')}
                  >
                    Estadísticas
                  </Button>
                )}

                {/* Botón de Crear eventos solo para organizadores */}
                {currentUser.rol === Rol.ROLE_ORGANIZER && (
                  <Button
                    startIcon={<EventAvailableIcon />}
                    color="inherit"
                    onClick={() => navigate('/crear-evento')}
                  >
                    Crear eventos
                  </Button>
                )}

                {/* Menú de usuario */}
                <Button
                  color="inherit"
                  endIcon={<ArrowDropDownIcon />}
                  onClick={handleUserMenuOpen}
                  sx={{ ml: 1 }}
                >
                  {currentUser.username}
                </Button>

                <Menu
                  anchorEl={userMenuAnchor}
                  open={Boolean(userMenuAnchor)}
                  onClose={handleUserMenuClose}
                  anchorOrigin={{
                    vertical: 'bottom',
                    horizontal: 'right',
                  }}
                  transformOrigin={{
                    vertical: 'top',
                    horizontal: 'right',
                  }}
                >
                  <MenuItem onClick={handleLogout}>
                    <LogoutIcon sx={{ mr: 1 }} />
                    Cerrar Sesión
                  </MenuItem>
                </Menu>
              </>
            ) : (
              // Usuario no autenticado
              <>
                <Button
                  startIcon={<PersonOutlineIcon />}
                  color="inherit"
                  onClick={() => navigate('/login')}
                >
                  Iniciar Sesión / Registrarse
                </Button>
              </>
            )}
          </Stack>
        </Toolbar>
      </MuiContainer>
    </AppBar>
  );
};

export default SiteHeader;


