import React, { useRef, useState } from 'react';
import { 
  X, 
  Printer, 
  Share2, 
  MessageSquare, 
  CheckCircle2, 
  Calendar, 
  Clock, 
  Phone, 
  Mail, 
  ShieldCheck, 
  QrCode,
  MapPin,
  Trophy,
  Smartphone,
  Send,
  Radio
} from 'lucide-react';
import { Booking, Language } from '../types';
import { APP_CONFIG, TRANSLATIONS } from '../data/initialData';
import { generateWhatsAppBookingUrl, generateBookingSmsText } from '../utils/helpers';

interface BookingTicketModalProps {
  booking: Booking | null;
  onClose: () => void;
  onResendSms?: (booking: Booking) => void;
  language: Language;
}

export const BookingTicketModal: React.FC<BookingTicketModalProps> = ({
  booking,
  onClose,
  onResendSms,
  language,
}) => {
  const t = TRANSLATIONS[language];
  const ticketRef = useRef<HTMLDivElement>(null);
  const [isResending, setIsResending] = useState<boolean>(false);
  const [resendSuccess, setResendSuccess] = useState<boolean>(false);

  if (!booking) return null;

  const handlePrint = () => {
    window.print();
  };

  const whatsAppUrl = generateWhatsAppBookingUrl(booking);

  const handleResendSmsClick = () => {
    if (isResending) return;
    setIsResending(true);
    setTimeout(() => {
      setIsResending(false);
      setResendSuccess(true);
      if (onResendSms) {
        onResendSms(booking);
      }
      setTimeout(() => setResendSuccess(false), 4000);
    }, 600);
  };

  const smsText = booking.smsMessagePreview || generateBookingSmsText({
    referenceCode: booking.referenceCode,
    teamName: booking.teamName,
    pitchName: booking.pitchName,
    date: booking.date,
    startTime: booking.startTime,
    endTime: booking.endTime,
    totalAmount: booking.totalAmount,
    language,
  });

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-950/75 backdrop-blur-sm flex items-center justify-center p-3 sm:p-4">
      <div 
        className="bg-white rounded-2xl w-full max-w-lg overflow-hidden shadow-2xl border border-slate-200 animate-in fade-in zoom-in-95 duration-200"
        id="booking-ticket-modal"
      >
        {/* Modal Top Bar */}
        <div className="bg-slate-900 text-white px-5 py-3.5 flex items-center justify-between border-b border-slate-800">
          <div className="flex items-center gap-2">
            <CheckCircle2 className="w-5 h-5 text-emerald-400" />
            <h3 className="font-bold text-sm sm:text-base">
              {language === 'en' ? 'Match Booking Voucher' : 'Tixraaca Ballanta Garoonka'}
            </h3>
          </div>
          <button
            onClick={onClose}
            className="text-slate-400 hover:text-white p-1 rounded-lg hover:bg-slate-800 transition-colors"
            id="close-ticket-modal-btn"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Printable Ticket Voucher Body */}
        <div ref={ticketRef} className="p-6 bg-white text-slate-900 print:p-0">
          {/* Ticket Header */}
          <div className="text-center pb-4 border-b border-dashed border-slate-200">
            <div className="inline-flex items-center justify-center w-12 h-12 rounded-xl bg-emerald-700 text-white mb-2 shadow-md">
              <Trophy className="w-6 h-6 text-emerald-100" />
            </div>
            <h2 className="text-xl font-black tracking-tight text-slate-900">
              {APP_CONFIG.appName}
            </h2>
            <p className="text-xs text-slate-500 font-medium">
              {language === 'en' ? APP_CONFIG.tagline : APP_CONFIG.somaliTagline}
            </p>
            <div className="flex items-center justify-center gap-3 mt-1 text-[11px] text-slate-600 font-medium">
              <span className="flex items-center gap-1">
                <MapPin className="w-3 h-3 text-emerald-600" />
                {language === 'en' ? '26 June District, Hargeisa' : 'Degmada 26 June, Hargeysa'}
              </span>
              <span>•</span>
              <span className="flex items-center gap-1">
                <Phone className="w-3 h-3 text-emerald-600" />
                {APP_CONFIG.contactPhone}
              </span>
            </div>
          </div>

          {/* Reference Banner */}
          <div className="my-4 bg-emerald-50 border border-emerald-200 rounded-xl p-3 flex items-center justify-between">
            <div>
              <span className="text-[10px] text-emerald-800 uppercase font-bold tracking-wider block">
                {language === 'en' ? 'Booking Reference' : 'Lambarka Tixraaca'}
              </span>
              <span className="text-xl font-black font-mono text-emerald-900 tracking-wider">
                #{booking.referenceCode}
              </span>
            </div>
            <div className="text-right">
              <span className={`text-[10px] uppercase font-bold px-2 py-0.5 rounded-full ${
                booking.paymentStatus === 'paid' 
                  ? 'bg-emerald-200 text-emerald-900' 
                  : 'bg-amber-200 text-amber-900'
              }`}>
                {booking.paymentStatus === 'paid' ? (language === 'en' ? 'Paid / Confirmed' : 'Waa La Bixiyey') : (language === 'en' ? 'Pending Verification' : 'Sugaya Xaqiijin')}
              </span>
              <span className="block text-[11px] font-bold text-slate-700 mt-1">
                ${booking.totalAmount} USD
              </span>
            </div>
          </div>

          {/* Match Details Grid */}
          <div className="space-y-3 text-xs">
            <div className="grid grid-cols-2 gap-3 p-3 bg-slate-50 rounded-xl border border-slate-100">
              <div>
                <span className="text-slate-600 text-[10px] uppercase font-semibold block">
                  {language === 'en' ? 'Pitch / Arena' : 'Garoonka'}
                </span>
                <span className="font-bold text-slate-900 text-xs sm:text-sm">
                  {booking.pitchName}
                </span>
              </div>
              <div>
                <span className="text-slate-600 text-[10px] uppercase font-semibold block">
                  {language === 'en' ? 'Team Name' : 'Kooxda'}
                </span>
                <span className="font-bold text-slate-900 text-xs sm:text-sm">
                  {booking.teamName}
                </span>
              </div>
              <div>
                <span className="text-slate-600 text-[10px] uppercase font-semibold block flex items-center gap-1">
                  <Calendar className="w-3 h-3 text-emerald-600" />
                  {language === 'en' ? 'Match Date' : 'Taariikhda'}
                </span>
                <span className="font-semibold text-slate-900">
                  {booking.date}
                </span>
              </div>
              <div>
                <span className="text-slate-600 text-[10px] uppercase font-semibold block flex items-center gap-1">
                  <Clock className="w-3 h-3 text-emerald-600" />
                  {language === 'en' ? 'Time Slot' : 'Saacadda'}
                </span>
                <span className="font-semibold text-slate-900">
                  {booking.startTime} - {booking.endTime} ({booking.durationHours}h)
                </span>
              </div>
            </div>

            {/* Captain & Contact */}
            <div className="p-3 bg-slate-50 rounded-xl border border-slate-100 grid grid-cols-2 gap-3">
              <div>
                <span className="text-slate-600 text-[10px] uppercase font-semibold block">
                  {language === 'en' ? 'Captain / Booker' : 'Kabtanka'}
                </span>
                <span className="font-semibold text-slate-800">{booking.customerName}</span>
              </div>
              <div>
                <span className="text-slate-600 text-[10px] uppercase font-semibold block">
                  {language === 'en' ? 'Phone' : 'Telefoon'}
                </span>
                <span className="font-semibold text-slate-800 font-mono">{booking.customerPhone}</span>
              </div>
            </div>

            {/* Payment Details */}
            <div className="p-3 bg-slate-50 rounded-xl border border-slate-100 flex items-center justify-between">
              <div>
                <span className="text-slate-600 text-[10px] uppercase font-semibold block">
                  {language === 'en' ? 'Payment Method' : 'Habka Lacag-bixinta'}
                </span>
                <span className="font-bold text-slate-900">
                  {booking.paymentMethod === 'zaad' ? 'Zaad Service' : 'eDahab'}
                </span>
                <span className="text-slate-600 text-[11px] block font-mono">
                  Merchant: {booking.merchantNumber}
                </span>
              </div>
              <div className="text-right">
                <span className="text-slate-600 text-[10px] uppercase font-semibold block">
                  {language === 'en' ? 'Transaction ID / TID' : 'Tixraaca (TID)'}
                </span>
                <span className="font-mono font-bold text-emerald-700">
                  {booking.transactionId || 'Awaiting Verification'}
                </span>
              </div>
            </div>

            {/* Automated SMS Dispatch Confirmation Card */}
            <div className="p-3 bg-emerald-50/70 rounded-xl border border-emerald-200/80 space-y-2">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-1.5 text-xs font-bold text-emerald-950">
                  <Smartphone className="w-3.5 h-3.5 text-emerald-700" />
                  <span>{language === 'en' ? 'Automated SMS Confirmation' : 'Xaqiijinta SMS-ka'}</span>
                  <span className="text-[10px] bg-emerald-200 text-emerald-900 font-mono font-bold px-1.5 py-0.5 rounded">
                    {language === 'en' ? 'Simulated SMS' : 'SMS Diray'}
                  </span>
                </div>
                <button
                  type="button"
                  disabled={isResending}
                  onClick={handleResendSmsClick}
                  className="text-[11px] font-bold text-emerald-700 hover:text-emerald-900 flex items-center gap-1 bg-white hover:bg-emerald-100/60 px-2 py-0.5 rounded-lg border border-emerald-300 transition-colors"
                  id="resend-sms-ticket-btn"
                >
                  <Send className={`w-3 h-3 ${isResending ? 'animate-spin' : ''}`} />
                  <span>{isResending ? (language === 'en' ? 'Sending...' : 'Diraya...') : (language === 'en' ? 'Resend SMS' : 'Dib u dir')}</span>
                </button>
              </div>

              <div className="bg-white/80 p-2.5 rounded-lg border border-emerald-200/60 text-[11px] text-slate-700 font-sans leading-relaxed">
                <span className="font-bold text-emerald-900 block text-[10px] uppercase tracking-wider mb-0.5">
                  {language === 'en' ? `Message Dispatched to ${booking.customerPhone}:` : `Farriinta loo diray ${booking.customerPhone}:`}
                </span>
                <p className="text-slate-800 font-medium">
                  {smsText}
                </p>
              </div>

              {resendSuccess && (
                <div className="text-[11px] text-emerald-800 font-bold flex items-center gap-1 animate-in fade-in">
                  <CheckCircle2 className="w-3.5 h-3.5 text-emerald-600" />
                  <span>
                    {language === 'en' 
                      ? `Automated SMS with ref #${booking.referenceCode} re-sent to ${booking.customerPhone}!` 
                      : `SMS-kii wadata #${booking.referenceCode} dib ayaa loogu diray ${booking.customerPhone}!`}
                  </span>
                </div>
              )}
            </div>

            {/* Simulated Barcode / QR Code Graphic */}
            <div className="flex items-center justify-between pt-2 px-1 text-slate-600">
              <div className="flex items-center gap-2">
                <QrCode className="w-9 h-9 text-slate-700" />
                <div className="text-[10px] leading-tight">
                  <span className="font-bold text-slate-800 block">26 JSC Verification Code</span>
                  <span>Present at front desk upon arrival</span>
                </div>
              </div>
              <div className="text-right text-[10px]">
                <span className="text-slate-600 block">Booked at:</span>
                <span className="font-medium text-slate-700">{new Date(booking.createdAt).toLocaleDateString()}</span>
              </div>
            </div>
          </div>
        </div>

        {/* Action Buttons */}
        <div className="bg-slate-50 px-6 py-4 border-t border-slate-200 flex flex-wrap items-center justify-between gap-2 print:hidden">
          <div className="flex items-center gap-2">
            <button
              onClick={handlePrint}
              className="px-3 py-2 bg-white hover:bg-slate-100 border border-slate-200 text-slate-700 rounded-xl text-xs font-semibold flex items-center gap-1.5 transition-colors shadow-sm"
              id="print-ticket-btn"
            >
              <Printer className="w-4 h-4 text-slate-600" />
              <span>{language === 'en' ? 'Print / PDF' : 'Daabac'}</span>
            </button>

            <button
              type="button"
              disabled={isResending}
              onClick={handleResendSmsClick}
              className="px-3 py-2 bg-white hover:bg-slate-100 border border-slate-200 text-emerald-700 rounded-xl text-xs font-semibold flex items-center gap-1.5 transition-colors shadow-sm"
              id="resend-sms-footer-btn"
            >
              <Smartphone className="w-4 h-4 text-emerald-600" />
              <span>{isResending ? (language === 'en' ? 'Sending SMS...' : 'Diraya SMS...') : (language === 'en' ? 'SMS Voucher' : 'SMS Ku dir')}</span>
            </button>
          </div>

          <div className="flex items-center gap-2">
            <a
              href={whatsAppUrl}
              target="_blank"
              rel="noreferrer"
              className="px-4 py-2 bg-emerald-600 hover:bg-emerald-500 text-white rounded-xl text-xs font-bold flex items-center gap-1.5 transition-colors shadow-sm"
              id="whatsapp-confirm-btn"
            >
              <MessageSquare className="w-4 h-4" />
              <span>{t.shareWhatsApp}</span>
            </a>

            <button
              onClick={onClose}
              className="px-4 py-2 bg-slate-900 hover:bg-slate-800 text-white rounded-xl text-xs font-bold transition-colors"
              id="ticket-done-btn"
            >
              {language === 'en' ? 'Done' : 'Dhammee'}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
