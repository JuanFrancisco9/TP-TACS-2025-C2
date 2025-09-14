import React, { useState } from 'react';
import HeroCarousel from '../components/HeroCarousel';
import './styles/landing.css';
import { useNavigate } from 'react-router-dom';

const LandingPage: React.FC = () => {
  const navigate = useNavigate();
  const [selected, setSelected] = useState<string | null>(null);

  const categories = [
    { key: 'music', label: 'Música', icon: '🎵' },
    { key: 'night', label: 'Vida nocturna', icon: '🎉' },
    { key: 'arts', label: 'Artes escénicas', icon: '🎭' },
    { key: 'holidays', label: 'Feriados', icon: '🎈' },
    { key: 'dating', label: 'Citas', icon: '💖' },
    { key: 'hobbies', label: 'Pasatiempos', icon: '🎮' },
    { key: 'business', label: 'Negocios', icon: '💼' },
    { key: 'food', label: 'Gastronomía', icon: '🍽️' },
  ];

  const handleCategoryClick = (key: string) => {
    const next = key === selected ? null : key;
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
          {categories.map((c) => (
            <button
              key={c.key}
              className={`category-button ${selected === c.key ? 'active' : ''}`}
              onClick={() => handleCategoryClick(c.key)}
              aria-pressed={selected === c.key}
              aria-label={`Filtrar por ${c.label}`}
            >
              <div className="category-circle" aria-hidden>
                <span>{c.icon}</span>
              </div>
              <div className="category-label">{c.label}</div>
            </button>
          ))}
        </section>
      </main>
    </div>
  );
};

export default LandingPage;
