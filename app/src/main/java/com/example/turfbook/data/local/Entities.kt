package com.example.turfbook.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.turfbook.data.model.GameFormat
import com.example.turfbook.data.model.PaymentMethod
import com.example.turfbook.data.model.SurfaceType
import com.example.turfbook.data.model.TeamMember
import com.example.turfbook.data.model.TeamStats

@Entity(tableName = "pitches")
data class PitchEntity(
    @PrimaryKey val id: String,
    val name: String,
    val somaliName: String,
    val format: GameFormat,
    val surface: SurfaceType,
    val hourlyRate: Double,
    val dayRate: Double,
    val nightRate: Double,
    val dimensions: String,
    val capacity: String,
    val features: List<String>,
    val imageUrl: String,
    val isFloodlit: Boolean,
    val status: String
)

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey val id: String,
    val referenceCode: String,
    val pitchId: String,
    val pitchName: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val durationHours: Int,
    val customerName: String,
    val teamName: String,
    val customerPhone: String,
    val customerEmail: String,
    val paymentMethod: PaymentMethod,
    val paymentStatus: String,
    val transactionId: String,
    val merchantNumber: String,
    val totalAmount: Double,
    val loyaltyPoints: Int = 0,
    val addOns: List<String>,
    val notes: String,
    val createdAt: String,
    val smsConfirmed: Boolean
)

@Entity(tableName = "blocked_slots")
data class BlockedSlotEntity(
    @PrimaryKey val id: String,
    val pitchId: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val reason: String
)

@Entity(tableName = "teams")
data class TeamEntity(
    @PrimaryKey val id: String,
    val name: String,
    val logoEmoji: String,
    val color: String,
    val captainName: String,
    val captainPhone: String,
    val preferredFormat: GameFormat,
    val skillLevel: String,
    val bio: String,
    val joinedDate: String,
    val stats: TeamStats,
    val members: List<TeamMember>
)

@Entity(tableName = "match_challenges")
data class MatchChallengeEntity(
    @PrimaryKey val id: String,
    val challengerTeamId: String,
    val challengerTeamName: String,
    val challengerCaptainPhone: String,
    val challengedTeamId: String,
    val challengedTeamName: String,
    val challengedCaptainPhone: String,
    val pitchId: String,
    val pitchName: String,
    val date: String,
    val slot: String,
    val totalAmount: Double,
    val splitMode: String,
    val status: String,
    val notes: String,
    val bookingRef: String,
    val createdAt: String
)

@Entity(tableName = "gallery_images")
data class GalleryImageEntity(
    @PrimaryKey val id: String,
    val title: String,
    val somaliTitle: String,
    val category: String,
    val imageUrl: String,
    val date: String,
    val pitchName: String,
    val teamsInvolved: String,
    val likes: Int
)
