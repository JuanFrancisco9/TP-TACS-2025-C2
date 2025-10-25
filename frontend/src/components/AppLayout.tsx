import React, { useEffect } from 'react';
import { Outlet, useLocation } from 'react-router-dom';
import SiteHeader from './SiteHeader';
import Footer from './Footer';
import authService from '../services/authService';

const AppLayout: React.FC = () => {
  const location = useLocation();

  useEffect(() => {
    const path = `${location.pathname}${location.search}`;
    if (!location.pathname.startsWith('/unauthorized')) {
      authService.updateNavigationHistory(path);
    }
  }, [location]);

  return (
    <div
      style={{
        minHeight: '100vh',
        backgroundColor: '#FDF3E0',
        color: '#2F1D4A',
        display: 'flex',
        flexDirection: 'column'
      }}
    >
      <SiteHeader />
      <main style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
        <Outlet />
      </main>
      <Footer />
    </div>
  );
};

export default AppLayout;

