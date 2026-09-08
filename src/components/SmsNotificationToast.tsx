import React, { useEffect, useState } from 'react';
import { 
  MessageSquare, 
  X, 
  CheckCircle2, 
  Smartphone, 
  ExternalLink,
  Radio
} from 'lucide-react';
import { SimulatedSmsNotification, Language } from '../types';

interface SmsNotificationToastProps {
  notification: SimulatedSmsNotification | null;
  onDismiss: () => void;
  onViewVoucher?: () => void;
  language: Language;
}

export const SmsNotificationToast: React.FC<SmsNotificationToastProps> = ({
  notification,
  onDismiss,
  onViewVoucher,
  language,
}) => {
  const [progress, setProgress] = useState<number>(100);

  useEffect(() => {
    if (!notification) return;
    setProgress(100);

    const interval = setInterval(() => {
      setProgress((prev) => {
        if (prev <= 0) {
          clearInterval(interval);
          onDismiss();
          return 0;
        }
        return prev - 1.25; // ~8 seconds
      });
    }, 100);

    return () => clearInterval(interval);
  }, [notification, onDismiss]);

  if (!notification) return null;

  return (
    <div 
      className="fixed top-4 right-4 z-50 max-w-md w-[calc(100vw-2rem)] animate-in slide-in-from-top-4 fade-in duration-300"
      id="simulated-sms-toast"
      role="alert"
    >
      <div className="bg-slate-900 text-white rounded-2xl shadow-2xl border border-slate-700/80 overflow-hidden ring-1 ring-emerald-500/30">
        {/* Top bar with simulated phone status */}
        <div className="bg-slate-950/80 px-4 py-2 border-b border-slate-800 flex items-center justify-between text-[11px] text-slate-400">
          <div className="flex items-center gap-1.5 text-emerald-400 font-semibold">
            <Radio className="w-3.5 h-3.5 animate-pulse" />
            <span>{language === 'en' ? 'Simulated SMS Gateway Dispatch' : 'Dirista SMS-ka (Telesom/Somtel)'}</span>
          </div>
          <div className="flex items-center gap-2">
            <span className="font-mono text-[10px] bg-slate-800 px-1.5 py-0.5 rounded text-slate-300">
              {notification.gateway}
            </span>
            <button
              onClick={onDismiss}
              className="text-slate-400 hover:text-white p-0.5 rounded transition-colors"
              aria-label="Dismiss simulated SMS"
            >
              <X className="w-3.5 h-3.5" />
            </button>
          </div>
        </div>

        {/* Message Content */}
        <div className="p-4 space-y-3">
          <div className="flex items-start gap-3">
            <div className="w-10 h-10 rounded-xl bg-emerald-600/20 border border-emerald-500/40 text-emerald-400 flex items-center justify-center shrink-0 mt-0.5">
              <Smartphone className="w-5 h-5" />
            </div>

            <div className="flex-1 min-w-0">
              <div className="flex items-center justify-between gap-1">
                <span className="text-xs font-bold text-slate-200 truncate">
                  SMS from: <strong className="text-emerald-400">26-JSC</strong>
                </span>
                <span className="text-[10px] text-slate-400 shrink-0 font-mono">
                  {notification.timestamp}
                </span>
              </div>
              <p className="text-[11px] text-slate-400 truncate">
                To: <span className="font-mono text-slate-200">{notification.recipientPhone}</span> ({notification.recipientName})
              </p>
            </div>
          </div>

          {/* SMS Bubble */}
          <div className="bg-slate-800/90 rounded-xl p-3 border border-slate-700 text-xs text-slate-200 leading-relaxed font-sans relative">
            <div className="flex items-center gap-1.5 text-[10px] font-bold text-emerald-400 uppercase tracking-wider mb-1">
              <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400 shrink-0" />
              <span>{language === 'en' ? 'Automated Confirmation Message' : 'Farriinta Xaqiijinta Ballanta'}</span>
            </div>
            <p className="text-slate-100 font-medium">
              {notification.message}
            </p>
          </div>

          {/* Action buttons */}
          <div className="flex items-center justify-between pt-1 text-xs">
            <span className="text-[10px] text-emerald-400/90 font-medium flex items-center gap-1">
              <span className="w-1.5 h-1.5 rounded-full bg-emerald-400"></span>
              {language === 'en' ? 'Reference Code Delivered' : 'Tixraaca waa la diray'}
            </span>
            <div className="flex items-center gap-2">
              {onViewVoucher && (
                <button
                  type="button"
                  onClick={() => {
                    onViewVoucher();
                    onDismiss();
                  }}
                  className="bg-emerald-600 hover:bg-emerald-500 text-white font-bold px-3 py-1.5 rounded-xl text-xs flex items-center gap-1 transition-colors"
                >
                  <span>{language === 'en' ? 'View Voucher' : 'Eeg Tigidhka'}</span>
                  <ExternalLink className="w-3 h-3" />
                </button>
              )}
              <button
                type="button"
                onClick={onDismiss}
                className="bg-slate-800 hover:bg-slate-700 text-slate-300 px-2.5 py-1.5 rounded-xl text-xs transition-colors"
              >
                {language === 'en' ? 'Dismiss' : 'Xidh'}
              </button>
            </div>
          </div>
        </div>

        {/* Progress timer bar */}
        <div className="h-1 w-full bg-slate-800">
          <div 
            className="h-full bg-emerald-500 transition-all duration-100 ease-linear"
            style={{ width: `${progress}%` }}
          />
        </div>
      </div>
    </div>
  );
};
