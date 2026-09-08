import React, { useEffect, useRef, useState } from 'react';
import L from 'leaflet';
import { 
  MapPin, 
  Navigation, 
  Layers, 
  Compass, 
  Phone, 
  Clock, 
  Car, 
  Sparkles,
  Trophy,
  ExternalLink,
  ShieldCheck,
  CheckCircle2
} from 'lucide-react';
import { APP_CONFIG } from '../data/initialData';
import { Language, Pitch } from '../types';

interface LiveMapViewProps {
  pitches: Pitch[];
  onBookPitch: (pitch: Pitch) => void;
  language: Language;
}

export const LiveMapView: React.FC<LiveMapViewProps> = ({
  pitches,
  onBookPitch,
  language,
}) => {
  const mapContainerRef = useRef<HTMLDivElement | null>(null);
  const mapInstanceRef = useRef<L.Map | null>(null);
  const [selectedPoint, setSelectedPoint] = useState<string | null>(null);
  const [mapLayer, setMapLayer] = useState<'streets' | 'satellite'>('streets');

  const centerLat = APP_CONFIG.coordinates.lat;
  const centerLng = APP_CONFIG.coordinates.lng;

  // Arena points of interest
  const POIs = [
    {
      id: 'main-gate',
      name: '26 JSC TurfBook Main Gate & Desk',
      somaliName: 'Albaabka Guud & Xafiiska 26 JSC',
      category: 'Entrance & Desk',
      lat: centerLat,
      lng: centerLng,
      description: 'Main reception desk, booking verification, payment assistance (Zaad 445686 / eDahab 10136).',
    },
    {
      id: 'pitch-1',
      name: 'Pitch 1 - Championship Arena (7-a-side)',
      somaliName: 'Garoonka 1-aad - Arena-da Guud (7-a-side)',
      category: 'Championship Pitch',
      lat: centerLat + 0.00035,
      lng: centerLng + 0.00045,
      pitchId: 'pitch-1',
      description: 'FIFA Synthetic Grass, full LED night floodlights, 14-18 players capacity.',
    },
    {
      id: 'pitch-2',
      name: 'Pitch 2 - Premier Astro Turf (5-a-side)',
      somaliName: 'Garoonka 2-aad - Astro Turf (5-a-side)',
      category: 'Fast Turf',
      lat: centerLat + 0.00032,
      lng: centerLng - 0.00040,
      pitchId: 'pitch-2',
      description: 'High-density AstroTurf, rapid ball movement, 10-14 players capacity.',
    },
    {
      id: 'pitch-3',
      name: 'Pitch 3 - VIP Floodlight Turf (6-a-side)',
      somaliName: 'Garoonka 3-aad - VIP Arena (6-a-side)',
      category: 'VIP Turf',
      lat: centerLat - 0.00030,
      lng: centerLng + 0.00035,
      pitchId: 'pitch-3',
      description: 'Shock-pad cushioned turf with VIP lounge seating and fixture camera mounts.',
    },
    {
      id: 'pitch-4',
      name: 'Pitch 4 - Skills & Futsal Cage',
      somaliName: 'Garoonka 4-aad - Futsal & Xirfadaha',
      category: 'Futsal Cage',
      lat: centerLat - 0.00032,
      lng: centerLng - 0.00035,
      pitchId: 'pitch-4',
      description: 'Indoor-style rubber shock turf cage for rapid 4v4/5v5 agility fixtures.',
    },
    {
      id: 'amenities',
      name: 'Dugout Lounge, Showers & Hydration Cafe',
      somaliName: 'Maqaayada, Qolka Labiska & Biyo Qabow',
      category: 'Facilities',
      lat: centerLat - 0.00010,
      lng: centerLng + 0.00010,
      description: 'Chilled energy drinks, mineral water, locker rooms, clean showers, and spectator benches.',
    },
  ];

  // Initialize and update Leaflet Map
  useEffect(() => {
    if (!mapContainerRef.current) return;

    // Destroy prior instance if existing
    if (mapInstanceRef.current) {
      mapInstanceRef.current.remove();
      mapInstanceRef.current = null;
    }

    const map = L.map(mapContainerRef.current, {
      center: [centerLat, centerLng],
      zoom: 18,
      zoomControl: false,
    });

    // Add custom zoom control top-right
    L.control.zoom({ position: 'topright' }).addTo(map);

    // Tile Layers
    const streetTiles = L.tileLayer(
      'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
      {
        attribution: '&copy; OpenStreetMap contributors',
        maxZoom: 19,
      }
    );

    const satelliteTiles = L.tileLayer(
      'https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}',
      {
        attribution: 'Tiles &copy; Esri &mdash; Source: Esri, i-cubed, USDA, USGS, AEX, GeoEye',
        maxZoom: 19,
      }
    );

    if (mapLayer === 'satellite') {
      satelliteTiles.addTo(map);
    } else {
      streetTiles.addTo(map);
    }

    // Custom Icon generator
    const createCustomIcon = (isMain: boolean, isPitch: boolean) => {
      return L.divIcon({
        className: 'custom-map-marker',
        html: `
          <div style="
            background-color: ${isMain ? '#059669' : isPitch ? '#2563eb' : '#d97706'};
            width: 32px;
            height: 32px;
            border-radius: 50%;
            border: 3px solid white;
            box-shadow: 0 4px 12px rgba(0,0,0,0.35);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 14px;
            font-weight: bold;
          ">
            ${isMain ? '🏟️' : isPitch ? '⚽' : '📍'}
          </div>
        `,
        iconSize: [32, 32],
        iconAnchor: [16, 16],
        popupAnchor: [0, -18],
      });
    };

    // Add Markers
    POIs.forEach((poi) => {
      const isMain = poi.id === 'main-gate';
      const isPitch = poi.id.startsWith('pitch');
      const marker = L.marker([poi.lat, poi.lng], {
        icon: createCustomIcon(isMain, isPitch),
      }).addTo(map);

      const popupContent = `
        <div style="font-family: sans-serif; font-size: 12px; padding: 4px; min-width: 180px;">
          <strong style="color: #0f172a; font-size: 13px; display: block; margin-bottom: 2px;">
            ${language === 'en' ? poi.name : poi.somaliName}
          </strong>
          <p style="color: #64748b; margin: 4px 0 8px 0; font-size: 11px; line-height: 1.4;">
            ${poi.description}
          </p>
          <div style="font-size: 10px; color: #059669; font-weight: bold;">
            📍 26 June District, Hargeisa
          </div>
        </div>
      `;

      marker.bindPopup(popupContent);
      marker.on('click', () => {
        setSelectedPoint(poi.id);
      });
    });

    // Add Arena Perimeter Polygon
    const arenaBounds: [number, number][] = [
      [centerLat + 0.00055, centerLng - 0.00065],
      [centerLat + 0.00055, centerLng + 0.00065],
      [centerLat - 0.00050, centerLng + 0.00065],
      [centerLat - 0.00050, centerLng - 0.00065],
    ];

    L.polygon(arenaBounds, {
      color: '#059669',
      weight: 2,
      dashArray: '5, 5',
      fillColor: '#10b981',
      fillOpacity: 0.12,
    }).addTo(map);

    mapInstanceRef.current = map;

    return () => {
      map.remove();
      mapInstanceRef.current = null;
    };
  }, [centerLat, centerLng, language, mapLayer]);

  const handleNavigateGoogleMaps = () => {
    const url = `https://www.google.com/maps/dir/?api=1&destination=${centerLat},${centerLng}`;
    window.open(url, '_blank', 'noopener,noreferrer');
  };

  const handleSelectPoiCard = (poi: typeof POIs[0]) => {
    setSelectedPoint(poi.id);
    if (mapInstanceRef.current) {
      mapInstanceRef.current.flyTo([poi.lat, poi.lng], 19, { duration: 1 });
    }
  };

  return (
    <div className="space-y-6" id="live-map-view">
      {/* Top Banner / Heading */}
      <div className="bg-white p-5 sm:p-6 rounded-3xl border border-slate-200/90 shadow-sm flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div className="inline-flex items-center gap-1.5 bg-emerald-50 text-emerald-700 px-2.5 py-0.5 rounded-full text-xs font-bold mb-1.5 border border-emerald-200/60">
            <Compass className="w-3.5 h-3.5" />
            <span>{language === 'en' ? 'Live Interactive Arena Map' : 'Khariidadda Tooska ah ee Garoonka'}</span>
          </div>
          <h2 className="text-xl sm:text-2xl font-black text-slate-900 tracking-tight">
            {language === 'en' ? 'Locate & Navigate to 26 JSC TurfBook' : 'Goobta & Dariiqa loo maro 26 JSC TurfBook'}
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            {language === 'en'
              ? `${APP_CONFIG.location}. View all 4 pitches, entrance gate, cafeteria, and secure parking.`
              : `${APP_CONFIG.somaliLocation}.`}
          </p>
        </div>

        {/* Action Buttons */}
        <div className="flex flex-wrap items-center gap-2">
          <button
            onClick={() => setMapLayer(mapLayer === 'streets' ? 'satellite' : 'streets')}
            className="flex items-center gap-1.5 bg-slate-100 hover:bg-slate-200 text-slate-800 font-bold px-3 py-2 rounded-xl text-xs transition-colors border border-slate-200"
            title="Toggle Satellite Imagery"
          >
            <Layers className="w-4 h-4 text-slate-600" />
            <span>{mapLayer === 'streets' ? 'Satellite View' : 'Street Map'}</span>
          </button>

          <button
            onClick={handleNavigateGoogleMaps}
            className="flex items-center gap-2 bg-emerald-600 hover:bg-emerald-500 text-white font-bold px-4 py-2 rounded-xl text-xs transition-all shadow-md active:scale-98"
            id="google-maps-directions-btn"
          >
            <Navigation className="w-4 h-4" />
            <span>{language === 'en' ? 'Get Directions (GPS)' : 'Fur Google Maps'}</span>
            <ExternalLink className="w-3.5 h-3.5 ml-0.5 opacity-80" />
          </button>
        </div>
      </div>

      {/* Main Map Frame */}
      <div className="relative bg-slate-900 rounded-3xl overflow-hidden border border-slate-200 shadow-xl min-h-[480px] h-[520px]">
        {/* Leaflet container */}
        <div ref={mapContainerRef} className="w-full h-full z-10" />

        {/* Floating Arena Quick Stats Pill */}
        <div className="absolute top-4 left-4 z-20 bg-slate-900/90 backdrop-blur-md text-white p-3 rounded-2xl border border-slate-700 shadow-xl max-w-xs text-xs space-y-1 hidden sm:block">
          <div className="flex items-center gap-2 font-bold text-emerald-400">
            <MapPin className="w-4 h-4" />
            <span>26 JSC TurfBook Arena</span>
          </div>
          <div className="text-[11px] text-slate-300">
            {language === 'en' ? APP_CONFIG.location : APP_CONFIG.somaliLocation}
          </div>
          <div className="flex items-center gap-2 text-[10px] text-slate-400 pt-1 font-mono">
            <span>Lat: {centerLat}</span>
            <span>•</span>
            <span>Lng: {centerLng}</span>
          </div>
        </div>

        {/* Floating Reset Recenter Button */}
        <div className="absolute bottom-4 left-4 z-20 flex items-center gap-2">
          <button
            onClick={() => {
              if (mapInstanceRef.current) {
                mapInstanceRef.current.flyTo([centerLat, centerLng], 18);
              }
            }}
            className="bg-white/95 hover:bg-white text-slate-900 font-bold px-3 py-1.5 rounded-xl shadow-lg border border-slate-200 text-xs flex items-center gap-1.5 transition-all"
          >
            <Compass className="w-3.5 h-3.5 text-emerald-600" />
            <span>{language === 'en' ? 'Recenter Arena' : 'Xarunta Garoonka'}</span>
          </button>
        </div>
      </div>

      {/* Points of Interest Grid */}
      <div>
        <h3 className="text-sm font-bold text-slate-900 uppercase tracking-wider mb-3 flex items-center gap-2">
          <Trophy className="w-4 h-4 text-emerald-600" />
          <span>{language === 'en' ? 'Arena Facilities & Pitches On-Site' : 'Qaybaha & Garoomada Xarunta'}</span>
        </h3>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {POIs.map((poi) => {
            const isPitch = poi.pitchId !== undefined;
            const matchedPitch = pitches.find((p) => p.id === poi.pitchId);

            return (
              <div
                key={poi.id}
                onClick={() => handleSelectPoiCard(poi)}
                className={`p-4 rounded-2xl border transition-all cursor-pointer bg-white hover:border-emerald-300 hover:shadow-md ${
                  selectedPoint === poi.id
                    ? 'border-emerald-500 ring-2 ring-emerald-500/20 shadow-sm'
                    : 'border-slate-200'
                }`}
              >
                <div className="flex items-start justify-between gap-2 mb-2">
                  <span className="text-xs font-bold text-slate-900 flex items-center gap-1.5">
                    <span>{isPitch ? '⚽' : '📍'}</span>
                    <span>{language === 'en' ? poi.name : poi.somaliName}</span>
                  </span>
                  <span className="text-[10px] font-bold px-2 py-0.5 rounded-md bg-slate-100 text-slate-600">
                    {poi.category}
                  </span>
                </div>

                <p className="text-xs text-slate-500 leading-relaxed mb-3">
                  {poi.description}
                </p>

                <div className="flex items-center justify-between text-xs pt-2 border-t border-slate-100">
                  <span className="text-[11px] text-emerald-700 font-medium">Click to focus on map</span>
                  {matchedPitch && (
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        onBookPitch(matchedPitch);
                      }}
                      className="text-[11px] font-bold bg-emerald-600 hover:bg-emerald-500 text-white px-2.5 py-1 rounded-lg transition-colors"
                    >
                      {language === 'en' ? 'Book Pitch' : 'Dalbo'}
                    </button>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* Getting Here & Directions Guide */}
      <div className="bg-white rounded-3xl border border-slate-200/90 p-6 shadow-sm">
        <h3 className="text-sm font-bold text-slate-900 uppercase tracking-wider mb-4 flex items-center gap-2">
          <Car className="w-4 h-4 text-emerald-600" />
          <span>{language === 'en' ? 'How to Reach 26 JSC TurfBook' : 'Sida Loo Yimaado Garoonka'}</span>
        </h3>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-5 text-xs">
          <div className="p-4 rounded-2xl bg-slate-50 border border-slate-100 space-y-1.5">
            <div className="font-bold text-slate-900 flex items-center gap-1.5">
              <Navigation className="w-3.5 h-3.5 text-emerald-600" />
              <span>{language === 'en' ? 'From Wadnaha Road' : 'Laga soo galo Wadnaha Road'}</span>
            </div>
            <p className="text-slate-600 leading-relaxed">
              {language === 'en'
                ? 'Turn south at the 26 June District intersection. Follow the floodlight towers visible 300 meters ahead.'
                : 'U leexo dhinaca koonfureed ee isgoyska 26 June. Raac ileyska garoonka ee muuqda 300m.'}
            </p>
          </div>

          <div className="p-4 rounded-2xl bg-slate-50 border border-slate-100 space-y-1.5">
            <div className="font-bold text-slate-900 flex items-center gap-1.5">
              <Car className="w-3.5 h-3.5 text-emerald-600" />
              <span>{language === 'en' ? 'Bajaaj / Taxi Instructions' : 'Tilmaamaha Bajaajka & Taksiga'}</span>
            </div>
            <p className="text-slate-600 leading-relaxed">
              {language === 'en'
                ? `Tell your driver: "26 June, Garoonka TurfBook ee cusub (New Turf Sports Centre)". Driver contact: ${APP_CONFIG.contactPhone}.`
                : `U sheeg darawalka: "26 June, Garoonka TurfBook ee cusub". Telefoonka: ${APP_CONFIG.contactPhone}.`}
            </p>
          </div>

          <div className="p-4 rounded-2xl bg-slate-50 border border-slate-100 space-y-1.5">
            <div className="font-bold text-slate-900 flex items-center gap-1.5">
              <Clock className="w-3.5 h-3.5 text-emerald-600" />
              <span>{language === 'en' ? 'Hours & Gate Security' : 'Saacadaha & Amniga'}</span>
            </div>
            <p className="text-slate-600 leading-relaxed">
              {language === 'en'
                ? `Open daily from 6:00 AM until 12:00 Midnight. Guarded parking available for team vehicles and bikes.`
                : `Furan maalin kasta 06:00 subaxnimo ilaa 12:00 habeenimo. Goob baarkin oo ammaan ah.`}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};
