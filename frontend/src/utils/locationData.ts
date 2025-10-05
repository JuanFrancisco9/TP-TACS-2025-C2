export type LocalidadOption = {
  nombre: string;
  latitud: number;
  longitud: number;
};

export const PROVINCIA_LOCALIDADES: Record<string, LocalidadOption[]> = {
  'Ciudad Autónoma de Buenos Aires': [
    { nombre: 'CABA', latitud: -34.603722, longitud: -58.381592 },
    { nombre: 'Palermo', latitud: -34.571149, longitud: -58.4233 },
    { nombre: 'Belgrano', latitud: -34.56255, longitud: -58.4585 },
    { nombre: 'Recoleta', latitud: -34.588383, longitud: -58.394972 },
    { nombre: 'Caballito', latitud: -34.61853, longitud: -58.4438 }
  ],
  'Buenos Aires': [
    { nombre: 'La Plata', latitud: -34.92145, longitud: -57.954533 },
    { nombre: 'Mar del Plata', latitud: -38.00042, longitud: -57.5562 },
    { nombre: 'Bahía Blanca', latitud: -38.7196, longitud: -62.27243 },
    { nombre: 'Tandil', latitud: -37.32167, longitud: -59.13316 },
    { nombre: 'San Isidro', latitud: -34.474145, longitud: -58.527663 }
  ],
  'Catamarca': [
    { nombre: 'San Fernando del Valle', latitud: -28.46957, longitud: -65.78524 },
    { nombre: 'Andalgalá', latitud: -27.57391, longitud: -66.31666 },
    { nombre: 'Tinogasta', latitud: -28.0629, longitud: -67.5631 }
  ],
  'Chaco': [
    { nombre: 'Resistencia', latitud: -27.4519, longitud: -58.987 },
    { nombre: 'Presidencia Roque Sáenz Peña', latitud: -26.7852, longitud: -60.4388 },
    { nombre: 'Villa Ángela', latitud: -27.5733, longitud: -60.7153 }
  ],
  'Chubut': [
    { nombre: 'Rawson', latitud: -43.30016, longitud: -65.10228 },
    { nombre: 'Trelew', latitud: -43.2471, longitud: -65.3051 },
    { nombre: 'Puerto Madryn', latitud: -42.7692, longitud: -65.0385 },
    { nombre: 'Esquel', latitud: -42.9097, longitud: -71.3195 }
  ],
  'Córdoba': [
    { nombre: 'Córdoba Capital', latitud: -31.420083, longitud: -64.188776 },
    { nombre: 'Villa Carlos Paz', latitud: -31.420654, longitud: -64.499993 },
    { nombre: 'Río Cuarto', latitud: -33.123163, longitud: -64.349564 },
    { nombre: 'Alta Gracia', latitud: -31.65304, longitud: -64.42861 }
  ],
  'Corrientes': [
    { nombre: 'Corrientes Capital', latitud: -27.4691, longitud: -58.8306 },
    { nombre: 'Goya', latitud: -29.1401, longitud: -59.2626 },
    { nombre: 'Paso de los Libres', latitud: -29.7126, longitud: -57.0877 }
  ],
  'Entre Ríos': [
    { nombre: 'Paraná', latitud: -31.7319, longitud: -60.5238 },
    { nombre: 'Concordia', latitud: -31.3929, longitud: -58.0209 },
    { nombre: 'Gualeguaychú', latitud: -33.0092, longitud: -58.5172 }
  ],
  'Formosa': [
    { nombre: 'Formosa Capital', latitud: -26.185, longitud: -58.175 },
    { nombre: 'Clorinda', latitud: -25.2847, longitud: -57.7196 },
    { nombre: 'Pirané', latitud: -25.7333, longitud: -59.1089 }
  ],
  'Jujuy': [
    { nombre: 'San Salvador de Jujuy', latitud: -24.1858, longitud: -65.2995 },
    { nombre: 'Palpalá', latitud: -24.2586, longitud: -65.2116 },
    { nombre: 'Perico', latitud: -24.3816, longitud: -65.1126 }
  ],
  'La Pampa': [
    { nombre: 'Santa Rosa', latitud: -36.6202, longitud: -64.2906 },
    { nombre: 'General Pico', latitud: -35.6566, longitud: -63.7568 },
    { nombre: 'Toay', latitud: -36.6761, longitud: -64.3833 }
  ],
  'La Rioja': [
    { nombre: 'La Rioja Capital', latitud: -29.4128, longitud: -66.8558 },
    { nombre: 'Chilecito', latitud: -29.1627, longitud: -67.4977 },
    { nombre: 'Aimogasta', latitud: -28.5636, longitud: -66.9482 }
  ],
  'Mendoza': [
    { nombre: 'Mendoza Capital', latitud: -32.889458, longitud: -68.845839 },
    { nombre: 'Godoy Cruz', latitud: -32.92397, longitud: -68.85809 },
    { nombre: 'San Rafael', latitud: -34.61772, longitud: -68.33007 },
    { nombre: 'Malargüe', latitud: -35.47505, longitud: -69.58541 }
  ],
  'Misiones': [
    { nombre: 'Posadas', latitud: -27.3621, longitud: -55.9009 },
    { nombre: 'Oberá', latitud: -27.4871, longitud: -55.1199 },
    { nombre: 'Eldorado', latitud: -26.4095, longitud: -54.6418 }
  ],
  'Neuquén': [
    { nombre: 'Neuquén Capital', latitud: -38.9516, longitud: -68.0591 },
    { nombre: 'San Martín de los Andes', latitud: -40.1579, longitud: -71.3534 },
    { nombre: 'Cutral Có', latitud: -38.9395, longitud: -69.2306 }
  ],
  'Río Negro': [
    { nombre: 'Viedma', latitud: -40.8135, longitud: -62.9967 },
    { nombre: 'San Carlos de Bariloche', latitud: -41.1335, longitud: -71.3103 },
    { nombre: 'Cipolletti', latitud: -38.9339, longitud: -67.9901 }
  ],
  'Salta': [
    { nombre: 'Salta Capital', latitud: -24.7821, longitud: -65.4232 },
    { nombre: 'Tartagal', latitud: -22.516, longitud: -63.8069 },
    { nombre: 'Cafayate', latitud: -26.0732, longitud: -65.977 }
  ],
  'San Juan': [
    { nombre: 'San Juan Capital', latitud: -31.5375, longitud: -68.5364 },
    { nombre: 'Caucete', latitud: -31.6515, longitud: -68.281 },
    { nombre: 'Calingasta', latitud: -31.335, longitud: -69.3969 }
  ],
  'San Luis': [
    { nombre: 'San Luis Capital', latitud: -33.3017, longitud: -66.3378 },
    { nombre: 'Villa Mercedes', latitud: -33.6757, longitud: -65.4573 },
    { nombre: 'Merlo', latitud: -32.3446, longitud: -65.0139 }
  ],
  'Santa Cruz': [
    { nombre: 'Río Gallegos', latitud: -51.6226, longitud: -69.2181 },
    { nombre: 'Caleta Olivia', latitud: -46.4393, longitud: -67.5231 },
    { nombre: 'El Calafate', latitud: -50.3379, longitud: -72.2648 }
  ],
  'Santa Fe': [
    { nombre: 'Rosario', latitud: -32.944242, longitud: -60.650539 },
    { nombre: 'Santa Fe Capital', latitud: -31.633333, longitud: -60.7 },
    { nombre: 'Rafaela', latitud: -31.25033, longitud: -61.4867 },
    { nombre: 'Venado Tuerto', latitud: -33.7473, longitud: -61.9688 }
  ],
  'Santiago del Estero': [
    { nombre: 'Santiago del Estero Capital', latitud: -27.7834, longitud: -64.2642 },
    { nombre: 'La Banda', latitud: -27.7349, longitud: -64.2527 },
    { nombre: 'Termas de Río Hondo', latitud: -27.4932, longitud: -64.8605 }
  ],
  'Tierra del Fuego': [
    { nombre: 'Ushuaia', latitud: -54.8019, longitud: -68.303 },
    { nombre: 'Río Grande', latitud: -53.7999, longitud: -67.699 },
    { nombre: 'Tolhuin', latitud: -54.509, longitud: -67.2009 }
  ],
  'Tucumán': [
    { nombre: 'San Miguel de Tucumán', latitud: -26.8083, longitud: -65.2176 },
    { nombre: 'Tafí Viejo', latitud: -26.732, longitud: -65.2592 },
    { nombre: 'Yerba Buena', latitud: -26.8162, longitud: -65.3169 }
  ]
};

export const PROVINCIAS = Object.keys(PROVINCIA_LOCALIDADES);

export function getLocalidades(provincia: string | undefined): LocalidadOption[] {
  if (!provincia) return [];
  return PROVINCIA_LOCALIDADES[provincia] ?? [];
}

export function getDefaultCoordenadas(
  provincia: string | undefined,
  localidad: string | undefined
): { latitud: number; longitud: number } | null {
  if (!provincia || !localidad) {
    return null;
  }
  const match = getLocalidades(provincia).find((loc) => loc.nombre === localidad);
  return match ? { latitud: match.latitud, longitud: match.longitud } : null;
}
