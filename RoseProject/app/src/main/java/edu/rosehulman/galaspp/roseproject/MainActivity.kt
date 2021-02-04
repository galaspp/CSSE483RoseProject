package edu.rosehulman.galaspp.roseproject

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.auth.FirebaseAuth
import edu.rosehulman.galaspp.roseproject.ui.SplashFragment
import edu.rosehulman.galaspp.roseproject.ui.createeditteam.CreateEditTeamAdapter
import edu.rosehulman.galaspp.roseproject.ui.WelcomeFragment
import edu.rosehulman.galaspp.roseproject.ui.createeditteam.NavDrawerAdapter
import edu.rosehulman.galaspp.roseproject.ui.createeditteam.TeamObject
import edu.rosehulman.galaspp.roseproject.ui.profile.ProfileFragment
import edu.rosehulman.galaspp.roseproject.ui.profile.ProfileModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.add_remove_members_modal.view.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.create_team_modal.view.*

class MainActivity : AppCompatActivity(), NavDrawerAdapter.OnNavDrawerListener,
        FragmentListener , SplashFragment.OnLoginButtonPressedListener,
        AuthenticationListener
{
    // Done: Create instance of FirebaseAuth and an AuthStateListener
    private val auth = FirebaseAuth.getInstance()
    lateinit var authStateListener: FirebaseAuth.AuthStateListener
    // Request code for launching the sign in Intent.
    private val RC_SIGN_IN = 1
//
    override lateinit var fab: FloatingActionButton
    private lateinit var appBar : AppBarLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        fab = findViewById(R.id.fab)

        //Navigation Drawer Start
        //https://www.droidcon.com/news-detail?content-id=/repository/collaboration/Groups/spaces/droidcon_hq/Documents/public/news/android-news/Android%20Material%20Component!%20An%20easy%20approach%20to%20Navigation%20Drawer%20(Part%20I)
        setSupportActionBar(toolbar)
        val drawerToggle = ActionBarDrawerToggle(this, drawer_layout, R.string.open, R.string.close)
        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val drawerRecyclerView = nav_view.recycler_view_nav_drawer
        val adapter = NavDrawerAdapter(nav_view.context, this)
        drawerRecyclerView.adapter = adapter
        drawerRecyclerView.layoutManager = LinearLayoutManager(this)
        drawerRecyclerView.setHasFixedSize(true)

        create_new_team_button.setOnClickListener{
            showCreateOrEditTeamModal(-1, adapter)
        }

        //Setup
        appBar = app_bar_view
        app_bar_view.isVisible = false
        ProfileFragment.listener = this
        fab.hide()

        initializeListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawer_layout.openDrawer(GravityCompat.START)
                true
            }
            R.id.action_profile -> {
                fab.hide()
                openProfile(ProfileModel())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //adds one profile to backstack
    private fun openProfile(profile: ProfileModel){
        //Prevent multiple profiles being added to backstack
        if(supportFragmentManager.backStackEntryCount == 0){
            val profileFragment = ProfileFragment.newInstance(profile)
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragment_container, profileFragment)
            ft.addToBackStack("profile")
            ft.commit()
        }
    }

    override fun openFragment(fragment: Fragment, addToBackStack: Boolean, name: String){
        appBar.isVisible = true
        drawer_layout.closeDrawer(GravityCompat.START)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, fragment)
        if(addToBackStack) ft.addToBackStack(name)
        ft.commit()
    }

    override fun onEditTeamItemSelected(position: Int, adapter: NavDrawerAdapter) {
        showCreateOrEditTeamModal(position, adapter)
    }


    private fun showCreateOrEditTeamModal(position: Int = -1, adapterNav: NavDrawerAdapter)
    {
        val builder = AlertDialog.Builder(this)
        //TODO: Change title based on whether editing or creating team
        //TODO: Prepopulate items as needed
        if(position != -1) {
            builder.setTitle("Edit Team?")
        }
        else {
            builder.setTitle("Create Team?")
        }

        val view = LayoutInflater.from(this).inflate(R.layout.create_team_modal, null, false)
        builder.setView(view)

        //TODO: Add Recycler View Layout
        //Maybe add to a different file
        val recyclerView = view.create_edit_team_recycler_view
        val adapter = CreateEditTeamAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        view.create_team_add_members_modal.setOnClickListener{
            showAddRemoveMemberModal(adapter)
        }

        if(position != -1)
        {
            view.edit_text_team_name.setText(adapterNav.getTeamDetails(position).teamName)
            view.edit_text_team_description.setText(adapterNav.getTeamDetails(position).teamDescription)
//            adapter.setListOfMembers(adapterNav.getTeamDetails(position).members)
//            adapter.setListOfMembers(adapterNav.getTeamDetails(position).members)
        }

        builder.setPositiveButton("Save") { _, _ ->
            //TODO: Create or Update Team Here
            if(position == -1)
            {
//                adapterNav.addTeam(TeamObject(view.edit_text_team_name.text.toString(),
//                    view.edit_text_team_description.text.toString(),
//                    adapter.getListOfMembers()
//                        ))
                adapterNav.addTeam(TeamObject(view.edit_text_team_name.text.toString(),
                    view.edit_text_team_description.text.toString()
                ))
            }
            else
            {
                adapterNav.editTeamAtPosition(position,
                        view.edit_text_team_name.text.toString(),
                        view.edit_text_team_description.text.toString(),
                        adapter.getListOfMembers()
                )
            }

        }

        builder.setNegativeButton(android.R.string.cancel, null)

        builder.create().show()
    }

    private fun showCreateProjectModal()
    {
        val builder = AlertDialog.Builder(this)
        //TODO: Change title based on whether editing or creating Project
        //TODO: Prepopulate items as needed
        builder.setTitle("Create Project? (Admin Only)")

        val view = LayoutInflater.from(this).inflate(R.layout.create_project_modal, null, false)
        builder.setView(view)

        builder.setPositiveButton("Save") { _, _ ->

        }

        builder.setNegativeButton(android.R.string.cancel, null)

        builder.create().show()
    }

    private fun showAddRemoveMemberModal(adapter: CreateEditTeamAdapter)
    {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add/Remove Member")

        val view = LayoutInflater.from(this).inflate(R.layout.add_remove_members_modal, null, false)
        builder.setView(view)

        //TODO: Add Spinner
        val arrayVal = resources.getStringArray(R.array.member_Permissions)
        val aa = ArrayAdapter(view.context, android.R.layout.simple_spinner_item, arrayVal)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        view.userPermissionSpinner.adapter = aa

        builder.setPositiveButton(R.string.add) { _, _ ->
            adapter.addName(view.edit_text_member_username.text.toString(), view.userPermissionSpinner.selectedItem.toString())
        }

        builder.setNeutralButton(android.R.string.cancel, null)

        builder.setNegativeButton("Remove") { _, _ ->
            adapter.removeName(view.edit_text_member_username.text.toString())
        }
        builder.create().show()
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authStateListener)
    }

    private fun initializeListeners() {
        // TODO: Create an AuthStateListener that passes the UID
        // to the MovieQuoteFragment if the user is logged in
        // and goes back to the Splash fragment otherwise.
        // See https://firebase.google.com/docs/auth/users#the_user_lifecycle
        authStateListener = FirebaseAuth.AuthStateListener { auth: FirebaseAuth ->
            val user = auth.currentUser
            Log.d(Constants.TAG, "In auth listener, User: $user")
            if (user != null) {
                Log.d(Constants.TAG, "UID: ${user.uid}")
                Log.d(Constants.TAG, "Name: ${user.displayName}")
                Log.d(Constants.TAG, "Email: ${user.email}")
                Log.d(Constants.TAG, "Phone: ${user.phoneNumber}")
                Log.d(Constants.TAG, "Photo URL: ${user.photoUrl}")
                // plus email, photoUrl, phoneNumber
                //TODO: Store UID somewhere to use for other fragments
//                switchToMovieQuoteFragment(user.uid)
                openFragment(WelcomeFragment(), false, "welcome")
                app_bar_view.isVisible = true
            } else {
                openFragment(SplashFragment(), false, "splash")
                app_bar_view.isVisible = false
            }
        }

    }

    override fun onLoginButtonPressed() {
        launchLoginUI()
    }

    private fun launchLoginUI() {
        // DONE: Build a login intent and startActivityForResult(intent, ...)
        // For details, see https://firebase.google.com/docs/auth/android/firebaseui#sign_in
        // Choose authentication providers
        val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.PhoneBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val loginIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
//                .setLogo(R.drawable.ic_launcher_custom)
                .build()

        // Create and launch sign-in intent
        startActivityForResult(loginIntent, RC_SIGN_IN)
    }

    override fun signOut() {
        appBar.isVisible = false
        auth.signOut()
    }
}