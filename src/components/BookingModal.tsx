import React, { useState } from 'react';
import { 
  X, 
  Check, 
  Calendar, 
  Clock, 
  CreditCard, 
  ChevronRight, 
  ChevronLeft, 
  Sparkles, 
  Copy, 
  CheckCircle2, 
  MessageSquare,
  AlertCircle,
  Smartphone,
  Radio,
  Send
} from 'lucide-react';
import confetti from 'canvas-confetti';
import { Pitch, Booking, AddOnItem, PaymentMethod, Language, BlockedSlot, SimulatedSmsNotification } from '../types';
import { APP_CONFIG, ADD_ON_ITEMS, TIME_SLOTS, TRANSLATIONS } from '../data/initialData';
import { generateBookingReference, getZaadUssdCode, getEdahabUssdCode, generateBookingSmsText, isNightSlot, getPitchSlotRate } from '../utils/helpers';

interface BookingModalProps {
  isOpen: boolean;
  onClose: () => void;
  pitches: Pitch[];
  selectedPitchId?: string;
  initialDate?: string;
  initialSlot?: string;
  existingBookings: Booking[];
  blockedSlots: BlockedSlot[];
  onBookingSuccess: (newBooking: Booking) => void;
  onSmsDispatched?: (sms: SimulatedSmsNotification) => void;
  language: Language;
}

export const BookingModal: React.FC<BookingModalProps> = ({
  isOpen,
  onClose,
  pitches,
  selectedPitchId,
  initialDate,
  initialSlot,
  existingBookings,
  blockedSlots,
  onBookingSuccess,
  onSmsDispatched,
  language,
}) => {
  const t = TRANSLATIONS[language];

  // Today's date string YYYY-MM-DD
  const todayStr = new Date().toISOString().split('T')[0];

  // Steps: 1: Pitch & Slot, 2: Addons & Team Info, 3: Zaad / eDahab Payment
  const [step, setStep] = useState<1 | 2 | 3>(1);

  // Booking state
  const [pitchId, setPitchId] = useState<string>(selectedPitchId || pitches[0]?.id || 'pitch-1');
  const [date, setDate] = useState<string>(initialDate || todayStr);
  const [slot, setSlot] = useState<string>(initialSlot || '19:00 - 20:00');
  const [durationHours, setDurationHours] = useState<number>(1);
  const [selectedAddons, setSelectedAddons] = useState<string[]>([]);

  // Team & Captain details
  const [teamName, setTeamName] = useState<string>('');
  const [captainName, setCaptainName] = useState<string>('');
  const [phone, setPhone] = useState<string>('+252');
  const [email, setEmail] = useState<string>('');
  const [notes, setNotes] = useState<string>('');

  // Payment state
  const [paymentMethod, setPaymentMethod] = useState<PaymentMethod>('zaad');
  const [transactionId, setTransactionId] = useState<string>('');
  const [copiedUssd, setCopiedUssd] = useState<boolean>(false);
  const [formError, setFormError] = useState<string>('');

  // SMS Gateway Integration Simulation State
  const [sendSmsConfirmation, setSendSmsConfirmation] = useState<boolean>(true);
  const [isDispatchingSms, setIsDispatchingSms] = useState<boolean>(false);
  const [previewRefCode] = useState<string>(() => generateBookingReference());

  if (!isOpen) return null;

  const currentPitch = pitches.find((p) => p.id === pitchId) || pitches[0];

  // Check collision for slot
  const isSlotBooked = (timeSlot: string) => {
    const [startHour] = timeSlot.split(' - ');
    return existingBookings.some(
      (b) => b.pitchId === pitchId && b.date === date && b.startTime === startHour.trim()
    );
  };

  const isSlotBlocked = (timeSlot: string) => {
    const [startHour] = timeSlot.split(' - ');
    return blockedSlots.some(
      (bs) => bs.pitchId === pitchId && bs.date === date && bs.startTime === startHour.trim()
    );
  };

  // Pricing calculations
  const isNight = isNightSlot(slot);
  const currentSlotRate = getPitchSlotRate(currentPitch, slot);
  const basePrice = currentSlotRate * durationHours;
  const addonsPrice = selectedAddons.reduce((acc, addId) => {
    const item = ADD_ON_ITEMS.find((a) => a.id === addId);
    return acc + (item ? item.price : 0);
  }, 0);
  const totalPrice = basePrice + addonsPrice;

  // Toggle addon
  const toggleAddon = (id: string) => {
    setSelectedAddons((prev) =>
      prev.includes(id) ? prev.filter((item) => item !== id) : [...prev, id]
    );
  };

  // Copy USSD
  const handleCopyUssd = () => {
    const code = paymentMethod === 'zaad' 
      ? getZaadUssdCode(totalPrice) 
      : getEdahabUssdCode(totalPrice);
    
    navigator.clipboard.writeText(code);
    setCopiedUssd(true);
    setTimeout(() => setCopiedUssd(false), 2500);
  };

  // Validation before step 3
  const handleProceedToPayment = () => {
    if (!teamName.trim()) {
      setFormError(language === 'en' ? 'Please enter your Team or Club name.' : 'Fadlan geli magaca kooxdaada.');
      return;
    }
    if (!captainName.trim()) {
      setFormError(language === 'en' ? 'Please enter the Captain / Booker name.' : 'Fadlan geli magaca kabtanka ama dalbadaha.');
      return;
    }
    if (!phone.trim() || phone.length < 8) {
      setFormError(language === 'en' ? 'Please enter a valid phone number (+252...).' : 'Fadlan geli lambar telefoon sax ah (+252...).');
      return;
    }
    setFormError('');
    setStep(3);
  };

  // Confirm booking
  const handleConfirmBooking = () => {
    if (isDispatchingSms) return;

    const [startHour, endHour] = slot.split(' - ');
    const merchantNum = paymentMethod === 'zaad' ? APP_CONFIG.zaadMerchant : APP_CONFIG.edahabMerchant;
    const bookingRef = previewRefCode;

    const smsMessage = generateBookingSmsText({
      referenceCode: bookingRef,
      teamName: teamName.trim() || 'Team',
      pitchName: currentPitch.name,
      date,
      startTime: startHour.trim(),
      endTime: endHour.trim(),
      totalAmount: totalPrice,
      language,
    });

    const isTelesom = phone.startsWith('+25263') || phone.startsWith('063') || paymentMethod === 'zaad';
    const telecomGateway = isTelesom ? 'Telesom SMS Gateway' : 'Somtel Bulk SMS Gateway';

    const newBooking: Booking = {
      id: `b-${Date.now()}`,
      referenceCode: bookingRef,
      pitchId: currentPitch.id,
      pitchName: currentPitch.name,
      date,
      startTime: startHour.trim(),
      endTime: endHour.trim(),
      durationHours,
      customerName: captainName.trim(),
      teamName: teamName.trim(),
      customerPhone: phone.trim(),
      customerEmail: email.trim() || APP_CONFIG.contactEmail,
      paymentMethod,
      paymentStatus: transactionId.trim() ? 'paid' : 'pending_verification',
      transactionId: transactionId.trim() || undefined,
      merchantNumber: merchantNum,
      totalAmount: totalPrice,
      addOns: selectedAddons,
      notes: notes.trim(),
      createdAt: new Date().toISOString(),
      smsConfirmed: sendSmsConfirmation,
      smsDispatchedAt: sendSmsConfirmation ? new Date().toISOString() : undefined,
      smsMessagePreview: sendSmsConfirmation ? smsMessage : undefined,
    };

    if (sendSmsConfirmation) {
      setIsDispatchingSms(true);
      setTimeout(() => {
        setIsDispatchingSms(false);
        try {
          confetti({
            particleCount: 80,
            spread: 70,
            origin: { y: 0.6 },
          });
        } catch {
          // ignore
        }

        if (onSmsDispatched) {
          onSmsDispatched({
            id: `sms-${Date.now()}`,
            bookingRef,
            recipientPhone: phone.trim(),
            recipientName: captainName.trim(),
            teamName: teamName.trim(),
            message: smsMessage,
            gateway: telecomGateway,
            timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
            status: 'delivered',
          });
        }

        onBookingSuccess(newBooking);
        onClose();
      }, 650);
    } else {
      try {
        confetti({
          particleCount: 80,
          spread: 70,
          origin: { y: 0.6 },
        });
      } catch {
        // ignore
      }
      onBookingSuccess(newBooking);
      onClose();
    }
  };

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-950/70 backdrop-blur-sm flex items-center justify-center p-3 sm:p-4">
      <div 
        className="bg-white rounded-2xl w-full max-w-2xl overflow-hidden shadow-2xl border border-slate-200 animate-in fade-in zoom-in-95 duration-200"
        id="booking-modal-container"
      >
        {/* Header with Title and Close Button */}
        <div className="bg-slate-900 text-white px-6 py-4 flex items-center justify-between border-b border-slate-800">
          <div>
            <div className="flex items-center gap-2">
              <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
              <h2 className="text-base sm:text-lg font-bold">
                {language === 'en' ? 'Book Pitch at 26 JSC TurfBook' : 'Dalbo Garoonka 26 JSC TurfBook'}
              </h2>
            </div>
            <p className="text-xs text-slate-400 mt-0.5">
              {language === 'en' ? 'Step ' + step + ' of 3' : 'Tallaabada ' + step + ' ee 3'} • {currentPitch.name}
            </p>
          </div>
          <button
            onClick={onClose}
            className="text-slate-400 hover:text-white p-1.5 rounded-lg hover:bg-slate-800 transition-colors"
            id="close-booking-modal-btn"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Step Indicator Progress Bar */}
        <div className="bg-slate-100 px-6 py-2.5 border-b border-slate-200 flex items-center justify-between text-xs">
          <div className="flex items-center gap-2">
            <span className={`w-5 h-5 rounded-full flex items-center justify-center text-[10px] font-bold ${
              step >= 1 ? 'bg-emerald-600 text-white' : 'bg-slate-300 text-slate-600'
            }`}>
              1
            </span>
            <span className={`font-semibold ${step >= 1 ? 'text-slate-900' : 'text-slate-400'}`}>
              {language === 'en' ? 'Slot & Pitch' : 'Garoonka & Saacadda'}
            </span>
          </div>
          <div className="h-0.5 flex-1 mx-3 bg-slate-200">
            <div 
              className="h-full bg-emerald-600 transition-all duration-300"
              style={{ width: step === 1 ? '0%' : step === 2 ? '50%' : '100%' }}
            />
          </div>
          <div className="flex items-center gap-2">
            <span className={`w-5 h-5 rounded-full flex items-center justify-center text-[10px] font-bold ${
              step >= 2 ? 'bg-emerald-600 text-white' : 'bg-slate-300 text-slate-600'
            }`}>
              2
            </span>
            <span className={`font-semibold ${step >= 2 ? 'text-slate-900' : 'text-slate-400'}`}>
              {language === 'en' ? 'Team Details' : 'Xogta Kooxda'}
            </span>
          </div>
          <div className="h-0.5 flex-1 mx-3 bg-slate-200">
            <div 
              className="h-full bg-emerald-600 transition-all duration-300"
              style={{ width: step === 3 ? '100%' : '0%' }}
            />
          </div>
          <div className="flex items-center gap-2">
            <span className={`w-5 h-5 rounded-full flex items-center justify-center text-[10px] font-bold ${
              step === 3 ? 'bg-emerald-600 text-white' : 'bg-slate-300 text-slate-600'
            }`}>
              3
            </span>
            <span className={`font-semibold ${step === 3 ? 'text-slate-900' : 'text-slate-400'}`}>
              {language === 'en' ? 'Zaad / eDahab' : 'Lacag-bixinta'}
            </span>
          </div>
        </div>

        {/* Modal Body */}
        <div className="p-6 max-h-[72vh] overflow-y-auto">
          {formError && (
            <div className="mb-4 p-3 bg-red-50 border border-red-200 text-red-700 rounded-xl text-xs flex items-center gap-2">
              <AlertCircle className="w-4 h-4 shrink-0" />
              <span>{formError}</span>
            </div>
          )}

          {/* STEP 1: Pitch & Time Slot */}
          {step === 1 && (
            <div className="space-y-5">
              {/* Pitch Selector Dropdown or Pills */}
              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">
                  {t.selectPitch}
                </label>
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
                  {pitches.map((p) => (
                    <button
                      key={p.id}
                      type="button"
                      onClick={() => setPitchId(p.id)}
                      className={`text-left p-3 rounded-xl border text-xs transition-all flex items-center justify-between ${
                        pitchId === p.id
                          ? 'border-emerald-600 bg-emerald-50/60 ring-1 ring-emerald-600'
                          : 'border-slate-200 hover:border-slate-300 bg-white'
                      }`}
                    >
                      <div>
                        <span className="font-bold text-slate-900 block">{p.name}</span>
                        <span className="text-slate-500 text-[11px]">{p.format} • {p.surface}</span>
                      </div>
                      <div className="text-right ml-2 shrink-0">
                        <div className="font-extrabold text-emerald-700 font-sans text-xs">
                          Day ${p.dayRate || 18} / Night ${p.nightRate || 25}
                        </div>
                        <div className="text-[10px] text-slate-400">per hour</div>
                      </div>
                    </button>
                  ))}
                </div>
              </div>

              {/* Date Selector */}
              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2 flex items-center gap-1.5">
                  <Calendar className="w-3.5 h-3.5 text-emerald-600" />
                  <span>{t.selectDate}</span>
                </label>
                <div className="flex flex-wrap gap-2 items-center">
                  <input
                    type="date"
                    min={todayStr}
                    value={date}
                    onChange={(e) => setDate(e.target.value)}
                    className="p-2.5 rounded-xl border border-slate-200 text-xs font-semibold text-slate-800 focus:outline-none focus:ring-2 focus:ring-emerald-500"
                    id="booking-date-input"
                  />
                  {/* Quick Shortcut Buttons */}
                  <button
                    type="button"
                    onClick={() => setDate(todayStr)}
                    className={`px-3 py-2 rounded-xl text-xs font-medium border transition-colors ${
                      date === todayStr ? 'bg-emerald-600 text-white border-emerald-600' : 'bg-slate-50 text-slate-700 border-slate-200 hover:bg-slate-100'
                    }`}
                  >
                    {t.today}
                  </button>
                  <button
                    type="button"
                    onClick={() => {
                      const tm = new Date();
                      tm.setDate(tm.getDate() + 1);
                      setDate(tm.toISOString().split('T')[0]);
                    }}
                    className={`px-3 py-2 rounded-xl text-xs font-medium border transition-colors ${
                      date !== todayStr ? 'bg-emerald-600 text-white border-emerald-600' : 'bg-slate-50 text-slate-700 border-slate-200 hover:bg-slate-100'
                    }`}
                  >
                    {t.tomorrow}
                  </button>
                </div>
              </div>

              {/* Time Slots Grid */}
              <div>
                <div className="flex items-center justify-between mb-2">
                  <label className="text-xs font-bold text-slate-700 uppercase tracking-wider flex items-center gap-1.5">
                    <Clock className="w-3.5 h-3.5 text-emerald-600" />
                    <span>{t.selectSlot}</span>
                  </label>
                  <span className="text-[11px] text-slate-500">
                    {language === 'en' ? '1 Hour Standard Slot' : '1 Saac Ciyaar'}
                  </span>
                </div>

                <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-2">
                  {TIME_SLOTS.map((timeSlot) => {
                    const booked = isSlotBooked(timeSlot);
                    const blocked = isSlotBlocked(timeSlot);
                    const disabled = booked || blocked;
                    const isSelected = slot === timeSlot;
                    const slotIsNight = isNightSlot(timeSlot);
                    const slotRate = slotIsNight ? (currentPitch?.nightRate || 25) : (currentPitch?.dayRate || 18);

                    return (
                      <button
                        key={timeSlot}
                        type="button"
                        disabled={disabled}
                        onClick={() => setSlot(timeSlot)}
                        className={`py-2 px-2 rounded-xl text-xs font-semibold transition-all text-center border ${
                          isSelected
                            ? 'bg-emerald-600 text-white border-emerald-600 shadow-sm'
                            : disabled
                            ? 'bg-slate-100 text-slate-400 border-slate-200 cursor-not-allowed line-through'
                            : 'bg-white hover:border-emerald-400 text-slate-800 border-slate-200 hover:bg-emerald-50/40'
                        }`}
                        id={`slot-btn-${timeSlot.replace(/[: ]/g, '-')}`}
                      >
                        <div className="text-xs font-bold">{timeSlot}</div>
                        <div className={`text-[10px] font-bold mt-0.5 ${
                          isSelected
                            ? 'text-emerald-100'
                            : slotIsNight
                            ? 'text-amber-700'
                            : 'text-emerald-700'
                        }`}>
                          {slotIsNight ? `🌙 $${slotRate}` : `☀️ $${slotRate}`}
                        </div>
                        {booked && (
                          <span className="text-[9px] text-red-500 font-bold block no-underline">
                            {t.booked}
                          </span>
                        )}
                        {blocked && (
                          <span className="text-[9px] text-amber-600 font-bold block no-underline">
                            {t.maintenance}
                          </span>
                        )}
                      </button>
                    );
                  })}
                </div>

                {/* Rate Explanatory Note */}
                <div className="mt-2.5 p-2 bg-slate-50 border border-slate-200 rounded-xl flex items-center justify-between text-[11px] text-slate-600">
                  <span className="flex items-center gap-1.5 font-medium">
                    <span className="text-amber-600">☀️</span>
                    <span>{language === 'en' ? 'Day Game: $18 / hr (Before 18:00)' : 'Maalintii: $18 / saac (Kahor 18:00)'}</span>
                  </span>
                  <span className="flex items-center gap-1.5 font-medium">
                    <span className="text-emerald-600">🌙</span>
                    <span>{language === 'en' ? 'Night Floodlit: $25 / hr (18:00 - 00:00)' : 'Habeenkii (Ileys): $25 / saac'}</span>
                  </span>
                </div>
              </div>

              {/* Duration selector */}
              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">
                  {t.duration}
                </label>
                <div className="flex gap-2">
                  {[1, 2].map((dur) => (
                    <button
                      key={dur}
                      type="button"
                      onClick={() => setDurationHours(dur)}
                      className={`flex-1 py-2 rounded-xl text-xs font-semibold border transition-all ${
                        durationHours === dur
                          ? 'bg-slate-900 text-white border-slate-900'
                          : 'bg-slate-50 text-slate-700 border-slate-200 hover:bg-slate-100'
                      }`}
                    >
                      {dur} {dur === 1 ? (language === 'en' ? 'Hour' : 'Saac') : (language === 'en' ? 'Hours' : 'Saacadood')} (${currentSlotRate * dur})
                    </button>
                  ))}
                </div>
              </div>
            </div>
          )}

          {/* STEP 2: Add-Ons & Captain Info */}
          {step === 2 && (
            <div className="space-y-5">
              {/* Optional Add-Ons */}
              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2 flex items-center gap-1.5">
                  <Sparkles className="w-3.5 h-3.5 text-emerald-600" />
                  <span>{language === 'en' ? 'Match Add-Ons & Extras (Optional)' : 'Adeegyo Dheeraad ah (Ikhtiyaari)'}</span>
                </label>
                <div className="space-y-2">
                  {ADD_ON_ITEMS.map((item) => {
                    const isChecked = selectedAddons.includes(item.id);
                    return (
                      <div
                        key={item.id}
                        onClick={() => toggleAddon(item.id)}
                        className={`p-3 rounded-xl border text-xs cursor-pointer transition-all flex items-center justify-between ${
                          isChecked
                            ? 'border-emerald-600 bg-emerald-50/70 ring-1 ring-emerald-600'
                            : 'border-slate-200 hover:border-slate-300 bg-white'
                        }`}
                      >
                        <div className="flex items-center gap-3">
                          <div className={`w-4 h-4 rounded border flex items-center justify-center ${
                            isChecked ? 'bg-emerald-600 border-emerald-600 text-white' : 'border-slate-300 bg-white'
                          }`}>
                            {isChecked && <Check className="w-3 h-3" />}
                          </div>
                          <div>
                            <span className="font-bold text-slate-900">
                              {language === 'en' ? item.name : item.somaliName}
                            </span>
                            <p className="text-slate-500 text-[11px]">{item.description}</p>
                          </div>
                        </div>
                        <span className="font-extrabold text-emerald-700 font-sans ml-2 shrink-0">
                          +${item.price}
                        </span>
                      </div>
                    );
                  })}
                </div>
              </div>

              {/* Team & Captain Details */}
              <div className="pt-3 border-t border-slate-100">
                <h3 className="text-xs font-bold text-slate-800 uppercase tracking-wider mb-3">
                  {t.captainDetails}
                </h3>
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                  <div>
                    <label className="block text-xs font-medium text-slate-700 mb-1">
                      {t.teamName} *
                    </label>
                    <input
                      type="text"
                      placeholder="e.g. 26 June FC"
                      value={teamName}
                      onChange={(e) => setTeamName(e.target.value)}
                      className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-medium text-slate-900 focus:outline-none focus:ring-2 focus:ring-emerald-500"
                      id="team-name-input"
                    />
                  </div>

                  <div>
                    <label className="block text-xs font-medium text-slate-700 mb-1">
                      {t.captainName} *
                    </label>
                    <input
                      type="text"
                      placeholder="e.g. Captain Axmed"
                      value={captainName}
                      onChange={(e) => setCaptainName(e.target.value)}
                      className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-medium text-slate-900 focus:outline-none focus:ring-2 focus:ring-emerald-500"
                      id="captain-name-input"
                    />
                  </div>

                  <div>
                    <label className="block text-xs font-medium text-slate-700 mb-1">
                      {t.phone} *
                    </label>
                    <input
                      type="tel"
                      placeholder="+25263XXXXXXX"
                      value={phone}
                      onChange={(e) => setPhone(e.target.value)}
                      className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-medium text-slate-900 focus:outline-none focus:ring-2 focus:ring-emerald-500"
                      id="phone-number-input"
                    />
                    <div className="flex items-center gap-1.5 text-[11px] text-emerald-700 mt-1.5 font-medium">
                      <Smartphone className="w-3.5 h-3.5 text-emerald-600 shrink-0" />
                      <span>
                        {language === 'en'
                          ? 'Automated SMS confirmation with reference code will be simulated to this phone.'
                          : 'Farriin SMS ah oo wadata tixraaca ballanta ayaa loo diri doonaa lambarkan.'}
                      </span>
                    </div>
                  </div>

                  <div>
                    <label className="block text-xs font-medium text-slate-700 mb-1">
                      {t.email}
                    </label>
                    <input
                      type="email"
                      placeholder="e.g. team@gmail.com"
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                      className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-medium text-slate-900 focus:outline-none focus:ring-2 focus:ring-emerald-500"
                      id="email-input"
                    />
                  </div>
                </div>

                <div className="mt-3">
                  <label className="block text-xs font-medium text-slate-700 mb-1">
                    {t.notes}
                  </label>
                  <textarea
                    rows={2}
                    placeholder="Any special match requirements..."
                    value={notes}
                    onChange={(e) => setNotes(e.target.value)}
                    className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-medium text-slate-900 focus:outline-none focus:ring-2 focus:ring-emerald-500"
                    id="booking-notes-input"
                  />
                </div>
              </div>
            </div>
          )}

          {/* STEP 3: Zaad & eDahab Payment */}
          {step === 3 && (
            <div className="space-y-5">
              {/* Total Summary Banner */}
              <div className="p-4 bg-slate-900 text-white rounded-2xl flex items-center justify-between shadow-md">
                <div>
                  <span className="text-xs text-slate-400 block font-medium">
                    {language === 'en' ? 'Total Match Fee' : 'Wadarta Lacagta Garoonka'}
                  </span>
                  <div className="text-2xl font-black text-emerald-400 font-sans">
                    ${totalPrice} USD
                  </div>
                  <span className="text-[11px] text-slate-300">
                    {currentPitch.name} • {date} • {slot} ({isNight ? (language === 'en' ? 'Night Floodlit: $25/hr' : 'Habeenkii: $25/saac') : (language === 'en' ? 'Day Game: $18/hr' : 'Maalintii: $18/saac')})
                  </span>
                </div>
                <div className="text-right">
                  <span className="bg-emerald-500/20 text-emerald-300 border border-emerald-500/30 text-[11px] px-2.5 py-1 rounded-full font-bold">
                    {teamName}
                  </span>
                </div>
              </div>

              {/* Payment Method Selector (Zaad vs eDahab) */}
              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2 flex items-center gap-1.5">
                  <CreditCard className="w-3.5 h-3.5 text-emerald-600" />
                  <span>{language === 'en' ? 'Select Mobile Money Provider' : 'Dooro Shirkadda Lacag-bixinta'}</span>
                </label>

                <div className="grid grid-cols-2 gap-3">
                  {/* Zaad Option */}
                  <button
                    type="button"
                    onClick={() => setPaymentMethod('zaad')}
                    className={`p-4 rounded-2xl border text-left transition-all relative ${
                      paymentMethod === 'zaad'
                        ? 'border-emerald-600 bg-emerald-50/80 ring-2 ring-emerald-600'
                        : 'border-slate-200 hover:border-slate-300 bg-white'
                    }`}
                    id="select-zaad-method"
                  >
                    <div className="flex items-center justify-between mb-1">
                      <span className="font-black text-slate-900 text-sm">Zaad Service</span>
                      <span className="text-[11px] font-bold text-emerald-700 bg-emerald-100 px-2 py-0.5 rounded-full">
                        Telesom
                      </span>
                    </div>
                    <div className="text-xs text-slate-600 font-medium">
                      Merchant: <span className="font-extrabold text-slate-900 font-mono text-sm">{APP_CONFIG.zaadMerchant}</span>
                    </div>
                  </button>

                  {/* eDahab Option */}
                  <button
                    type="button"
                    onClick={() => setPaymentMethod('edahab')}
                    className={`p-4 rounded-2xl border text-left transition-all relative ${
                      paymentMethod === 'edahab'
                        ? 'border-amber-600 bg-amber-50/80 ring-2 ring-amber-600'
                        : 'border-slate-200 hover:border-slate-300 bg-white'
                    }`}
                    id="select-edahab-method"
                  >
                    <div className="flex items-center justify-between mb-1">
                      <span className="font-black text-slate-900 text-sm">eDahab</span>
                      <span className="text-[11px] font-bold text-amber-700 bg-amber-100 px-2 py-0.5 rounded-full">
                        Somtel
                      </span>
                    </div>
                    <div className="text-xs text-slate-600 font-medium">
                      Merchant: <span className="font-extrabold text-slate-900 font-mono text-sm">{APP_CONFIG.edahabMerchant}</span>
                    </div>
                  </button>
                </div>
              </div>

              {/* Step-by-step payment instructions box */}
              <div className="p-4 bg-slate-50 border border-slate-200 rounded-2xl">
                <div className="flex items-center justify-between mb-2">
                  <h4 className="text-xs font-bold text-slate-900 uppercase tracking-wider">
                    {paymentMethod === 'zaad' ? 'Zaad Payment Instructions' : 'eDahab Payment Instructions'}
                  </h4>
                  <span className="text-[11px] font-bold text-emerald-700">
                    Merchant: {paymentMethod === 'zaad' ? APP_CONFIG.zaadMerchant : APP_CONFIG.edahabMerchant}
                  </span>
                </div>

                {/* USSD Quick Dial Helper */}
                <div className="bg-slate-900 text-white p-3 rounded-xl mb-3 flex items-center justify-between font-mono text-xs">
                  <div>
                    <span className="text-[10px] text-slate-400 block font-sans">
                      {language === 'en' ? 'Quick USSD Dial Code:' : 'Koodhka Tooska ah ee USSD:'}
                    </span>
                    <span className="text-emerald-400 font-bold text-sm">
                      {paymentMethod === 'zaad' ? getZaadUssdCode(totalPrice) : getEdahabUssdCode(totalPrice)}
                    </span>
                  </div>
                  <button
                    type="button"
                    onClick={handleCopyUssd}
                    className="bg-slate-800 hover:bg-slate-700 text-white px-2.5 py-1.5 rounded-lg text-xs font-sans font-medium flex items-center gap-1.5 transition-colors border border-slate-700"
                    id="copy-ussd-btn"
                  >
                    {copiedUssd ? (
                      <>
                        <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400" />
                        <span className="text-emerald-400">{language === 'en' ? 'Copied' : 'Waa la guuriyey'}</span>
                      </>
                    ) : (
                      <>
                        <Copy className="w-3.5 h-3.5 text-slate-300" />
                        <span>{language === 'en' ? 'Copy USSD' : 'Koobi'}</span>
                      </>
                    )}
                  </button>
                </div>

                <ol className="text-xs text-slate-700 space-y-1.5 list-decimal pl-4">
                  {paymentMethod === 'zaad' ? (
                    <>
                      <li>
                        {language === 'en' ? 'Dial *880# or open Telesom Zaad App' : 'Garaac *880# ama fur Telesom Zaad App'}
                      </li>
                      <li>
                        {language === 'en' ? 'Choose Pay Merchant / U wareeji Ganacsi' : 'Dooro Bixi Biil / U wareeji Ganacsi'}
                      </li>
                      <li>
                        {language === 'en' ? 'Enter Merchant Number:' : 'Geli Lambarka Ganacsiga:'}{' '}
                        <strong className="text-emerald-800 font-mono text-xs">{APP_CONFIG.zaadMerchant}</strong> (26 JSC TurfBook)
                      </li>
                      <li>
                        {language === 'en' ? `Enter Amount: $${totalPrice} USD` : `Geli Lacagta: $${totalPrice} USD`}
                      </li>
                      <li>
                        {language === 'en' ? 'Confirm PIN & copy the Transaction Reference (TID)' : 'Geli Lambarkaaga Sirta ah (PIN) kadibna soo qaado Tixraaca (TID)'}
                      </li>
                    </>
                  ) : (
                    <>
                      <li>
                        {language === 'en' ? 'Dial *789# or *712# or open Somtel eDahab' : 'Garaac *789# ama *712# ama fur Somtel eDahab'}
                      </li>
                      <li>
                        {language === 'en' ? 'Select Pay Merchant' : 'Dooro Bixi Biil Ganacsi'}
                      </li>
                      <li>
                        {language === 'en' ? 'Enter Merchant ID:' : 'Geli Merchant ID:'}{' '}
                        <strong className="text-amber-800 font-mono text-xs">{APP_CONFIG.edahabMerchant}</strong> (26 JSC TurfBook)
                      </li>
                      <li>
                        {language === 'en' ? `Enter Amount: $${totalPrice} USD` : `Geli Lacagta: $${totalPrice} USD`}
                      </li>
                      <li>
                        {language === 'en' ? 'Complete PIN & note down the Transaction ID' : 'Dhamaystir PIN-kaaga oo tixraaca halkan ku qor'}
                      </li>
                    </>
                  )}
                </ol>
              </div>

              {/* Transaction ID verification input */}
              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                  {t.enterTid} <span className="text-slate-400 font-normal">({language === 'en' ? 'Optional, or verify via WhatsApp' : 'Ikhtiyaari ama WhatsApp ku xaqiiji'})</span>
                </label>
                <input
                  type="text"
                  placeholder={paymentMethod === 'zaad' ? 'e.g. ZD-998241 or SMS Ref' : 'e.g. ED-883102 or SMS Ref'}
                  value={transactionId}
                  onChange={(e) => setTransactionId(e.target.value)}
                  className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-mono font-medium text-slate-900 focus:outline-none focus:ring-2 focus:ring-emerald-500"
                  id="transaction-id-input"
                />
                <p className="text-[11px] text-slate-500 mt-1">
                  {language === 'en'
                    ? `You can also submit and WhatsApp the receipt to ${APP_CONFIG.contactPhone} immediately.`
                    : `Waxaad sidoo kale ku xaqiijin kartaa WhatsApp-ka ${APP_CONFIG.contactPhone}.`}
                </p>
              </div>

              {/* Automated SMS Confirmation Integration (Placeholder Simulation) */}
              <div 
                className="p-4 bg-slate-900 text-white rounded-2xl border border-slate-800 shadow-sm space-y-3"
                id="sms-integration-placeholder-card"
              >
                <div className="flex items-center justify-between gap-3">
                  <div className="flex items-center gap-2.5">
                    <div className="w-8 h-8 rounded-xl bg-emerald-500/20 text-emerald-400 border border-emerald-500/30 flex items-center justify-center shrink-0">
                      <Smartphone className="w-4 h-4" />
                    </div>
                    <div>
                      <div className="flex items-center gap-2 flex-wrap">
                        <span className="text-xs font-bold text-slate-100">
                          {language === 'en' ? 'Automated SMS Confirmation' : 'Xaqiijinta Tooska ah ee SMS-ka'}
                        </span>
                        <span className="text-[10px] font-mono bg-emerald-500/20 text-emerald-300 border border-emerald-500/30 px-1.5 py-0.5 rounded font-bold">
                          {language === 'en' ? 'SMS Gateway Placeholder' : 'Iskuxirka SMS Gateway'}
                        </span>
                      </div>
                      <p className="text-[11px] text-slate-400 mt-0.5">
                        {phone.startsWith('+25263') || phone.startsWith('063') || paymentMethod === 'zaad'
                          ? 'Telesom SMS Gateway (Simulated)'
                          : 'Somtel Bulk SMS Gateway (Simulated)'}
                      </p>
                    </div>
                  </div>

                  {/* Toggle Checkbox */}
                  <label className="relative inline-flex items-center cursor-pointer shrink-0">
                    <input
                      type="checkbox"
                      checked={sendSmsConfirmation}
                      onChange={(e) => setSendSmsConfirmation(e.target.checked)}
                      className="sr-only peer"
                      id="toggle-sms-checkbox"
                    />
                    <div className="w-9 h-5 bg-slate-700 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-slate-300 after:border after:rounded-full after:h-4 after:w-4 after:transition-all peer-checked:bg-emerald-600"></div>
                  </label>
                </div>

                {sendSmsConfirmation ? (
                  <div className="space-y-2 pt-2 border-t border-slate-800">
                    <div className="flex items-center justify-between text-[11px] text-slate-400">
                      <span>
                        {language === 'en' ? 'Dispatch To:' : 'Loo dirayaa:'}{' '}
                        <strong className="text-emerald-400 font-mono">{phone.trim() || '+252...'}</strong>
                      </span>
                      <span className="text-[10px] text-slate-400">
                        Ref Code: <strong className="text-emerald-300 font-mono">#{previewRefCode}</strong>
                      </span>
                    </div>

                    {/* Simulated Phone Message Preview Box */}
                    <div className="bg-slate-950 rounded-xl p-3 border border-slate-800 text-xs font-mono text-slate-200">
                      <div className="flex items-center justify-between text-[10px] text-slate-400 font-sans pb-1.5 border-b border-slate-800/80 mb-1.5">
                        <span className="font-semibold text-emerald-400 flex items-center gap-1">
                          <MessageSquare className="w-3 h-3" />
                          Sender ID: 26-JSC
                        </span>
                        <span className="text-emerald-400/90 font-medium">Ready to dispatch</span>
                      </div>
                      <p className="leading-relaxed text-slate-100 font-sans text-[11px]">
                        {generateBookingSmsText({
                          referenceCode: previewRefCode,
                          teamName: teamName.trim() || (language === 'en' ? 'Your Team' : 'Kooxdaada'),
                          pitchName: currentPitch.name,
                          date,
                          startTime: slot.split(' - ')[0]?.trim() || '19:00',
                          endTime: slot.split(' - ')[1]?.trim() || '20:00',
                          totalAmount: totalPrice,
                          language,
                        })}
                      </p>
                    </div>

                    <div className="flex items-center gap-1.5 text-[11px] text-slate-400">
                      <Radio className="w-3.5 h-3.5 text-emerald-400 animate-pulse shrink-0" />
                      <span>
                        {language === 'en'
                          ? 'Simulates automated SMS delivery with the unique booking reference code to your mobile device upon confirmation.'
                          : 'Waxay tijaabinaysaa dirista fariin SMS toos ah oo wadata lambarka tixraaca ee taleefankaaga markaad xaqiijiso.'}
                      </span>
                    </div>
                  </div>
                ) : (
                  <p className="text-[11px] text-slate-400 italic">
                    {language === 'en'
                      ? 'Automated SMS confirmation simulation is paused. You can still print or download the digital voucher.'
                      : 'Dirista SMS-ka waa la hakiyey. Waxaad weli heli doontaa tigidhka dhijitaalka ah.'}
                  </p>
                )}
              </div>
            </div>
          )}
        </div>

        {/* Modal Footer Controls */}
        <div className="bg-slate-50 px-6 py-4 border-t border-slate-200 flex items-center justify-between gap-3">
          {step > 1 ? (
            <button
              type="button"
              onClick={() => {
                setFormError('');
                setStep((prev) => (prev - 1) as 1 | 2);
              }}
              className="px-4 py-2 rounded-xl text-xs font-semibold text-slate-700 hover:bg-slate-200/80 transition-colors flex items-center gap-1.5"
              id="booking-prev-step-btn"
            >
              <ChevronLeft className="w-4 h-4" />
              <span>{language === 'en' ? 'Back' : 'Dib u noqo'}</span>
            </button>
          ) : (
            <div className="text-xs text-slate-500">
              <span className="font-bold text-slate-900 font-sans">${totalPrice}</span> {language === 'en' ? 'Total' : 'Wadarta'}
            </div>
          )}

          <div className="flex items-center gap-2">
            {step === 1 && (
              <button
                type="button"
                onClick={() => setStep(2)}
                className="bg-emerald-600 hover:bg-emerald-700 text-white font-bold px-5 py-2.5 rounded-xl text-xs sm:text-sm transition-all flex items-center gap-1.5 shadow-sm"
                id="booking-step1-next-btn"
              >
                <span>{language === 'en' ? 'Next: Team Info' : 'Ku xiga: Xogta'}</span>
                <ChevronRight className="w-4 h-4" />
              </button>
            )}

            {step === 2 && (
              <button
                type="button"
                onClick={handleProceedToPayment}
                className="bg-emerald-600 hover:bg-emerald-700 text-white font-bold px-5 py-2.5 rounded-xl text-xs sm:text-sm transition-all flex items-center gap-1.5 shadow-sm"
                id="booking-step2-next-btn"
              >
                <span>{t.proceedPayment}</span>
                <ChevronRight className="w-4 h-4" />
              </button>
            )}

            {step === 3 && (
              <button
                type="button"
                disabled={isDispatchingSms}
                onClick={handleConfirmBooking}
                className={`bg-emerald-600 hover:bg-emerald-700 text-white font-extrabold px-6 py-2.5 rounded-xl text-xs sm:text-sm transition-all flex items-center gap-2 shadow-md hover:shadow-emerald-600/30 active:scale-[0.98] ${
                  isDispatchingSms ? 'opacity-80 cursor-wait' : ''
                }`}
                id="confirm-booking-final-btn"
              >
                {isDispatchingSms ? (
                  <>
                    <span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></span>
                    <span>
                      {language === 'en' ? 'Dispatching SMS & Confirming...' : 'Diraya SMS & Xaqiijinaya...'}
                    </span>
                  </>
                ) : (
                  <>
                    <Check className="w-4 h-4" />
                    <span>{t.confirmBooking}</span>
                  </>
                )}
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
