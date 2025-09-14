import React, { useCallback } from 'react';
import { MapContainer, TileLayer, CircleMarker, Popup } from 'react-leaflet';
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

const MapView: React.FC<Props> = ({ center, zoom = 12, points = [], style }) => {
  const onCreated = useCallback(
    (map: L.Map) => {
      console.debug('[MapView] Map created', { center, zoom, pointsCount: points.length });

      // Forzar redibujado inicial
      setTimeout(() => {
        map.invalidateSize();
      }, 0);

      map.on('tileerror', (e) => {
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
      };
    },
    [center, zoom, points.length]
  );

  return (
    <div
      style={{
        width: '100%',
        height: '100%',
        display: 'flex', // 👈 clave para que MapContainer se expanda
        ...style,
      }}
    >
      <MapContainer
        whenCreated={onCreated}
        center={center}
        zoom={zoom}
        style={{ flex: 1 }} // 👈 se adapta al padre
        scrollWheelZoom={true}
      >
        <TileLayer
          attribution='&copy; OpenStreetMap contributors'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
        {points.map((p) => (
          <CircleMarker
            key={p.id}
            center={p.position}
            radius={10}
            pathOptions={{ color: '#2F1D4A', fillColor: '#7d6ae2', fillOpacity: 0.9 }}
          >
            <Popup>{p.title}</Popup>
          </CircleMarker>
        ))}
      </MapContainer>
    </div>
  );
};

export default MapView;
