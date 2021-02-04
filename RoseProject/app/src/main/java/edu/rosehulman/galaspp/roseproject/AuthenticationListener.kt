package edu.rosehulman.galaspp.roseproject

import android.os.Parcelable
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.parcel.Parcelize

interface AuthenticationListener {
    fun signOut()
}