import React from 'react';
import { Outlet } from 'react-router-dom';
import SiteHeader from './SiteHeader';
import Footer from './Footer';

const AppLayout: React.FC = () => {
  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#FDF3E0', color: '#2F1D4A' }}>
      <SiteHeader />
      <main>
        <Outlet />
      </main>
      <Footer />
    </div>
  );
};

export default AppLayout;

