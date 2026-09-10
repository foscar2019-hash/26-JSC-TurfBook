package com.example.turfbook.data.local

import androidx.room.TypeConverter
import com.example.turfbook.data.model.GameFormat
import com.example.turfbook.data.model.PaymentMethod
import com.example.turfbook.data.model.SurfaceType
import com.example.turfbook.data.model.TeamMember
import com.example.turfbook.data.model.TeamStats
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TurfConverters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromStringList(value: List<String>): String = json.encodeToString(value)

    @TypeConverter
    fun toStringList(value: String): List<String> = try {
        json.decodeFromString(value)
    } catch (e: Exception) {
        emptyList()
    }

    @TypeConverter
    fun fromTeamMemberList(value: List<TeamMember>): String = json.encodeToString(value)

    @TypeConverter
    fun toTeamMemberList(value: String): List<TeamMember> = try {
        json.decodeFromString(value)
    } catch (e: Exception) {
        emptyList()
    }

    @TypeConverter
    fun fromTeamStats(value: TeamStats): String = json.encodeToString(value)

    @TypeConverter
    fun toTeamStats(value: String): TeamStats = try {
        json.decodeFromString(value)
    } catch (e: Exception) {
        TeamStats()
    }

    @TypeConverter
    fun fromGameFormat(value: GameFormat): String = value.name

    @TypeConverter
    fun toGameFormat(value: String): GameFormat = try {
        GameFormat.valueOf(value)
    } catch (e: Exception) {
        GameFormat.ALL
    }

    @TypeConverter
    fun fromSurfaceType(value: SurfaceType): String = value.name

    @TypeConverter
    fun toSurfaceType(value: String): SurfaceType = try {
        SurfaceType.valueOf(value)
    } catch (e: Exception) {
        SurfaceType.FIFA_SYNTHETIC
    }

    @TypeConverter
    fun fromPaymentMethod(value: PaymentMethod): String = value.name

    @TypeConverter
    fun toPaymentMethod(value: String): PaymentMethod = try {
        PaymentMethod.valueOf(value)
    } catch (e: Exception) {
        PaymentMethod.ZAAD
    }
}
