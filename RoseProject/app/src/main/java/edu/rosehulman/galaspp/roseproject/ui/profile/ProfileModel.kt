package edu.rosehulman.galaspp.roseproject.ui.profile

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProfileModel (var username: String = "reidcj", var name: String = "Cameron Reid"): Parcelable
//Todo: Add teams and profile pic variables