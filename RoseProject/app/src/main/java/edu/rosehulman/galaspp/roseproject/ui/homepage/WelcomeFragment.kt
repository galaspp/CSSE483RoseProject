package edu.rosehulman.galaspp.roseproject.ui.homepage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import edu.rosehulman.galaspp.roseproject.Constants
import edu.rosehulman.galaspp.roseproject.FragmentListener
import edu.rosehulman.galaspp.roseproject.R
import edu.rosehulman.galaspp.roseproject.ui.createeditteam.MemberObject
import kotlinx.android.synthetic.main.fragment_welcome.view.*

class WelcomeFragment(var user: MemberObject, val listener : FragmentListener) : Fragment() {

    private lateinit var adapter : HomepageAdapter

    private val tasksRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.TASKS_COLLECTION)
    private val membersRef = FirebaseFirestore
            .getInstance()
            .collection(Constants.MEMBER_COLLECTION)

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_welcome, container, false)

        listener.fab.hide()

        view.welcome_name_text_view.text = "Welcome, ${user.name.substringBefore(" ")}"
        //Create recycler view for list of teams
        val recyclerView = view.findViewById<RecyclerView>(R.id.homepage_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        adapter = HomepageAdapter(context, user, listener)
        recyclerView.adapter = adapter
        reload()
        membersRef.addSnapshotListener { snapshot: QuerySnapshot? , error ->
            for(docChange in snapshot?.documentChanges!!){
                val mo = MemberObject.fromSnapshot(docChange.document)
                if(mo.id == user.id && mo.statuses != user.statuses){
                    this.user = mo
                    Log.d(Constants.TAG, mo.statuses.toString())
                    reload()
                }
            }

        }

        return view
    }

    fun reload() {
        Log.d(Constants.TAG, "RELOAD")
        adapter.reload(user)
    }

}