<<<<<<< HEAD
import React, { useEffect, useRef } from 'react';
import { MapContainer, TileLayer, CircleMarker, Popup } from 'react-leaflet';
=======
import React, { useEffect } from 'react';
import { MapContainer, TileLayer, CircleMarker, Popup, useMap } from 'react-leaflet';
>>>>>>> origin/main
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';

export type MapPoint = {
  id: string;
  title: string;
  position: [number, number];
};

type Props = {
  center: [number, number];
  zoom?: number;
  points?: MapPoint[];
  style?: React.CSSProperties;
};

// Componente interno para acceder al mapa
const MapEventHandler: React.FC = () => {
  const map = useMap();

  useEffect(() => {
    console.debug('[MapView] Map created', { center: map.getCenter(), zoom: map.getZoom() });

    // Forzar redibujado inicial
    setTimeout(() => {
      map.invalidateSize();
    }, 0);

    map.on('tileerror', (e: L.TileErrorEvent) => {
      console.error('[MapView] Tile load error', e);
    });
    map.on('load', () => {
      console.debug('[MapView] Map load complete');
    });

    // recalcular tamaño en cada resize de ventana
    const handleResize = () => map.invalidateSize();
    window.addEventListener('resize', handleResize);

    return () => {
      window.removeEventListener('resize', handleResize);
      map.off('tileerror');
      map.off('load');
    };
  }, [map]);

  return null;
};

const MapView: React.FC<Props> = ({ center, zoom = 12, points = [], style }) => {
<<<<<<< HEAD
  const mapRef = useRef<L.Map | null>(null);

  useEffect(() => {
    const map = mapRef.current;
    if (!map) {
      return;
    }

    console.debug('[MapView] Map ready', { center, zoom, pointsCount: points.length });

    const refreshSize = () => map.invalidateSize();
    const handleTileError = (e: L.LeafletEvent) => console.error('[MapView] Tile load error', e);
    const handleLoad = () => console.debug('[MapView] Map load complete');

    setTimeout(refreshSize, 0);
    map.on('tileerror', handleTileError);
    map.on('load', handleLoad);
    window.addEventListener('resize', refreshSize);

    return () => {
      map.off('tileerror', handleTileError);
      map.off('load', handleLoad);
      window.removeEventListener('resize', refreshSize);
    };
  }, [center, zoom, points.length]);

=======
>>>>>>> origin/main
  return (
    <div
      style={{
        width: '100%',
        height: '100%',
        display: 'flex',
        ...style,
      }}
    >
      <MapContainer
<<<<<<< HEAD
        ref={mapRef}
=======
>>>>>>> origin/main
        center={center}
        zoom={zoom}
        style={{ flex: 1 }}
        scrollWheelZoom={true}
      >
        <MapEventHandler />
        <TileLayer
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
        {points.map((p) => (
          <CircleMarker
            key={p.id}
            center={p.position}
            radius={10}
            pathOptions={{
              color: '#2F1D4A',
              fillColor: '#7d6ae2',
              fillOpacity: 0.9
            }}
          >
            <Popup>{p.title}</Popup>
          </CircleMarker>
        ))}
      </MapContainer>
    </div>
  );
};

export default MapView;
