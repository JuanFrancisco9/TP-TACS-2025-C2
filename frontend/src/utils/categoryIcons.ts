import type { ElementType } from 'react';
import * as MuiIcons from '@mui/icons-material';
import type { CategoriaIconRule } from '../types/evento';

type IconsDictionary = Record<string, ElementType>;

const icons = MuiIcons as IconsDictionary;
export const DEFAULT_ICON_NAME = 'Category';
const DEFAULT_ICON = icons[DEFAULT_ICON_NAME];

const FALLBACK_RULES: CategoriaIconRule[] = [
  { icono: 'Event', keywords: ['conferencia', 'charla', 'congreso', 'seminario'] },
  { icono: 'MusicNote', keywords: ['musica', 'música', 'concierto', 'recital', 'festival'] },
  { icono: 'SportsEsports', keywords: ['deporte', 'futbol', 'fútbol', 'basquet', 'sport'] },
  { icono: 'TheaterComedy', keywords: ['teatro', 'obra', 'comedia', 'drama'] },
  { icono: 'School', keywords: ['educacion', 'educación', 'clase', 'curso', 'taller', 'capacitacion'] },
  { icono: 'Science', keywords: ['ciencia', 'tecnologia', 'tech', 'robot'] },
  { icono: 'Restaurant', keywords: ['gastronomia', 'gastronomía', 'comida', 'degustacion'] },
  { icono: 'TravelExplore', keywords: ['turismo', 'viaje', 'aventura'] },
  { icono: 'VolunteerActivism', keywords: ['solidario', 'benefico', 'donacion', 'charity'] },
];

export const normalizeKey = (value?: string) =>
  (value ?? '')
    .normalize('NFD')
    .replace(/\p{M}+/gu, '')
    .toLowerCase()
    .trim()
    .replace(/\s+/g, ' ');

const prepareRules = (rules?: CategoriaIconRule[]) =>
  (rules && rules.length > 0 ? rules : FALLBACK_RULES).map((rule) => ({
    icono: rule.icono,
    normalizedKeywords: rule.keywords
      .map(normalizeKey)
      .filter((keyword) => keyword.length > 0),
  }));

export function inferIconName(
  rules: CategoriaIconRule[] | undefined,
  tipo?: string,
  explicitIcon?: string
): string {
  if (explicitIcon && icons[explicitIcon]) {
    return explicitIcon;
  }

  const normalizedNombre = normalizeKey(tipo);
  if (!normalizedNombre) {
    return DEFAULT_ICON_NAME;
  }

  const match = prepareRules(rules).find((rule) =>
    rule.normalizedKeywords.some((keyword) => normalizedNombre.includes(keyword))
  );

  return match?.icono ?? DEFAULT_ICON_NAME;
}

export function getCategoryIconComponent(iconName?: string): ElementType {
  if (iconName && icons[iconName]) {
    return icons[iconName];
  }
  return DEFAULT_ICON;
}

export function getCategoryIconFor(
  rules: CategoriaIconRule[] | undefined,
  iconName?: string,
  tipo?: string
): ElementType {
  const resolved = inferIconName(rules, tipo, iconName);
  return getCategoryIconComponent(resolved);
}
