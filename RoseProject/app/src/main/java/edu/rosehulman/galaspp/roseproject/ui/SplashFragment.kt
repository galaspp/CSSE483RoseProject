package edu.rosehulman.galaspp.roseproject.ui


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.rosehulman.galaspp.roseproject.Constants
import edu.rosehulman.galaspp.roseproject.R
import kotlinx.android.synthetic.main.fragment_splash.view.*

class SplashFragment : Fragment() {
    var listener: OnLoginButtonPressedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_splash, container, false)
        view.login_button_google.setOnClickListener {
            listener?.onLoginButtonPressed(Constants.PROVIDER_GOOGLE)
        }
        view.login_button_email.setOnClickListener {
            listener?.onLoginButtonPressed(Constants.PROVIDER_EMAIL)
        }
        view.login_button_rose.setOnClickListener {
            listener?.onLoginButtonPressed(Constants.PROVIDER_ROSE)
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnLoginButtonPressedListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnLoginButtonPressedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnLoginButtonPressedListener {
        fun onLoginButtonPressed(providerType: Int)
    }
}
