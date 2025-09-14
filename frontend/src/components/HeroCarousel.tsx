import React, { useEffect, useState } from 'react';
import { Box, Container, IconButton, Typography, MobileStepper } from '@mui/material';
import { ArrowBackIosNew, ArrowForwardIos } from '@mui/icons-material';

const images = [
  'https://picsum.photos/id/1015/1200/450',
  'https://picsum.photos/id/1005/1200/450',
  'https://picsum.photos/id/1011/1200/450',
  'https://picsum.photos/id/1025/1200/450'
];

const HeroCarousel: React.FC = () => {
  const [index, setIndex] = useState(0);

  useEffect(() => {
    const t = setInterval(() => setIndex((i) => (i + 1) % images.length), 5000);
    return () => clearInterval(t);
  }, []);

  const prev = () => setIndex((i) => (i - 1 + images.length) % images.length);
  const next = () => setIndex((i) => (i + 1) % images.length);

  return (
    <Box sx={{ position: 'relative', overflow: 'hidden' }}>
      <Box
        sx={{
          position: 'relative',
          height: { xs: 340, sm: 400, md: 480 },
          bgcolor: 'transparent',
          backgroundImage: 'linear-gradient(135deg, #6a0572 0%, #0f8b8d 100%)'
        }}
      >
        {images.map((src, i) => (
          <Box
            key={src}
            component="img"
            src={src}
            alt={`slide-${i}`}
            draggable={false}
            sx={{
              position: 'absolute',
              inset: 0,
              width: '100%',
              height: '100%',
              objectFit: 'cover',
              opacity: i === index ? 1 : 0,
              transform: i === index ? 'scale(1)' : 'scale(1.02)',
              transition: 'opacity 600ms ease, transform 900ms ease'
            }}
          />
        ))}

        <Container maxWidth={false} sx={{ position: 'relative', height: '100%' }}>
          <Box sx={{ position: 'absolute', left: { xs: 16, md: 40 }, top: { xs: 24, md: 48 }, color: '#fff', textShadow: '0 2px 8px rgba(0,0,0,0.25)' }}>
            <Typography variant="subtitle1" sx={{ opacity: 0.95 }}>Buscá lo que</Typography>
            <Typography variant="h3" sx={{ fontWeight: 800, lineHeight: 1.05 }}>te apasiona</Typography>
          </Box>

          {/* Arrows */}
          <IconButton onClick={prev} aria-label="Anterior" sx={{ position: 'absolute', top: '50%', left: 16, transform: 'translateY(-50%)', bgcolor: 'rgba(255,255,255,0.9)', '&:hover': { bgcolor: 'rgba(255,255,255,1)' } }}>
            <ArrowBackIosNew fontSize="small" />
          </IconButton>
          <IconButton onClick={next} aria-label="Siguiente" sx={{ position: 'absolute', top: '50%', right: 16, transform: 'translateY(-50%)', bgcolor: 'rgba(255,255,255,0.9)', '&:hover': { bgcolor: 'rgba(255,255,255,1)' } }}>
            <ArrowForwardIos fontSize="small" />
          </IconButton>
        </Container>

        {/* Bottom gradient overlay */}
        <Box sx={{ position: 'absolute', left: 0, right: 0, bottom: 0, height: 140, background: 'linear-gradient(to top, rgba(0,0,0,0.55), rgba(0,0,0,0))' }} />

        {/* Dots stepper (MUI) */}
        <Box sx={{ position: 'absolute', left: 0, right: 0, bottom: 8, display: 'flex', justifyContent: 'center' }}>
          <MobileStepper
            variant="dots"
            steps={images.length}
            position="static"
            activeStep={index}
            backButton={<Box sx={{ display: 'none' }} />}
            nextButton={<Box sx={{ display: 'none' }} />}
            sx={{ background: 'transparent' }}
          />
        </Box>
      </Box>
    </Box>
  );
};

export default HeroCarousel;
