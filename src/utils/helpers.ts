import { Booking } from '../types';
import { APP_CONFIG } from '../data/initialData';

export function formatCurrency(amount: number): string {
  return `$${amount.toFixed(0)}`;
}

export function generateBookingReference(): string {
  const rand = Math.floor(1000 + Math.random() * 9000);
  return `JSC-${rand}`;
}

export const generateReferenceCode = generateBookingReference;

export function generateWhatsAppBookingUrl(booking: Booking): string {
  const cleanPhone = APP_CONFIG.contactPhone.replace(/[^0-9]/g, '');
  const text = encodeURIComponent(
    `⚽ *26 JSC TurfBook Booking Request*\n` +
    `Ref: #${booking.referenceCode}\n` +
    `Pitch: ${booking.pitchName}\n` +
    `Date: ${booking.date}\n` +
    `Time: ${booking.startTime} - ${booking.endTime}\n` +
    `Team: ${booking.teamName} (Captain: ${booking.customerName})\n` +
    `Phone: ${booking.customerPhone}\n` +
    `Total: $${booking.totalAmount}\n` +
    `Payment: ${booking.paymentMethod.toUpperCase()} (Merchant: ${booking.merchantNumber})\n` +
    `TID / Ref: ${booking.transactionId || 'Pending Verification'}\n\n` +
    `As-Salaamu Alaykum, please confirm my turf match slot!`
  );
  return `https://wa.me/${cleanPhone}?text=${text}`;
}

export function getZaadUssdCode(amount: number): string {
  return `*880*${APP_CONFIG.zaadMerchant}*${amount}#`;
}

export function getEdahabUssdCode(amount: number): string {
  return `*789*${APP_CONFIG.edahabMerchant}*${amount}#`;
}

export function generateBookingSmsText(data: {
  referenceCode: string;
  teamName: string;
  pitchName: string;
  date: string;
  startTime: string;
  endTime: string;
  totalAmount?: number;
  language?: 'en' | 'so';
}): string {
  const isSomali = data.language === 'so';
  if (isSomali) {
    return `26 JSC TurfBook: Ballantaadu waa la xaqiijiyey! Tixraacaada: #${data.referenceCode}. Kooxda: ${data.teamName}. Garoonka: ${data.pitchName}. Taariikhda: ${data.date} (${data.startTime} - ${data.endTime}). Front Desk: ${APP_CONFIG.contactPhone}. Ku soo dhowow garoonka!`;
  }
  return `26 JSC TurfBook: Booking confirmed! Your Reference Code is #${data.referenceCode}. Team: ${data.teamName}. Pitch: ${data.pitchName}. Date: ${data.date} (${data.startTime} - ${data.endTime}). Front Desk: ${APP_CONFIG.contactPhone}. See you on the pitch!`;
}

/**
 * Checks whether a given slot or start time is a night fixture (18:00 onwards).
 * Night games require stadium floodlights ($25/hr).
 * Day games are before 18:00 ($18/hr).
 */
export function isNightSlot(startTimeOrSlot?: string): boolean {
  if (!startTimeOrSlot) return false;
  const startHourStr = startTimeOrSlot.includes(' - ')
    ? startTimeOrSlot.split(' - ')[0].trim()
    : startTimeOrSlot.trim();
  const hour = parseInt(startHourStr.split(':')[0], 10);
  if (isNaN(hour)) return false;
  return hour >= 18 || hour < 6;
}

/**
 * Calculates the accurate hourly rate for any pitch based on time slot.
 * Day game: $18 / hr
 * Night floodlit game: $25 / hr
 */
export function getPitchSlotRate(
  pitch?: { dayRate?: number; nightRate?: number; hourlyRate?: number } | null,
  startTimeOrSlot?: string
): number {
  if (!pitch) return 25;
  if (startTimeOrSlot && isNightSlot(startTimeOrSlot)) {
    return pitch.nightRate ?? 25;
  }
  return pitch.dayRate ?? 18;
}


