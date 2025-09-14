import React, { useState } from 'react';
import {
  AppBar, Toolbar, Container as MuiContainer, Box, Stack, TextField,
  Button, IconButton, useMediaQuery
} from '@mui/material';
import { useTheme } from '@mui/material/styles';
import { useNavigate } from 'react-router-dom';
import { PersonOutline as PersonOutlineIcon, EventAvailable as EventAvailableIcon, Search as SearchIcon } from '@mui/icons-material'; 

const SiteHeader: React.FC = () => {
  const theme = useTheme();
  const mdUp = useMediaQuery(theme.breakpoints.up('md'));
  const navigate = useNavigate();

  const [query, setQuery] = useState('');

  const handleSearch = () => {
    const params = new URLSearchParams();
    if (query.trim()) params.set('q', query.trim());
    navigate({ pathname: '/eventos', search: params.toString() ? `?${params}` : '' });
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
            <Button startIcon={<PersonOutlineIcon />} color="inherit" onClick={() => navigate('/login')}>Iniciar Sesión /  Registrarse</Button>
            <Button startIcon={<EventAvailableIcon />} color="inherit" onClick={() => navigate('/crear-evento')}>Crear eventos</Button>
          </Stack>
        </Toolbar>
      </MuiContainer>
    </AppBar>
  );
};

export default SiteHeader;


