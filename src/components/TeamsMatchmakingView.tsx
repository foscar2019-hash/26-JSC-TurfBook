import React, { useState } from 'react';
import { 
  Users, 
  Trophy, 
  Swords, 
  Plus, 
  Calendar, 
  Clock, 
  ShieldCheck, 
  MessageSquare, 
  Phone, 
  CheckCircle2, 
  AlertCircle, 
  ChevronRight, 
  UserCheck, 
  Sparkles,
  X,
  CreditCard
} from 'lucide-react';
import { 
  Team, 
  TeamMember, 
  MatchChallenge, 
  Pitch, 
  Language, 
  Booking 
} from '../types';
import { APP_CONFIG, TIME_SLOTS } from '../data/initialData';
import { generateReferenceCode } from '../utils/helpers';

interface TeamsMatchmakingViewProps {
  teams: Team[];
  challenges: MatchChallenge[];
  pitches: Pitch[];
  onAddTeam: (team: Team) => void;
  onAddMemberToTeam: (teamId: string, member: TeamMember) => void;
  onCreateChallenge: (challenge: MatchChallenge) => void;
  onAcceptChallenge: (challengeId: string) => void;
  onBookAgreedMatch: (challenge: MatchChallenge, booking: Booking) => void;
  language: Language;
}

export const TeamsMatchmakingView: React.FC<TeamsMatchmakingViewProps> = ({
  teams,
  challenges,
  pitches,
  onAddTeam,
  onAddMemberToTeam,
  onCreateChallenge,
  onAcceptChallenge,
  onBookAgreedMatch,
  language,
}) => {
  const [subTab, setSubTab] = useState<'challenges' | 'teams'>('challenges');
  const [selectedTeamId, setSelectedTeamId] = useState<string | null>(null);

  // Challenge Wizard Modal State
  const [isChallengeModalOpen, setIsChallengeModalOpen] = useState(false);
  const [challengerTeamId, setChallengerTeamId] = useState<string>(teams[0]?.id || '');
  const [challengedTeamId, setChallengedTeamId] = useState<string>(teams[1]?.id || '');
  const [selectedPitchId, setSelectedPitchId] = useState<string>(pitches[0]?.id || '');
  const [matchDate, setMatchDate] = useState<string>(
    new Date(Date.now() + 86400000).toISOString().split('T')[0]
  );
  const [matchSlot, setMatchSlot] = useState<string>('20:00 - 21:00');
  const [splitMode, setSplitMode] = useState<'50-50' | 'challenger-covers' | 'loser-pays'>('50-50');
  const [challengeNotes, setChallengeNotes] = useState<string>('');

  // Register Team Modal State
  const [isRegisterTeamOpen, setIsRegisterTeamOpen] = useState(false);
  const [newTeamName, setNewTeamName] = useState('');
  const [newCaptainName, setNewCaptainName] = useState('');
  const [newCaptainPhone, setNewCaptainPhone] = useState('+252');
  const [newFormat, setNewFormat] = useState<any>('7-a-side');
  const [newSkillLevel, setNewSkillLevel] = useState<'Casual' | 'Competitive' | 'Semi-Pro'>('Competitive');
  const [newEmoji, setNewEmoji] = useState('⚽');
  const [newBio, setNewBio] = useState('');

  // Add Member Modal State
  const [activeAddMemberTeamId, setActiveAddMemberTeamId] = useState<string | null>(null);
  const [memberName, setMemberName] = useState('');
  const [memberPosition, setMemberPosition] = useState<'Forward' | 'Midfielder' | 'Defender' | 'Goalkeeper'>('Midfielder');
  const [memberJersey, setMemberJersey] = useState<number>(10);

  const selectedPitch = pitches.find((p) => p.id === selectedPitchId) || pitches[0];

  const handleOpenChallenge = (targetOpponentId?: string) => {
    if (targetOpponentId) {
      setChallengedTeamId(targetOpponentId);
      const other = teams.find((t) => t.id !== targetOpponentId);
      if (other) setChallengerTeamId(other.id);
    }
    setIsChallengeModalOpen(true);
  };

  const handleCreateChallengeSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (challengerTeamId === challengedTeamId) {
      alert(language === 'en' ? 'Please select two different teams.' : 'Fadlan dooro laba kooxood oo kala duwan.');
      return;
    }

    const challenger = teams.find((t) => t.id === challengerTeamId);
    const challenged = teams.find((t) => t.id === challengedTeamId);
    if (!challenger || !challenged || !selectedPitch) return;

    const newChallenge: MatchChallenge = {
      id: `ch-${Date.now()}`,
      challengerTeamId: challenger.id,
      challengerTeamName: challenger.name,
      challengerCaptainPhone: challenger.captainPhone,
      challengedTeamId: challenged.id,
      challengedTeamName: challenged.name,
      challengedCaptainPhone: challenged.captainPhone,
      pitchId: selectedPitch.id,
      pitchName: selectedPitch.name,
      date: matchDate,
      slot: matchSlot,
      totalAmount: selectedPitch.hourlyRate,
      splitMode,
      status: 'pending_opponent',
      notes: challengeNotes.trim() || undefined,
      createdAt: new Date().toISOString(),
    };

    onCreateChallenge(newChallenge);
    setIsChallengeModalOpen(false);
    setChallengeNotes('');
  };

  const handleRegisterTeamSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newTeamName.trim() || !newCaptainName.trim()) return;

    const newTeam: Team = {
      id: `team-${Date.now()}`,
      name: newTeamName.trim(),
      logoEmoji: newEmoji,
      color: '#059669',
      captainName: newCaptainName.trim(),
      captainPhone: newCaptainPhone.trim(),
      preferredFormat: newFormat,
      skillLevel: newSkillLevel,
      bio: newBio.trim() || undefined,
      joinedDate: new Date().toISOString().split('T')[0],
      stats: {
        matchesPlayed: 0,
        wins: 0,
        draws: 0,
        losses: 0,
      },
      members: [
        {
          id: `m-${Date.now()}-cap`,
          name: `${newCaptainName.trim()} (C)`,
          position: 'Midfielder',
          jerseyNumber: 10,
          isCaptain: true,
        },
      ],
    };

    onAddTeam(newTeam);
    setIsRegisterTeamOpen(false);
    setNewTeamName('');
    setNewCaptainName('');
    setNewCaptainPhone('+252');
    setNewBio('');
  };

  const handleAddMemberSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!activeAddMemberTeamId || !memberName.trim()) return;

    const newMember: TeamMember = {
      id: `m-${Date.now()}`,
      name: memberName.trim(),
      position: memberPosition,
      jerseyNumber: Number(memberJersey) || 9,
    };

    onAddMemberToTeam(activeAddMemberTeamId, newMember);
    setActiveAddMemberTeamId(null);
    setMemberName('');
  };

  const handleBookAgreedFixture = (challenge: MatchChallenge) => {
    const pitch = pitches.find((p) => p.id === challenge.pitchId) || pitches[0];
    const [startTime, endTime] = challenge.slot.split(' - ');
    const refCode = generateReferenceCode();

    const booking: Booking = {
      id: `booking-versus-${Date.now()}`,
      referenceCode: refCode,
      pitchId: challenge.pitchId,
      pitchName: challenge.pitchName,
      date: challenge.date,
      startTime: startTime || '20:00',
      endTime: endTime || '21:00',
      durationHours: 1,
      customerName: `${challenge.challengerTeamName} vs ${challenge.challengedTeamName}`,
      teamName: `${challenge.challengerTeamName} vs ${challenge.challengedTeamName}`,
      customerPhone: challenge.challengerCaptainPhone,
      customerEmail: APP_CONFIG.contactEmail,
      paymentMethod: 'zaad',
      paymentStatus: 'pending_verification',
      merchantNumber: APP_CONFIG.zaadMerchant,
      totalAmount: challenge.totalAmount,
      addOns: [],
      notes: `Versus Match: ${challenge.challengerTeamName} vs ${challenge.challengedTeamName}. Split mode: ${challenge.splitMode}. ${challenge.notes || ''}`,
      createdAt: new Date().toISOString(),
    };

    onBookAgreedMatch(challenge, booking);
  };

  return (
    <div className="space-y-6" id="teams-matchmaking-view">
      {/* Header Banner */}
      <div className="bg-white p-5 sm:p-6 rounded-3xl border border-slate-200/90 shadow-sm flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div className="inline-flex items-center gap-1.5 bg-emerald-50 text-emerald-700 px-2.5 py-0.5 rounded-full text-xs font-bold mb-1.5 border border-emerald-200/60">
            <Swords className="w-3.5 h-3.5" />
            <span>{language === 'en' ? 'Teams & Versus Matchmaking' : 'Kooxaha & Tartanka Ciyaaraha'}</span>
          </div>
          <h2 className="text-xl sm:text-2xl font-black text-slate-900 tracking-tight">
            {language === 'en' ? 'Challenge Other Teams & Agree Match Times' : 'U Dir Koox Tartan oo Doorta Waqtiga'}
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            {language === 'en'
              ? 'Browse registered football clubs, inspect membership rosters, challenge squads to fixtures, and split turf fees.'
              : 'Eeg dhammaan kooxaha diiwaangashan iyo xubnahooda, dooro koox aad la ciyaarto saacadda aad ku heshiisaan.'}
          </p>
        </div>

        {/* Action CTAs */}
        <div className="flex flex-wrap items-center gap-2">
          <button
            onClick={() => handleOpenChallenge()}
            className="flex items-center gap-2 bg-emerald-600 hover:bg-emerald-500 text-white font-bold px-4 py-2.5 rounded-2xl text-xs transition-all shadow-md active:scale-98"
            id="create-challenge-btn"
          >
            <Swords className="w-4 h-4" />
            <span>{language === 'en' ? 'Challenge a Team' : 'U Dir Tartan / Ciyaar'}</span>
          </button>

          <button
            onClick={() => setIsRegisterTeamOpen(true)}
            className="flex items-center gap-2 bg-slate-900 hover:bg-slate-800 text-white font-bold px-4 py-2.5 rounded-2xl text-xs transition-all shadow-sm active:scale-98"
            id="register-new-team-btn"
          >
            <Plus className="w-4 h-4" />
            <span>{language === 'en' ? 'Register New Team' : 'Diiwaangeli Koox Cusub'}</span>
          </button>
        </div>
      </div>

      {/* Sub-tab Navigation */}
      <div className="flex items-center gap-2 border-b border-slate-200 pb-2">
        <button
          onClick={() => setSubTab('challenges')}
          className={`flex items-center gap-2 px-4 py-2 rounded-2xl text-xs font-bold transition-all ${
            subTab === 'challenges'
              ? 'bg-slate-900 text-white shadow-xs'
              : 'bg-white text-slate-600 border border-slate-200 hover:bg-slate-50'
          }`}
        >
          <Swords className="w-3.5 h-3.5" />
          <span>{language === 'en' ? 'Versus Matches & Challenges' : 'Ciyaaraha & Tartamada'}</span>
          <span className="bg-emerald-500 text-slate-950 px-1.5 py-0.2 rounded-full text-[10px] font-black">
            {challenges.length}
          </span>
        </button>

        <button
          onClick={() => setSubTab('teams')}
          className={`flex items-center gap-2 px-4 py-2 rounded-2xl text-xs font-bold transition-all ${
            subTab === 'teams'
              ? 'bg-slate-900 text-white shadow-xs'
              : 'bg-white text-slate-600 border border-slate-200 hover:bg-slate-50'
          }`}
        >
          <Users className="w-3.5 h-3.5" />
          <span>{language === 'en' ? 'Clubs & Membership List' : 'Kooxaha & Liiska Xubnaha'}</span>
          <span className="bg-slate-200 text-slate-700 px-1.5 py-0.2 rounded-full text-[10px] font-black">
            {teams.length}
          </span>
        </button>
      </div>

      {/* SUB-TAB 1: VERSUS MATCHES & CHALLENGES */}
      {subTab === 'challenges' && (
        <div className="space-y-4">
          <div className="flex items-center justify-between text-xs text-slate-500 px-1">
            <span>
              {language === 'en'
                ? 'Teams propose matches, agree on turf time, and finalize pitch booking with Zaad 445686 or eDahab 10136.'
                : 'Kooxuhu waxay ku heshiin karaan saacadda iyo garoonka, ka dibna waxaa toos loogu bixin karaa Zaad ama eDahab.'}
            </span>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {challenges.map((ch) => {
              const feePerTeam = (ch.totalAmount / 2).toFixed(2);
              const isPending = ch.status === 'pending_opponent';
              const isAccepted = ch.status === 'accepted';
              const isBooked = ch.status === 'booked';

              return (
                <div
                  key={ch.id}
                  className="bg-white rounded-3xl border border-slate-200 p-5 shadow-sm space-y-4 hover:shadow-md transition-shadow"
                >
                  {/* Match Header with Status */}
                  <div className="flex items-center justify-between gap-2 border-b border-slate-100 pb-3">
                    <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400">
                      {language === 'en' ? 'Match Proposal' : 'Dalabka Ciyaarta'}
                    </span>

                    <span
                      className={`text-[10px] font-bold uppercase px-2.5 py-0.5 rounded-full ${
                        isBooked
                          ? 'bg-emerald-100 text-emerald-800'
                          : isAccepted
                          ? 'bg-blue-100 text-blue-800'
                          : 'bg-amber-100 text-amber-800'
                      }`}
                    >
                      {isBooked
                        ? (language === 'en' ? 'Slot Booked in Arena' : 'Waa La Dalbay Garoonka')
                        : isAccepted
                        ? (language === 'en' ? 'Both Agreed • Ready to Book' : 'Waa Lagu Heshiiyey')
                        : (language === 'en' ? 'Pending Opponent Agreement' : 'Sugaya Oggolaansho')}
                    </span>
                  </div>

                  {/* Team vs Team Card */}
                  <div className="bg-slate-50 p-4 rounded-2xl border border-slate-100 flex items-center justify-between gap-2">
                    <div className="text-center flex-1">
                      <div className="text-xs font-black text-slate-900 truncate">
                        {ch.challengerTeamName}
                      </div>
                      <div className="text-[10px] text-slate-500 font-mono mt-0.5">
                        {ch.challengerCaptainPhone}
                      </div>
                    </div>

                    <div className="shrink-0 flex flex-col items-center justify-center px-3">
                      <span className="w-8 h-8 rounded-full bg-slate-900 text-white font-black text-xs flex items-center justify-center shadow-xs">
                        VS
                      </span>
                    </div>

                    <div className="text-center flex-1">
                      <div className="text-xs font-black text-slate-900 truncate">
                        {ch.challengedTeamName}
                      </div>
                      <div className="text-[10px] text-slate-500 font-mono mt-0.5">
                        {ch.challengedCaptainPhone}
                      </div>
                    </div>
                  </div>

                  {/* Agreed Match Schedule Specs */}
                  <div className="grid grid-cols-2 gap-2 text-xs text-slate-700">
                    <div className="flex items-center gap-1.5 bg-slate-50 p-2.5 rounded-xl border border-slate-100">
                      <Calendar className="w-3.5 h-3.5 text-emerald-600 shrink-0" />
                      <span className="font-semibold">{ch.date}</span>
                    </div>
                    <div className="flex items-center gap-1.5 bg-slate-50 p-2.5 rounded-xl border border-slate-100">
                      <Clock className="w-3.5 h-3.5 text-emerald-600 shrink-0" />
                      <span className="font-semibold">{ch.slot}</span>
                    </div>
                  </div>

                  <div className="text-xs text-slate-600 bg-slate-50 p-2.5 rounded-xl border border-slate-100 space-y-1">
                    <div className="font-bold text-slate-900">{ch.pitchName}</div>
                    <div className="flex items-center justify-between text-[11px] text-slate-500">
                      <span>
                        Fee Split: <strong>{ch.splitMode === '50-50' ? `50/50 ($${feePerTeam} each)` : ch.splitMode}</strong>
                      </span>
                      <span className="font-bold text-emerald-700 text-xs">${ch.totalAmount} USD Total</span>
                    </div>
                  </div>

                  {ch.notes && (
                    <p className="text-[11px] text-slate-500 italic bg-amber-50/50 p-2 rounded-xl border border-amber-100">
                      "{ch.notes}"
                    </p>
                  )}

                  {/* Action Buttons for Match Agreements */}
                  <div className="pt-2 border-t border-slate-100 flex flex-wrap items-center justify-between gap-2">
                    <div className="flex items-center gap-2">
                      <a
                        href={`https://wa.me/${ch.challengedCaptainPhone.replace(/[^0-9]/g, '')}?text=${encodeURIComponent(
                          `Asc Captain ${ch.challengedTeamName}! We challenge you to a match on ${ch.date} (${ch.slot}) at 26 JSC TurfBook (${ch.pitchName}). Let us know if you agree!`
                        )}`}
                        target="_blank"
                        rel="noreferrer"
                        className="px-3 py-1.5 bg-emerald-50 hover:bg-emerald-100 text-emerald-800 rounded-xl text-xs font-semibold flex items-center gap-1.5 transition-colors border border-emerald-200"
                        title="Chat with Captain on WhatsApp"
                      >
                        <MessageSquare className="w-3.5 h-3.5 text-emerald-600" />
                        <span>Chat WhatsApp</span>
                      </a>
                    </div>

                    {isPending && (
                      <button
                        onClick={() => onAcceptChallenge(ch.id)}
                        className="px-4 py-1.5 bg-blue-600 hover:bg-blue-500 text-white rounded-xl text-xs font-bold transition-all shadow-xs"
                      >
                        {language === 'en' ? 'Accept & Agree Time' : 'Aqbal & Heshii'}
                      </button>
                    )}

                    {isAccepted && (
                      <button
                        onClick={() => handleBookAgreedFixture(ch)}
                        className="px-4 py-1.5 bg-emerald-600 hover:bg-emerald-500 text-white rounded-xl text-xs font-black transition-all shadow-md flex items-center gap-1.5 active:scale-98"
                      >
                        <CheckCircle2 className="w-3.5 h-3.5" />
                        <span>{language === 'en' ? 'Confirm & Book Turf' : 'Xaqiiji & Dalbo Garoonka'}</span>
                      </button>
                    )}

                    {isBooked && (
                      <span className="text-xs font-bold text-emerald-700 flex items-center gap-1">
                        <CheckCircle2 className="w-3.5 h-3.5 text-emerald-600" />
                        <span>{language === 'en' ? 'Scheduled in Arena' : 'Waa La Jadwaleeyay'}</span>
                      </span>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* SUB-TAB 2: CLUBS & MEMBERSHIP LIST */}
      {subTab === 'teams' && (
        <div className="space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {teams.map((team) => (
              <div
                key={team.id}
                className="bg-white rounded-3xl border border-slate-200/90 shadow-sm p-5 space-y-4 hover:border-emerald-300 hover:shadow-md transition-all flex flex-col justify-between"
              >
                <div>
                  {/* Team Card Header */}
                  <div className="flex items-start justify-between gap-3">
                    <div className="flex items-center gap-2.5">
                      <div className="w-12 h-12 rounded-2xl bg-slate-100 flex items-center justify-center text-2xl border border-slate-200 shadow-inner">
                        {team.logoEmoji}
                      </div>
                      <div>
                        <h3 className="font-black text-sm text-slate-900 leading-tight">
                          {team.name}
                        </h3>
                        <div className="text-[11px] text-slate-500 flex items-center gap-2 mt-0.5">
                          <span>{team.preferredFormat}</span>
                          <span>•</span>
                          <span className="font-semibold text-emerald-700">{team.skillLevel}</span>
                        </div>
                      </div>
                    </div>

                    <span className="text-[10px] font-mono font-bold bg-slate-100 text-slate-700 px-2 py-0.5 rounded-lg border border-slate-200">
                      {team.members.length} Players
                    </span>
                  </div>

                  {/* Captain Contact */}
                  <div className="mt-3 p-3 bg-slate-50 rounded-2xl border border-slate-100 text-xs space-y-1">
                    <div className="flex items-center justify-between text-slate-700">
                      <span>Captain: <strong className="text-slate-900">{team.captainName}</strong></span>
                      <a
                        href={`tel:${team.captainPhone}`}
                        className="text-emerald-700 font-mono font-bold hover:underline flex items-center gap-1"
                      >
                        <Phone className="w-3 h-3" />
                        <span>{team.captainPhone}</span>
                      </a>
                    </div>
                    {team.bio && (
                      <p className="text-[11px] text-slate-500 leading-relaxed pt-1">
                        {team.bio}
                      </p>
                    )}
                  </div>

                  {/* Match Stats */}
                  <div className="grid grid-cols-4 gap-1.5 text-center mt-3 text-[11px] bg-slate-900 text-white p-2.5 rounded-2xl">
                    <div>
                      <div className="text-[9px] uppercase tracking-wider text-slate-400">Pld</div>
                      <div className="font-black text-xs">{team.stats.matchesPlayed}</div>
                    </div>
                    <div>
                      <div className="text-[9px] uppercase tracking-wider text-emerald-400">Won</div>
                      <div className="font-black text-xs text-emerald-400">{team.stats.wins}</div>
                    </div>
                    <div>
                      <div className="text-[9px] uppercase tracking-wider text-amber-400">Draw</div>
                      <div className="font-black text-xs text-amber-400">{team.stats.draws}</div>
                    </div>
                    <div>
                      <div className="text-[9px] uppercase tracking-wider text-rose-400">Lost</div>
                      <div className="font-black text-xs text-rose-400">{team.stats.losses}</div>
                    </div>
                  </div>

                  {/* Membership List / Squad Roster */}
                  <div className="mt-4 space-y-2">
                    <div className="flex items-center justify-between text-xs font-bold text-slate-800">
                      <span className="flex items-center gap-1.5">
                        <UserCheck className="w-3.5 h-3.5 text-emerald-600" />
                        <span>{language === 'en' ? 'Squad Membership List' : 'Liiska Xubnaha Kooxda'}</span>
                      </span>

                      <button
                        onClick={() => setActiveAddMemberTeamId(team.id)}
                        className="text-[11px] text-emerald-700 hover:text-emerald-800 font-bold hover:underline"
                      >
                        + Add Member
                      </button>
                    </div>

                    <div className="max-h-40 overflow-y-auto space-y-1.5 pr-1 text-xs">
                      {team.members.map((member) => (
                        <div
                          key={member.id}
                          className="flex items-center justify-between p-2 rounded-xl bg-slate-50 border border-slate-100 text-[11px]"
                        >
                          <div className="flex items-center gap-2">
                            <span className="w-5 h-5 rounded-full bg-slate-200 font-mono font-bold text-[10px] flex items-center justify-center text-slate-700">
                              #{member.jerseyNumber}
                            </span>
                            <span className="font-semibold text-slate-900">
                              {member.name}
                            </span>
                          </div>

                          <span className="text-[10px] text-slate-500 font-medium px-2 py-0.5 rounded-md bg-white border border-slate-200">
                            {member.position}
                          </span>
                        </div>
                      ))}
                    </div>
                  </div>
                </div>

                {/* Challenge this team CTA */}
                <div className="pt-3 border-t border-slate-100 flex items-center justify-between gap-2">
                  <button
                    onClick={() => handleOpenChallenge(team.id)}
                    className="w-full bg-emerald-600 hover:bg-emerald-500 text-white font-bold py-2 rounded-xl text-xs transition-colors flex items-center justify-center gap-1.5 shadow-xs active:scale-98"
                  >
                    <Swords className="w-3.5 h-3.5" />
                    <span>{language === 'en' ? 'Challenge This Team' : 'La Ciyaar Kooxdan'}</span>
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* MODAL 1: Create Versus Challenge */}
      {isChallengeModalOpen && (
        <div className="fixed inset-0 z-50 bg-slate-950/70 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl max-w-lg w-full p-6 shadow-2xl border border-slate-200 space-y-4">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <div className="flex items-center gap-2">
                <div className="p-2 bg-emerald-50 rounded-xl text-emerald-600">
                  <Swords className="w-5 h-5" />
                </div>
                <h3 className="font-black text-base text-slate-900">
                  {language === 'en' ? 'Setup Match Challenge' : 'Diyaari Ciyaar & Heshiis'}
                </h3>
              </div>
              <button
                onClick={() => setIsChallengeModalOpen(false)}
                className="text-slate-400 hover:text-slate-700 p-1"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleCreateChallengeSubmit} className="space-y-3.5 text-xs">
              {/* Teams Selection */}
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="font-bold text-slate-700 block mb-1">
                    {language === 'en' ? 'Challenger Team (Team A) *' : 'Kooxda Dalbanaysa (Kooxda A) *'}
                  </label>
                  <select
                    value={challengerTeamId}
                    onChange={(e) => setChallengerTeamId(e.target.value)}
                    className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-semibold focus:outline-none focus:ring-2 focus:ring-emerald-500 bg-white"
                  >
                    {teams.map((t) => (
                      <option key={t.id} value={t.id}>
                        {t.name}
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="font-bold text-slate-700 block mb-1">
                    {language === 'en' ? 'Opponent Team (Team B) *' : 'Kooxda Tartanka (Kooxda B) *'}
                  </label>
                  <select
                    value={challengedTeamId}
                    onChange={(e) => setChallengedTeamId(e.target.value)}
                    className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-semibold focus:outline-none focus:ring-2 focus:ring-emerald-500 bg-white"
                  >
                    {teams.map((t) => (
                      <option key={t.id} value={t.id}>
                        {t.name}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              {/* Pitch Selection */}
              <div>
                <label className="font-bold text-slate-700 block mb-1">
                  {language === 'en' ? 'Choose Arena Pitch *' : 'Dooro Garoonka *'}
                </label>
                <select
                  value={selectedPitchId}
                  onChange={(e) => setSelectedPitchId(e.target.value)}
                  className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-semibold focus:outline-none focus:ring-2 focus:ring-emerald-500 bg-white"
                >
                  {pitches.map((p) => (
                    <option key={p.id} value={p.id}>
                      {p.name} (${p.hourlyRate}/hr) - {p.surface}
                    </option>
                  ))}
                </select>
              </div>

              {/* Date & Agreed Time Slot */}
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="font-bold text-slate-700 block mb-1">
                    {language === 'en' ? 'Agreed Date *' : 'Taariikhda Ciyaarta *'}
                  </label>
                  <input
                    type="date"
                    required
                    min={new Date().toISOString().split('T')[0]}
                    value={matchDate}
                    onChange={(e) => setMatchDate(e.target.value)}
                    className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-semibold focus:outline-none focus:ring-2 focus:ring-emerald-500"
                  />
                </div>

                <div>
                  <label className="font-bold text-slate-700 block mb-1">
                    {language === 'en' ? 'Agreed Time Slot *' : 'Saacadda Ciyaarta *'}
                  </label>
                  <select
                    value={matchSlot}
                    onChange={(e) => setMatchSlot(e.target.value)}
                    className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-semibold focus:outline-none focus:ring-2 focus:ring-emerald-500 bg-white"
                  >
                    {TIME_SLOTS.map((slot) => (
                      <option key={slot} value={slot}>
                        {slot}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              {/* Fee Split Mode */}
              <div>
                <label className="font-bold text-slate-700 block mb-1">
                  {language === 'en' ? 'Turf Payment Agreement *' : 'Heshiiska Bixinta Lacagta Garoonka *'}
                </label>
                <div className="grid grid-cols-3 gap-2">
                  {[
                    { id: '50-50', label: '50 / 50 Split' },
                    { id: 'challenger-covers', label: 'Challenger Pays' },
                    { id: 'loser-pays', label: 'Loser Pays' },
                  ].map((m) => (
                    <button
                      key={m.id}
                      type="button"
                      onClick={() => setSplitMode(m.id as any)}
                      className={`p-2 rounded-xl text-center font-bold border transition-all ${
                        splitMode === m.id
                          ? 'bg-emerald-50 border-emerald-500 text-emerald-900 ring-2 ring-emerald-500/20'
                          : 'bg-slate-50 border-slate-200 text-slate-600 hover:bg-slate-100'
                      }`}
                    >
                      {m.label}
                    </button>
                  ))}
                </div>
              </div>

              <div>
                <label className="font-bold text-slate-700 block mb-1">
                  {language === 'en' ? 'Match Notes / Rules' : 'Qodobbada Heshiiska'}
                </label>
                <textarea
                  rows={2}
                  placeholder="e.g. Referee needed, yellow jerseys vs blue jerseys..."
                  value={challengeNotes}
                  onChange={(e) => setChallengeNotes(e.target.value)}
                  className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-medium focus:outline-none focus:ring-2 focus:ring-emerald-500"
                />
              </div>

              <div className="p-3 bg-emerald-50 rounded-2xl border border-emerald-200 text-[11px] text-emerald-900 flex items-center justify-between">
                <span>
                  Pitch Rate: <strong>${selectedPitch.hourlyRate} USD</strong>
                </span>
                <span>
                  Payable via: <strong>Zaad {APP_CONFIG.zaadMerchant}</strong> / <strong>eDahab {APP_CONFIG.edahabMerchant}</strong>
                </span>
              </div>

              <div className="flex items-center justify-end gap-2 pt-2 border-t border-slate-100">
                <button
                  type="button"
                  onClick={() => setIsChallengeModalOpen(false)}
                  className="px-4 py-2 rounded-xl text-xs font-semibold text-slate-600 hover:bg-slate-100"
                >
                  {language === 'en' ? 'Cancel' : 'Ka Noqo'}
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 rounded-xl text-xs font-black bg-emerald-600 hover:bg-emerald-500 text-white shadow-sm"
                >
                  {language === 'en' ? 'Send Challenge' : 'Dir Dalabka'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* MODAL 2: Register New Team */}
      {isRegisterTeamOpen && (
        <div className="fixed inset-0 z-50 bg-slate-950/70 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl max-w-lg w-full p-6 shadow-2xl border border-slate-200 space-y-4">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <div className="flex items-center gap-2">
                <div className="p-2 bg-emerald-50 rounded-xl text-emerald-600">
                  <Plus className="w-5 h-5" />
                </div>
                <h3 className="font-black text-base text-slate-900">
                  {language === 'en' ? 'Register New Football Club' : 'Diiwaangeli Koox Cusub'}
                </h3>
              </div>
              <button
                onClick={() => setIsRegisterTeamOpen(false)}
                className="text-slate-400 hover:text-slate-700 p-1"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleRegisterTeamSubmit} className="space-y-3.5 text-xs">
              <div>
                <label className="font-bold text-slate-700 block mb-1">
                  {language === 'en' ? 'Team Name *' : 'Magaca Kooxda *'}
                </label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Hargeisa United FC"
                  value={newTeamName}
                  onChange={(e) => setNewTeamName(e.target.value)}
                  className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-semibold focus:outline-none focus:ring-2 focus:ring-emerald-500"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="font-bold text-slate-700 block mb-1">
                    {language === 'en' ? 'Captain / Manager Name *' : 'Magaca Kabtanka / Maamulaha *'}
                  </label>
                  <input
                    type="text"
                    required
                    placeholder="e.g. Guuleed Maxamed"
                    value={newCaptainName}
                    onChange={(e) => setNewCaptainName(e.target.value)}
                    className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-semibold focus:outline-none focus:ring-2 focus:ring-emerald-500"
                  />
                </div>

                <div>
                  <label className="font-bold text-slate-700 block mb-1">
                    {language === 'en' ? 'Captain Phone Number *' : 'Telefoonka Kabtanka *'}
                  </label>
                  <input
                    type="tel"
                    required
                    placeholder="+25263..."
                    value={newCaptainPhone}
                    onChange={(e) => setNewCaptainPhone(e.target.value)}
                    className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-semibold focus:outline-none focus:ring-2 focus:ring-emerald-500"
                  />
                </div>
              </div>

              <div className="grid grid-cols-3 gap-3">
                <div>
                  <label className="font-bold text-slate-700 block mb-1">
                    {language === 'en' ? 'Format' : 'Cabbirka'}
                  </label>
                  <select
                    value={newFormat}
                    onChange={(e: any) => setNewFormat(e.target.value)}
                    className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-semibold focus:outline-none focus:ring-2 focus:ring-emerald-500 bg-white"
                  >
                    <option value="7-a-side">7-a-side</option>
                    <option value="5-a-side">5-a-side</option>
                    <option value="6-a-side">6-a-side</option>
                    <option value="Futsal">Futsal</option>
                  </select>
                </div>

                <div>
                  <label className="font-bold text-slate-700 block mb-1">
                    {language === 'en' ? 'Skill Level' : 'Heerka'}
                  </label>
                  <select
                    value={newSkillLevel}
                    onChange={(e: any) => setNewSkillLevel(e.target.value)}
                    className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-semibold focus:outline-none focus:ring-2 focus:ring-emerald-500 bg-white"
                  >
                    <option value="Competitive">Competitive</option>
                    <option value="Semi-Pro">Semi-Pro</option>
                    <option value="Casual">Casual</option>
                  </select>
                </div>

                <div>
                  <label className="font-bold text-slate-700 block mb-1">
                    {language === 'en' ? 'Logo Emoji' : 'Astaanta'}
                  </label>
                  <select
                    value={newEmoji}
                    onChange={(e) => setNewEmoji(e.target.value)}
                    className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-semibold focus:outline-none focus:ring-2 focus:ring-emerald-500 bg-white"
                  >
                    <option value="⚽">⚽ Football</option>
                    <option value="🛡️">🛡️ Shield</option>
                    <option value="🦁">🦁 Lion</option>
                    <option value="⚡">⚡ Bolt</option>
                    <option value="⭐">⭐ Star</option>
                    <option value="🔥">🔥 Fire</option>
                    <option value="🦅">🦅 Eagle</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="font-bold text-slate-700 block mb-1">
                  {language === 'en' ? 'Club Bio / Description' : 'Sharaxaadda Kooxda'}
                </label>
                <textarea
                  rows={2}
                  placeholder="e.g. Neighborhood team training on Monday & Thursday nights."
                  value={newBio}
                  onChange={(e) => setNewBio(e.target.value)}
                  className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-medium focus:outline-none focus:ring-2 focus:ring-emerald-500"
                />
              </div>

              <div className="flex items-center justify-end gap-2 pt-2 border-t border-slate-100">
                <button
                  type="button"
                  onClick={() => setIsRegisterTeamOpen(false)}
                  className="px-4 py-2 rounded-xl text-xs font-semibold text-slate-600 hover:bg-slate-100"
                >
                  {language === 'en' ? 'Cancel' : 'Ka Noqo'}
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 rounded-xl text-xs font-black bg-emerald-600 hover:bg-emerald-500 text-white shadow-sm"
                >
                  {language === 'en' ? 'Register Team' : 'Diiwaangeli Kooxda'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* MODAL 3: Add Member to Squad */}
      {activeAddMemberTeamId && (
        <div className="fixed inset-0 z-50 bg-slate-950/70 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl max-w-sm w-full p-6 shadow-2xl border border-slate-200 space-y-4">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <div className="flex items-center gap-2">
                <div className="p-2 bg-emerald-50 rounded-xl text-emerald-600">
                  <UserCheck className="w-5 h-5" />
                </div>
                <h3 className="font-black text-sm text-slate-900">
                  {language === 'en' ? 'Add Player to Squad' : 'Kudar Ciyaartoy'}
                </h3>
              </div>
              <button
                onClick={() => setActiveAddMemberTeamId(null)}
                className="text-slate-400 hover:text-slate-700 p-1"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleAddMemberSubmit} className="space-y-3.5 text-xs">
              <div>
                <label className="font-bold text-slate-700 block mb-1">
                  {language === 'en' ? 'Player Full Name *' : 'Magaca Ciyaartoyga *'}
                </label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Mahad Cilmi"
                  value={memberName}
                  onChange={(e) => setMemberName(e.target.value)}
                  className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-semibold focus:outline-none focus:ring-2 focus:ring-emerald-500"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="font-bold text-slate-700 block mb-1">
                    {language === 'en' ? 'Position' : 'Booska'}
                  </label>
                  <select
                    value={memberPosition}
                    onChange={(e: any) => setMemberPosition(e.target.value)}
                    className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-semibold focus:outline-none focus:ring-2 focus:ring-emerald-500 bg-white"
                  >
                    <option value="Forward">Forward</option>
                    <option value="Midfielder">Midfielder</option>
                    <option value="Defender">Defender</option>
                    <option value="Goalkeeper">Goalkeeper</option>
                  </select>
                </div>

                <div>
                  <label className="font-bold text-slate-700 block mb-1">
                    {language === 'en' ? 'Jersey Number' : 'Lambarka Fanaanka'}
                  </label>
                  <input
                    type="number"
                    min={1}
                    max={99}
                    value={memberJersey}
                    onChange={(e) => setMemberJersey(Number(e.target.value))}
                    className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-semibold focus:outline-none focus:ring-2 focus:ring-emerald-500"
                  />
                </div>
              </div>

              <div className="flex items-center justify-end gap-2 pt-2 border-t border-slate-100">
                <button
                  type="button"
                  onClick={() => setActiveAddMemberTeamId(null)}
                  className="px-4 py-2 rounded-xl text-xs font-semibold text-slate-600 hover:bg-slate-100"
                >
                  {language === 'en' ? 'Cancel' : 'Ka Noqo'}
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 rounded-xl text-xs font-black bg-emerald-600 hover:bg-emerald-500 text-white shadow-sm"
                >
                  {language === 'en' ? 'Save Player' : 'Kaydi Ciyaartoyga'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
