import React, { useEffect, useRef } from 'react';
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
        ref={mapRef}
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
