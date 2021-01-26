package edu.rosehulman.galaspp.roseproject.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import edu.rosehulman.galaspp.roseproject.FragmentListener
import edu.rosehulman.galaspp.roseproject.ui.profile.ProfileAdapter
import edu.rosehulman.galaspp.roseproject.R
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.app_bar_main.view.*
import kotlinx.android.synthetic.main.fragment_sign_in.view.*

class LaunchFragment(var appBarView: AppBarLayout) : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_in, container, false)

        view.sign_in_button.setOnClickListener {
            if(context is FragmentListener){
                (context as FragmentListener).openFragment(WelcomeFragment(), false, "welcome")
                appBarView.isVisible = true
            }
//            view.findViewById<AppBarLayout>(app_bar_view.)

        }
        return view
    }
}