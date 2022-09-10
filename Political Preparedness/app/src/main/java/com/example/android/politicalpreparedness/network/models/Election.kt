package com.example.android.politicalpreparedness.network.models

import android.os.Parcelable
import androidx.room.*
import com.example.android.politicalpreparedness.network.jsonadapter.DateAdapter
import com.example.android.politicalpreparedness.network.jsonadapter.ElectionAdapter
import com.squareup.moshi.*
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "election_table")
data class Election(
        @PrimaryKey val id: Int,
        @ColumnInfo(name = "name")val name: String,
        @ColumnInfo(name = "electionDay")val electionDay: Date,
        @Embedded(prefix = "division_") @Json(name="ocdDivisionId") val division: Division
) : Parcelable

fun Election.dateToFormatedDate() = DateAdapter().dateFromJson(
        DateAdapter().dateToJson(electionDay)
)

fun Election.ocdDivisionIdToDivision() = ElectionAdapter().divisionFromJson(
        ElectionAdapter().divisionToJson(division)
)
