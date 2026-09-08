import React from 'react';
import { 
  Users, 
  Maximize2, 
  Zap, 
  Check, 
  Calendar, 
  Sparkles 
} from 'lucide-react';
import { Pitch, Language } from '../types';
import { TRANSLATIONS } from '../data/initialData';

interface PitchCardProps {
  pitch: Pitch;
  language: Language;
  onBookPitch: (pitch: Pitch) => void;
  onViewPitchSchedule: (pitchId: string) => void;
}

export const PitchCard: React.FC<PitchCardProps> = ({
  pitch,
  language,
  onBookPitch,
  onViewPitchSchedule,
}) => {
  const t = TRANSLATIONS[language];

  return (
    <div 
      className="bg-white rounded-2xl overflow-hidden border border-slate-200/80 shadow-sm hover:shadow-lg transition-all duration-300 flex flex-col group"
      id={`pitch-card-${pitch.id}`}
    >
      {/* Top Banner Image with Badges */}
      <div className="relative h-48 sm:h-56 overflow-hidden bg-slate-900">
        <img
          src={pitch.imageUrl}
          alt={pitch.name}
          className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500 opacity-90"
        />
        <div className="absolute inset-0 bg-gradient-to-t from-slate-950/80 via-transparent to-black/30" />

        {/* Top Badges */}
        <div className="absolute top-3 left-3 flex flex-wrap gap-1.5">
          <span className="bg-emerald-600/90 backdrop-blur-md text-white font-bold text-xs px-2.5 py-1 rounded-full uppercase tracking-wider shadow-sm">
            {pitch.format}
          </span>
          {pitch.isFloodlit && (
            <span className="bg-amber-500/90 backdrop-blur-md text-slate-950 font-bold text-xs px-2.5 py-1 rounded-full flex items-center gap-1 shadow-sm">
              <Zap className="w-3.5 h-3.5 fill-slate-950" />
              <span>{language === 'en' ? 'Night Floodlit' : 'Ileys Habeen'}</span>
            </span>
          )}
        </div>

        {/* Day & Night Price Tag */}
        <div className="absolute bottom-3 right-3 bg-slate-950/90 backdrop-blur-md border border-slate-700/80 text-white px-3 py-1.5 rounded-xl shadow-md text-right">
          <div className="flex items-center justify-end gap-1.5 text-xs leading-none">
            <span className="text-[10px] text-amber-300 font-semibold uppercase">☀️ Day:</span>
            <span className="font-extrabold text-white font-sans">${pitch.dayRate || 18}</span>
            <span className="text-[10px] text-slate-400">/h</span>
          </div>
          <div className="flex items-center justify-end gap-1.5 text-xs leading-none mt-1">
            <span className="text-[10px] text-emerald-400 font-semibold uppercase">🌙 Night:</span>
            <span className="font-extrabold text-emerald-400 font-sans">${pitch.nightRate || 25}</span>
            <span className="text-[10px] text-slate-400">/h</span>
          </div>
        </div>

        {/* Title overlay at bottom-left */}
        <div className="absolute bottom-3 left-3 right-40">
          <h3 className="text-white font-bold text-lg leading-tight drop-shadow-md">
            {language === 'en' ? pitch.name : pitch.somaliName}
          </h3>
          <p className="text-slate-300 text-xs font-medium mt-0.5">
            {pitch.surface}
          </p>
        </div>
      </div>

      {/* Body specifications */}
      <div className="p-5 flex-1 flex flex-col justify-between">
        <div>
          {/* Quick Specs Grid */}
          <div className="grid grid-cols-2 gap-2.5 mb-4 p-2.5 bg-slate-50 rounded-xl border border-slate-100 text-xs text-slate-700">
            <div className="flex items-center gap-2">
              <Users className="w-4 h-4 text-emerald-600 shrink-0" />
              <div>
                <span className="text-[10px] text-slate-600 block uppercase font-medium">
                  {language === 'en' ? 'Capacity' : 'Awoodda'}
                </span>
                <span className="font-semibold text-slate-800">{pitch.capacity}</span>
              </div>
            </div>
            <div className="flex items-center gap-2">
              <Maximize2 className="w-4 h-4 text-emerald-600 shrink-0" />
              <div>
                <span className="text-[10px] text-slate-600 block uppercase font-medium">
                  {language === 'en' ? 'Pitch Size' : 'Cabbirka'}
                </span>
                <span className="font-semibold text-slate-800">{pitch.dimensions}</span>
              </div>
            </div>
          </div>

          {/* Key Features List */}
          <div className="mb-4">
            <h4 className="text-xs font-bold text-slate-700 uppercase tracking-wider mb-2 flex items-center gap-1.5">
              <Sparkles className="w-3.5 h-3.5 text-emerald-600" />
              <span>{t.amenities}</span>
            </h4>
            <ul className="space-y-1.5">
              {pitch.features.map((feat, idx) => (
                <li key={idx} className="text-xs text-slate-600 flex items-start gap-2">
                  <Check className="w-3.5 h-3.5 text-emerald-600 mt-0.5 shrink-0" />
                  <span>{feat}</span>
                </li>
              ))}
            </ul>
          </div>
        </div>

        {/* Action Buttons */}
        <div className="pt-4 border-t border-slate-100 flex items-center gap-2">
          <button
            onClick={() => onViewPitchSchedule(pitch.id)}
            className="px-3 py-2.5 border border-slate-200 hover:bg-slate-50 text-slate-700 rounded-xl text-xs font-semibold transition-colors flex items-center justify-center gap-1.5"
            id={`view-schedule-${pitch.id}`}
            title="View Pitch Schedule"
          >
            <Calendar className="w-4 h-4 text-slate-500" />
            <span className="hidden sm:inline">{language === 'en' ? 'Slots' : 'Saacadaha'}</span>
          </button>

          <button
            onClick={() => onBookPitch(pitch)}
            className="flex-1 bg-emerald-600 hover:bg-emerald-700 text-white font-bold py-2.5 px-4 rounded-xl text-xs sm:text-sm transition-all shadow-sm hover:shadow-emerald-600/20 active:scale-[0.99] flex items-center justify-center gap-2"
            id={`book-pitch-btn-${pitch.id}`}
          >
            <span>{t.bookNow}</span>
          </button>
        </div>
      </div>
    </div>
  );
};
