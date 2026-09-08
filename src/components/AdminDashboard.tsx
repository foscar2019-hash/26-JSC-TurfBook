import React, { useState } from 'react';
import { 
  ShieldCheck, 
  DollarSign, 
  Calendar, 
  CheckCircle2, 
  Clock, 
  PlusCircle, 
  Lock, 
  Unlock, 
  AlertTriangle, 
  Phone, 
  FileText,
  CreditCard,
  Trash2,
  Trophy
} from 'lucide-react';
import { Booking, Pitch, BlockedSlot, Language, PaymentMethod } from '../types';
import { APP_CONFIG, TIME_SLOTS, TRANSLATIONS } from '../data/initialData';
import { generateBookingReference, getPitchSlotRate, isNightSlot } from '../utils/helpers';

interface AdminDashboardProps {
  bookings: Booking[];
  pitches: Pitch[];
  blockedSlots: BlockedSlot[];
  onUpdateBookingStatus: (bookingId: string, status: 'paid' | 'pending_verification') => void;
  onDeleteBooking: (bookingId: string) => void;
  onAddWalkinBooking: (booking: Booking) => void;
  onBlockSlot: (blockedSlot: BlockedSlot) => void;
  onUnblockSlot: (blockedSlotId: string) => void;
  onTogglePitchStatus: (pitchId: string, status: 'available' | 'maintenance') => void;
  language: Language;
}

export const AdminDashboard: React.FC<AdminDashboardProps> = ({
  bookings,
  pitches,
  blockedSlots,
  onUpdateBookingStatus,
  onDeleteBooking,
  onAddWalkinBooking,
  onBlockSlot,
  onUnblockSlot,
  onTogglePitchStatus,
  language,
}) => {
  const t = TRANSLATIONS[language];
  const todayStr = new Date().toISOString().split('T')[0];

  // Tab inside admin: 'overview' | 'bookings' | 'block-slot' | 'walk-in'
  const [adminSubTab, setAdminSubTab] = useState<'overview' | 'bookings' | 'block-slot' | 'walk-in'>('overview');

  // Walk-in booking state
  const [walkinPitchId, setWalkinPitchId] = useState<string>(pitches[0]?.id || 'pitch-1');
  const [walkinDate, setWalkinDate] = useState<string>(todayStr);
  const [walkinSlot, setWalkinSlot] = useState<string>('18:00 - 19:00');
  const [walkinTeam, setWalkinTeam] = useState<string>('');
  const [walkinCaptain, setWalkinCaptain] = useState<string>('');
  const [walkinPhone, setWalkinPhone] = useState<string>('+252');
  const [walkinPaymentMethod, setWalkinPaymentMethod] = useState<PaymentMethod>('zaad');
  const [walkinTid, setWalkinTid] = useState<string>('');
  const [walkinError, setWalkinError] = useState<string>('');

  // Block slot state
  const [blockPitchId, setBlockPitchId] = useState<string>(pitches[0]?.id || 'pitch-1');
  const [blockDate, setBlockDate] = useState<string>(todayStr);
  const [blockSlot, setBlockSlot] = useState<string>('15:00 - 16:00');
  const [blockReason, setBlockReason] = useState<string>('Pitch Maintenance & Grass Infill');

  // Compute Revenue Stats
  const totalRevenue = bookings.reduce((sum, b) => (b.paymentStatus === 'paid' ? sum + b.totalAmount : sum), 0);
  const zaadRevenue = bookings
    .filter((b) => b.paymentMethod === 'zaad' && b.paymentStatus === 'paid')
    .reduce((sum, b) => sum + b.totalAmount, 0);
  const edahabRevenue = bookings
    .filter((b) => b.paymentMethod === 'edahab' && b.paymentStatus === 'paid')
    .reduce((sum, b) => sum + b.totalAmount, 0);
  const pendingCount = bookings.filter((b) => b.paymentStatus === 'pending_verification').length;

  // Handle submit walk-in
  const handleCreateWalkin = (e: React.FormEvent) => {
    e.preventDefault();
    if (!walkinTeam.trim() || !walkinCaptain.trim() || !walkinPhone.trim()) {
      setWalkinError('Please fill in team name, captain name, and contact phone.');
      return;
    }

    const [startHour, endHour] = walkinSlot.split(' - ');
    const currentPitch = pitches.find((p) => p.id === walkinPitchId) || pitches[0];
    const merchant = walkinPaymentMethod === 'zaad' ? APP_CONFIG.zaadMerchant : APP_CONFIG.edahabMerchant;

    const newBooking: Booking = {
      id: `walkin-${Date.now()}`,
      referenceCode: generateBookingReference(),
      pitchId: walkinPitchId,
      pitchName: currentPitch.name,
      date: walkinDate,
      startTime: startHour.trim(),
      endTime: endHour.trim(),
      durationHours: 1,
      customerName: walkinCaptain.trim(),
      teamName: walkinTeam.trim(),
      customerPhone: walkinPhone.trim(),
      customerEmail: APP_CONFIG.contactEmail,
      paymentMethod: walkinPaymentMethod,
      paymentStatus: 'paid',
      transactionId: walkinTid.trim() || `WALKIN-${Math.floor(1000 + Math.random() * 9000)}`,
      merchantNumber: merchant,
      totalAmount: getPitchSlotRate(currentPitch, walkinSlot),
      addOns: [],
      notes: 'Direct Front Desk / Walk-in Registration',
      createdAt: new Date().toISOString(),
    };

    onAddWalkinBooking(newBooking);
    setWalkinTeam('');
    setWalkinCaptain('');
    setWalkinPhone('+252');
    setWalkinTid('');
    setWalkinError('');
    setAdminSubTab('bookings');
  };

  // Handle block slot
  const handleCreateBlock = (e: React.FormEvent) => {
    e.preventDefault();
    const [startHour, endHour] = blockSlot.split(' - ');
    const newBlocked: BlockedSlot = {
      id: `block-${Date.now()}`,
      pitchId: blockPitchId,
      date: blockDate,
      startTime: startHour.trim(),
      endTime: endHour.trim(),
      reason: blockReason.trim() || 'Maintenance',
    };
    onBlockSlot(newBlocked);
    setAdminSubTab('overview');
  };

  return (
    <div className="space-y-6" id="admin-dashboard-view">
      {/* Top Banner with Admin badge */}
      <div className="bg-slate-900 text-white p-5 rounded-2xl border border-slate-800 shadow-md flex flex-wrap items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2">
            <ShieldCheck className="w-5 h-5 text-amber-400" />
            <h2 className="text-lg font-bold">
              {language === 'en' ? '26 JSC TurfBook Operations Centre' : 'Xarunta Maamulka ee 26 JSC TurfBook'}
            </h2>
            <span className="bg-amber-400/20 text-amber-300 text-[10px] font-bold px-2 py-0.5 rounded-full border border-amber-400/30 uppercase">
              Manager Access
            </span>
          </div>
          <p className="text-xs text-slate-400 mt-1">
            Reconciliation for Zaad ({APP_CONFIG.zaadMerchant}) & eDahab ({APP_CONFIG.edahabMerchant}) • Contact: {APP_CONFIG.contactPhone}
          </p>
        </div>

        {/* Sub-nav tabs */}
        <div className="flex items-center gap-1.5 bg-slate-950 p-1 rounded-xl border border-slate-800">
          <button
            onClick={() => setAdminSubTab('overview')}
            className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-colors ${
              adminSubTab === 'overview' ? 'bg-slate-800 text-white' : 'text-slate-400 hover:text-white'
            }`}
          >
            Overview
          </button>
          <button
            onClick={() => setAdminSubTab('bookings')}
            className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-colors flex items-center gap-1 ${
              adminSubTab === 'bookings' ? 'bg-slate-800 text-white' : 'text-slate-400 hover:text-white'
            }`}
          >
            <span>Bookings</span>
            {pendingCount > 0 && (
              <span className="bg-amber-500 text-slate-950 font-black text-[9px] px-1 rounded-full">
                {pendingCount}
              </span>
            )}
          </button>
          <button
            onClick={() => setAdminSubTab('walk-in')}
            className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-colors flex items-center gap-1 ${
              adminSubTab === 'walk-in' ? 'bg-slate-800 text-white' : 'text-slate-400 hover:text-white'
            }`}
          >
            <PlusCircle className="w-3.5 h-3.5 text-emerald-400" />
            <span>Walk-In</span>
          </button>
          <button
            onClick={() => setAdminSubTab('block-slot')}
            className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-colors flex items-center gap-1 ${
              adminSubTab === 'block-slot' ? 'bg-slate-800 text-white' : 'text-slate-400 hover:text-white'
            }`}
          >
            <Lock className="w-3.5 h-3.5 text-amber-400" />
            <span>Block Slot</span>
          </button>
        </div>
      </div>

      {/* OVERVIEW SUBTAB */}
      {adminSubTab === 'overview' && (
        <div className="space-y-6">
          {/* Revenue KPI Cards */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
              <span className="text-[10px] uppercase font-bold text-slate-400 block mb-1">
                Total Confirmed Revenue
              </span>
              <div className="text-2xl sm:text-3xl font-black text-slate-900 font-sans">
                ${totalRevenue} <span className="text-xs text-slate-400 font-normal">USD</span>
              </div>
              <span className="text-[11px] text-slate-500 mt-1 block">
                Across {bookings.filter((b) => b.paymentStatus === 'paid').length} completed games
              </span>
            </div>

            <div className="bg-white p-5 rounded-2xl border border-emerald-200/80 shadow-sm">
              <div className="flex items-center justify-between">
                <span className="text-[10px] uppercase font-bold text-emerald-700 block mb-1">
                  Zaad Revenue ({APP_CONFIG.zaadMerchant})
                </span>
                <span className="text-[10px] font-bold text-emerald-700 bg-emerald-100 px-1.5 py-0.2 rounded">
                  Telesom
                </span>
              </div>
              <div className="text-2xl sm:text-3xl font-black text-emerald-900 font-sans">
                ${zaadRevenue} <span className="text-xs text-emerald-600 font-normal">USD</span>
              </div>
              <span className="text-[11px] text-emerald-700 mt-1 block">
                Merchant account: {APP_CONFIG.zaadMerchant}
              </span>
            </div>

            <div className="bg-white p-5 rounded-2xl border border-amber-200/80 shadow-sm">
              <div className="flex items-center justify-between">
                <span className="text-[10px] uppercase font-bold text-amber-700 block mb-1">
                  eDahab Revenue ({APP_CONFIG.edahabMerchant})
                </span>
                <span className="text-[10px] font-bold text-amber-700 bg-amber-100 px-1.5 py-0.2 rounded">
                  Somtel
                </span>
              </div>
              <div className="text-2xl sm:text-3xl font-black text-amber-900 font-sans">
                ${edahabRevenue} <span className="text-xs text-amber-600 font-normal">USD</span>
              </div>
              <span className="text-[11px] text-amber-700 mt-1 block">
                Merchant account: {APP_CONFIG.edahabMerchant}
              </span>
            </div>

            <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
              <span className="text-[10px] uppercase font-bold text-slate-400 block mb-1">
                Pending Verifications
              </span>
              <div className="text-2xl sm:text-3xl font-black text-amber-600 font-sans">
                {pendingCount}
              </div>
              <span className="text-[11px] text-slate-500 mt-1 block">
                Requires checking TID with bank SMS
              </span>
            </div>
          </div>

          {/* Pitch Status Controls */}
          <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
            <h3 className="text-xs font-bold text-slate-800 uppercase tracking-wider mb-3">
              Pitch Operational Status Controls
            </h3>
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-3">
              {pitches.map((pitch) => (
                <div
                  key={pitch.id}
                  className="p-3.5 rounded-xl border border-slate-200 bg-slate-50 flex flex-col justify-between"
                >
                  <div>
                    <span className="text-xs font-bold text-slate-900 block truncate">{pitch.name}</span>
                    <span className="text-[11px] text-slate-500 font-medium">
                      {pitch.format} • Day: ${pitch.dayRate || 18}/h • Night: ${pitch.nightRate || 25}/h
                    </span>
                  </div>
                  <div className="mt-3 pt-2 border-t border-slate-200 flex items-center justify-between">
                    <span className={`text-[10px] font-bold uppercase px-2 py-0.5 rounded-full ${
                      pitch.status === 'available' ? 'bg-emerald-100 text-emerald-800' : 'bg-rose-100 text-rose-800'
                    }`}>
                      {pitch.status}
                    </span>
                    <button
                      onClick={() =>
                        onTogglePitchStatus(
                          pitch.id,
                          pitch.status === 'available' ? 'maintenance' : 'available'
                        )
                      }
                      className="text-xs font-semibold text-slate-700 hover:text-slate-900 underline"
                    >
                      {pitch.status === 'available' ? 'Set Maint' : 'Set Active'}
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Blocked Slots Table */}
          {blockedSlots.length > 0 && (
            <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
              <h3 className="text-xs font-bold text-slate-800 uppercase tracking-wider mb-3 flex items-center gap-1.5">
                <Lock className="w-3.5 h-3.5 text-amber-600" />
                <span>Blocked Maintenance & Reserved Slots ({blockedSlots.length})</span>
              </h3>
              <div className="space-y-2">
                {blockedSlots.map((bs) => {
                  const p = pitches.find((item) => item.id === bs.pitchId);
                  return (
                    <div
                      key={bs.id}
                      className="p-3 bg-slate-50 rounded-xl border border-slate-200 flex items-center justify-between text-xs"
                    >
                      <div>
                        <strong className="text-slate-900">{p?.name || bs.pitchId}</strong>
                        <span className="text-slate-500 ml-2">
                          {bs.date} • {bs.startTime} - {bs.endTime}
                        </span>
                        <p className="text-[11px] text-amber-800 mt-0.5">Reason: {bs.reason}</p>
                      </div>
                      <button
                        onClick={() => onUnblockSlot(bs.id)}
                        className="px-2.5 py-1 bg-white hover:bg-slate-100 border border-slate-200 rounded-lg text-xs font-semibold text-rose-600"
                      >
                        Unblock Slot
                      </button>
                    </div>
                  );
                })}
              </div>
            </div>
          )}
        </div>
      )}

      {/* BOOKINGS MANAGEMENT SUBTAB */}
      {adminSubTab === 'bookings' && (
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
          <div className="p-4 border-b border-slate-100 flex items-center justify-between">
            <h3 className="font-bold text-sm text-slate-900">
              All Match Bookings ({bookings.length})
            </h3>
            <span className="text-xs text-slate-500">
              Zaad: {APP_CONFIG.zaadMerchant} | eDahab: {APP_CONFIG.edahabMerchant}
            </span>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs text-slate-700">
              <thead className="bg-slate-50 text-slate-500 uppercase text-[10px] font-bold border-b border-slate-200">
                <tr>
                  <th className="py-3 px-4">Ref & Team</th>
                  <th className="py-3 px-4">Pitch & Schedule</th>
                  <th className="py-3 px-4">Captain & Phone</th>
                  <th className="py-3 px-4">Payment & TID</th>
                  <th className="py-3 px-4">Amount</th>
                  <th className="py-3 px-4">Status</th>
                  <th className="py-3 px-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 font-medium">
                {bookings.map((booking) => (
                  <tr key={booking.id} className="hover:bg-slate-50/60 transition-colors">
                    <td className="py-3 px-4">
                      <span className="font-mono font-bold text-slate-900 block">
                        #{booking.referenceCode}
                      </span>
                      <span className="font-bold text-slate-800 text-[11px]">{booking.teamName}</span>
                    </td>
                    <td className="py-3 px-4">
                      <span className="font-bold text-slate-900 block">{booking.pitchName}</span>
                      <span className="text-[11px] text-slate-500">
                        {booking.date} • {booking.startTime}-{booking.endTime}
                      </span>
                    </td>
                    <td className="py-3 px-4">
                      <span className="block text-slate-900 font-semibold">{booking.customerName}</span>
                      <span className="font-mono text-slate-500 text-[11px]">{booking.customerPhone}</span>
                    </td>
                    <td className="py-3 px-4">
                      <span className="uppercase font-bold text-[11px] text-slate-800 block">
                        {booking.paymentMethod} ({booking.merchantNumber})
                      </span>
                      <span className="font-mono text-emerald-800 text-[10px]">
                        {booking.transactionId || 'No TID yet'}
                      </span>
                    </td>
                    <td className="py-3 px-4 font-bold font-sans text-slate-900">
                      ${booking.totalAmount}
                    </td>
                    <td className="py-3 px-4">
                      <span className={`text-[10px] uppercase font-bold px-2 py-0.5 rounded-full ${
                        booking.paymentStatus === 'paid'
                          ? 'bg-emerald-100 text-emerald-800'
                          : 'bg-amber-100 text-amber-800'
                      }`}>
                        {booking.paymentStatus}
                      </span>
                    </td>
                    <td className="py-3 px-4 text-right">
                      <div className="flex items-center justify-end gap-1.5">
                        {booking.paymentStatus !== 'paid' ? (
                          <button
                            onClick={() => onUpdateBookingStatus(booking.id, 'paid')}
                            className="bg-emerald-600 hover:bg-emerald-700 text-white px-2 py-1 rounded text-[11px] font-bold"
                            title="Mark as Paid"
                          >
                            Verify Paid
                          </button>
                        ) : (
                          <button
                            onClick={() => onUpdateBookingStatus(booking.id, 'pending_verification')}
                            className="text-slate-500 hover:text-slate-700 text-[10px] underline"
                          >
                            Set Pending
                          </button>
                        )}
                        <button
                          onClick={() => onDeleteBooking(booking.id)}
                          className="p-1 text-slate-400 hover:text-rose-600 rounded"
                          title="Delete Booking"
                        >
                          <Trash2 className="w-3.5 h-3.5" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* WALK-IN REGISTRATION SUBTAB */}
      {adminSubTab === 'walk-in' && (
        <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm max-w-xl mx-auto">
          <h3 className="font-bold text-base text-slate-900 mb-1 flex items-center gap-2">
            <PlusCircle className="w-5 h-5 text-emerald-600" />
            <span>Record Front-Desk / Phone Call Booking</span>
          </h3>
          <p className="text-xs text-slate-500 mb-4">
            Register a walk-in player or phone call reservation to +252633347832.
          </p>

          {walkinError && (
            <div className="mb-4 p-3 bg-red-50 text-red-700 rounded-xl text-xs">
              {walkinError}
            </div>
          )}

          <form onSubmit={handleCreateWalkin} className="space-y-3 text-xs">
            <div>
              <label className="block font-medium text-slate-700 mb-1">Select Pitch</label>
              <select
                value={walkinPitchId}
                onChange={(e) => setWalkinPitchId(e.target.value)}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-medium"
              >
                {pitches.map((p) => (
                  <option key={p.id} value={p.id}>
                    {p.name} (${p.hourlyRate}/hr)
                  </option>
                ))}
              </select>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block font-medium text-slate-700 mb-1">Date</label>
                <input
                  type="date"
                  value={walkinDate}
                  onChange={(e) => setWalkinDate(e.target.value)}
                  className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-medium"
                />
              </div>
              <div>
                <label className="block font-medium text-slate-700 mb-1 flex items-center justify-between">
                  <span>Time Slot</span>
                  <span className="text-[11px] font-bold text-emerald-700">
                    Fee: ${getPitchSlotRate(pitches.find((p) => p.id === walkinPitchId) || pitches[0], walkinSlot)} USD ({isNightSlot(walkinSlot) ? 'Night Floodlit' : 'Day Game'})
                  </span>
                </label>
                <select
                  value={walkinSlot}
                  onChange={(e) => setWalkinSlot(e.target.value)}
                  className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-medium"
                >
                  {TIME_SLOTS.map((s) => {
                    const slotNight = isNightSlot(s);
                    return (
                      <option key={s} value={s}>
                        {s} {slotNight ? '🌙 ($25 Night)' : '☀️ ($18 Day)'}
                      </option>
                    );
                  })}
                </select>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block font-medium text-slate-700 mb-1">Team / Club Name</label>
                <input
                  type="text"
                  placeholder="e.g. Shaab FC"
                  value={walkinTeam}
                  onChange={(e) => setWalkinTeam(e.target.value)}
                  className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-medium"
                />
              </div>
              <div>
                <label className="block font-medium text-slate-700 mb-1">Captain Name</label>
                <input
                  type="text"
                  placeholder="e.g. Mustafe"
                  value={walkinCaptain}
                  onChange={(e) => setWalkinCaptain(e.target.value)}
                  className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-medium"
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block font-medium text-slate-700 mb-1">Customer Phone</label>
                <input
                  type="tel"
                  placeholder="+25263..."
                  value={walkinPhone}
                  onChange={(e) => setWalkinPhone(e.target.value)}
                  className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-medium"
                />
              </div>
              <div>
                <label className="block font-medium text-slate-700 mb-1">Payment Method</label>
                <select
                  value={walkinPaymentMethod}
                  onChange={(e) => setWalkinPaymentMethod(e.target.value as PaymentMethod)}
                  className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-medium"
                >
                  <option value="zaad">Zaad (Merchant: {APP_CONFIG.zaadMerchant})</option>
                  <option value="edahab">eDahab (Merchant: {APP_CONFIG.edahabMerchant})</option>
                  <option value="cash">Direct Cash / At Desk</option>
                </select>
              </div>
            </div>

            <div>
              <label className="block font-medium text-slate-700 mb-1">Transaction Ref / Note</label>
              <input
                type="text"
                placeholder="e.g. ZD-5541 or Paid in cash"
                value={walkinTid}
                onChange={(e) => setWalkinTid(e.target.value)}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-medium"
              />
            </div>

            <button
              type="submit"
              className="w-full mt-4 bg-emerald-600 hover:bg-emerald-700 text-white font-bold py-2.5 rounded-xl text-xs transition-colors"
            >
              Confirm & Save Walk-In Booking
            </button>
          </form>
        </div>
      )}

      {/* BLOCK SLOT SUBTAB */}
      {adminSubTab === 'block-slot' && (
        <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm max-w-xl mx-auto">
          <h3 className="font-bold text-base text-slate-900 mb-1 flex items-center gap-2">
            <Lock className="w-5 h-5 text-amber-600" />
            <span>Block Pitch Slot for Maintenance / Event</span>
          </h3>
          <p className="text-xs text-slate-500 mb-4">
            Prevent bookings during turf maintenance, brush grooming, or private club tournaments.
          </p>

          <form onSubmit={handleCreateBlock} className="space-y-3 text-xs">
            <div>
              <label className="block font-medium text-slate-700 mb-1">Select Pitch to Block</label>
              <select
                value={blockPitchId}
                onChange={(e) => setBlockPitchId(e.target.value)}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-medium"
              >
                {pitches.map((p) => (
                  <option key={p.id} value={p.id}>
                    {p.name}
                  </option>
                ))}
              </select>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block font-medium text-slate-700 mb-1">Date</label>
                <input
                  type="date"
                  value={blockDate}
                  onChange={(e) => setBlockDate(e.target.value)}
                  className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-medium"
                />
              </div>
              <div>
                <label className="block font-medium text-slate-700 mb-1">Time Slot</label>
                <select
                  value={blockSlot}
                  onChange={(e) => setBlockSlot(e.target.value)}
                  className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-medium"
                >
                  {TIME_SLOTS.map((s) => (
                    <option key={s} value={s}>
                      {s}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div>
              <label className="block font-medium text-slate-700 mb-1">Reason for Blocking</label>
              <input
                type="text"
                placeholder="e.g. Turf rubber infill & brushing or Private Tournament"
                value={blockReason}
                onChange={(e) => setBlockReason(e.target.value)}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-medium"
              />
            </div>

            <button
              type="submit"
              className="w-full mt-4 bg-amber-600 hover:bg-amber-700 text-white font-bold py-2.5 rounded-xl text-xs transition-colors"
            >
              Block This Time Slot
            </button>
          </form>
        </div>
      )}
    </div>
  );
};
