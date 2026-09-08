export type SurfaceType = 'FIFA Synthetic Turf' | 'High-Density AstroTurf' | 'Shock-Pad Pro Turf' | 'Indoor Futsal Rubber Turf';

export type GameFormat = '5-a-side' | '6-a-side' | '7-a-side' | 'Futsal' | 'Training';

export interface Pitch {
  id: string;
  name: string;
  somaliName: string;
  format: GameFormat;
  surface: SurfaceType;
  hourlyRate: number; // default rate (Night $25 / Day $18)
  dayRate: number; // Day game rate in USD ($18)
  nightRate: number; // Night floodlight rate in USD ($25)
  dimensions: string;
  capacity: string;
  features: string[];
  imageUrl: string;
  isFloodlit: boolean;
  status: 'available' | 'maintenance' | 'busy';
}

export interface AddOnItem {
  id: string;
  name: string;
  somaliName: string;
  price: number; // in USD
  description: string;
  iconName: string;
}

export type PaymentMethod = 'zaad' | 'edahab' | 'cash';

export interface Booking {
  id: string;
  referenceCode: string; // e.g. JSC-9281
  pitchId: string;
  pitchName: string;
  date: string; // YYYY-MM-DD
  startTime: string; // e.g. "18:00"
  endTime: string; // e.g. "19:00"
  durationHours: number;
  customerName: string;
  teamName: string;
  customerPhone: string;
  customerEmail: string;
  paymentMethod: PaymentMethod;
  paymentStatus: 'paid' | 'pending_verification' | 'unpaid';
  transactionId?: string;
  merchantNumber: string; // "445686" for Zaad, "10136" for eDahab
  totalAmount: number; // in USD
  addOns: string[]; // array of AddOnItem IDs
  notes?: string;
  createdAt: string;
  smsConfirmed?: boolean;
  smsDispatchedAt?: string;
  smsMessagePreview?: string;
}

export interface SimulatedSmsNotification {
  id: string;
  bookingRef: string;
  recipientPhone: string;
  recipientName: string;
  teamName: string;
  message: string;
  gateway: string;
  timestamp: string;
  status: 'sent' | 'delivered';
}

export interface BlockedSlot {
  id: string;
  pitchId: string;
  date: string;
  startTime: string;
  endTime: string;
  reason: string;
}

export interface TeamMember {
  id: string;
  name: string;
  position: 'Forward' | 'Midfielder' | 'Defender' | 'Goalkeeper';
  jerseyNumber: number;
  isCaptain?: boolean;
}

export interface Team {
  id: string;
  name: string;
  logoEmoji: string;
  color: string;
  captainName: string;
  captainPhone: string;
  preferredFormat: GameFormat;
  skillLevel: 'Casual' | 'Competitive' | 'Semi-Pro';
  members: TeamMember[];
  stats: {
    matchesPlayed: number;
    wins: number;
    draws: number;
    losses: number;
  };
  bio?: string;
  joinedDate: string;
}

export interface MatchChallenge {
  id: string;
  challengerTeamId: string;
  challengerTeamName: string;
  challengerCaptainPhone: string;
  challengedTeamId: string;
  challengedTeamName: string;
  challengedCaptainPhone: string;
  pitchId: string;
  pitchName: string;
  date: string;
  slot: string;
  totalAmount: number;
  splitMode: '50-50' | 'challenger-covers' | 'loser-pays';
  status: 'pending_opponent' | 'accepted' | 'declined' | 'booked';
  notes?: string;
  bookingRef?: string;
  createdAt: string;
}

export interface GameImage {
  id: string;
  title: string;
  somaliTitle: string;
  category: 'Match Action' | 'Night Floodlights' | 'Tournament' | 'Celebration';
  imageUrl: string;
  date: string;
  pitchName: string;
  teamsInvolved?: string;
  likes: number;
}

export type Language = 'en' | 'so';

export type ActiveTab = 
  | 'pitches' 
  | 'schedule' 
  | 'my-bookings' 
  | 'teams' 
  | 'gallery' 
  | 'live-map' 
  | 'payment-info' 
  | 'admin';
