import React, { useState } from 'react';
import { 
  Camera, 
  Heart, 
  Sparkles, 
  Share2, 
  X, 
  Plus, 
  Calendar, 
  MapPin, 
  Users, 
  Trophy,
  CheckCircle2,
  Filter
} from 'lucide-react';
import { GameImage, Language } from '../types';
import { APP_CONFIG } from '../data/initialData';

interface GameImagesPortalProps {
  images: GameImage[];
  onAddImage: (image: GameImage) => void;
  onLikeImage: (id: string) => void;
  language: Language;
}

export const GameImagesPortal: React.FC<GameImagesPortalProps> = ({
  images,
  onAddImage,
  onLikeImage,
  language,
}) => {
  const [activeCategory, setActiveCategory] = useState<string>('All');
  const [searchTerm, setSearchTerm] = useState<string>('');
  const [selectedImage, setSelectedImage] = useState<GameImage | null>(null);
  const [isUploadModalOpen, setIsUploadModalOpen] = useState<boolean>(false);

  // Upload Form State
  const [newTitle, setNewTitle] = useState('');
  const [newCategory, setNewCategory] = useState<'Match Action' | 'Night Floodlights' | 'Tournament' | 'Celebration'>('Match Action');
  const [newImageUrl, setNewImageUrl] = useState('');
  const [newPitchName, setNewPitchName] = useState('Pitch 1 - Championship Arena');
  const [newTeams, setNewTeams] = useState('');

  const categories = ['All', 'Match Action', 'Night Floodlights', 'Tournament', 'Celebration'];

  const filteredImages = images.filter((img) => {
    const matchesCategory = activeCategory === 'All' || img.category === activeCategory;
    const matchesSearch =
      !searchTerm.trim() ||
      img.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
      img.pitchName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      (img.teamsInvolved && img.teamsInvolved.toLowerCase().includes(searchTerm.toLowerCase()));
    return matchesCategory && matchesSearch;
  });

  const handleCreateImage = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newTitle.trim() || !newImageUrl.trim()) return;

    const newEntry: GameImage = {
      id: `img-${Date.now()}`,
      title: newTitle.trim(),
      somaliTitle: newTitle.trim(),
      category: newCategory,
      imageUrl: newImageUrl.trim(),
      date: new Date().toISOString().split('T')[0],
      pitchName: newPitchName,
      teamsInvolved: newTeams.trim() || undefined,
      likes: 1,
    };

    onAddImage(newEntry);
    setIsUploadModalOpen(false);
    setNewTitle('');
    setNewImageUrl('');
    setNewTeams('');
  };

  const samplePhotoUrls = [
    'https://images.unsplash.com/photo-1579952363873-27f3bade9f55?auto=format&fit=crop&w=1200&q=80',
    'https://images.unsplash.com/photo-1517927033932-b3d18e61fb3a?auto=format&fit=crop&w=1200&q=80',
    'https://images.unsplash.com/photo-1543326727-cf6c39e8f84c?auto=format&fit=crop&w=1200&q=80',
  ];

  return (
    <div className="space-y-6" id="game-images-portal">
      {/* Header Banner */}
      <div className="bg-white p-5 sm:p-6 rounded-3xl border border-slate-200/90 shadow-sm flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div className="inline-flex items-center gap-1.5 bg-emerald-50 text-emerald-700 px-2.5 py-0.5 rounded-full text-xs font-bold mb-1.5 border border-emerald-200/60">
            <Camera className="w-3.5 h-3.5" />
            <span>{language === 'en' ? 'Game Moments & Highlights' : 'Sawirrada & Dhacdooyinka Ciyaaraha'}</span>
          </div>
          <h2 className="text-xl sm:text-2xl font-black text-slate-900 tracking-tight">
            {language === 'en' ? '26 JSC TurfBook Match Gallery' : 'Maktabadda Sawirrada Ciyaaraha'}
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            {language === 'en'
              ? 'Browse high-definition photos from tournament fixtures, evening derbies, and floodlit championship games.'
              : 'Daawo sawirrada tartamada, ciyaaraha saaxiibtinimo iyo kuwa habeenkii ee garoomadayada.'}
          </p>
        </div>

        <button
          onClick={() => setIsUploadModalOpen(true)}
          className="flex items-center gap-2 bg-slate-900 hover:bg-slate-800 text-white font-bold px-4 py-2.5 rounded-2xl text-xs transition-all shadow-sm active:scale-98 shrink-0"
          id="upload-match-photo-btn"
        >
          <Plus className="w-4 h-4" />
          <span>{language === 'en' ? 'Submit Match Photo' : 'Geli Sawirka Ciyaarta'}</span>
        </button>
      </div>

      {/* Filter and Search Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
        {/* Category Pills */}
        <div className="flex items-center gap-1.5 overflow-x-auto pb-1 sm:pb-0">
          {categories.map((cat) => (
            <button
              key={cat}
              onClick={() => setActiveCategory(cat)}
              className={`px-3 py-1.5 rounded-xl text-xs font-bold transition-all shrink-0 ${
                activeCategory === cat
                  ? 'bg-emerald-600 text-white shadow-xs'
                  : 'bg-white border border-slate-200 text-slate-600 hover:bg-slate-50'
              }`}
            >
              {cat}
            </button>
          ))}
        </div>

        {/* Search Bar */}
        <input
          type="text"
          placeholder={language === 'en' ? 'Search by team or pitch...' : 'Ku raadi koox ama garoon...'}
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="bg-white border border-slate-200 rounded-xl px-3 py-1.5 text-xs text-slate-900 font-medium focus:outline-none focus:ring-2 focus:ring-emerald-500 w-full sm:w-64"
        />
      </div>

      {/* Gallery Grid */}
      {filteredImages.length === 0 ? (
        <div className="bg-white rounded-3xl border border-slate-200 p-10 text-center space-y-3">
          <Camera className="w-10 h-10 text-slate-300 mx-auto" />
          <p className="text-xs text-slate-500">
            {language === 'en' ? 'No photos found for this search.' : 'Wax sawirro ah lagama helin raadintan.'}
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredImages.map((img) => (
            <div
              key={img.id}
              className="group bg-white rounded-3xl border border-slate-200/90 overflow-hidden shadow-sm hover:shadow-xl transition-all duration-300 flex flex-col"
            >
              {/* Image Frame with Aspect Ratio */}
              <div
                className="relative aspect-video overflow-hidden bg-slate-900 cursor-pointer"
                onClick={() => setSelectedImage(img)}
              >
                <img
                  src={img.imageUrl}
                  alt={img.title}
                  className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                  loading="lazy"
                />
                <div className="absolute inset-0 bg-gradient-to-t from-slate-950/80 via-transparent to-black/20 opacity-0 group-hover:opacity-100 transition-opacity flex items-end p-4">
                  <span className="text-white text-xs font-semibold flex items-center gap-1.5">
                    <span>Click to enlarge photo</span>
                  </span>
                </div>

                <div className="absolute top-3 left-3 bg-slate-900/80 backdrop-blur-md text-white px-2.5 py-0.5 rounded-full text-[10px] font-bold border border-white/20">
                  {img.category}
                </div>

                <div className="absolute top-3 right-3 bg-slate-900/80 backdrop-blur-md text-emerald-400 px-2 py-0.5 rounded-full text-[10px] font-bold flex items-center gap-1">
                  <MapPin className="w-3 h-3" />
                  <span>{img.pitchName.split('-')[0]}</span>
                </div>
              </div>

              {/* Details and Actions */}
              <div className="p-4 flex-1 flex flex-col justify-between space-y-3">
                <div>
                  <h3 className="font-black text-sm text-slate-900 leading-snug">
                    {language === 'en' ? img.title : img.somaliTitle}
                  </h3>
                  {img.teamsInvolved && (
                    <div className="flex items-center gap-1.5 text-xs text-emerald-700 font-bold mt-1">
                      <Users className="w-3.5 h-3.5" />
                      <span>{img.teamsInvolved}</span>
                    </div>
                  )}
                </div>

                <div className="flex items-center justify-between pt-2 border-t border-slate-100 text-xs text-slate-500">
                  <div className="flex items-center gap-1 text-[11px]">
                    <Calendar className="w-3 h-3 text-slate-400" />
                    <span>{img.date}</span>
                  </div>

                  <div className="flex items-center gap-2">
                    <button
                      onClick={() => onLikeImage(img.id)}
                      className="flex items-center gap-1 px-2.5 py-1 rounded-xl bg-rose-50 hover:bg-rose-100 text-rose-600 font-bold text-xs transition-colors"
                      title="Like photo"
                    >
                      <Heart className="w-3.5 h-3.5 fill-current" />
                      <span>{img.likes}</span>
                    </button>

                    <a
                      href={`https://wa.me/${APP_CONFIG.contactPhone.replace(/[^0-9]/g, '')}?text=${encodeURIComponent(
                        `Check out this 26 JSC TurfBook match highlight: "${img.title}" at ${img.pitchName}`
                      )}`}
                      target="_blank"
                      rel="noreferrer"
                      className="p-1.5 rounded-xl text-slate-400 hover:text-emerald-600 hover:bg-slate-50 transition-colors"
                      title="Share to WhatsApp"
                    >
                      <Share2 className="w-4 h-4" />
                    </a>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Lightbox Modal */}
      {selectedImage && (
        <div className="fixed inset-0 z-50 bg-slate-950/90 backdrop-blur-md flex items-center justify-center p-4">
          <div className="relative max-w-4xl w-full bg-slate-900 rounded-3xl overflow-hidden border border-slate-800 shadow-2xl text-white">
            <button
              onClick={() => setSelectedImage(null)}
              className="absolute top-4 right-4 z-10 w-9 h-9 rounded-full bg-black/50 hover:bg-black/80 flex items-center justify-center text-white transition-colors"
            >
              <X className="w-5 h-5" />
            </button>

            <img
              src={selectedImage.imageUrl}
              alt={selectedImage.title}
              className="w-full max-h-[70vh] object-contain bg-black"
            />

            <div className="p-6 bg-slate-900 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
              <div>
                <div className="text-xs text-emerald-400 font-bold mb-1">
                  {selectedImage.pitchName} • {selectedImage.date}
                </div>
                <h3 className="text-lg font-bold text-white">
                  {language === 'en' ? selectedImage.title : selectedImage.somaliTitle}
                </h3>
                {selectedImage.teamsInvolved && (
                  <p className="text-xs text-slate-300 mt-0.5">
                    Teams: {selectedImage.teamsInvolved}
                  </p>
                )}
              </div>

              <div className="flex items-center gap-3">
                <button
                  onClick={() => onLikeImage(selectedImage.id)}
                  className="flex items-center gap-1.5 px-4 py-2 rounded-xl bg-rose-600 hover:bg-rose-500 text-white font-bold text-xs shadow-md transition-all"
                >
                  <Heart className="w-4 h-4 fill-current" />
                  <span>{selectedImage.likes} Likes</span>
                </button>

                <a
                  href={`https://wa.me/${APP_CONFIG.contactPhone.replace(/[^0-9]/g, '')}?text=${encodeURIComponent(
                    `Inquiry about photo: ${selectedImage.title} from 26 JSC TurfBook`
                  )}`}
                  target="_blank"
                  rel="noreferrer"
                  className="flex items-center gap-1.5 px-4 py-2 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white font-bold text-xs shadow-md transition-all"
                >
                  <Share2 className="w-4 h-4" />
                  <span>Send on WhatsApp</span>
                </a>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Upload Match Photo Dialog */}
      {isUploadModalOpen && (
        <div className="fixed inset-0 z-50 bg-slate-950/70 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl max-w-lg w-full p-6 shadow-2xl border border-slate-200 space-y-4">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <div className="flex items-center gap-2">
                <div className="p-2 bg-emerald-50 rounded-xl text-emerald-600">
                  <Camera className="w-5 h-5" />
                </div>
                <h3 className="font-black text-base text-slate-900">
                  {language === 'en' ? 'Submit Match Photo' : 'Geli Sawirka Ciyaarta'}
                </h3>
              </div>
              <button
                onClick={() => setIsUploadModalOpen(false)}
                className="text-slate-400 hover:text-slate-700 p-1"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleCreateImage} className="space-y-3.5 text-xs">
              <div>
                <label className="font-bold text-slate-700 block mb-1">
                  {language === 'en' ? 'Photo Title / Caption *' : 'Cinwaanka Sawirka *'}
                </label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Derby Clash - 26 June Warriors vs Elman"
                  value={newTitle}
                  onChange={(e) => setNewTitle(e.target.value)}
                  className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-semibold focus:outline-none focus:ring-2 focus:ring-emerald-500"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="font-bold text-slate-700 block mb-1">
                    {language === 'en' ? 'Category' : 'Qaybta'}
                  </label>
                  <select
                    value={newCategory}
                    onChange={(e: any) => setNewCategory(e.target.value)}
                    className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-semibold focus:outline-none focus:ring-2 focus:ring-emerald-500 bg-white"
                  >
                    <option value="Match Action">Match Action</option>
                    <option value="Night Floodlights">Night Floodlights</option>
                    <option value="Tournament">Tournament</option>
                    <option value="Celebration">Celebration</option>
                  </select>
                </div>

                <div>
                  <label className="font-bold text-slate-700 block mb-1">
                    {language === 'en' ? 'Pitch Location' : 'Garoonka'}
                  </label>
                  <select
                    value={newPitchName}
                    onChange={(e) => setNewPitchName(e.target.value)}
                    className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-semibold focus:outline-none focus:ring-2 focus:ring-emerald-500 bg-white"
                  >
                    <option value="Pitch 1 - Championship Arena">Pitch 1 (7-a-side)</option>
                    <option value="Pitch 2 - Premier Astro Turf">Pitch 2 (5-a-side)</option>
                    <option value="Pitch 3 - VIP Floodlight Turf">Pitch 3 (6-a-side)</option>
                    <option value="Pitch 4 - Skills & Futsal Cage">Pitch 4 (Futsal)</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="font-bold text-slate-700 block mb-1">
                  {language === 'en' ? 'Teams Involved' : 'Kooxaha Ciyaaray'}
                </label>
                <input
                  type="text"
                  placeholder="e.g. 26 June Warriors vs Burao United"
                  value={newTeams}
                  onChange={(e) => setNewTeams(e.target.value)}
                  className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-semibold focus:outline-none focus:ring-2 focus:ring-emerald-500"
                />
              </div>

              <div>
                <label className="font-bold text-slate-700 block mb-1">
                  {language === 'en' ? 'Image Web URL *' : 'Link-ga Sawirka (URL) *'}
                </label>
                <input
                  type="url"
                  required
                  placeholder="https://..."
                  value={newImageUrl}
                  onChange={(e) => setNewImageUrl(e.target.value)}
                  className="w-full p-2.5 rounded-xl border border-slate-200 text-xs font-semibold focus:outline-none focus:ring-2 focus:ring-emerald-500"
                />
                <div className="flex items-center gap-1.5 mt-1.5 flex-wrap">
                  <span className="text-[10px] text-slate-400 font-bold">Try sample:</span>
                  {samplePhotoUrls.map((url, idx) => (
                    <button
                      key={idx}
                      type="button"
                      onClick={() => setNewImageUrl(url)}
                      className="text-[10px] bg-slate-100 hover:bg-slate-200 text-slate-700 px-2 py-0.5 rounded-md"
                    >
                      Photo {idx + 1}
                    </button>
                  ))}
                </div>
              </div>

              <div className="flex items-center justify-end gap-2 pt-3 border-t border-slate-100">
                <button
                  type="button"
                  onClick={() => setIsUploadModalOpen(false)}
                  className="px-4 py-2 rounded-xl text-xs font-semibold text-slate-600 hover:bg-slate-100"
                >
                  {language === 'en' ? 'Cancel' : 'Ka Noqo'}
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 rounded-xl text-xs font-black bg-emerald-600 hover:bg-emerald-500 text-white shadow-sm"
                >
                  {language === 'en' ? 'Publish to Gallery' : 'Daabac Sawirka'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
