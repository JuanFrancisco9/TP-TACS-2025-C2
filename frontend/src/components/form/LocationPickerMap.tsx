import * as React from 'react';
import { MapContainer, TileLayer, Marker, useMapEvents, useMap } from 'react-leaflet';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';

const DEFAULT_ICON = new L.Icon({
  iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
  iconSize: [25, 41],
  iconAnchor: [12, 41]
});

type Props = {
  latitud: string | null;
  longitud: string | null;
  onChange: (latitud: string, longitud: string) => void;
  disabled?: boolean;
};

type LatLngTuple = [number, number];

const fallbackCenter: LatLngTuple = [-34.6037, -58.3816]; // Obelisco

const parseCoordinate = (value: string | null | undefined): number | null => {
  if (value == null) return null;
  const numeric = Number(value);
  return Number.isFinite(numeric) ? numeric : null;
};

const RecenterMap: React.FC<{ center: LatLngTuple }> = ({ center }) => {
  const map = useMap();
  React.useEffect(() => {
    map.setView(center);
  }, [center, map]);
  return null;
};

const MapClickHandler: React.FC<{ onPick: (lat: number, lng: number) => void; disabled?: boolean }> = ({ onPick, disabled }) => {
  useMapEvents({
    click: (event) => {
      if (!disabled) {
        const { lat, lng } = event.latlng;
        onPick(lat, lng);
      }
    }
  });
  return null;
};

const LocationPickerMap: React.FC<Props> = ({ latitud, longitud, onChange, disabled }) => {
  const lat = parseCoordinate(latitud);
  const lng = parseCoordinate(longitud);
  const center: LatLngTuple = lat != null && lng != null ? [lat, lng] : fallbackCenter;

  const [markerPosition, setMarkerPosition] = React.useState<LatLngTuple>(center);

  React.useEffect(() => {
    if (lat != null && lng != null) {
      setMarkerPosition([lat, lng]);
    }
  }, [lat, lng]);

  const handlePick = (newLat: number, newLng: number) => {
    setMarkerPosition([newLat, newLng]);
    onChange(newLat.toFixed(6), newLng.toFixed(6));
  };

  return (
    <Box sx={{ width: '100%', height: 280, borderRadius: 2, overflow: 'hidden', border: '1px solid #e0e0e0', position: 'relative' }}>
      <MapContainer
        center={center}
        zoom={16}
        style={{ width: '100%', height: '100%' }}
        scrollWheelZoom={!disabled}
      >
        <RecenterMap center={markerPosition} />
        <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />
        <MapClickHandler onPick={handlePick} disabled={disabled} />
        <Marker
          draggable={!disabled}
          position={markerPosition}
          icon={DEFAULT_ICON}
          eventHandlers={{
            dragend: (event) => {
              const marker = event.target as L.Marker;
              const { lat: newLat, lng: newLng } = marker.getLatLng();
              handlePick(newLat, newLng);
            }
          }}
        />
      </MapContainer>
      <Box
        sx={{
          position: 'absolute',
          bottom: 8,
          left: 8,
          bgcolor: 'rgba(33, 33, 33, 0.7)',
          color: 'white',
          px: 1.5,
          py: 0.5,
          borderRadius: 1,
          fontSize: 12,
          display: 'flex',
          gap: 1,
          pointerEvents: 'none'
        }}
      >
        <Typography variant="caption">Lat: {markerPosition[0].toFixed(6)}</Typography>
        <Typography variant="caption">Lon: {markerPosition[1].toFixed(6)}</Typography>
      </Box>
      {disabled && (
        <Box
          sx={{
            position: 'absolute',
            inset: 0,
            bgcolor: 'rgba(255,255,255,0.6)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            pointerEvents: 'none'
          }}
        >
          <Typography variant="body2" color="text.secondary">
            Seleccioná modalidad presencial para ajustar la ubicación
          </Typography>
        </Box>
      )}
    </Box>
  );
};

export default LocationPickerMap;
