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
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.galaspp.roseproject.ui.CustomExpandableListAdapter
import edu.rosehulman.galaspp.roseproject.ui.ExpandableListDataPump
import edu.rosehulman.galaspp.roseproject.ui.createeditteam.CreateEditTeamAdapter
import edu.rosehulman.galaspp.roseproject.ui.WelcomeFragment
import edu.rosehulman.galaspp.roseproject.ui.createeditteam.NavDrawerAdapter
import edu.rosehulman.galaspp.roseproject.ui.createeditteam.TeamObject
import edu.rosehulman.galaspp.roseproject.ui.profile.ProfileFragment
import edu.rosehulman.galaspp.roseproject.ui.profile.ProfileModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.add_remove_members_modal.view.*
import kotlinx.android.synthetic.main.create_edit_task_modal.view.*
import kotlinx.android.synthetic.main.create_team_modal.view.*

class MainActivity : AppCompatActivity(), NavDrawerAdapter.OnNavDrawerListener {

//    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        fab = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

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
        //Navigation Drawer End

        openFragment(WelcomeFragment(fab))
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

    private fun openProfile(profile: ProfileModel){
        val profileFragment = ProfileFragment.newInstance(profile)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, profileFragment)
        ft.addToBackStack("profile")
        ft.commit()

    }

    private fun openFragment(fragment: Fragment){
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, fragment)
//        ft.addToBackStack("welcome")
        ft.commit()
    }

//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment)
//        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
//    }

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
            adapter.setListOfMembers(adapterNav.getTeamDetails(position).members)
        }

        builder.setPositiveButton("Save") { _, _ ->
            //TODO: Create or Update Team Here
            if(position == -1)
            {
                adapterNav.addTeam(TeamObject(view.edit_text_team_name.text.toString(),
                    view.edit_text_team_description.text.toString(),
                    adapter.getListOfMembers()
                        ))
            }
            else
            {
                adapterNav.editTeamAtPosition(position,
                        view.edit_text_team_name.text.toString(),
                        view.edit_text_team_description.text.toString(),
                        adapter.getListOfMembers(),
                        adapterNav.getListOfProjects(position)
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

    private fun showCreateorEditTaskModal()
    {
        val builder = AlertDialog.Builder(this)
        //TODO: Change title based on whether editing or creating team
        //TODO: Prepopulate items as needed
        builder.setTitle("Create Task?")

        val view = LayoutInflater.from(this).inflate(R.layout.create_edit_task_modal, null, false)
        builder.setView(view)

        var arrayVal = resources.getStringArray(R.array.task_status_arrry)
        var aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayVal)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        view.spinner.adapter = aa

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
}