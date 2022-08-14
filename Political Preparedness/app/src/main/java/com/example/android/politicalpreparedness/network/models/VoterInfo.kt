package com.example.android.politicalpreparedness.network.models


data class VoterInfo (
    val election: Election,
    val pollingLocations: String?,
    val party: String? = null,
    val phones: List<String>? = null,
    val urls: List<String>? = null,
    val photoUrl: String? = null,
    val channels: List<Channel>? = null
)