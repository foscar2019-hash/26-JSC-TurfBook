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
}
