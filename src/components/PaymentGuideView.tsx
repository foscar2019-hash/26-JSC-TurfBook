import React, { useState } from 'react';
import { 
  CreditCard, 
  Phone, 
  Mail, 
  Clock, 
  MapPin, 
  Copy, 
  CheckCircle2, 
  MessageSquare, 
  ExternalLink,
  ShieldCheck,
  Zap,
  HelpCircle,
  Sparkles
} from 'lucide-react';
import { Language } from '../types';
import { APP_CONFIG, TRANSLATIONS } from '../data/initialData';

interface PaymentGuideViewProps {
  language: Language;
  onBookNow: () => void;
}

export const PaymentGuideView: React.FC<PaymentGuideViewProps> = ({
  language,
  onBookNow,
}) => {
  const t = TRANSLATIONS[language];
  const [copiedZaad, setCopiedZaad] = useState(false);
  const [copiedEdahab, setCopiedEdahab] = useState(false);
  const [copiedPhone, setCopiedPhone] = useState(false);
  const [copiedEmail, setCopiedEmail] = useState(false);

  const copyToClipboard = (text: string, setter: (val: boolean) => void) => {
    navigator.clipboard.writeText(text);
    setter(true);
    setTimeout(() => setter(false), 2000);
  };

  return (
    <div className="space-y-8" id="payment-guide-view">
      {/* Hero Banner */}
      <div className="bg-gradient-to-r from-slate-900 via-slate-800 to-emerald-950 text-white p-6 sm:p-8 rounded-3xl shadow-lg border border-slate-700/60 relative overflow-hidden">
        <div className="relative z-10 max-w-2xl">
          <span className="bg-emerald-500/20 text-emerald-300 border border-emerald-500/30 text-xs font-bold px-3 py-1 rounded-full uppercase tracking-wider mb-3 inline-block">
            Official Payment Guide • Zaad & eDahab
          </span>
          <h2 className="text-2xl sm:text-3xl font-black tracking-tight mb-2">
            {language === 'en'
              ? 'Fast & Secure Mobile Money Booking'
              : 'Habka Lacag-bixinta Tooska ah ee Zaad & eDahab'}
          </h2>
          <p className="text-slate-300 text-xs sm:text-sm leading-relaxed mb-6">
            {language === 'en'
              ? `At 26 JSC TurfBook, you can book pitch time instantly using your preferred mobile wallet. Pay to our official registered merchant accounts below and verify your booking on WhatsApp.`
              : `Garoomada 26 JSC TurfBook waxaad si fudud ugu bixin kartaa lacagta adiga oo isticmaalaya Zaad ama eDahab. Xaqiiji tixraaca lacagta si booskaaga laguugu xiro.`}
          </p>

          <div className="flex flex-wrap items-center gap-3">
            <button
              onClick={onBookNow}
              className="bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-black px-6 py-3 rounded-xl text-xs sm:text-sm transition-all shadow-md active:scale-98"
              id="guide-book-now-btn"
            >
              {t.bookNow}
            </button>
            <a
              href={`https://wa.me/${APP_CONFIG.contactPhone.replace(/[^0-9]/g, '')}`}
              target="_blank"
              rel="noreferrer"
              className="bg-slate-800/90 hover:bg-slate-700 text-white border border-slate-600 font-bold px-5 py-3 rounded-xl text-xs sm:text-sm transition-all flex items-center gap-2"
              id="guide-whatsapp-btn"
            >
              <MessageSquare className="w-4 h-4 text-emerald-400" />
              <span>{language === 'en' ? 'Chat on WhatsApp' : 'Kala xidhiidh WhatsApp'}</span>
            </a>
          </div>
        </div>
      </div>

      {/* Two Payment Rails Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* ZAAD SERVICE CARD */}
        <div className="bg-white rounded-3xl border border-slate-200 shadow-sm p-6 flex flex-col justify-between hover:shadow-md transition-shadow">
          <div>
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center gap-2.5">
                <div className="w-10 h-10 rounded-2xl bg-emerald-600 text-white flex items-center justify-center font-black text-sm">
                  Z
                </div>
                <div>
                  <h3 className="font-black text-slate-900 text-base">Zaad Service</h3>
                  <span className="text-xs text-emerald-700 font-semibold">Telesom Somaliland</span>
                </div>
              </div>
              <span className="bg-emerald-50 text-emerald-800 text-xs font-bold px-2.5 py-1 rounded-full border border-emerald-200">
                Active
              </span>
            </div>

            {/* Merchant Number Box */}
            <div className="bg-emerald-50/60 border border-emerald-200/80 rounded-2xl p-4 mb-4">
              <span className="text-[10px] uppercase font-bold text-emerald-800 block tracking-wider">
                {language === 'en' ? 'Zaad Merchant Account' : 'Lambarka Ganacsiga Zaad'}
              </span>
              <div className="flex items-center justify-between mt-1">
                <span className="text-2xl sm:text-3xl font-black font-mono text-emerald-950 tracking-wider">
                  {APP_CONFIG.zaadMerchant}
                </span>
                <button
                  onClick={() => copyToClipboard(APP_CONFIG.zaadMerchant, setCopiedZaad)}
                  className="p-2 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-semibold flex items-center gap-1.5 transition-colors shadow-xs"
                  id="copy-zaad-merchant-btn"
                  title="Copy Merchant Number"
                >
                  {copiedZaad ? <CheckCircle2 className="w-4 h-4" /> : <Copy className="w-4 h-4" />}
                  <span>{copiedZaad ? 'Copied' : 'Copy'}</span>
                </button>
              </div>
              <span className="text-[11px] text-emerald-800 font-medium mt-1 block">
                Account Name: <strong>26 JSC TurfBook Arena</strong>
              </span>
            </div>

            {/* Instructions */}
            <div className="space-y-3">
              <h4 className="text-xs font-bold text-slate-700 uppercase tracking-wider">
                {language === 'en' ? 'How to Pay via Zaad:' : 'Sida loogu bixiyo Zaad:'}
              </h4>
              <ol className="space-y-2 text-xs text-slate-600 list-decimal pl-4">
                <li>
                  {language === 'en' ? 'Dial' : 'Garaac'} <strong className="font-mono text-slate-900">*880#</strong> {language === 'en' ? 'on your mobile or open the Zaad App.' : 'ama fur Telesom Zaad App.'}
                </li>
                <li>
                  {language === 'en' ? 'Select' : 'Dooro'} <strong>{language === 'en' ? 'Pay Merchant / U wareeji Ganacsi' : 'Bixi Biil / Ganacsi'}</strong>.
                </li>
                <li>
                  {language === 'en' ? 'Enter Merchant Number:' : 'Geli Lambarka:'} <strong className="font-mono text-emerald-800 text-xs">{APP_CONFIG.zaadMerchant}</strong>.
                </li>
                <li>
                  {language === 'en' ? 'Enter the Match Fee Amount (e.g. $18 or $25).' : 'Geli Qiimaha Garoonka (tusaale $18 ama $25).'}
                </li>
                <li>
                  {language === 'en' ? 'Enter your secret PIN and complete transfer.' : 'Geli lambarkaaga sirta ah oo xaqiiji.'}
                </li>
                <li>
                  {language === 'en' ? 'Note the Transaction Reference (TID) from Telesom SMS and submit with your booking.' : 'Soo qaado lambarka tixraaca (TID) ee SMS-ka Telesom kuugu yimaada.'}
                </li>
              </ol>
            </div>
          </div>

          <div className="mt-6 pt-4 border-t border-slate-100 flex items-center justify-between text-xs text-slate-500">
            <span>Instant Confirmation</span>
            <span className="font-mono font-bold text-emerald-700">*880*{APP_CONFIG.zaadMerchant}*AMT#</span>
          </div>
        </div>

        {/* EDAHAB SERVICE CARD */}
        <div className="bg-white rounded-3xl border border-slate-200 shadow-sm p-6 flex flex-col justify-between hover:shadow-md transition-shadow">
          <div>
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center gap-2.5">
                <div className="w-10 h-10 rounded-2xl bg-amber-500 text-white flex items-center justify-center font-black text-sm">
                  E
                </div>
                <div>
                  <h3 className="font-black text-slate-900 text-base">eDahab</h3>
                  <span className="text-xs text-amber-700 font-semibold">Somtel Somaliland</span>
                </div>
              </div>
              <span className="bg-amber-50 text-amber-800 text-xs font-bold px-2.5 py-1 rounded-full border border-amber-200">
                Active
              </span>
            </div>

            {/* Merchant Number Box */}
            <div className="bg-amber-50/60 border border-amber-200/80 rounded-2xl p-4 mb-4">
              <span className="text-[10px] uppercase font-bold text-amber-800 block tracking-wider">
                {language === 'en' ? 'eDahab Merchant Account' : 'Lambarka Ganacsiga eDahab'}
              </span>
              <div className="flex items-center justify-between mt-1">
                <span className="text-2xl sm:text-3xl font-black font-mono text-amber-950 tracking-wider">
                  {APP_CONFIG.edahabMerchant}
                </span>
                <button
                  onClick={() => copyToClipboard(APP_CONFIG.edahabMerchant, setCopiedEdahab)}
                  className="p-2 bg-amber-600 hover:bg-amber-700 text-white rounded-xl text-xs font-semibold flex items-center gap-1.5 transition-colors shadow-xs"
                  id="copy-edahab-merchant-btn"
                  title="Copy eDahab Merchant Number"
                >
                  {copiedEdahab ? <CheckCircle2 className="w-4 h-4" /> : <Copy className="w-4 h-4" />}
                  <span>{copiedEdahab ? 'Copied' : 'Copy'}</span>
                </button>
              </div>
              <span className="text-[11px] text-amber-800 font-medium mt-1 block">
                Account Name: <strong>26 JSC TurfBook Centre</strong>
              </span>
            </div>

            {/* Instructions */}
            <div className="space-y-3">
              <h4 className="text-xs font-bold text-slate-700 uppercase tracking-wider">
                {language === 'en' ? 'How to Pay via eDahab:' : 'Sida loogu bixiyo eDahab:'}
              </h4>
              <ol className="space-y-2 text-xs text-slate-600 list-decimal pl-4">
                <li>
                  {language === 'en' ? 'Dial' : 'Garaac'} <strong className="font-mono text-slate-900">*789#</strong> {language === 'en' ? 'or *712# or open the Somtel eDahab App.' : 'ama *712# ama fur eDahab App.'}
                </li>
                <li>
                  {language === 'en' ? 'Select' : 'Dooro'} <strong>{language === 'en' ? 'Pay Merchant / Bixi Biil Ganacsi' : 'Bixi Biil Ganacsi'}</strong>.
                </li>
                <li>
                  {language === 'en' ? 'Enter Merchant ID:' : 'Geli Merchant ID:'} <strong className="font-mono text-amber-800 text-xs">{APP_CONFIG.edahabMerchant}</strong>.
                </li>
                <li>
                  {language === 'en' ? 'Enter the Match Fee Amount in USD.' : 'Geli Lacagta Garoonka oo Dollar ah.'}
                </li>
                <li>
                  {language === 'en' ? 'Confirm your PIN to authorize payment.' : 'Geli PIN-kaaga oo xaqiiji.'}
                </li>
                <li>
                  {language === 'en' ? 'Copy the confirmation reference and verify your slot.' : 'Nuuxi koodhka tixraaca si ballantaada loo xiro.'}
                </li>
              </ol>
            </div>
          </div>

          <div className="mt-6 pt-4 border-t border-slate-100 flex items-center justify-between text-xs text-slate-500">
            <span>Somtel Network Supported</span>
            <span className="font-mono font-bold text-amber-700">*789*{APP_CONFIG.edahabMerchant}*AMT#</span>
          </div>
        </div>
      </div>

      {/* Standard Pitch Pricing Matrix Card */}
      <div className="bg-slate-900 text-white rounded-3xl p-6 sm:p-8 shadow-md border border-slate-800">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-6">
          <div>
            <div className="flex items-center gap-2 text-emerald-400 text-xs font-bold uppercase tracking-wider mb-1">
              <Zap className="w-4 h-4" />
              <span>{language === 'en' ? 'Official Arena Pricing Structure' : 'Qiimaha Rasmiga ah ee Garoomada'}</span>
            </div>
            <h3 className="text-xl font-black text-white">
              {language === 'en' ? 'Standard Rates Across All 4 Pitches' : 'Qiimaha Guud ee 4-ta Garoon'}
            </h3>
            <p className="text-xs text-slate-400 mt-1">
              {language === 'en'
                ? 'Pitch 1, Pitch 2, Pitch 3 & Pitch 4 feature unified fair rates with premium LED night floodlights.'
                : 'Garoonka 1, 2, 3 iyo 4 waxay wadaagaan qiime midaysan oo caddaalad ah.'}
            </p>
          </div>

          <div className="flex items-center gap-2.5">
            <button
              onClick={onBookNow}
              className="bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold px-4 py-2 rounded-xl text-xs transition-colors"
            >
              {language === 'en' ? 'Book Match Now' : 'Dalbo Garoon'}
            </button>
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div className="p-5 rounded-2xl bg-slate-800/80 border border-slate-700/80 flex items-start justify-between gap-3">
            <div>
              <div className="flex items-center gap-2 text-amber-400 font-bold text-sm mb-1">
                <span>☀️</span>
                <span>{language === 'en' ? 'Day Game Rate' : 'Ciyaaraha Maalintii'}</span>
              </div>
              <p className="text-xs text-slate-300 mb-2">
                {language === 'en'
                  ? 'All standard day fixtures scheduled from 06:00 AM to 18:00 (6:00 PM).'
                  : 'Dhammaan ballamaha maalintii inta u dhaxaysa 06:00 aroornimo ilaa 18:00 galabnimo.'}
              </p>
              <span className="text-[11px] text-slate-400 font-mono">Applies to: Pitch 1, 2, 3 & 4</span>
            </div>
            <div className="text-right shrink-0">
              <div className="text-3xl font-black text-amber-400 font-sans">$18</div>
              <div className="text-[11px] text-slate-400 font-medium">/ hour</div>
            </div>
          </div>

          <div className="p-5 rounded-2xl bg-slate-800/80 border border-slate-700/80 flex items-start justify-between gap-3">
            <div>
              <div className="flex items-center gap-2 text-emerald-400 font-bold text-sm mb-1">
                <span>🌙</span>
                <span>{language === 'en' ? 'Night Floodlit Rate' : 'Ciyaaraha Habeenkii (Ileys)'}</span>
              </div>
              <p className="text-xs text-slate-300 mb-2">
                {language === 'en'
                  ? 'Prime evening and night fixtures scheduled from 18:00 (6:00 PM) to Midnight under stadium LED towers.'
                  : 'Ciyaaraha fiidka iyo habeenkii ee 18:00 ilaa 12:00 habeenimo iyadoo ileysku shidan yahay.'}
              </p>
              <span className="text-[11px] text-slate-400 font-mono">Applies to: Pitch 1, 2, 3 & 4</span>
            </div>
            <div className="text-right shrink-0">
              <div className="text-3xl font-black text-emerald-400 font-sans">$25</div>
              <div className="text-[11px] text-slate-400 font-medium">/ hour</div>
            </div>
          </div>
        </div>
      </div>

      {/* Official Help & Support Desk Banner */}
      <div className="bg-white rounded-3xl border border-slate-200 p-6 sm:p-8 shadow-sm">
        <div className="flex items-center gap-2 text-emerald-700 font-bold text-xs uppercase tracking-wider mb-2">
          <HelpCircle className="w-4 h-4" />
          <span>{language === 'en' ? 'Need Assistance or Custom Match Booking?' : 'Ma u baahan tahay Caawimaad ama Ballan Gaar ah?'}</span>
        </div>
        <h3 className="text-xl font-bold text-slate-900 mb-2">
          {language === 'en' ? '26 JSC TurfBook Front Desk & Support' : 'Xafiiska Xidhiidhka & Taageerada ee 26 JSC TurfBook'}
        </h3>
        <p className="text-xs sm:text-sm text-slate-600 max-w-2xl mb-6">
          {language === 'en'
            ? 'Our facility management is on site 7 days a week from 06:00 AM to midnight. Contact us for tournament inquiries, corporate leagues, or immediate payment assistance.'
            : 'Maamulka xaruntu waxa uu diyaar yahay 7 maalmood todobaadkii 06:00 aroornimo ilaa 12:00 habeenimo. Nala soo xiriir wixii su\'aalo ah ama tartammo kooxeed ah.'}
        </p>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 text-xs">
          {/* Phone / WhatsApp */}
          <div className="p-4 bg-slate-50 rounded-2xl border border-slate-100">
            <span className="text-[10px] uppercase font-bold text-slate-400 block mb-1">
              Direct Phone & WhatsApp
            </span>
            <div className="flex items-center justify-between">
              <a
                href={`tel:${APP_CONFIG.contactPhone}`}
                className="font-bold text-slate-900 text-sm hover:text-emerald-600 transition-colors font-mono"
              >
                {APP_CONFIG.contactPhone}
              </a>
              <button
                onClick={() => copyToClipboard(APP_CONFIG.contactPhone, setCopiedPhone)}
                className="text-slate-400 hover:text-slate-700"
                title="Copy phone"
              >
                {copiedPhone ? <CheckCircle2 className="w-3.5 h-3.5 text-emerald-600" /> : <Copy className="w-3.5 h-3.5" />}
              </button>
            </div>
            <a
              href={`https://wa.me/${APP_CONFIG.contactPhone.replace(/[^0-9]/g, '')}`}
              target="_blank"
              rel="noreferrer"
              className="text-emerald-700 font-bold text-[11px] mt-2 inline-flex items-center gap-1 hover:underline"
            >
              <MessageSquare className="w-3 h-3" />
              <span>Send WhatsApp Message</span>
            </a>
          </div>

          {/* Email */}
          <div className="p-4 bg-slate-50 rounded-2xl border border-slate-100">
            <span className="text-[10px] uppercase font-bold text-slate-400 block mb-1">
              Official Email
            </span>
            <div className="flex items-center justify-between">
              <a
                href={`mailto:${APP_CONFIG.contactEmail}`}
                className="font-bold text-slate-900 text-xs truncate hover:text-emerald-600 transition-colors"
              >
                {APP_CONFIG.contactEmail}
              </a>
              <button
                onClick={() => copyToClipboard(APP_CONFIG.contactEmail, setCopiedEmail)}
                className="text-slate-400 hover:text-slate-700 ml-1 shrink-0"
                title="Copy email"
              >
                {copiedEmail ? <CheckCircle2 className="w-3.5 h-3.5 text-emerald-600" /> : <Copy className="w-3.5 h-3.5" />}
              </button>
            </div>
            <span className="text-[11px] text-slate-500 mt-2 block">
              Inquiries & League Partnerships
            </span>
          </div>

          {/* Location & Hours */}
          <div className="p-4 bg-slate-50 rounded-2xl border border-slate-100">
            <span className="text-[10px] uppercase font-bold text-slate-400 block mb-1">
              Location & Hours
            </span>
            <span className="font-bold text-slate-900 block truncate">
              {language === 'en' ? APP_CONFIG.location : APP_CONFIG.somaliLocation}
            </span>
            <span className="text-[11px] text-slate-600 mt-1 block">
              {APP_CONFIG.openingHours}
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};
