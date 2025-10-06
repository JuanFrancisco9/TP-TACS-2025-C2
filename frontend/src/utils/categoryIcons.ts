import type { ElementType } from 'react';
import * as MuiIcons from '@mui/icons-material';
import type { CategoriaIconRule } from '../types/evento';

type IconsDictionary = Record<string, ElementType>;

const icons = MuiIcons as IconsDictionary;
export const DEFAULT_ICON_NAME = 'Category';
const DEFAULT_ICON = icons[DEFAULT_ICON_NAME];

export function inferIconName(
  _rules: CategoriaIconRule[] | undefined,
  _tipo?: string,
  explicitIcon?: string
): string {
  if (explicitIcon && icons[explicitIcon]) {
    return explicitIcon;
  }
  return DEFAULT_ICON_NAME;
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
