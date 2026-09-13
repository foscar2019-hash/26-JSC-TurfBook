package com.example.turfbook.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TurfDao {
    // Pitches
    @Query("SELECT * FROM pitches")
    fun getAllPitches(): Flow<List<PitchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPitches(pitches: List<PitchEntity>)

    @Update
    suspend fun updatePitch(pitch: PitchEntity)

    @Query("UPDATE pitches SET status = :status WHERE id = :pitchId")
    suspend fun updatePitchStatus(pitchId: String, status: String)

    // Bookings
    @Query("SELECT * FROM bookings ORDER BY date DESC, startTime DESC")
    fun getAllBookings(): Flow<List<BookingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookings(bookings: List<BookingEntity>)

    @Query("DELETE FROM bookings WHERE id = :bookingId")
    suspend fun deleteBooking(bookingId: String)

    // Blocked slots
    @Query("SELECT * FROM blocked_slots")
    fun getAllBlockedSlots(): Flow<List<BlockedSlotEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedSlot(slot: BlockedSlotEntity)

    @Query("DELETE FROM blocked_slots WHERE id = :slotId")
    suspend fun deleteBlockedSlot(slotId: String)

    // Teams
    @Query("SELECT * FROM teams")
    fun getAllTeams(): Flow<List<TeamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeam(team: TeamEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeams(teams: List<TeamEntity>)

    @Update
    suspend fun updateTeam(team: TeamEntity)

    // Challenges
    @Query("SELECT * FROM match_challenges ORDER BY createdAt DESC")
    fun getAllChallenges(): Flow<List<MatchChallengeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenge(challenge: MatchChallengeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenges(challenges: List<MatchChallengeEntity>)

    @Query("UPDATE match_challenges SET status = :status WHERE id = :challengeId")
    suspend fun updateChallengeStatus(challengeId: String, status: String)

    // Gallery
    @Query("SELECT * FROM gallery_images")
    fun getAllGalleryImages(): Flow<List<GalleryImageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGalleryImages(images: List<GalleryImageEntity>)

    @Query("UPDATE gallery_images SET likes = likes + 1 WHERE id = :imageId")
    suspend fun incrementLikes(imageId: String)

    // Referrals
    @Query("SELECT * FROM referrals ORDER BY date DESC")
    fun getAllReferrals(): Flow<List<ReferralEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReferral(referral: ReferralEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReferrals(referrals: List<ReferralEntity>)

    // Pitch Reviews
    @Query("SELECT * FROM pitch_reviews ORDER BY date DESC")
    fun getAllReviews(): Flow<List<PitchReviewEntity>>

    @Query("SELECT * FROM pitch_reviews WHERE pitchId = :pitchId ORDER BY date DESC")
    fun getReviewsForPitch(pitchId: String): Flow<List<PitchReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: PitchReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<PitchReviewEntity>)

    // Waitlist
    @Query("SELECT * FROM waitlist_entries ORDER BY createdAt ASC")
    fun getAllWaitlistEntries(): Flow<List<WaitlistEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaitlistEntry(entry: WaitlistEntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaitlistEntries(entries: List<WaitlistEntryEntity>)

    @Query("DELETE FROM waitlist_entries WHERE id = :id")
    suspend fun deleteWaitlistEntry(id: String)

    @Query("UPDATE waitlist_entries SET status = :status WHERE id = :id")
    suspend fun updateWaitlistStatus(id: String, status: String)
}

