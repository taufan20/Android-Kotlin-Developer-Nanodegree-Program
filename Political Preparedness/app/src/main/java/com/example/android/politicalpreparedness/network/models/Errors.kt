package com.example.android.politicalpreparedness.network.models


data class Errors (
  val message : String,
  val domain  : String,
  val reason  : String
)