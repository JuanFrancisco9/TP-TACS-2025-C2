import React, { useEffect, useState } from 'react';
import HeroCarousel from '../components/HeroCarousel';
import './styles/landing.css';
import { useNavigate } from 'react-router-dom';
import { CircularProgress } from '@mui/material';
import { EventoService } from '../services/eventoService';
import type { CategoriaDTO } from '../types/evento.ts';
import { getCategoryIconFor } from '../utils/categoryIcons';

const LandingPage: React.FC = () => {
  const navigate = useNavigate();
  const [selected, setSelected] = useState<string | null>(null);
  const [categories, setCategories] = useState<CategoriaDTO[]>([]);
  const [loading, setLoading] = useState<boolean>(false);

  useEffect(() => {
    let mounted = true;
    setLoading(true);
    EventoService.obtenerCategorias()
      .then((cats) => { if (mounted) setCategories(cats); })
      .finally(() => { if (mounted) setLoading(false); });
    return () => { mounted = false };
  }, []);

  const handleCategoryClick = (tipo: string) => {
    const next = tipo === selected ? null : tipo;
    setSelected(next);
    const search = next ? `?categoria=${encodeURIComponent(next)}` : '';
    navigate({ pathname: '/eventos', search }, { replace: false });
  };

  return (
    <div className="landing-page" style={{ backgroundColor: 'transparent' }}>
      <main>
        <HeroCarousel />

        {/* Categorías como círculos */}
        <section className="categories-row" aria-label="Categorías principales">
          {loading ? (
            <div style={{ display: 'flex', justifyContent: 'center', width: '100%', padding: '12px 0' }}>
              <CircularProgress size={28} />
            </div>
          ) : (
            categories.map((categoria) => {
              const tipo = categoria.tipo;
              const IconComponent = getCategoryIconFor(undefined, categoria.icono, tipo);
              return (
                <button
                  key={tipo}
                  className={`category-button ${selected === tipo ? 'active' : ''}`}
                  onClick={() => handleCategoryClick(tipo)}
                  aria-pressed={selected === tipo}
                  aria-label={`Filtrar por ${tipo}`}
                >
                  <div className="category-circle" aria-hidden>
                    <IconComponent fontSize="medium" />
                  </div>
                  <div className="category-label">{tipo}</div>
                </button>
              );
            })
          )}
        </section>
      </main>
    </div>
  );
};

export default LandingPage;
