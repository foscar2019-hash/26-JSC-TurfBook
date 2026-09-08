import React, { useState } from 'react';
import { 
  Trophy, 
  Sparkles, 
  ShieldCheck, 
  Zap, 
  Calendar, 
  CreditCard, 
  MessageSquare,
  Users,
  CheckCircle2,
  Clock,
  Phone
} from 'lucide-react';
import { Pitch, Language, GameFormat } from '../types';
import { PitchCard } from './PitchCard';
import { APP_CONFIG, TRANSLATIONS } from '../data/initialData';

interface PitchesViewProps {
  pitches: Pitch[];
  language: Language;
  onBookPitch: (pitch: Pitch) => void;
  onViewSchedule: (pitchId?: string) => void;
  onOpenPaymentInfo: () => void;
}

export const PitchesView: React.FC<PitchesViewProps> = ({
  pitches,
  language,
  onBookPitch,
  onViewSchedule,
  onOpenPaymentInfo,
}) => {
  const t = TRANSLATIONS[language];
  const [formatFilter, setFormatFilter] = useState<string>('all');

  const filteredPitches = formatFilter === 'all'
    ? pitches
    : pitches.filter((p) => p.format.toLowerCase().includes(formatFilter.toLowerCase()));

  return (
    <div className="space-y-10" id="pitches-overview-section">
      {/* Hero Showcase Section */}
      <div className="relative rounded-3xl overflow-hidden bg-slate-900 text-white p-6 sm:p-10 border border-slate-800 shadow-xl">
        {/* Background ambient lighting */}
        <div className="absolute top-0 right-0 w-96 h-96 bg-emerald-600/20 rounded-full blur-3xl pointer-events-none -mr-20 -mt-20"></div>
        <div className="absolute bottom-0 left-1/3 w-80 h-80 bg-teal-600/10 rounded-full blur-3xl pointer-events-none"></div>

        <div className="relative z-10 max-w-3xl">
          <div className="inline-flex items-center gap-2 bg-emerald-950/80 border border-emerald-700/50 text-emerald-400 px-3 py-1 rounded-full text-xs font-bold mb-4 backdrop-blur-md">
            <Sparkles className="w-3.5 h-3.5" />
            <span>{language === 'en' ? 'Welcome to 26 JSC TurfBook Arena' : 'Ku Soo Dhawoow 26 JSC TurfBook'}</span>
          </div>

          <h1 className="text-3xl sm:text-5xl font-black tracking-tight leading-none mb-3">
            {APP_CONFIG.appName}
          </h1>

          <p className="text-slate-300 text-xs sm:text-base leading-relaxed mb-6 font-normal">
            {language === 'en'
              ? 'Hargeisa\'s premier high-definition floodlit artificial grass sport pitches. Book your team slot online with instant mobile money payment via Zaad (445686) or eDahab (10136).'
              : 'Garoomada casriga ah ee cawska macmalka ah lehna ileyska habeenkii. Ku xiro booskaaga toos adigoo isticmaalaya Zaad (445686) ama eDahab (10136).'}
          </p>

          {/* Quick Action Badges */}
          <div className="flex flex-wrap items-center gap-3 mb-6">
            <div className="flex items-center gap-2 bg-slate-800/80 backdrop-blur-md border border-slate-700 px-3 py-1.5 rounded-xl text-xs font-semibold">
              <span className="w-2 h-2 rounded-full bg-emerald-400 animate-ping"></span>
              <span className="text-slate-200">{language === 'en' ? 'Zaad Merchant:' : 'Merchant Zaad:'}</span>
              <strong className="text-emerald-400 font-mono text-xs">{APP_CONFIG.zaadMerchant}</strong>
            </div>

            <div className="flex items-center gap-2 bg-slate-800/80 backdrop-blur-md border border-slate-700 px-3 py-1.5 rounded-xl text-xs font-semibold">
              <span className="w-2 h-2 rounded-full bg-amber-400"></span>
              <span className="text-slate-200">{language === 'en' ? 'eDahab Merchant:' : 'Merchant eDahab:'}</span>
              <strong className="text-amber-400 font-mono text-xs">{APP_CONFIG.edahabMerchant}</strong>
            </div>

            <a
              href={`tel:${APP_CONFIG.contactPhone}`}
              className="flex items-center gap-1.5 bg-slate-800/80 hover:bg-slate-700/90 border border-slate-700 px-3 py-1.5 rounded-xl text-xs font-semibold text-slate-200 transition-colors"
            >
              <Phone className="w-3.5 h-3.5 text-emerald-400" />
              <span>{APP_CONFIG.contactPhone}</span>
            </a>
          </div>

          {/* Action CTAs */}
          <div className="flex flex-wrap items-center gap-3">
            <button
              onClick={() => onBookPitch(pitches[0])}
              className="bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-black px-6 py-3 rounded-xl text-xs sm:text-sm transition-all shadow-lg shadow-emerald-500/20 active:scale-98 flex items-center gap-2"
              id="hero-book-pitch-btn"
            >
              <Trophy className="w-4 h-4" />
              <span>{t.bookNow}</span>
            </button>

            <button
              onClick={() => onViewSchedule()}
              className="bg-slate-800/90 hover:bg-slate-700 text-white font-bold px-5 py-3 rounded-xl text-xs sm:text-sm transition-all border border-slate-700 flex items-center gap-2"
              id="hero-view-schedule-btn"
            >
              <Calendar className="w-4 h-4 text-emerald-400" />
              <span>{t.viewSchedule}</span>
            </button>

            <button
              onClick={onOpenPaymentInfo}
              className="text-xs text-slate-400 hover:text-white px-3 py-2 underline transition-colors"
            >
              {language === 'en' ? 'Payment details & codes →' : 'Faahfaahinta lacag-bixinta →'}
            </button>
          </div>
        </div>
      </div>

      {/* Pitches Filter Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-200 pb-4">
        <div>
          <h2 className="text-xl sm:text-2xl font-black text-slate-900 tracking-tight">
            {language === 'en' ? 'Available Sport Pitches' : 'Garoomada Diyaarka ah'}
          </h2>
          <p className="text-xs text-slate-500 mt-0.5">
            {language === 'en'
              ? 'Select your preferred turf specification and game format below.'
              : 'Dooro garoonka ku habboon kooxdaada iyo ciyaartiina.'}
          </p>
        </div>

        {/* Format Filter Tabs */}
        <div className="flex items-center gap-1.5 overflow-x-auto pb-1 sm:pb-0">
          {[
            { key: 'all', label: language === 'en' ? 'All Formats' : 'Dhammaan' },
            { key: '7-a-side', label: '7-a-side' },
            { key: '5-a-side', label: '5-a-side' },
            { key: '6-a-side', label: '6-a-side VIP' },
            { key: 'futsal', label: 'Futsal Cage' },
          ].map((item) => (
            <button
              key={item.key}
              onClick={() => setFormatFilter(item.key)}
              className={`px-3 py-1.5 rounded-xl text-xs font-bold transition-all shrink-0 ${
                formatFilter === item.key
                  ? 'bg-slate-900 text-white shadow-xs'
                  : 'bg-white border border-slate-200 text-slate-600 hover:bg-slate-100'
              }`}
            >
              {item.label}
            </button>
          ))}
        </div>
      </div>

      {/* Pricing Notification Bar */}
      <div className="bg-emerald-50/80 border border-emerald-200/80 rounded-2xl p-3.5 flex flex-wrap items-center justify-between gap-3 text-xs">
        <div className="flex items-center gap-2 text-emerald-950 font-bold">
          <Zap className="w-4 h-4 text-emerald-600 shrink-0" />
          <span>{language === 'en' ? 'Unified Rates for All 4 Pitches:' : 'Qiimaha Midaysan ee 4-ta Garoon:'}</span>
        </div>
        <div className="flex items-center gap-3">
          <span className="inline-flex items-center gap-1.5 bg-amber-100 text-amber-900 px-2.5 py-1 rounded-lg font-bold border border-amber-200 text-xs">
            <span>☀️ Day Game:</span>
            <span className="font-extrabold text-amber-950">$18 / hr</span>
            <span className="text-[10px] text-amber-700 font-normal">(06:00 - 18:00)</span>
          </span>
          <span className="inline-flex items-center gap-1.5 bg-emerald-100 text-emerald-900 px-2.5 py-1 rounded-lg font-bold border border-emerald-200 text-xs">
            <span>🌙 Night Floodlit:</span>
            <span className="font-extrabold text-emerald-950">$25 / hr</span>
            <span className="text-[10px] text-emerald-700 font-normal">(18:00 - 00:00)</span>
          </span>
        </div>
      </div>

      {/* Pitch Cards Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-2 gap-6">
        {filteredPitches.map((pitch) => (
          <PitchCard
            key={pitch.id}
            pitch={pitch}
            language={language}
            onBookPitch={onBookPitch}
            onViewPitchSchedule={onViewSchedule}
          />
        ))}
      </div>

      {/* Value Pillars Section */}
      <div className="bg-white rounded-3xl border border-slate-200/90 p-6 sm:p-8 shadow-sm">
        <h3 className="text-sm font-bold text-slate-900 uppercase tracking-wider mb-5 flex items-center gap-2">
          <ShieldCheck className="w-4 h-4 text-emerald-600" />
          <span>{language === 'en' ? 'Why Play at 26 JSC TurfBook?' : 'Maxaad u Dooranaysaa 26 JSC TurfBook?'}</span>
        </h3>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
          <div className="p-4 rounded-2xl bg-slate-50 border border-slate-100">
            <div className="w-8 h-8 rounded-xl bg-emerald-100 text-emerald-700 flex items-center justify-center font-bold mb-3">
              <Zap className="w-4 h-4" />
            </div>
            <h4 className="font-bold text-xs text-slate-900 mb-1">
              {language === 'en' ? 'FIFA-Spec Synthetic Grass' : 'Caws Macmal ah oo Heer Caalami ah'}
            </h4>
            <p className="text-[11px] text-slate-500 leading-relaxed">
              {language === 'en'
                ? 'Monofilament shock-absorbing turf protecting knees and joints during high-intensity games.'
                : 'Caws jilicsan oo ilaaliya jilbaha iyo xubnaha inta lagu jiro ciyaaraha adag.'}
            </p>
          </div>

          <div className="p-4 rounded-2xl bg-slate-50 border border-slate-100">
            <div className="w-8 h-8 rounded-xl bg-emerald-100 text-emerald-700 flex items-center justify-center font-bold mb-3">
              <Clock className="w-4 h-4" />
            </div>
            <h4 className="font-bold text-xs text-slate-900 mb-1">
              {language === 'en' ? 'Night Floodlit Fixtures' : 'Ileys Habeenkii ah'}
            </h4>
            <p className="text-[11px] text-slate-500 leading-relaxed">
              {language === 'en'
                ? 'Stadium-grade LED floodlights operating until midnight every single night.'
                : 'Ileys xooggan oo ku habboon ciyaaraha habeenkii ilaa 12:00 habeenimo.'}
            </p>
          </div>

          <div className="p-4 rounded-2xl bg-slate-50 border border-slate-100">
            <div className="w-8 h-8 rounded-xl bg-emerald-100 text-emerald-700 flex items-center justify-center font-bold mb-3">
              <CreditCard className="w-4 h-4" />
            </div>
            <h4 className="font-bold text-xs text-slate-900 mb-1">
              {language === 'en' ? 'Zaad 445686 & eDahab 10136' : 'Zaad 445686 & eDahab 10136'}
            </h4>
            <p className="text-[11px] text-slate-500 leading-relaxed">
              {language === 'en'
                ? 'Instant verification with your mobile money reference or one-tap WhatsApp confirmation.'
                : 'Bixin toos ah oo degdeg ah adigoo isticmaalaya Zaad ama eDahab.'}
            </p>
          </div>

          <div className="p-4 rounded-2xl bg-slate-50 border border-slate-100">
            <div className="w-8 h-8 rounded-xl bg-emerald-100 text-emerald-700 flex items-center justify-center font-bold mb-3">
              <Users className="w-4 h-4" />
            </div>
            <h4 className="font-bold text-xs text-slate-900 mb-1">
              {language === 'en' ? 'Referees & Match Bibs' : 'Garsoore & Fanaanado'}
            </h4>
            <p className="text-[11px] text-slate-500 leading-relaxed">
              {language === 'en'
                ? 'Official balls, team pinnies, cold hydration buckets, and certified referees on demand.'
                : 'Kubbado rasmi ah, fanaanado kala saar ah, iyo biyo qabow oo diyaarsan.'}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};
