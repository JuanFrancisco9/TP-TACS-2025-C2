import React, { useEffect, useRef, useState } from 'react';
import HeroCarousel from '../components/HeroCarousel';
import './styles/landing.css';
import { useNavigate } from 'react-router-dom';
import {CircularProgress, IconButton, Tooltip} from '@mui/material';
import { EventoService } from '../services/eventoService';
import type { CategoriaDTO } from '../types/evento.ts';
import { getCategoryIconFor } from '../utils/categoryIcons';
import { ArrowBackIosNew, ArrowForwardIos } from '@mui/icons-material';

const LandingPage: React.FC = () => {
  const navigate = useNavigate();
  const [selected, setSelected] = useState<string | null>('Todas');
  const [categories, setCategories] = useState<CategoriaDTO[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const viewportRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    let mounted = true;
    setLoading(true);
    EventoService.obtenerCategorias()
      .then((cats) => { if (mounted) setCategories(cats); })
      .finally(() => { if (mounted) setLoading(false); });
    return () => { mounted = false };
  }, []);

  const handleCategoryClick = (tipo: string) => {
    if (tipo === 'Todas') {
      setSelected('Todas');
      navigate({ pathname: '/eventos', search: '' }, { replace: false });
      return;
    }
    const next = tipo === selected ? null : tipo;
    setSelected(next);
    const search = next ? `?categoria=${encodeURIComponent(next)}` : '';
    navigate({ pathname: '/eventos', search }, { replace: false });
  };

  const scrollCats = (dir: 1 | -1) => {
    const el = viewportRef.current;
    if (!el) return;

    const items = Array.from(el.querySelectorAll('.category-button')) as HTMLElement[];
    if (!items.length) return;

    // cuántas categorías por vista: 1 mobile, 2 desktop
    const perView = window.matchMedia('(min-width: 900px)').matches ? 2 : 1;

    // índice actual: el más cercano a scrollLeft usando bounding rect para evitar offsets relativos
    const { scrollLeft } = el;
    const elLeft = el.getBoundingClientRect().left;
    let curr = 0;
    let bestDiff = Number.POSITIVE_INFINITY;
    for (let i = 0; i < items.length; i++) {
      const itemLeft = items[i].getBoundingClientRect().left - elLeft + scrollLeft;
      const diff = Math.abs(itemLeft - scrollLeft);
      if (diff < bestDiff) {
        bestDiff = diff;
        curr = i;
      }
    }

    // siguiente índice, evitando dejar "huecos" al final
    const maxStart = Math.max(0, items.length - perView);
    const next = Math.min(maxStart, Math.max(0, curr + dir * perView));

    const targetLeft = items[next].getBoundingClientRect().left - elLeft + scrollLeft;
    el.scrollTo({ left: targetLeft, behavior: 'auto' });
  };

  return (
    <div className="landing-page" style={{ backgroundColor: 'transparent' }}>
      <main>
        <HeroCarousel />

        {/* Quick search CTA */}
        <section className="search-cta" aria-label="Buscar eventos">
          <h3 className="search-cta-title">Buscá eventos por la categoría que más te interese</h3>
        </section>

        {/* Categories carousel without transitions */}
        <section className="categories-carousel" aria-label="Categorias principales">
          <Tooltip title="Ver categorías anteriores" placement="top" arrow>
            <IconButton
              aria-label="Anterior"
              onClick={() => scrollCats(-1)}
              sx={{
                position: 'absolute',
                top: '50%',
                left: 16,
                transform: 'translateY(-50%)',
                bgcolor: 'rgba(255,255,255,0.9)',
                '&:hover': { bgcolor: 'rgba(255,255,255,1)' },
                zIndex: 2
              }}
            >
              <ArrowBackIosNew fontSize="small" />
            </IconButton>
          </Tooltip>

          <div className="categories-viewport" ref={viewportRef}>
            <div className="categories-track" role="list">
              {loading ? (
                <div style={{ display: 'flex', justifyContent: 'center', width: '100%', padding: '12px 0' }}>
                  <CircularProgress size={28} />
                </div>
              ) : (
                [{ tipo: 'Todas' } as CategoriaDTO, ...categories].map((categoria) => {
                  const tipo = categoria.tipo;
                  const IconComponent = getCategoryIconFor(undefined, categoria.icono, tipo);
                  return (
                    <button
                      key={tipo}
                      role="listitem"
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
            </div>
          </div>

          <Tooltip title="Ver más categorías" placement="top" arrow>
            <IconButton
              aria-label="Siguiente"
              onClick={() => scrollCats(1)}
              sx={{
                position: 'absolute',
                top: '50%',
                right: 16,
                transform: 'translateY(-50%)',
                bgcolor: 'rgba(255,255,255,0.9)',
                '&:hover': { bgcolor: 'rgba(255,255,255,1)' },
                zIndex: 2
              }}
            >
              <ArrowForwardIos fontSize="small" />
            </IconButton>
          </Tooltip>
        </section>

        {/* Categorías como círculos */}
        <section className="categories-row" aria-label="Categorías principales">
          {loading ? (
            <div style={{ display: 'flex', justifyContent: 'center', width: '100%', padding: '12px 0' }}>
              <CircularProgress size={28} />
            </div>
          ) : (
            ([{ tipo: 'Todas' } as CategoriaDTO, ...categories]).map((categoria) => {
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
