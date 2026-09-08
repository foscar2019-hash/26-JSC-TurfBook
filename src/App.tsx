import React, { useState, useEffect } from 'react';
import { 
  Header 
} from './components/Header';
import { PitchesView } from './components/PitchesView';
import { ScheduleMatrix } from './components/ScheduleMatrix';
import { MyBookingsView } from './components/MyBookingsView';
import { TeamsMatchmakingView } from './components/TeamsMatchmakingView';
import { GameImagesPortal } from './components/GameImagesPortal';
import { LiveMapView } from './components/LiveMapView';
import { PaymentGuideView } from './components/PaymentGuideView';
import { AdminDashboard } from './components/AdminDashboard';
import { BookingModal } from './components/BookingModal';
import { BookingTicketModal } from './components/BookingTicketModal';
import { SmsNotificationToast } from './components/SmsNotificationToast';
import { WhatsAppChatWidget } from './components/WhatsAppChatWidget';
import { 
  Pitch, 
  Booking, 
  BlockedSlot, 
  ActiveTab, 
  Language,
  Team,
  TeamMember,
  MatchChallenge,
  GameImage,
  SimulatedSmsNotification
} from './types';
import { generateBookingSmsText } from './utils/helpers';
import { 
  getStoredBookings, 
  saveStoredBookings, 
  getStoredPitches, 
  saveStoredPitches, 
  getStoredBlockedSlots, 
  saveStoredBlockedSlots, 
  getStoredLanguage, 
  saveStoredLanguage,
  getStoredTeams,
  saveStoredTeams,
  getStoredChallenges,
  saveStoredChallenges,
  getStoredGalleryImages,
  saveStoredGalleryImages
} from './utils/storage';
import { APP_CONFIG, TRANSLATIONS } from './data/initialData';
import { 
  Phone, 
  Mail, 
  MapPin, 
  Clock, 
  MessageSquare, 
  CheckCircle2, 
  Trophy, 
  Compass,
  Swords,
  Camera
} from 'lucide-react';

export default function App() {
  const [activeTab, setActiveTab] = useState<ActiveTab>('pitches');
  const [language, setLanguage] = useState<Language>('en');

  // Core Data loaded from persistent storage
  const [pitches, setPitches] = useState<Pitch[]>([]);
  const [bookings, setBookings] = useState<Booking[]>([]);
  const [blockedSlots, setBlockedSlots] = useState<BlockedSlot[]>([]);
  const [teams, setTeams] = useState<Team[]>([]);
  const [challenges, setChallenges] = useState<MatchChallenge[]>([]);
  const [galleryImages, setGalleryImages] = useState<GameImage[]>([]);

  // Booking Modal State
  const [isBookingModalOpen, setIsBookingModalOpen] = useState<boolean>(false);
  const [modalPitchId, setModalPitchId] = useState<string | undefined>(undefined);
  const [modalDate, setModalDate] = useState<string | undefined>(undefined);
  const [modalSlot, setModalSlot] = useState<string | undefined>(undefined);

  // Ticket modal
  const [activeTicket, setActiveTicket] = useState<Booking | null>(null);

  // Toast Notification
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  // Automated SMS Simulation Notification Toast
  const [activeSmsNotification, setActiveSmsNotification] = useState<SimulatedSmsNotification | null>(null);

  const showToast = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 4000);
  };

  const handleSmsDispatched = (sms: SimulatedSmsNotification) => {
    setActiveSmsNotification(sms);
  };

  const handleSimulateSmsForBooking = (b: Booking) => {
    const isTelesom = b.customerPhone.startsWith('+25263') || b.customerPhone.startsWith('063') || b.paymentMethod === 'zaad';
    const smsMsg = b.smsMessagePreview || generateBookingSmsText({
      referenceCode: b.referenceCode,
      teamName: b.teamName,
      pitchName: b.pitchName,
      date: b.date,
      startTime: b.startTime,
      endTime: b.endTime,
      totalAmount: b.totalAmount,
      language,
    });

    setActiveSmsNotification({
      id: `sms-${Date.now()}`,
      bookingRef: b.referenceCode,
      recipientPhone: b.customerPhone,
      recipientName: b.customerName,
      teamName: b.teamName,
      message: smsMsg,
      gateway: isTelesom ? 'Telesom SMS Gateway' : 'Somtel Bulk SMS Gateway',
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      status: 'delivered',
    });
  };

  // Initial load
  useEffect(() => {
    setPitches(getStoredPitches());
    setBookings(getStoredBookings());
    setBlockedSlots(getStoredBlockedSlots());
    setTeams(getStoredTeams());
    setChallenges(getStoredChallenges());
    setGalleryImages(getStoredGalleryImages());
    setLanguage(getStoredLanguage());
  }, []);

  // Sync Language
  const handleSetLanguage = (lang: Language) => {
    setLanguage(lang);
    saveStoredLanguage(lang);
  };

  // Handle new booking
  const handleBookingSuccess = (newBooking: Booking) => {
    const updated = [newBooking, ...bookings];
    setBookings(updated);
    saveStoredBookings(updated);
    setActiveTicket(newBooking);
    showToast(
      language === 'en'
        ? `Booking #${newBooking.referenceCode} confirmed for ${newBooking.teamName}!`
        : `Ballanta #${newBooking.referenceCode} waa la xaqiijiyey ee kooxda ${newBooking.teamName}!`
    );
  };

  // Handle cancel booking
  const handleCancelBooking = (bookingId: string) => {
    const updated = bookings.filter((b) => b.id !== bookingId);
    setBookings(updated);
    saveStoredBookings(updated);
    showToast(language === 'en' ? 'Booking cancelled successfully.' : 'Ballanta waa la tirtiray.');
  };

  // Teams & Matchmaking Handlers
  const handleAddTeam = (newTeam: Team) => {
    const updated = [...teams, newTeam];
    setTeams(updated);
    saveStoredTeams(updated);
    showToast(
      language === 'en'
        ? `Club "${newTeam.name}" registered successfully!`
        : `Kooxda "${newTeam.name}" si guul leh ayaa loo diiwaangeliyey!`
    );
  };

  const handleAddMemberToTeam = (teamId: string, member: TeamMember) => {
    const updated = teams.map((t) => {
      if (t.id === teamId) {
        return {
          ...t,
          members: [...t.members, member],
        };
      }
      return t;
    });
    setTeams(updated);
    saveStoredTeams(updated);
    showToast(
      language === 'en'
        ? `Player ${member.name} (#${member.jerseyNumber}) added to squad!`
        : `Ciyaartoyga ${member.name} ayaa lagu daray liiska xubnaha kooxda!`
    );
  };

  const handleCreateChallenge = (newChallenge: MatchChallenge) => {
    const updated = [newChallenge, ...challenges];
    setChallenges(updated);
    saveStoredChallenges(updated);
    showToast(
      language === 'en'
        ? `Challenge sent to ${newChallenge.challengedTeamName} for ${newChallenge.date} (${newChallenge.slot})!`
        : `Dalabka ciyaarta waxaa loo diray ${newChallenge.challengedTeamName} ee ${newChallenge.date}!`
    );
  };

  const handleAcceptChallenge = (challengeId: string) => {
    const updated = challenges.map((ch) =>
      ch.id === challengeId ? { ...ch, status: 'accepted' as const } : ch
    );
    setChallenges(updated);
    saveStoredChallenges(updated);
    showToast(
      language === 'en'
        ? 'Both teams agreed on match time! Click "Confirm & Book Turf" to lock the pitch.'
        : 'Labada kooxood way ku heshiiyeen waqtiga ciyaarta! Guji "Xaqiiji & Dalbo Garoonka".'
    );
  };

  const handleBookAgreedMatch = (challenge: MatchChallenge, booking: Booking) => {
    // Add to bookings
    const updatedBookings = [booking, ...bookings];
    setBookings(updatedBookings);
    saveStoredBookings(updatedBookings);

    // Update challenge status to booked
    const updatedChallenges = challenges.map((ch) =>
      ch.id === challenge.id ? { ...ch, status: 'booked' as const, bookingRef: booking.referenceCode } : ch
    );
    setChallenges(updatedChallenges);
    saveStoredChallenges(updatedChallenges);

    // Show ticket
    setActiveTicket(booking);
    showToast(
      language === 'en'
        ? `Match locked into schedule (#${booking.referenceCode})! Pay via Zaad 445686 or eDahab 10136.`
        : `Ciyaarta waa la jadwaleeyay (#${booking.referenceCode})! Ku bixi Zaad 445686 ama eDahab 10136.`
    );
  };

  // Gallery Handlers
  const handleAddGalleryImage = (newImage: GameImage) => {
    const updated = [newImage, ...galleryImages];
    setGalleryImages(updated);
    saveStoredGalleryImages(updated);
    showToast(language === 'en' ? 'Match photo published to gallery!' : 'Sawirka waa la daabacay!');
  };

  const handleLikeGalleryImage = (imageId: string) => {
    const updated = galleryImages.map((img) =>
      img.id === imageId ? { ...img, likes: img.likes + 1 } : img
    );
    setGalleryImages(updated);
    saveStoredGalleryImages(updated);
  };

  // Admin: update payment status
  const handleUpdateBookingStatus = (bookingId: string, status: 'paid' | 'pending_verification') => {
    const updated = bookings.map((b) => (b.id === bookingId ? { ...b, paymentStatus: status } : b));
    setBookings(updated);
    saveStoredBookings(updated);
    showToast(`Booking marked as ${status}.`);
  };

  // Admin: delete booking
  const handleDeleteBooking = (bookingId: string) => {
    const updated = bookings.filter((b) => b.id !== bookingId);
    setBookings(updated);
    saveStoredBookings(updated);
    showToast('Booking deleted.');
  };

  // Admin: add walk-in booking
  const handleAddWalkinBooking = (booking: Booking) => {
    const updated = [booking, ...bookings];
    setBookings(updated);
    saveStoredBookings(updated);
    showToast(`Walk-in recorded for ${booking.teamName} (#${booking.referenceCode})`);
  };

  // Admin: block slot
  const handleBlockSlot = (slot: BlockedSlot) => {
    const updated = [...blockedSlots, slot];
    setBlockedSlots(updated);
    saveStoredBlockedSlots(updated);
    showToast('Slot blocked successfully.');
  };

  // Admin: unblock slot
  const handleUnblockSlot = (slotId: string) => {
    const updated = blockedSlots.filter((bs) => bs.id !== slotId);
    setBlockedSlots(updated);
    saveStoredBlockedSlots(updated);
    showToast('Slot unblocked.');
  };

  // Admin: toggle pitch status
  const handleTogglePitchStatus = (pitchId: string, status: 'available' | 'maintenance') => {
    const updated = pitches.map((p) => (p.id === pitchId ? { ...p, status } : p));
    setPitches(updated);
    saveStoredPitches(updated);
    showToast(`Pitch status updated to ${status}.`);
  };

  // Quick book trigger
  const handleOpenBookForPitch = (pitch: Pitch) => {
    setModalPitchId(pitch.id);
    setModalDate(undefined);
    setModalSlot(undefined);
    setIsBookingModalOpen(true);
  };

  // Trigger from Schedule Matrix
  const handleSelectSlotFromSchedule = (pitchId: string, date: string, slot: string) => {
    setModalPitchId(pitchId);
    setModalDate(date);
    setModalSlot(slot);
    setIsBookingModalOpen(true);
  };

  return (
    <div className="min-h-screen flex flex-col bg-slate-100/60 font-sans text-slate-900">
      {/* Toast Notification Popup */}
      {toastMessage && (
        <div className="fixed bottom-6 left-6 z-50 bg-slate-900 text-white px-4 py-3 rounded-2xl shadow-2xl border border-slate-700 text-xs sm:text-sm font-semibold flex items-center gap-2.5 animate-in slide-in-from-bottom-3 duration-300">
          <CheckCircle2 className="w-5 h-5 text-emerald-400 shrink-0" />
          <span>{toastMessage}</span>
        </div>
      )}

      {/* Main Top Header */}
      <Header
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        language={language}
        setLanguage={handleSetLanguage}
        onOpenQuickBook={() => {
          setModalPitchId(pitches[0]?.id);
          setIsBookingModalOpen(true);
        }}
        bookingCount={bookings.length}
      />

      {/* Main Content Area */}
      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 py-6 sm:py-8">
        {activeTab === 'pitches' && (
          <PitchesView
            pitches={pitches}
            language={language}
            onBookPitch={handleOpenBookForPitch}
            onViewSchedule={(pitchId) => {
              setActiveTab('schedule');
            }}
            onOpenPaymentInfo={() => setActiveTab('payment-info')}
          />
        )}

        {activeTab === 'schedule' && (
          <ScheduleMatrix
            pitches={pitches}
            bookings={bookings}
            blockedSlots={blockedSlots}
            onSelectSlot={handleSelectSlotFromSchedule}
            language={language}
          />
        )}

        {activeTab === 'my-bookings' && (
          <MyBookingsView
            bookings={bookings}
            onViewTicket={(b) => setActiveTicket(b)}
            onCancelBooking={handleCancelBooking}
            onOpenBookPitch={() => {
              setModalPitchId(pitches[0]?.id);
              setIsBookingModalOpen(true);
            }}
            onSimulateSms={handleSimulateSmsForBooking}
            language={language}
          />
        )}

        {activeTab === 'teams' && (
          <TeamsMatchmakingView
            teams={teams}
            challenges={challenges}
            pitches={pitches}
            onAddTeam={handleAddTeam}
            onAddMemberToTeam={handleAddMemberToTeam}
            onCreateChallenge={handleCreateChallenge}
            onAcceptChallenge={handleAcceptChallenge}
            onBookAgreedMatch={handleBookAgreedMatch}
            language={language}
          />
        )}

        {activeTab === 'gallery' && (
          <GameImagesPortal
            images={galleryImages}
            onAddImage={handleAddGalleryImage}
            onLikeImage={handleLikeGalleryImage}
            language={language}
          />
        )}

        {activeTab === 'live-map' && (
          <LiveMapView
            pitches={pitches}
            onBookPitch={handleOpenBookForPitch}
            language={language}
          />
        )}

        {activeTab === 'payment-info' && (
          <PaymentGuideView
            language={language}
            onBookNow={() => {
              setModalPitchId(pitches[0]?.id);
              setIsBookingModalOpen(true);
            }}
          />
        )}

        {activeTab === 'admin' && (
          <AdminDashboard
            bookings={bookings}
            pitches={pitches}
            blockedSlots={blockedSlots}
            onUpdateBookingStatus={handleUpdateBookingStatus}
            onDeleteBooking={handleDeleteBooking}
            onAddWalkinBooking={handleAddWalkinBooking}
            onBlockSlot={handleBlockSlot}
            onUnblockSlot={handleUnblockSlot}
            onTogglePitchStatus={handleTogglePitchStatus}
            language={language}
          />
        )}
      </main>

      {/* Floating Interactive WhatsApp Chat Assistant */}
      <WhatsAppChatWidget language={language} />

      {/* Simulated Automated SMS Notification Toast */}
      {activeSmsNotification && (
        <SmsNotificationToast
          notification={activeSmsNotification}
          onDismiss={() => setActiveSmsNotification(null)}
          onViewBookingVoucher={(refCode) => {
            const match = bookings.find((b) => b.referenceCode.toLowerCase() === refCode.toLowerCase());
            if (match) {
              setActiveTicket(match);
            }
          }}
          language={language}
        />
      )}

      {/* Booking Wizard Modal */}
      {isBookingModalOpen && (
        <BookingModal
          isOpen={isBookingModalOpen}
          onClose={() => setIsBookingModalOpen(false)}
          pitches={pitches}
          selectedPitchId={modalPitchId}
          initialDate={modalDate}
          initialSlot={modalSlot}
          existingBookings={bookings}
          blockedSlots={blockedSlots}
          onBookingSuccess={handleBookingSuccess}
          onSmsDispatched={handleSmsDispatched}
          language={language}
        />
      )}

      {/* Ticket Pass Modal */}
      {activeTicket && (
        <BookingTicketModal
          booking={activeTicket}
          onClose={() => setActiveTicket(null)}
          onResendSms={handleSimulateSmsForBooking}
          language={language}
        />
      )}

      {/* Footer */}
      <footer className="bg-slate-900 text-white border-t border-slate-800 mt-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 py-10">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-8 mb-8 text-xs">
            {/* Column 1: Brand */}
            <div className="space-y-3 md:col-span-1">
              <div className="flex items-center gap-2">
                <div className="w-8 h-8 rounded-lg bg-emerald-600 flex items-center justify-center text-white font-bold">
                  <Trophy className="w-4 h-4" />
                </div>
                <span className="text-lg font-black tracking-tight">{APP_CONFIG.appName}</span>
              </div>
              <p className="text-slate-400 leading-relaxed">
                {language === 'en' ? APP_CONFIG.tagline : APP_CONFIG.somaliTagline}
              </p>
              <div className="pt-1 flex items-center gap-2 font-mono">
                <span className="bg-emerald-500/20 text-emerald-400 px-2 py-0.5 rounded text-[11px] font-bold border border-emerald-500/30">
                  Zaad: {APP_CONFIG.zaadMerchant}
                </span>
                <span className="bg-amber-500/20 text-amber-300 px-2 py-0.5 rounded text-[11px] font-bold border border-amber-500/30">
                  eDahab: {APP_CONFIG.edahabMerchant}
                </span>
              </div>
            </div>

            {/* Column 2: Quick Links */}
            <div>
              <h4 className="font-bold uppercase tracking-wider text-slate-300 mb-3 text-[11px]">
                {language === 'en' ? 'Features & Portals' : 'Qaybaha Xarunta'}
              </h4>
              <ul className="space-y-2 text-slate-400">
                <li>
                  <button onClick={() => setActiveTab('pitches')} className="hover:text-emerald-400 transition-colors">
                    {language === 'en' ? 'Pitches & Pricing' : 'Garoomada & Qiimaha'}
                  </button>
                </li>
                <li>
                  <button onClick={() => setActiveTab('teams')} className="hover:text-emerald-400 transition-colors flex items-center gap-1">
                    <Swords className="w-3 h-3 text-emerald-400" />
                    <span>{language === 'en' ? 'Teams & Match Challenges' : 'Kooxaha & Tartanka'}</span>
                  </button>
                </li>
                <li>
                  <button onClick={() => setActiveTab('gallery')} className="hover:text-emerald-400 transition-colors flex items-center gap-1">
                    <Camera className="w-3 h-3 text-emerald-400" />
                    <span>{language === 'en' ? 'Match Highlights Gallery' : 'Sawirrada Ciyaaraha'}</span>
                  </button>
                </li>
                <li>
                  <button onClick={() => setActiveTab('live-map')} className="hover:text-emerald-400 transition-colors flex items-center gap-1">
                    <Compass className="w-3 h-3 text-emerald-400" />
                    <span>{language === 'en' ? 'Live Arena Map & GPS' : 'Khariidadda Garoonka'}</span>
                  </button>
                </li>
                <li>
                  <button onClick={() => setActiveTab('my-bookings')} className="hover:text-emerald-400 transition-colors">
                    {language === 'en' ? 'Lookup Bookings' : 'Raadi Ballantaada'}
                  </button>
                </li>
              </ul>
            </div>

            {/* Column 3: Contact & Booking Desk */}
            <div>
              <h4 className="font-bold uppercase tracking-wider text-slate-300 mb-3 text-[11px]">
                {language === 'en' ? 'Direct Contact' : 'Nala Soo Xidhiidh'}
              </h4>
              <ul className="space-y-2 text-slate-400">
                <li>
                  <a href={`tel:${APP_CONFIG.contactPhone}`} className="hover:text-emerald-400 flex items-center gap-2 font-mono font-bold">
                    <Phone className="w-3.5 h-3.5 text-emerald-400 shrink-0" />
                    <span>{APP_CONFIG.contactPhone}</span>
                  </a>
                </li>
                <li>
                  <a href={`mailto:${APP_CONFIG.contactEmail}`} className="hover:text-emerald-400 flex items-center gap-2">
                    <Mail className="w-3.5 h-3.5 text-emerald-400 shrink-0" />
                    <span>{APP_CONFIG.contactEmail}</span>
                  </a>
                </li>
                <li>
                  <a 
                    href={`https://wa.me/${APP_CONFIG.contactPhone.replace(/[^0-9]/g, '')}`} 
                    target="_blank" 
                    rel="noreferrer" 
                    className="hover:text-emerald-400 flex items-center gap-2 font-bold text-emerald-400"
                  >
                    <MessageSquare className="w-3.5 h-3.5 shrink-0" />
                    <span>WhatsApp Desk (+252633347832)</span>
                  </a>
                </li>
              </ul>
            </div>

            {/* Column 4: Arena Hours & District */}
            <div>
              <h4 className="font-bold uppercase tracking-wider text-slate-300 mb-3 text-[11px]">
                {language === 'en' ? 'Arena Location & Hours' : 'Goobta & Saacadaha'}
              </h4>
              <div className="space-y-2 text-slate-400">
                <div className="flex items-start gap-2">
                  <MapPin className="w-3.5 h-3.5 text-emerald-400 mt-0.5 shrink-0" />
                  <span>{language === 'en' ? APP_CONFIG.location : APP_CONFIG.somaliLocation}</span>
                </div>
                <div className="flex items-center gap-2">
                  <Clock className="w-3.5 h-3.5 text-amber-400 shrink-0" />
                  <span>{APP_CONFIG.openingHours}</span>
                </div>
                <div className="pt-1">
                  <button
                    onClick={() => setActiveTab('live-map')}
                    className="text-emerald-400 hover:text-emerald-300 underline font-semibold flex items-center gap-1"
                  >
                    <Compass className="w-3 h-3" />
                    <span>View interactive GPS map →</span>
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div className="border-t border-slate-800 pt-6 flex flex-col sm:flex-row items-center justify-between text-xs text-slate-500 gap-3">
            <p>© {new Date().getFullYear()} {APP_CONFIG.appName}. All rights reserved.</p>
            <div className="flex items-center gap-4">
              <span>Zaad Merchant: <strong>{APP_CONFIG.zaadMerchant}</strong></span>
              <span>•</span>
              <span>eDahab Merchant: <strong>{APP_CONFIG.edahabMerchant}</strong></span>
              <span>•</span>
              <span>Hargeisa, Somaliland</span>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
}
