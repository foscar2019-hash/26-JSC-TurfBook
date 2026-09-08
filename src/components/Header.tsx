import React from 'react';
import { 
  Phone, 
  Mail, 
  Clock, 
  MapPin, 
  CalendarDays, 
  ShieldCheck, 
  CreditCard, 
  UserCheck, 
  Globe, 
  MessageSquare,
  Trophy,
  Swords,
  Camera,
  Compass
} from 'lucide-react';
import { ActiveTab, Language } from '../types';
import { APP_CONFIG, TRANSLATIONS } from '../data/initialData';

interface HeaderProps {
  activeTab: ActiveTab;
  setActiveTab: (tab: ActiveTab) => void;
  language: Language;
  setLanguage: (lang: Language) => void;
  onOpenQuickBook: () => void;
  bookingCount: number;
}

export const Header: React.FC<HeaderProps> = ({
  activeTab,
  setActiveTab,
  language,
  setLanguage,
  onOpenQuickBook,
  bookingCount,
}) => {
  const t = TRANSLATIONS[language];

  const navItems: { id: ActiveTab; label: string; icon: React.ReactNode; badge?: number }[] = [
    {
      id: 'pitches',
      label: t.bookPitch,
      icon: <Trophy className="w-3.5 h-3.5" />,
    },
    {
      id: 'schedule',
      label: t.viewSchedule,
      icon: <CalendarDays className="w-3.5 h-3.5" />,
    },
    {
      id: 'my-bookings',
      label: t.myBookings,
      icon: <UserCheck className="w-3.5 h-3.5" />,
      badge: bookingCount,
    },
    {
      id: 'teams',
      label: language === 'en' ? 'Teams & Matches' : 'Kooxaha & Tartanka',
      icon: <Swords className="w-3.5 h-3.5" />,
    },
    {
      id: 'gallery',
      label: language === 'en' ? 'Gallery' : 'Sawirrada',
      icon: <Camera className="w-3.5 h-3.5" />,
    },
    {
      id: 'live-map',
      label: language === 'en' ? 'Live Map' : 'Khariidadda',
      icon: <Compass className="w-3.5 h-3.5" />,
    },
    {
      id: 'payment-info',
      label: language === 'en' ? 'Zaad & eDahab' : 'Lacag-bixinta',
      icon: <CreditCard className="w-3.5 h-3.5" />,
    },
    {
      id: 'admin',
      label: t.adminDashboard,
      icon: <ShieldCheck className="w-3.5 h-3.5 text-amber-400" />,
    },
  ];

  return (
    <header className="bg-slate-900 text-white sticky top-0 z-40 border-b border-slate-800 shadow-md">
      {/* Top emergency & contact utility bar */}
      <div className="bg-slate-950 border-b border-slate-800/80 px-4 py-1.5 text-xs text-slate-300">
        <div className="max-w-7xl mx-auto flex flex-wrap items-center justify-between gap-2">
          <div className="flex items-center gap-4 flex-wrap">
            <a 
              href={`tel:${APP_CONFIG.contactPhone}`} 
              className="flex items-center gap-1.5 hover:text-emerald-400 transition-colors font-medium font-mono"
              id="header-phone-link"
            >
              <Phone className="w-3.5 h-3.5 text-emerald-400" />
              <span>{APP_CONFIG.contactPhone}</span>
            </a>
            <a 
              href={`mailto:${APP_CONFIG.contactEmail}`} 
              className="flex items-center gap-1.5 hover:text-emerald-400 transition-colors"
              id="header-email-link"
            >
              <Mail className="w-3.5 h-3.5 text-emerald-400" />
              <span>{APP_CONFIG.contactEmail}</span>
            </a>
            <span className="hidden lg:flex items-center gap-1 text-slate-400">
              <Clock className="w-3.5 h-3.5 text-amber-400" />
              <span>{APP_CONFIG.openingHours}</span>
            </span>
          </div>

          <div className="flex items-center gap-3">
            {/* Quick Merchant indicators */}
            <div className="hidden sm:flex items-center gap-2">
              <span className="bg-emerald-950/80 text-emerald-400 px-2 py-0.5 rounded text-[11px] font-semibold border border-emerald-800/60 font-mono">
                Zaad: {APP_CONFIG.zaadMerchant}
              </span>
              <span className="bg-amber-950/80 text-amber-300 px-2 py-0.5 rounded text-[11px] font-semibold border border-amber-800/60 font-mono">
                eDahab: {APP_CONFIG.edahabMerchant}
              </span>
            </div>

            {/* Language Toggle */}
            <button
              onClick={() => setLanguage(language === 'en' ? 'so' : 'en')}
              className="flex items-center gap-1.5 bg-slate-800 hover:bg-slate-700 text-slate-200 px-2.5 py-1 rounded-xl text-xs transition-colors border border-slate-700 font-bold"
              id="language-toggle-btn"
              title="Switch Language"
            >
              <Globe className="w-3.5 h-3.5 text-emerald-400" />
              <span>{language === 'en' ? 'SOOMAALI' : 'ENGLISH'}</span>
            </button>
          </div>
        </div>
      </div>

      {/* Main navigation row */}
      <div className="max-w-7xl mx-auto px-4 py-2.5 flex items-center justify-between gap-4">
        {/* Brand Logo & Name */}
        <div 
          onClick={() => setActiveTab('pitches')} 
          className="flex items-center gap-2.5 cursor-pointer group select-none shrink-0"
          id="brand-header"
        >
          <div className="w-9 h-9 rounded-xl bg-gradient-to-br from-emerald-500 to-emerald-700 flex items-center justify-center text-white shadow-md shadow-emerald-900/40 group-hover:scale-105 transition-transform">
            <Trophy className="w-5 h-5 text-emerald-100" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <span className="text-lg font-black tracking-tight text-white font-sans leading-none">
                {APP_CONFIG.appName}
              </span>
              <span className="bg-emerald-500/20 text-emerald-400 text-[10px] font-bold px-1.5 py-0.2 rounded uppercase tracking-wider border border-emerald-500/30">
                Arena
              </span>
            </div>
            <p className="text-[11px] text-slate-400 truncate max-w-[200px] sm:max-w-xs mt-0.5">
              {language === 'en' ? APP_CONFIG.tagline : APP_CONFIG.somaliTagline}
            </p>
          </div>
        </div>

        {/* Navigation Tabs (Desktop / Tablet) */}
        <nav className="hidden xl:flex items-center gap-1 bg-slate-950/70 p-1 rounded-2xl border border-slate-800 text-xs">
          {navItems.map((item) => {
            const isActive = activeTab === item.id;
            return (
              <button
                key={item.id}
                onClick={() => setActiveTab(item.id)}
                id={`nav-tab-${item.id}`}
                className={`px-3 py-1.5 rounded-xl font-bold transition-all flex items-center gap-1.5 ${
                  isActive
                    ? 'bg-emerald-600 text-white shadow-sm'
                    : 'text-slate-400 hover:text-white hover:bg-slate-800/60'
                }`}
              >
                {item.icon}
                <span>{item.label}</span>
                {item.badge !== undefined && item.badge > 0 && (
                  <span className="bg-emerald-400 text-slate-950 text-[10px] font-black px-1.5 rounded-full">
                    {item.badge}
                  </span>
                )}
              </button>
            );
          })}
        </nav>

        {/* Action Button: Book Now & WhatsApp */}
        <div className="flex items-center gap-2 shrink-0">
          <a
            href={`https://wa.me/${APP_CONFIG.contactPhone.replace(/[^0-9]/g, '')}?text=${encodeURIComponent(
              'Hello 26 JSC TurfBook, I would like to book a pitch or inquire about match fixtures.'
            )}`}
            target="_blank"
            rel="noreferrer"
            className="hidden sm:inline-flex items-center gap-1.5 bg-emerald-600 hover:bg-emerald-500 text-white px-3 py-1.5 rounded-xl text-xs font-bold transition-colors shadow-sm"
            id="header-whatsapp-btn"
          >
            <MessageSquare className="w-3.5 h-3.5" />
            <span>WhatsApp</span>
          </a>

          <button
            onClick={onOpenQuickBook}
            className="bg-emerald-400 hover:bg-emerald-300 text-slate-950 font-black px-3.5 py-1.5 rounded-xl text-xs transition-all shadow-md active:scale-98"
            id="header-book-now-btn"
          >
            {t.bookNow}
          </button>
        </div>
      </div>

      {/* Horizontal Scrollable Sub-Navigation for Mobile & Medium Screens */}
      <div className="xl:hidden flex items-center gap-1 overflow-x-auto no-scrollbar border-t border-slate-800/90 bg-slate-950/95 py-2 px-3 text-xs">
        {navItems.map((item) => {
          const isActive = activeTab === item.id;
          return (
            <button
              key={item.id}
              onClick={() => setActiveTab(item.id)}
              className={`px-3 py-1 rounded-xl font-bold whitespace-nowrap transition-all flex items-center gap-1.5 shrink-0 ${
                isActive
                  ? 'bg-emerald-600 text-white shadow-xs'
                  : 'text-slate-400 hover:text-white bg-slate-900/60'
              }`}
            >
              {item.icon}
              <span>{item.label}</span>
              {item.badge !== undefined && item.badge > 0 && (
                <span className="bg-emerald-400 text-slate-950 text-[10px] font-black px-1 rounded-full">
                  {item.badge}
                </span>
              )}
            </button>
          );
        })}
      </div>
    </header>
  );
};
