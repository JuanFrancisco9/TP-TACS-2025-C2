import React from 'react';
import {
  AppBar, Toolbar, Container as MuiContainer, Box, Stack, TextField,
  Button, IconButton, useMediaQuery
} from '@mui/material';
import { useTheme } from '@mui/material/styles';
import { PersonOutline as PersonOutlineIcon, EventAvailable as EventAvailableIcon, Search as SearchIcon } from '@mui/icons-material'; 

const SiteHeader: React.FC = () => {
  const theme = useTheme();
  const mdUp = useMediaQuery(theme.breakpoints.up('md'));

  return (
    <AppBar position="static" elevation={0}
      sx={{ bgcolor: '#FDF3E0', color: '#2F1D4A', borderBottom: '1px solid', borderColor: 'divider' }}>
      <MuiContainer maxWidth={false}>
        <Toolbar disableGutters sx={{ py: 1 }}>
          <Box sx={{ display: 'flex', alignItems: 'center', mr: 2 }}>
            <Box component="img" src="/logo.PNG" alt="Gatherly"
                 sx={{ height: 28, mr: 1, objectFit: 'contain' }}
                 onError={(e: any) => { e.currentTarget.src = '/favicon.svg'; }} />
          </Box>

          {mdUp && (
            <Stack direction="row" spacing={1} sx={{ flexGrow: 1, px: 3 }}>
              <TextField size="small" label="Buscar" fullWidth />
              <TextField size="small" label="Ubicación" sx={{ width: 160 }} />
              <IconButton aria-label="Buscar" title="Buscar"
                sx={{ bgcolor: '#2F1D4A', color: '#fff', '&:hover': { bgcolor: '#26173d' } }}>
                <SearchIcon />
              </IconButton>
            </Stack>
          )}

          <Stack direction="row" spacing={1} sx={{ ml: 'auto' }}>
            <Button startIcon={<PersonOutlineIcon />} color="inherit">Iniciar Sesión /  Registrarse</Button>
            <Button startIcon={<EventAvailableIcon />} color="inherit">Crear eventos</Button>
          </Stack>
        </Toolbar>
      </MuiContainer>
    </AppBar>
  );
};

export default SiteHeader;
