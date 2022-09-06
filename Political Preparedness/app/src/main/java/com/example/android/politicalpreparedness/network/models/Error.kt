package com.example.android.politicalpreparedness.network.models


data class Error (
  val code    : Int,
  val message : String,
  val errors  : List<Errors>
)