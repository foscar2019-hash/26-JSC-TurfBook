import React, { useState } from 'react';
import { 
  MessageSquare, 
  X, 
  Send, 
  Phone, 
  CheckCircle2, 
  Sparkles, 
  Clock,
  ShieldCheck
} from 'lucide-react';
import { APP_CONFIG } from '../data/initialData';
import { Language } from '../types';

interface WhatsAppChatWidgetProps {
  language: Language;
}

export const WhatsAppChatWidget: React.FC<WhatsAppChatWidgetProps> = ({ language }) => {
  const [isOpen, setIsOpen] = useState(false);
  const [message, setMessage] = useState('');

  const cleanPhone = APP_CONFIG.contactPhone.replace(/[^0-9]/g, '');

  const quickPrompts = [
    {
      en: '👋 Hello! I want to check available pitch slots today.',
      so: '👋 Asc! Waxaan rabaa inaan hubiyo saacadaha bannaan maanta.',
    },
    {
      en: `💳 Confirming my payment via Zaad (${APP_CONFIG.zaadMerchant})`,
      so: `💳 Waxaan rabaa inaan xaqiijiyo lacag aan ku diray Zaad (${APP_CONFIG.zaadMerchant})`,
    },
    {
      en: `💳 Confirming my payment via eDahab (${APP_CONFIG.edahabMerchant})`,
      so: `💳 Waxaan rabaa inaan xaqiijiyo lacag aan ku diray eDahab (${APP_CONFIG.edahabMerchant})`,
    },
    {
      en: '⚽ We want to challenge another team for a friendly match!',
      so: '⚽ Waxaan doonaynaa inaan koox kale la ciyaarno ciyaar saaxiibtinimo!',
    },
  ];

  const handleSendWhatsApp = (textToSend?: string) => {
    const text = textToSend || message;
    if (!text.trim()) return;

    const encoded = encodeURIComponent(text);
    const url = `https://wa.me/${cleanPhone}?text=${encoded}`;
    window.open(url, '_blank', 'noopener,noreferrer');
    setMessage('');
  };

  return (
    <div className="fixed bottom-5 right-5 z-40" id="whatsapp-chat-widget">
      {/* Floating Toggle Button */}
      {!isOpen && (
        <button
          onClick={() => setIsOpen(true)}
          className="group flex items-center gap-2.5 bg-emerald-500 hover:bg-emerald-600 text-slate-950 font-bold px-4 py-3 rounded-full shadow-2xl transition-all duration-300 hover:scale-105 active:scale-95 border-2 border-emerald-300"
          id="open-whatsapp-chat-btn"
          title="Chat with 26 JSC TurfBook on WhatsApp"
        >
          <div className="relative">
            <MessageSquare className="w-6 h-6 text-slate-950 fill-current" />
            <span className="absolute -top-1 -right-1 w-3 h-3 bg-white rounded-full border-2 border-emerald-500 animate-pulse"></span>
          </div>
          <div className="text-left hidden sm:block">
            <div className="text-[11px] font-black uppercase tracking-wider text-slate-900 leading-none">
              WhatsApp Desk
            </div>
            <div className="text-[10px] text-emerald-950 font-medium">
              {APP_CONFIG.contactPhone}
            </div>
          </div>
        </button>
      )}

      {/* WhatsApp Chat Drawer / Box */}
      {isOpen && (
        <div className="bg-white rounded-3xl shadow-2xl border border-slate-200 w-[92vw] sm:w-96 overflow-hidden flex flex-col animate-in slide-in-from-bottom-5 duration-200">
          {/* Header */}
          <div className="bg-emerald-600 text-white p-4 flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-2xl bg-white/20 backdrop-blur-sm flex items-center justify-center font-bold text-white border border-white/30">
                <MessageSquare className="w-5 h-5" />
              </div>
              <div>
                <h3 className="font-black text-sm tracking-tight">
                  26 JSC TurfBook Desk
                </h3>
                <div className="flex items-center gap-1.5 text-[11px] text-emerald-100 font-medium">
                  <span className="w-2 h-2 rounded-full bg-emerald-300 animate-ping"></span>
                  <span>Online • {APP_CONFIG.contactPhone}</span>
                </div>
              </div>
            </div>

            <button
              onClick={() => setIsOpen(false)}
              className="w-8 h-8 rounded-full bg-black/10 hover:bg-black/20 flex items-center justify-center text-white transition-colors"
            >
              <X className="w-4 h-4" />
            </button>
          </div>

          {/* Chat Messages Body */}
          <div className="p-4 bg-slate-50 space-y-3 max-h-80 overflow-y-auto text-xs">
            {/* Automated Greeting Bubble */}
            <div className="flex items-start gap-2 max-w-[88%]">
              <div className="w-7 h-7 rounded-full bg-emerald-600 text-white flex items-center justify-center shrink-0 text-[10px] font-bold">
                JSC
              </div>
              <div className="bg-white p-3 rounded-2xl rounded-tl-xs shadow-xs border border-slate-200/80 text-slate-800 space-y-1.5">
                <p className="font-medium leading-relaxed">
                  {language === 'en'
                    ? '👋 Hello! Welcome to 26 JSC TurfBook Support. How can we assist your team today?'
                    : '👋 Asc! Ku soo dhawoow xafiiska 26 JSC TurfBook. Sideen kuugu caawinaa maanta?'}
                </p>
                <div className="text-[10px] text-slate-400 flex items-center gap-2 pt-1 border-t border-slate-100">
                  <span>Zaad: <strong>{APP_CONFIG.zaadMerchant}</strong></span>
                  <span>•</span>
                  <span>eDahab: <strong>{APP_CONFIG.edahabMerchant}</strong></span>
                </div>
              </div>
            </div>

            {/* Quick Prompt Suggestions */}
            <div className="space-y-1.5 pt-1">
              <div className="text-[10px] font-bold uppercase tracking-wider text-slate-400 px-1">
                {language === 'en' ? 'Quick Topics:' : 'Mowduucyo Degdeg ah:'}
              </div>
              {quickPrompts.map((p, idx) => {
                const promptText = language === 'en' ? p.en : p.so;
                return (
                  <button
                    key={idx}
                    onClick={() => handleSendWhatsApp(promptText)}
                    className="w-full text-left bg-white hover:bg-emerald-50 text-slate-700 hover:text-emerald-900 border border-slate-200 hover:border-emerald-300 p-2 rounded-xl text-[11px] font-medium transition-all flex items-center justify-between group shadow-xs"
                  >
                    <span className="truncate pr-2">{promptText}</span>
                    <Send className="w-3 h-3 text-slate-400 group-hover:text-emerald-600 shrink-0" />
                  </button>
                );
              })}
            </div>
          </div>

          {/* Message Input & Action */}
          <div className="p-3 bg-white border-t border-slate-200">
            <div className="flex items-center gap-2">
              <input
                type="text"
                placeholder={language === 'en' ? 'Type your message to WhatsApp...' : 'Qor fariintaada...'}
                value={message}
                onChange={(e) => setMessage(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === 'Enter') handleSendWhatsApp();
                }}
                className="flex-1 bg-slate-100 rounded-xl px-3 py-2 text-xs font-medium text-slate-900 focus:outline-none focus:ring-2 focus:ring-emerald-500"
              />
              <button
                onClick={() => handleSendWhatsApp()}
                disabled={!message.trim()}
                className="bg-emerald-600 disabled:bg-slate-200 hover:bg-emerald-500 text-white disabled:text-slate-400 p-2 rounded-xl transition-all shadow-xs"
                title="Send"
              >
                <Send className="w-4 h-4" />
              </button>
            </div>

            <div className="mt-2 flex items-center justify-between text-[10px] text-slate-500">
              <span>Direct: <strong className="text-emerald-700">{APP_CONFIG.contactPhone}</strong></span>
              <a
                href={`tel:${APP_CONFIG.contactPhone}`}
                className="hover:text-slate-800 flex items-center gap-1 font-semibold text-emerald-600"
              >
                <Phone className="w-3 h-3" />
                <span>Call Us</span>
              </a>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
