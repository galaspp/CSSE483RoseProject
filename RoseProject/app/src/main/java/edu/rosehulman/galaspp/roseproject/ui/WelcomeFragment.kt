package edu.rosehulman.galaspp.roseproject.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import edu.rosehulman.galaspp.roseproject.R
import kotlinx.android.synthetic.main.fragment_welcome.view.*

class WelcomeFragment(val name: String) : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_welcome, container, false)

        view.welcome_name_text_view.text = name

        return view
    }
}