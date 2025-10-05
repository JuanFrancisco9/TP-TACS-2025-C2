
import type { ElementType } from 'react';
import * as MuiIcons from '@mui/icons-material';

type IconsDictionary = Record<string, ElementType>;

const icons = MuiIcons as IconsDictionary;
const DEFAULT_ICON = icons.Category;

export const CATEGORY_PRESETS = [
  { tipo: 'Conferencia', icono: 'Event' },
  { tipo: 'Concierto', icono: 'MusicNote' },
  { tipo: 'Deportes', icono: 'SportsEsports' },
  { tipo: 'Teatro', icono: 'TheaterComedy' },
  { tipo: 'Educación', icono: 'School' },
  { tipo: 'Entretenimiento', icono: 'LocalActivity' },
  { tipo: 'Turismo', icono: 'TravelExplore' },
  { tipo: 'Ciencia', icono: 'Science' },
  { tipo: 'Comunidad', icono: 'Diversity3' }
] as const;

const TYPE_ICON_MAP: Record<string, string> = CATEGORY_PRESETS.reduce((acc, categoria) => {
  acc[categoria.tipo.toLowerCase()] = categoria.icono;
  return acc;
}, {} as Record<string, string>);

export function getCategoryIcon(iconName?: string, tipo?: string): ElementType {
  if (iconName) {
    const candidate = icons[iconName];
    if (candidate) {
      return candidate;
    }
  }

  if (tipo) {
    const candidateName = TYPE_ICON_MAP[tipo.toLowerCase()];
    if (candidateName) {
      const candidate = icons[candidateName];
      if (candidate) {
        return candidate;
      }
    }
  }

  return DEFAULT_ICON;
}
