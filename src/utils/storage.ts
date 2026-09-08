import { Booking, Pitch, BlockedSlot, Language, Team, MatchChallenge, GameImage } from '../types';
import { INITIAL_BOOKINGS, INITIAL_PITCHES, INITIAL_TEAMS, INITIAL_CHALLENGES, INITIAL_GALLERY_IMAGES } from '../data/initialData';

const BOOKINGS_KEY = '26jsc_turfbook_bookings';
const PITCHES_KEY = '26jsc_turfbook_pitches';
const BLOCKED_KEY = '26jsc_turfbook_blocked_slots';
const TEAMS_KEY = '26jsc_turfbook_teams';
const CHALLENGES_KEY = '26jsc_turfbook_challenges';
const GALLERY_KEY = '26jsc_turfbook_gallery';
const LANG_KEY = '26jsc_turfbook_language';

// Helper to check new key or fallback to legacy key
function getLegacyOrNew(newKey: string, legacyKey: string): string | null {
  try {
    const val = localStorage.getItem(newKey);
    if (val) return val;
    const legacyVal = localStorage.getItem(legacyKey);
    if (legacyVal) {
      localStorage.setItem(newKey, legacyVal);
      return legacyVal;
    }
    return null;
  } catch {
    return null;
  }
}

export function getStoredBookings(): Booking[] {
  try {
    const raw = getLegacyOrNew(BOOKINGS_KEY, '26jsc_turfboo_bookings');
    if (!raw) {
      localStorage.setItem(BOOKINGS_KEY, JSON.stringify(INITIAL_BOOKINGS));
      return INITIAL_BOOKINGS;
    }
    return JSON.parse(raw);
  } catch {
    return INITIAL_BOOKINGS;
  }
}

export function saveStoredBookings(bookings: Booking[]): void {
  try {
    localStorage.setItem(BOOKINGS_KEY, JSON.stringify(bookings));
  } catch (err) {
    console.error('Failed to save bookings to localStorage', err);
  }
}

export function getStoredPitches(): Pitch[] {
  try {
    const raw = getLegacyOrNew(PITCHES_KEY, '26jsc_turfboo_pitches');
    if (!raw) {
      localStorage.setItem(PITCHES_KEY, JSON.stringify(INITIAL_PITCHES));
      return INITIAL_PITCHES;
    }
    const parsed: Pitch[] = JSON.parse(raw);
    // Ensure all pitches adhere to the updated day/night pricing ($18 Day / $25 Night)
    const migrated = parsed.map((p) => {
      const initial = INITIAL_PITCHES.find((item) => item.id === p.id);
      return {
        ...p,
        dayRate: 18,
        nightRate: 25,
        hourlyRate: 25,
        features: initial ? initial.features : p.features,
      };
    });
    return migrated;
  } catch {
    return INITIAL_PITCHES;
  }
}

export function saveStoredPitches(pitches: Pitch[]): void {
  try {
    localStorage.setItem(PITCHES_KEY, JSON.stringify(pitches));
  } catch (err) {
    console.error('Failed to save pitches to localStorage', err);
  }
}

export function getStoredBlockedSlots(): BlockedSlot[] {
  try {
    const raw = getLegacyOrNew(BLOCKED_KEY, '26jsc_turfboo_blocked_slots');
    if (!raw) return [];
    return JSON.parse(raw);
  } catch {
    return [];
  }
}

export function saveStoredBlockedSlots(slots: BlockedSlot[]): void {
  try {
    localStorage.setItem(BLOCKED_KEY, JSON.stringify(slots));
  } catch (err) {
    console.error('Failed to save blocked slots to localStorage', err);
  }
}

export function getStoredLanguage(): Language {
  try {
    const raw = getLegacyOrNew(LANG_KEY, '26jsc_turfboo_language');
    if (raw === 'so' || raw === 'en') return raw;
    return 'en';
  } catch {
    return 'en';
  }
}

export function saveStoredLanguage(lang: Language): void {
  try {
    localStorage.setItem(LANG_KEY, lang);
  } catch (err) {
    console.error('Failed to save language', err);
  }
}

export function getStoredTeams(): Team[] {
  try {
    const raw = getLegacyOrNew(TEAMS_KEY, '26jsc_turfboo_teams');
    if (!raw) {
      localStorage.setItem(TEAMS_KEY, JSON.stringify(INITIAL_TEAMS));
      return INITIAL_TEAMS;
    }
    return JSON.parse(raw);
  } catch {
    return INITIAL_TEAMS;
  }
}

export function saveStoredTeams(teams: Team[]): void {
  try {
    localStorage.setItem(TEAMS_KEY, JSON.stringify(teams));
  } catch (err) {
    console.error('Failed to save teams', err);
  }
}

export function getStoredChallenges(): MatchChallenge[] {
  try {
    const raw = getLegacyOrNew(CHALLENGES_KEY, '26jsc_turfboo_challenges');
    if (!raw) {
      localStorage.setItem(CHALLENGES_KEY, JSON.stringify(INITIAL_CHALLENGES));
      return INITIAL_CHALLENGES;
    }
    return JSON.parse(raw);
  } catch {
    return INITIAL_CHALLENGES;
  }
}

export function saveStoredChallenges(challenges: MatchChallenge[]): void {
  try {
    localStorage.setItem(CHALLENGES_KEY, JSON.stringify(challenges));
  } catch (err) {
    console.error('Failed to save challenges', err);
  }
}

export function getStoredGalleryImages(): GameImage[] {
  try {
    const raw = getLegacyOrNew(GALLERY_KEY, '26jsc_turfboo_gallery');
    if (!raw) {
      localStorage.setItem(GALLERY_KEY, JSON.stringify(INITIAL_GALLERY_IMAGES));
      return INITIAL_GALLERY_IMAGES;
    }
    return JSON.parse(raw);
  } catch {
    return INITIAL_GALLERY_IMAGES;
  }
}

export function saveStoredGalleryImages(images: GameImage[]): void {
  try {
    localStorage.setItem(GALLERY_KEY, JSON.stringify(images));
  } catch (err) {
    console.error('Failed to save gallery images', err);
  }
}
