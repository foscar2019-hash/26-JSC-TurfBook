import React, { useState, useMemo } from 'react';
import { 
  Search, 
  Calendar, 
  Clock, 
  Receipt, 
  MessageSquare, 
  XCircle, 
  CheckCircle2, 
  AlertCircle, 
  Trophy, 
  Phone,
  Filter,
  Hash,
  Users,
  Check,
  X,
  Smartphone
} from 'lucide-react';
import { Booking, Language } from '../types';
import { APP_CONFIG, TRANSLATIONS } from '../data/initialData';
import { generateWhatsAppBookingUrl } from '../utils/helpers';

interface MyBookingsViewProps {
  bookings: Booking[];
  onViewTicket: (booking: Booking) => void;
  onCancelBooking: (bookingId: string) => void;
  onOpenBookPitch: () => void;
  onSimulateSms?: (booking: Booking) => void;
  language: Language;
}

type FilterMode = 'all' | 'team' | 'ref';

// Safe text highlighter for matching queries
function highlightMatch(text: string, query: string): React.ReactNode {
  if (!query || !query.trim()) return text;
  const cleaned = query.trim().replace(/^#+/, '').trim();
  const searchPattern = cleaned || query.trim();
  if (!searchPattern) return text;

  try {
    const escaped = searchPattern.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    const regex = new RegExp(`(${escaped})`, 'gi');
    const parts = text.split(regex);
    if (parts.length <= 1) return text;
    return parts.map((part, idx) =>
      regex.test(part) ? (
        <mark
          key={idx}
          className="bg-amber-200 text-amber-950 font-bold px-0.5 rounded-xs"
        >
          {part}
        </mark>
      ) : (
        part
      )
    );
  } catch {
    return text;
  }
}

export const MyBookingsView: React.FC<MyBookingsViewProps> = ({
  bookings,
  onViewTicket,
  onCancelBooking,
  onOpenBookPitch,
  onSimulateSms,
  language,
}) => {
  const t = TRANSLATIONS[language];
  const [searchTerm, setSearchTerm] = useState<string>('');
  const [filterMode, setFilterMode] = useState<FilterMode>('all');
  const [statusFilter, setStatusFilter] = useState<'all' | 'paid' | 'pending'>('all');
  const [cancelModalBooking, setCancelModalBooking] = useState<Booking | null>(null);

  // Extract unique team names for quick filter chips
  const uniqueTeamNames = useMemo(() => {
    const set = new Set<string>();
    bookings.forEach((b) => {
      if (b.teamName && b.teamName.trim()) set.add(b.teamName.trim());
    });
    return Array.from(set);
  }, [bookings]);

  // Extract unique reference codes for quick filter chips
  const uniqueRefCodes = useMemo(() => {
    const set = new Set<string>();
    bookings.forEach((b) => {
      if (b.referenceCode && b.referenceCode.trim()) set.add(b.referenceCode.trim());
    });
    return Array.from(set);
  }, [bookings]);

  // Filter logic strictly targeting Team Name or Reference Code
  const filteredBookings = useMemo(() => {
    return bookings.filter((b) => {
      // Status filter
      if (statusFilter === 'paid' && b.paymentStatus !== 'paid') return false;
      if (statusFilter === 'pending' && b.paymentStatus === 'paid') return false;

      // Search term filter
      if (!searchTerm.trim()) return true;
      const term = searchTerm.trim().toLowerCase();
      const cleanRef = term.replace(/^#+/, '').trim();
      const normalizedTerm = term.replace(/[^a-z0-9]/g, '');
      const normalizedRef = b.referenceCode.toLowerCase().replace(/[^a-z0-9]/g, '');

      // Check match on Team Name
      const matchesTeam = b.teamName.toLowerCase().includes(term);

      // Check match on Reference Code (handles '#JSC-8841', '8841', 'jsc-8841', 'jsc 8841', etc.)
      const matchesRef =
        b.referenceCode.toLowerCase().includes(term) ||
        (cleanRef.length > 0 && b.referenceCode.toLowerCase().includes(cleanRef)) ||
        (normalizedTerm.length > 0 && normalizedRef.includes(normalizedTerm));

      if (filterMode === 'team') {
        return matchesTeam;
      } else if (filterMode === 'ref') {
        return matchesRef;
      } else {
        // 'all': matches either Team Name OR Reference Code (also fallback to captain name if searched)
        return (
          matchesTeam ||
          matchesRef ||
          b.customerName.toLowerCase().includes(term) ||
          b.customerPhone.toLowerCase().includes(term)
        );
      }
    });
  }, [bookings, searchTerm, filterMode, statusFilter]);

  return (
    <div className="space-y-6" id="my-bookings-view">
      {/* Header & Dedicated Search / Filter Bar */}
      <div className="bg-white p-5 sm:p-6 rounded-3xl border border-slate-200/90 shadow-sm space-y-4" id="bookings-search-container">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div className="inline-flex items-center gap-1.5 bg-emerald-50 text-emerald-700 px-2.5 py-0.5 rounded-full text-xs font-bold mb-1.5 border border-emerald-200/60">
              <Trophy className="w-3.5 h-3.5" />
              <span>{language === 'en' ? 'Bookings Manager' : 'Maamulka Ballamaha'}</span>
            </div>
            <h2 className="text-xl sm:text-2xl font-black text-slate-900 tracking-tight">
              {language === 'en' ? 'Search & Filter Your Bookings' : 'Raadi & Kala Saar Ballamahaaga'}
            </h2>
            <p className="text-xs text-slate-500 mt-1">
              {language === 'en'
                ? 'Search instantly by Team Name (e.g. "Banaadir United", "26 June FC") or Reference Code (e.g. "#JSC-8841" or "8841").'
                : 'Kaga raadi toos Magaca Kooxdaada ama Lambarka Tixraaca (#JSC-8841).'}
            </p>
          </div>

          <div className="flex items-center gap-2">
            <span className="text-xs font-bold text-slate-600 bg-slate-100 px-3 py-1.5 rounded-xl border border-slate-200" id="bookings-count-badge">
              {filteredBookings.length} / {bookings.length} {language === 'en' ? 'Bookings' : 'Ballamood'}
            </span>
            <button
              onClick={onOpenBookPitch}
              className="bg-emerald-600 hover:bg-emerald-500 text-white font-bold px-4 py-1.5 rounded-xl text-xs transition-colors shadow-xs"
              id="header-new-booking-btn"
            >
              + {language === 'en' ? 'New Booking' : 'Ballan Cusub'}
            </button>
          </div>
        </div>

        {/* Search Input Box */}
        <div className="space-y-3 pt-1">
          <div className="relative">
            <Search className="w-4 h-4 text-emerald-600 absolute left-3.5 top-1/2 -translate-y-1/2 pointer-events-none" />
            <input
              type="text"
              id="bookings-search-input"
              name="searchBookings"
              autoComplete="off"
              aria-label={language === 'en' ? 'Search bookings by team name or reference code' : 'Raadi ballamaha adigoo isticmaalaya magaca kooxda ama lambarka tixraaca'}
              placeholder={
                filterMode === 'team'
                  ? (language === 'en' ? 'Search by Team Name (e.g. Banaadir United, 26 June FC)...' : 'Ku kala saar Magaca Kooxda (tusaale: Banaadir, 26 June)...')
                  : filterMode === 'ref'
                  ? (language === 'en' ? 'Search by Reference Code (e.g. #JSC-8841, 8841)...' : 'Ku kala saar Lambarka Tixraaca (#JSC-8841, 8841)...')
                  : (language === 'en' ? 'Search bookings by Team Name or Reference Code (#JSC-8841)...' : 'Ku raadi Magaca Kooxda ama Lambarka Tixraaca (#JSC-8841)...')
              }
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === 'Escape') setSearchTerm('');
              }}
              className="w-full pl-10 pr-10 py-3 rounded-2xl border-2 border-slate-200 focus:border-emerald-500 text-xs sm:text-sm font-semibold text-slate-900 placeholder:text-slate-400 focus:outline-none bg-slate-50/70 focus:bg-white transition-all shadow-inner"
            />
            {searchTerm && (
              <button
                type="button"
                onClick={() => setSearchTerm('')}
                className="absolute right-3.5 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-700 p-1 rounded-md transition-colors"
                title={language === 'en' ? 'Clear search (Esc)' : 'Tirtir raadinta'}
                id="clear-bookings-search-btn"
                aria-label="Clear search input"
              >
                <X className="w-4 h-4" />
              </button>
            )}
          </div>

          {/* Active Filter Notification Banner */}
          {searchTerm.trim() && (
            <div className="flex items-center gap-2 text-xs bg-emerald-50/90 text-emerald-900 px-3 py-2 rounded-xl border border-emerald-200">
              <Search className="w-3.5 h-3.5 text-emerald-600 shrink-0" />
              <div className="flex-1 truncate">
                <span>{language === 'en' ? 'Filtering for: ' : 'Shaandhaynta: '}</span>
                <span className="font-mono font-bold bg-white px-1.5 py-0.5 rounded border border-emerald-200 text-emerald-950">
                  "{searchTerm}"
                </span>
                <span className="text-slate-600 ml-1.5">
                  ({language === 'en' 
                    ? `in ${filterMode === 'team' ? 'Team Name' : filterMode === 'ref' ? 'Reference Code' : 'Team Name or Reference Code'}`
                    : `gudaha ${filterMode === 'team' ? 'Magaca Kooxda' : filterMode === 'ref' ? 'Tixraaca' : 'Kooxda ama Tixraaca'}`})
                </span>
              </div>
              <span className="font-bold text-emerald-800 text-[11px] shrink-0">
                {filteredBookings.length} {language === 'en' ? (filteredBookings.length === 1 ? 'match' : 'matches') : 'la helay'}
              </span>
              <button
                onClick={() => setSearchTerm('')}
                className="text-emerald-700 hover:text-emerald-950 font-bold underline text-xs cursor-pointer ml-1"
              >
                {language === 'en' ? 'Clear' : 'Tirtir'}
              </button>
            </div>
          )}

          {/* Filter Target Pills & Status Filter */}
          <div className="flex flex-wrap items-center justify-between gap-2 pt-1 text-xs">
            <div className="flex items-center gap-1.5 flex-wrap">
              <span className="text-[11px] font-bold text-slate-400 uppercase tracking-wider mr-1">
                {language === 'en' ? 'Filter Target:' : 'Kala Saar:'}
              </span>

              <button
                type="button"
                onClick={() => setFilterMode('all')}
                className={`px-3 py-1 rounded-xl font-bold transition-all ${
                  filterMode === 'all'
                    ? 'bg-slate-900 text-white shadow-xs'
                    : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                }`}
                id="filter-by-all-btn"
              >
                {language === 'en' ? 'Team or Reference' : 'Labada'}
              </button>

              <button
                type="button"
                onClick={() => setFilterMode('team')}
                className={`px-3 py-1 rounded-xl font-bold transition-all flex items-center gap-1 ${
                  filterMode === 'team'
                    ? 'bg-emerald-600 text-white shadow-xs'
                    : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                }`}
                id="filter-by-team-btn"
              >
                <Users className="w-3 h-3" />
                <span>{language === 'en' ? 'Team Name Only' : 'Magaca Kooxda'}</span>
              </button>

              <button
                type="button"
                onClick={() => setFilterMode('ref')}
                className={`px-3 py-1 rounded-xl font-bold transition-all flex items-center gap-1 ${
                  filterMode === 'ref'
                    ? 'bg-emerald-600 text-white shadow-xs'
                    : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                }`}
                id="filter-by-ref-btn"
              >
                <Hash className="w-3 h-3" />
                <span>{language === 'en' ? 'Reference Code (#JSC)' : 'Lambarka Tixraaca'}</span>
              </button>
            </div>

            {/* Status quick toggle */}
            <div className="flex items-center gap-1 bg-slate-100 p-1 rounded-xl">
              <button
                type="button"
                onClick={() => setStatusFilter('all')}
                className={`px-2.5 py-0.5 rounded-lg font-bold text-[11px] transition-all ${
                  statusFilter === 'all' ? 'bg-white text-slate-900 shadow-xs' : 'text-slate-600 hover:text-slate-900'
                }`}
              >
                {language === 'en' ? 'All' : 'Dhammaan'}
              </button>
              <button
                type="button"
                onClick={() => setStatusFilter('paid')}
                className={`px-2.5 py-0.5 rounded-lg font-bold text-[11px] transition-all ${
                  statusFilter === 'paid' ? 'bg-emerald-600 text-white shadow-xs' : 'text-slate-600 hover:text-slate-900'
                }`}
              >
                {language === 'en' ? 'Paid' : 'La Bixiyey'}
              </button>
              <button
                type="button"
                onClick={() => setStatusFilter('pending')}
                className={`px-2.5 py-0.5 rounded-lg font-bold text-[11px] transition-all ${
                  statusFilter === 'pending' ? 'bg-amber-500 text-white shadow-xs' : 'text-slate-600 hover:text-slate-900'
                }`}
              >
                {language === 'en' ? 'Pending' : 'Sugaya'}
              </button>
            </div>
          </div>

          {/* Quick Filter Chips: Teams & Reference Codes */}
          {(uniqueTeamNames.length > 0 || uniqueRefCodes.length > 0) && (
            <div className="space-y-1.5 pt-1 border-t border-slate-100">
              {/* Quick Teams */}
              {uniqueTeamNames.length > 0 && (
                <div className="flex items-center gap-1.5 flex-wrap">
                  <span className="text-[10px] text-slate-400 font-bold uppercase tracking-wider">
                    {language === 'en' ? 'Teams:' : 'Kooxaha:'}
                  </span>
                  {uniqueTeamNames.slice(0, 5).map((name) => (
                    <button
                      key={name}
                      type="button"
                      onClick={() => {
                        setFilterMode('team');
                        setSearchTerm(name);
                      }}
                      className={`text-[11px] px-2.5 py-0.5 rounded-lg font-medium transition-colors border ${
                        searchTerm.toLowerCase() === name.toLowerCase() && filterMode === 'team'
                          ? 'bg-emerald-100 text-emerald-900 border-emerald-300 font-bold'
                          : 'bg-white text-slate-600 border-slate-200 hover:bg-slate-50'
                      }`}
                    >
                      {name}
                    </button>
                  ))}
                </div>
              )}

              {/* Quick Reference Codes */}
              {uniqueRefCodes.length > 0 && (
                <div className="flex items-center gap-1.5 flex-wrap">
                  <span className="text-[10px] text-slate-400 font-bold uppercase tracking-wider">
                    {language === 'en' ? 'Ref Codes:' : 'Tixraac:'}
                  </span>
                  {uniqueRefCodes.slice(0, 5).map((ref) => (
                    <button
                      key={ref}
                      type="button"
                      onClick={() => {
                        setFilterMode('ref');
                        setSearchTerm(ref);
                      }}
                      className={`text-[11px] font-mono px-2 py-0.5 rounded-lg font-medium transition-colors border ${
                        searchTerm.toLowerCase().includes(ref.toLowerCase()) && filterMode === 'ref'
                          ? 'bg-emerald-100 text-emerald-900 border-emerald-300 font-bold'
                          : 'bg-white text-slate-600 border-slate-200 hover:bg-slate-50'
                      }`}
                    >
                      #{ref}
                    </button>
                  ))}
                </div>
              )}
            </div>
          )}
        </div>
      </div>

      {/* Bookings List Display */}
      {filteredBookings.length === 0 ? (
        <div className="bg-white rounded-3xl border border-slate-200 p-10 text-center space-y-4 shadow-sm" id="bookings-no-results">
          <div className="w-14 h-14 rounded-2xl bg-slate-100 text-slate-500 flex items-center justify-center mx-auto">
            <Search className="w-7 h-7" />
          </div>
          <div>
            <h3 className="text-base font-bold text-slate-800">
              {language === 'en' ? 'No Matching Bookings Found' : 'Lama helin ballamo u dhigma raadintaada'}
            </h3>
            <p className="text-xs text-slate-500 max-w-md mx-auto mt-1">
              {searchTerm
                ? (language === 'en' 
                    ? `No bookings matching "${searchTerm}". Try checking the team name spelling or entering the reference code (e.g. #JSC-8841 or 8841).` 
                    : `Wax ballan ah lagama helin "${searchTerm}". Fadlan hubi higaada magaca kooxda ama lambarka tixraaca (#JSC-8841).`)
                : (language === 'en' 
                    ? 'No bookings in the system yet. Reserve a pitch to get started!' 
                    : 'Weli ma jiro wax ballan ah. Dooro garoon si aad u bilowdo!')}
            </p>
          </div>
          <div className="flex items-center justify-center gap-2">
            {searchTerm && (
              <button
                type="button"
                onClick={() => {
                  setSearchTerm('');
                  setFilterMode('all');
                  setStatusFilter('all');
                }}
                className="bg-slate-100 hover:bg-slate-200 text-slate-700 font-bold px-4 py-2 rounded-xl text-xs transition-colors"
                id="reset-search-btn"
              >
                {language === 'en' ? 'Clear Search & Show All' : 'Tirtir Raadinta & Muuji Dhammaan'}
              </button>
            )}
            <button
              onClick={onOpenBookPitch}
              className="bg-emerald-600 hover:bg-emerald-700 text-white font-bold px-5 py-2 rounded-xl text-xs transition-all shadow-sm"
              id="empty-state-book-btn"
            >
              {t.bookNow}
            </button>
          </div>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4" id="filtered-bookings-grid">
          {filteredBookings.map((booking) => {
            const whatsAppUrl = generateWhatsAppBookingUrl(booking);

            return (
              <div
                key={booking.id}
                className="bg-white rounded-2xl border border-slate-200/90 shadow-sm p-5 flex flex-col justify-between hover:shadow-md hover:border-emerald-200 transition-all"
                id={`booking-card-${booking.id}`}
              >
                <div>
                  {/* Card Header with Ref and Status */}
                  <div className="flex items-center justify-between gap-2 pb-3 border-b border-slate-100 mb-3">
                    <div className="flex items-center gap-2 min-w-0">
                      <span className="font-mono font-black text-sm text-slate-900 bg-emerald-50 text-emerald-800 px-2 py-0.5 rounded-lg border border-emerald-200 shrink-0">
                        #{highlightMatch(booking.referenceCode, searchTerm)}
                      </span>
                      <span className="font-bold text-xs text-slate-900 truncate">
                        {highlightMatch(booking.teamName, searchTerm)}
                      </span>
                    </div>

                    <div className="flex items-center gap-1.5 flex-wrap justify-end">
                      {booking.smsConfirmed && (
                        <span 
                          className="inline-flex items-center gap-1 text-[10px] font-bold text-emerald-800 bg-emerald-100/90 px-2 py-0.5 rounded-full border border-emerald-300/80 shrink-0" 
                          title="Automated SMS confirmation simulated"
                        >
                          <Smartphone className="w-3 h-3 text-emerald-700" />
                          <span>SMS Sent</span>
                        </span>
                      )}
                      <span
                        className={`text-[10px] font-bold uppercase px-2 py-0.5 rounded-full shrink-0 ${
                          booking.paymentStatus === 'paid'
                            ? 'bg-emerald-100 text-emerald-800'
                            : 'bg-amber-100 text-amber-800'
                        }`}
                      >
                        {booking.paymentStatus === 'paid'
                          ? (language === 'en' ? 'Confirmed / Paid' : 'Waa La Bixiyey')
                          : (language === 'en' ? 'Awaiting Payment' : 'Sugaya Lacag')}
                      </span>
                    </div>
                  </div>

                  {/* Main Details */}
                  <div className="space-y-2 text-xs">
                    <div className="font-bold text-slate-900 text-sm">
                      {booking.pitchName}
                    </div>

                    <div className="grid grid-cols-2 gap-2 text-slate-600 bg-slate-50 p-2.5 rounded-xl border border-slate-100">
                      <div className="flex items-center gap-1.5">
                        <Calendar className="w-3.5 h-3.5 text-emerald-600 shrink-0" />
                        <span className="font-medium text-slate-800">{booking.date}</span>
                      </div>
                      <div className="flex items-center gap-1.5">
                        <Clock className="w-3.5 h-3.5 text-emerald-600 shrink-0" />
                        <span className="font-medium text-slate-800">{booking.startTime} - {booking.endTime}</span>
                      </div>
                    </div>

                    <div className="flex items-center justify-between text-slate-600 pt-1">
                      <span>Captain: <strong className="text-slate-800">{booking.customerName}</strong></span>
                      <span className="font-mono text-slate-700">{booking.customerPhone}</span>
                    </div>

                    <div className="flex items-center justify-between text-slate-600 text-[11px] pt-1 border-t border-slate-100">
                      <span>
                        {booking.paymentMethod.toUpperCase()} (Merchant: <strong>{booking.merchantNumber}</strong>)
                      </span>
                      <span className="font-black text-emerald-700 text-sm font-sans">
                        ${booking.totalAmount} USD
                      </span>
                    </div>

                    {booking.transactionId && (
                      <div className="text-[10px] font-mono text-slate-500 truncate">
                        TID / Tixraac: {booking.transactionId}
                      </div>
                    )}
                  </div>
                </div>

                {/* Actions */}
                <div className="mt-4 pt-3 border-t border-slate-100 flex flex-wrap items-center justify-between gap-2">
                  <div className="flex items-center gap-2">
                    <button
                      onClick={() => onViewTicket(booking)}
                      className="px-3 py-1.5 bg-slate-900 hover:bg-slate-800 text-white rounded-xl text-xs font-semibold flex items-center gap-1.5 transition-colors shadow-xs"
                      id={`view-ticket-${booking.id}`}
                    >
                      <Receipt className="w-3.5 h-3.5" />
                      <span>{language === 'en' ? 'Ticket Pass' : 'Tigidhka'}</span>
                    </button>

                    <a
                      href={whatsAppUrl}
                      target="_blank"
                      rel="noreferrer"
                      className="px-3 py-1.5 bg-emerald-50 hover:bg-emerald-100 text-emerald-800 rounded-xl text-xs font-semibold flex items-center gap-1.5 transition-colors border border-emerald-200"
                      id={`whatsapp-booking-${booking.id}`}
                      title="Send on WhatsApp to +252633347832"
                    >
                      <MessageSquare className="w-3.5 h-3.5 text-emerald-600" />
                      <span className="hidden sm:inline">WhatsApp</span>
                    </a>

                    {onSimulateSms && (
                      <button
                        type="button"
                        onClick={() => onSimulateSms(booking)}
                        className="px-2.5 py-1.5 bg-slate-100 hover:bg-emerald-50 text-slate-700 hover:text-emerald-800 rounded-xl text-xs font-semibold flex items-center gap-1.5 transition-colors border border-slate-200"
                        id={`simulate-sms-btn-${booking.id}`}
                        title="View or simulate SMS notification"
                      >
                        <Smartphone className="w-3.5 h-3.5 text-emerald-600" />
                        <span className="hidden sm:inline">SMS</span>
                      </button>
                    )}
                  </div>

                  <button
                    onClick={() => setCancelModalBooking(booking)}
                    className="text-xs text-rose-600 hover:text-rose-700 hover:underline font-medium p-1"
                    id={`cancel-booking-btn-${booking.id}`}
                  >
                    {language === 'en' ? 'Cancel' : 'Tirtir'}
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Cancellation Confirmation Dialog */}
      {cancelModalBooking && (
        <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-950/70 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl border border-slate-200 space-y-4">
            <div className="flex items-center gap-3 text-rose-600">
              <div className="p-2 bg-rose-50 rounded-xl">
                <AlertCircle className="w-6 h-6" />
              </div>
              <h3 className="font-bold text-base text-slate-900">
                {language === 'en' ? 'Cancel Match Slot?' : 'Ma hubtaa inaad tirtirto ballanta?'}
              </h3>
            </div>
            <p className="text-xs text-slate-600 leading-relaxed">
              {language === 'en'
                ? `Are you sure you want to cancel the booking for ${cancelModalBooking.teamName} on ${cancelModalBooking.date} (${cancelModalBooking.startTime} - ${cancelModalBooking.endTime})?`
                : `Ma doonaysaa inaad tirtirto ballanta kooxda ${cancelModalBooking.teamName} ee ${cancelModalBooking.date}?`}
            </p>
            <div className="p-3 bg-slate-50 rounded-xl border border-slate-200 text-[11px] text-slate-500">
              {language === 'en'
                ? `For refund processing via Zaad (${APP_CONFIG.zaadMerchant}) or eDahab (${APP_CONFIG.edahabMerchant}), please contact WhatsApp: ${APP_CONFIG.contactPhone}.`
                : `Haddii aad lacag bixisay, fadlan kala xidhiidh WhatsApp: ${APP_CONFIG.contactPhone}.`}
            </div>
            <div className="flex items-center justify-end gap-2 pt-2">
              <button
                onClick={() => setCancelModalBooking(null)}
                className="px-4 py-2 rounded-xl text-xs font-semibold text-slate-700 hover:bg-slate-100"
              >
                {language === 'en' ? 'Keep Booking' : 'Iska daa'}
              </button>
              <button
                onClick={() => {
                  onCancelBooking(cancelModalBooking.id);
                  setCancelModalBooking(null);
                }}
                className="px-4 py-2 rounded-xl text-xs font-bold bg-rose-600 hover:bg-rose-700 text-white shadow-sm"
                id="confirm-cancellation-action-btn"
              >
                {language === 'en' ? 'Yes, Cancel Slot' : 'Haa, Tirtir'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
