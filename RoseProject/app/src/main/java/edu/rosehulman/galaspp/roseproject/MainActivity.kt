package edu.rosehulman.galaspp.roseproject

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import edu.rosehulman.galaspp.roseproject.ui.NewUserFragment
import edu.rosehulman.galaspp.roseproject.ui.SplashFragment
import edu.rosehulman.galaspp.roseproject.ui.homepage.WelcomeFragment
import edu.rosehulman.galaspp.roseproject.ui.createeditteam.CreateEditTeamAdapter
import edu.rosehulman.galaspp.roseproject.ui.createeditteam.MemberObject
import edu.rosehulman.galaspp.roseproject.ui.createeditteam.NavDrawerAdapter
import edu.rosehulman.galaspp.roseproject.ui.createeditteam.TeamObject
import edu.rosehulman.galaspp.roseproject.ui.profile.ProfileFragment
import edu.rosehulman.galaspp.roseproject.ui.project.ProjectFragment
import edu.rosehulman.rosefire.Rosefire
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.add_remove_members_modal.view.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.create_team_modal.view.*

class MainActivity : AppCompatActivity(), NavDrawerAdapter.OnNavDrawerListener,
        FragmentListener , SplashFragment.OnLoginButtonPressedListener,
        AuthenticationListener
{
    // Create instance of FirebaseAuth and an AuthStateListener
    private val auth = FirebaseAuth.getInstance()
    lateinit var authStateListener: FirebaseAuth.AuthStateListener
    // Request code for launching the sign in Intent.
    private val RC_SIGN_IN = 1
    private val RC_ROSEFIRE_LOGIN = 1001
    // Firebase References
    private val membersRef = FirebaseFirestore
            .getInstance()
            .collection(Constants.MEMBER_COLLECTION)
    private val teamsRef = FirebaseFirestore
            .getInstance()
            .collection(Constants.TEAMS_COLLECTION)
    //Views
    override lateinit var fab: FloatingActionButton
    private lateinit var appBar : AppBarLayout
    //Useful global objects
    lateinit var userObject: MemberObject
    lateinit var navAdapter: NavDrawerAdapter
    private val WRITE_EXTERNAL_STORAGE_PERMISSION = 2
//    override var startFragment : String = Constants.WELCOME

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
        navAdapter = NavDrawerAdapter(nav_view.context, this)
        drawerRecyclerView.adapter = navAdapter
        drawerRecyclerView.layoutManager = LinearLayoutManager(this)
        drawerRecyclerView.setHasFixedSize(true)

        create_new_team_button.setOnClickListener{
            showCreateOrEditTeamModal(-1, navAdapter)
        }

        //Setup
        appBar = app_bar_view
        app_bar_view.isVisible = false
        ProfileFragment.listener = this
        fab.hide()
        initializeListeners()

        //Snapshot Listener for member object
        membersRef.addSnapshotListener { snapshot: QuerySnapshot?, exception: FirebaseFirestoreException? ->
            for (userChange in snapshot!!.documentChanges) {
                if (userChange.type == DocumentChange.Type.MODIFIED) {
                    Log.d(Constants.TAG, "User Changed")
                    userObject = MemberObject.fromSnapshot(userChange.document)
                    addTeamsToUserObject()
                }
            }
        }
        checkPermissions()
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
                openProfile(userObject)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //FRAGMENTS
    override fun openProfile(user: MemberObject){
        //Prevent multiple profiles being added to backstack
        val backStackSize = supportFragmentManager.backStackEntryCount
        if(backStackSize == 0 || supportFragmentManager.getBackStackEntryAt(backStackSize - 1).name != "profile"){
            //Add profile fragment
            val profileFragment = ProfileFragment.newInstance(user, this)
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

    override fun removeCurrentFragment(){
        onBackPressed()
    }

    override fun getUpdatedUser(): MemberObject? {
        return navAdapter.userObject
    }

    override fun openProjectFromHome(teamID: String, projID: String, taskType : Int) {
        val team = navAdapter.teams.filter { teamObject ->  teamObject.id == teamID}[0]
        val proj = team.projects.filter { projectObject -> projectObject.id == projID }[0]
        openFragment(ProjectFragment.newInstance(proj, userObject.id, teamID, taskType), true, proj.projectTitle)
    }

    //MODALS
    override fun onEditTeamItemSelected(position: Int, adapter: NavDrawerAdapter) {
        showCreateOrEditTeamModal(position, adapter)
    }

    private fun showCreateOrEditTeamModal(position: Int = -1, adapterNav: NavDrawerAdapter){
        //Create Builder
        val builder = AlertDialog.Builder(this)
        builder.setTitle( if(position!=-1) "Edit Team" else "Create Team")
        val view = LayoutInflater.from(this).inflate(R.layout.create_team_modal, null, false)
        builder.setView(view)
        //Add Recycler View Layout
        val recyclerView = view.create_edit_team_recycler_view
        val adapter = CreateEditTeamAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        //Add Listener to add Members
        view.create_team_add_members_modal.setOnClickListener{
            showAddRemoveMemberModal(adapter, view, position, adapterNav)
        }
        if(position != -1){
            view.edit_text_team_name.setText(adapterNav.getTeamDetails(position).teamName)
            view.edit_text_team_description.setText(adapterNav.getTeamDetails(position).teamDescription)
            membersRef.get().addOnSuccessListener {
                for(snapshot in it){
                    if(adapterNav.getTeamDetails(position).teamMemberReferences.contains(snapshot.id)){
                        val memberObject = MemberObject.fromSnapshot(snapshot)
                        if(memberObject.statuses[adapterNav.getTeamDetails(position).id] == Constants.OWNER)
                            adapter.addMember(memberObject, snapshot.id, 1)
                        else
                            adapter.addMember(memberObject, snapshot.id, 0)
                    }
                }
            }
        }

        //Set Buttons
        builder.setPositiveButton("Save") { _, _ ->
            //DONE: Create or Update Team Here
            if(position == -1){
                val list = adapter.getMemberObjectIds()
                val statusList = adapter.getMemberStatusList()
                list.add(userObject.id)
                adapterNav.addTeam(TeamObject(view.edit_text_team_name.text.toString(),
                        view.edit_text_team_description.text.toString(),
                        list
                ), list, statusList)
            } else{
                val list = adapter.getMemberObjectIds()
                val statusList = adapter.getMemberStatusList()
                adapterNav.editTeamAtPosition(position,
                        view.edit_text_team_name.text.toString(),
                        view.edit_text_team_description.text.toString(),
                        adapter.getMemberObjectIds()
                )
                for(i in 0 until list.size){
                    var nestedData: HashMap<String, String>
                    if(statusList[i] == 0){
                        nestedData = hashMapOf(adapterNav.getTeamDetails(position).id to Constants.MEMBER)
                    } else {
                        nestedData = hashMapOf(adapterNav.getTeamDetails(position).id to Constants.OWNER)
                    }
                    val data = hashMapOf(Constants.STATUSES_FIELD to nestedData)
                    membersRef.document(list[i]).set(data, SetOptions.merge())
                }
            }
        }
        //Check permissions for current user to edit or create a team
        if((position != -1 && userObject.statuses[navAdapter.getTeamDetails(position).id] == Constants.OWNER) || position == -1) {
            builder.setNegativeButton(android.R.string.cancel, null)
            builder.create().show()
        } else {
            val parentLayout = findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, "You do not have this permission!", Snackbar.LENGTH_LONG).show()
        }

    }

    private fun showAddRemoveMemberModal(adapter: CreateEditTeamAdapter, addTeamView: View, position: Int, adapterNav: NavDrawerAdapter){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add/Remove Member")
        val view = LayoutInflater.from(this).inflate(R.layout.add_remove_members_modal, null, false)
        builder.setView(view)

        //Set Autocomplete
        val autoAdapter = ArrayAdapter(this, android.R.layout.select_dialog_item, getAllMemberNames())
        view.edit_text_member_username.threshold = 1
        view.edit_text_member_username.setAdapter(autoAdapter)

        val arrayVal = resources.getStringArray(R.array.member_Permissions)
        val aa = ArrayAdapter(view.context, android.R.layout.simple_spinner_item, arrayVal)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        view.userPermissionSpinner.adapter = aa

        builder.setPositiveButton(R.string.add) { _, _ ->
            membersRef.whereEqualTo("name", view.edit_text_member_username.text.toString()).get()
                .addOnSuccessListener {
                    if(!it.isEmpty){
                        adapter.addMember(MemberObject.fromSnapshot(it.documents[0]), it.documents[0].id, view.userPermissionSpinner.selectedItemPosition)
                    } else{
                        Snackbar.make(addTeamView, "User ${view.edit_text_member_username.text} does not exist", Snackbar.LENGTH_LONG).show()
                    }
            }
        }
        builder.setNeutralButton(android.R.string.cancel, null)
        builder.setNegativeButton("Remove") { _, _ ->
            adapter.removeName(view.edit_text_member_username.text.toString())
        }
        builder.create().show()
    }

    private fun getAllMemberNames() : ArrayList<String> {
        //Access members reference and extract name of each user, return in array
        val ret = ArrayList<String>()
        membersRef.get().addOnSuccessListener { snapshot: QuerySnapshot ->
            for (doc in snapshot) ret.add(MemberObject.fromSnapshot(doc).name)
            ret.remove(userObject.name)//Prevents adding self to list and changing status
        }
        return ret
    }

    //AUTHENTICATION
    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        Log.d(Constants.TAG, "STOPPPPED")
        auth.removeAuthStateListener(authStateListener)
    }

    private fun initializeListeners() {
        //Create an AuthStateListener that passes the UID to the MovieQuoteFragment if the user is
        // logged in and goes back to the Splash fragment otherwise.
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
                val userID = user.uid
                membersRef.whereEqualTo("id", userID).get().addOnSuccessListener {
                    if(it.isEmpty){
                        Log.d(Constants.TAG, "New User")
                        val newMember = MemberObject("", user.displayName ?: "", userID)
                        membersRef.document(userID).set(newMember).addOnSuccessListener {
                            userObject = newMember
                            addTeamsToUserObject()
                            navAdapter.logout()
                            navAdapter.setup(userObject)
                            app_bar_view.isVisible = false
                            openFragment(NewUserFragment(this, userObject, app_bar_view), false, "new user")
                        }
                    } else {
                        Log.d(Constants.TAG, "Old User")
                        userObject = MemberObject.fromSnapshot(it.documents[0])
                        app_bar_view.isVisible = true
                        startFirstFragment()
//                        openFragment(WelcomeFragment(userObject.userName), false, "welcome")
                        navAdapter.userObject = userObject
                        addTeamsToUserObject()
                        navAdapter.logout()
                        navAdapter.setup(userObject)
                    }
                }
            } else {
                openFragment(SplashFragment(), false, "splash")
                app_bar_view.isVisible = false
            }
        }
    }

    private fun startFirstFragment(){
        //Used to return to fragment after a picture is retrieved from the camera or gallery
        Log.d(Constants.TAG, "NU: ${NewUserFragment.hasPicture}")
        if(ProfileFragment.hasPicture){
            ProfileFragment.hasPicture = false
            openProfile(userObject)
        } else if(NewUserFragment.hasPicture){
            NewUserFragment.hasPicture = false
            openFragment(NewUserFragment(this, userObject, appBar), false, "new user")
        } else {
            openFragment(WelcomeFragment(userObject, this), false, "welcome")
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun addTeamsToUserObject() {
        membersRef.document(userObject.id).get().addOnSuccessListener {
            val ids = (it[Constants.STATUSES_FIELD] as Map<String, String>).keys
            for (id in ids) {
                teamsRef.document(id).get().addOnSuccessListener { snapshot: DocumentSnapshot ->
                    val team = TeamObject.fromSnapshot(snapshot)
                    if(!userObject.teams.contains(team)){
                        userObject.teams.add(team)
                    }
                }
            }
        }
    }

    @Suppress("CanBeVal")
    override fun onLoginButtonPressed(providerType: Int) {
        var loginIntent : Intent?
        when (providerType){
            Constants.PROVIDER_EMAIL -> {
                loginIntent = AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(arrayListOf(AuthUI.IdpConfig.EmailBuilder().build()))
                        .setTheme(R.style.LoginTheme)
                        .build()
                startActivityForResult(loginIntent, RC_SIGN_IN)
            }
            Constants.PROVIDER_GOOGLE -> {
                loginIntent = AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build()))
                        .setTheme(R.style.LoginTheme)
                        .build()
                startActivityForResult(loginIntent, RC_SIGN_IN)
            }
            Constants.PROVIDER_ROSE -> {
                loginIntent = Rosefire.getSignInIntent(this, Constants.ROSE_STRING)
                startActivityForResult(loginIntent, RC_ROSEFIRE_LOGIN)
            }
            else -> Log.e("Provider Error", "Authentication provider does not exist")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_ROSEFIRE_LOGIN) {
            val result = Rosefire.getSignInResultFromIntent(data)
            FirebaseAuth.getInstance().signInWithCustomToken(result.token)
        }
    }

    override fun signOut() {
        appBar.isVisible = false
        navAdapter.logout()
        onBackPressed() //Tom Foolery
        supportFragmentManager.fragments.clear()
        Log.d(Constants.TAG, "F on backstack: ${supportFragmentManager.fragments.size}")
        auth.signOut()
    }

    // CAMERA
    // Android’s security policy requires permissions to be requested
    // before some features are used.
    private fun checkPermissions() {
        // Check to see if we already have permissions
        if (ContextCompat
                .checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            // If we do not, request them from the user
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                WRITE_EXTERNAL_STORAGE_PERMISSION
            )
        }
    }
    // Callback once permissions are granted.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            WRITE_EXTERNAL_STORAGE_PERMISSION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    Log.d(Constants.TAG, "Permission granted")
                } else {
                    // permission denied
                }
                return
            }
        }
    }
}