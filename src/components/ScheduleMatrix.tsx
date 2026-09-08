import React, { useState } from 'react';
import { 
  Calendar, 
  Clock, 
  Check, 
  Lock, 
  ChevronLeft, 
  ChevronRight, 
  Sparkles,
  Info
} from 'lucide-react';
import { Pitch, Booking, BlockedSlot, Language } from '../types';
import { TIME_SLOTS, TRANSLATIONS } from '../data/initialData';
import { isNightSlot } from '../utils/helpers';

interface ScheduleMatrixProps {
  pitches: Pitch[];
  bookings: Booking[];
  blockedSlots: BlockedSlot[];
  onSelectSlot: (pitchId: string, date: string, slot: string) => void;
  language: Language;
}

export const ScheduleMatrix: React.FC<ScheduleMatrixProps> = ({
  pitches,
  bookings,
  blockedSlots,
  onSelectSlot,
  language,
}) => {
  const t = TRANSLATIONS[language];
  const todayStr = new Date().toISOString().split('T')[0];
  const [selectedDate, setSelectedDate] = useState<string>(todayStr);
  const [pitchFilter, setPitchFilter] = useState<string>('all');

  // Change date helper
  const handleOffsetDate = (offsetDays: number) => {
    const current = new Date(selectedDate);
    current.setDate(current.getDate() + offsetDays);
    setSelectedDate(current.toISOString().split('T')[0]);
  };

  const displayedPitches = pitchFilter === 'all' 
    ? pitches 
    : pitches.filter((p) => p.id === pitchFilter);

  // Compute summary stats for the selected date
  const totalSlotsForDate = displayedPitches.length * TIME_SLOTS.length;
  const bookedCountForDate = bookings.filter(
    (b) => b.date === selectedDate && (pitchFilter === 'all' || b.pitchId === pitchFilter)
  ).length;
  const availableCount = Math.max(0, totalSlotsForDate - bookedCountForDate);

  return (
    <div className="space-y-6" id="schedule-matrix-view">
      {/* Top Controls Bar */}
      <div className="bg-white p-4 sm:p-5 rounded-2xl border border-slate-200/80 shadow-sm flex flex-col md:flex-row md:items-center justify-between gap-4">
        {/* Date Selector with Next / Prev */}
        <div className="flex items-center gap-2">
          <button
            onClick={() => handleOffsetDate(-1)}
            className="p-2 border border-slate-200 hover:bg-slate-50 text-slate-700 rounded-xl transition-colors"
            id="prev-day-btn"
            title="Previous Day"
          >
            <ChevronLeft className="w-4 h-4" />
          </button>

          <div className="flex items-center gap-2 bg-slate-50 px-3 py-1.5 rounded-xl border border-slate-200">
            <Calendar className="w-4 h-4 text-emerald-600" />
            <input
              type="date"
              value={selectedDate}
              onChange={(e) => setSelectedDate(e.target.value)}
              className="bg-transparent text-xs sm:text-sm font-bold text-slate-800 focus:outline-none cursor-pointer"
              id="schedule-date-picker"
            />
          </div>

          <button
            onClick={() => handleOffsetDate(1)}
            className="p-2 border border-slate-200 hover:bg-slate-50 text-slate-700 rounded-xl transition-colors"
            id="next-day-btn"
            title="Next Day"
          >
            <ChevronRight className="w-4 h-4" />
          </button>

          {selectedDate !== todayStr && (
            <button
              onClick={() => setSelectedDate(todayStr)}
              className="text-xs font-semibold text-emerald-600 hover:text-emerald-700 hover:underline px-2 py-1"
            >
              {language === 'en' ? 'Today' : 'Maanta'}
            </button>
          )}
        </div>

        {/* Pitch Filter Pills */}
        <div className="flex items-center gap-1.5 overflow-x-auto pb-1 md:pb-0">
          <button
            onClick={() => setPitchFilter('all')}
            className={`px-3 py-1.5 rounded-xl text-xs font-bold transition-all shrink-0 ${
              pitchFilter === 'all'
                ? 'bg-slate-900 text-white shadow-sm'
                : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
            }`}
            id="filter-all-pitches"
          >
            {t.allPitches}
          </button>
          {pitches.map((p) => (
            <button
              key={p.id}
              onClick={() => setPitchFilter(p.id)}
              className={`px-3 py-1.5 rounded-xl text-xs font-bold transition-all shrink-0 ${
                pitchFilter === p.id
                  ? 'bg-emerald-600 text-white shadow-sm'
                  : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
              }`}
              id={`filter-pitch-${p.id}`}
            >
              {p.format} ({p.name.split(' - ')[0]})
            </button>
          ))}
        </div>
      </div>

      {/* Legend & Stats Banner */}
      <div className="flex flex-wrap items-center justify-between gap-3 text-xs px-1">
        <div className="flex items-center gap-4 flex-wrap">
          <span className="flex items-center gap-1.5 text-slate-700 font-medium">
            <span className="w-3 h-3 rounded-md bg-emerald-500"></span>
            <span>{language === 'en' ? 'Available (Click to Book)' : 'Bannaan (Guji si aad u dalbato)'}</span>
          </span>
          <span className="flex items-center gap-1.5 text-slate-700 font-medium">
            <span className="w-3 h-3 rounded-md bg-rose-500"></span>
            <span>{language === 'en' ? 'Booked Match' : 'Ciyaar La Qabsaday'}</span>
          </span>
          <span className="flex items-center gap-1.5 text-slate-700 font-medium">
            <span className="w-3 h-3 rounded-md bg-slate-400"></span>
            <span>{language === 'en' ? 'Reserved / Maint.' : 'Dayactir / Xidhan'}</span>
          </span>
        </div>

        <div className="text-slate-500 font-medium">
          <span className="text-emerald-700 font-bold">{availableCount} {language === 'en' ? 'Free Slots' : 'Saacadood oo Bannaan'}</span> • <span className="text-rose-700 font-bold">{bookedCountForDate} {language === 'en' ? 'Booked' : 'La qabsaday'}</span>
        </div>
      </div>

      {/* Pitches Schedule Grid */}
      <div className="space-y-6">
        {displayedPitches.map((pitch) => {
          return (
            <div 
              key={pitch.id} 
              className="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden"
              id={`schedule-row-${pitch.id}`}
            >
              {/* Pitch Header */}
              <div className="bg-slate-900 text-white px-5 py-3.5 flex flex-wrap items-center justify-between gap-2">
                <div className="flex items-center gap-3">
                  <span className="bg-emerald-500 text-slate-950 text-xs font-black px-2.5 py-0.5 rounded-full uppercase">
                    {pitch.format}
                  </span>
                  <div>
                    <h3 className="font-bold text-sm sm:text-base">
                      {language === 'en' ? pitch.name : pitch.somaliName}
                    </h3>
                    <p className="text-xs text-slate-400">
                      {pitch.surface} • {pitch.dimensions}
                    </p>
                  </div>
                </div>

                <div className="text-right">
                  <div className="flex items-center justify-end gap-1.5 text-xs">
                    <span className="bg-amber-400/20 text-amber-300 px-2 py-0.5 rounded-md font-semibold border border-amber-400/30">
                      ☀️ Day: ${pitch.dayRate || 18}
                    </span>
                    <span className="bg-emerald-500/20 text-emerald-300 px-2 py-0.5 rounded-md font-semibold border border-emerald-500/30">
                      🌙 Night: ${pitch.nightRate || 25}
                    </span>
                  </div>
                </div>
              </div>

              {/* Time Slots Row */}
              <div className="p-4 sm:p-5">
                <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-7 gap-2.5">
                  {TIME_SLOTS.map((timeSlot) => {
                    const [startHour] = timeSlot.split(' - ');
                    const isNight = isNightSlot(timeSlot);
                    const slotRate = isNight ? (pitch.nightRate || 25) : (pitch.dayRate || 18);
                    const booking = bookings.find(
                      (b) => b.pitchId === pitch.id && b.date === selectedDate && b.startTime === startHour.trim()
                    );
                    const blocked = blockedSlots.find(
                      (bs) => bs.pitchId === pitch.id && bs.date === selectedDate && bs.startTime === startHour.trim()
                    );

                    if (booking) {
                      return (
                        <div
                          key={timeSlot}
                          className="p-2.5 rounded-xl border border-rose-200 bg-rose-50/70 text-left relative group select-none"
                          title={`Booked by ${booking.teamName} (${booking.customerName})`}
                        >
                          <div className="flex items-center justify-between">
                            <span className="text-xs font-bold text-rose-950">{timeSlot}</span>
                            <span className="w-2 h-2 rounded-full bg-rose-500"></span>
                          </div>
                          <p className="text-[11px] font-bold text-rose-800 truncate mt-1">
                            {booking.teamName}
                          </p>
                          <span className="text-[10px] text-rose-600 block truncate">
                            {booking.paymentStatus === 'paid' ? 'Paid' : 'Pending'} • ${booking.totalAmount}
                          </span>
                        </div>
                      );
                    }

                    if (blocked) {
                      return (
                        <div
                          key={timeSlot}
                          className="p-2.5 rounded-xl border border-slate-200 bg-slate-100 text-left select-none opacity-80"
                        >
                          <div className="flex items-center justify-between">
                            <span className="text-xs font-bold text-slate-600">{timeSlot}</span>
                            <Lock className="w-3 h-3 text-slate-400" />
                          </div>
                          <p className="text-[11px] font-medium text-slate-500 truncate mt-1">
                            {blocked.reason || (language === 'en' ? 'Reserved' : 'Xidhan')}
                          </p>
                        </div>
                      );
                    }

                    // Available slot
                    return (
                      <button
                        key={timeSlot}
                        onClick={() => onSelectSlot(pitch.id, selectedDate, timeSlot)}
                        className="p-2.5 rounded-xl border border-emerald-200 bg-emerald-50/60 hover:bg-emerald-600 hover:text-white hover:border-emerald-600 text-slate-800 transition-all text-left shadow-xs hover:shadow-md group active:scale-[0.98]"
                        id={`book-slot-${pitch.id}-${startHour}`}
                      >
                        <div className="flex items-center justify-between">
                          <span className="text-xs font-bold">{timeSlot}</span>
                          <span className="w-2 h-2 rounded-full bg-emerald-500 group-hover:bg-white"></span>
                        </div>
                        <div className="flex items-center justify-between mt-1">
                          <span className="text-[10px] font-semibold text-emerald-800 group-hover:text-emerald-100 flex items-center gap-1">
                            <span>{isNight ? '🌙' : '☀️'}</span>
                            <span>{isNight ? (language === 'en' ? 'Night' : 'Habeen') : (language === 'en' ? 'Day' : 'Maalin')}</span>
                          </span>
                          <span className="text-[10px] font-extrabold font-sans">
                            ${slotRate}
                          </span>
                        </div>
                      </button>
                    );
                  })}
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
