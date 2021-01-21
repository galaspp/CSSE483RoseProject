package edu.rosehulman.galaspp.roseproject

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ExpandableListView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import edu.rosehulman.galaspp.roseproject.ui.CustomExpandableListAdapter
import edu.rosehulman.galaspp.roseproject.ui.ExpandableListDataPump
import edu.rosehulman.galaspp.roseproject.ui.WelcomeFragment
import edu.rosehulman.galaspp.roseproject.ui.profile.ProfileFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_remove_members_modal.view.*
import kotlinx.android.synthetic.main.create_edit_task_modal.*
import kotlinx.android.synthetic.main.create_edit_task_modal.view.*
import kotlinx.android.synthetic.main.drawer_row_view.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            showAddRemoveModal()
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
        //Navigation Drawer End

        openFragment(WelcomeFragment())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                populateMenu()
                drawer_layout.openDrawer(GravityCompat.START)
                true
            }
            R.id.action_profile -> {
                openFragment(ProfileFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun populateMenu(){
        val expandableListView : ExpandableListView = findViewById(R.id.expandableListView)
        val expandableListDetail = ExpandableListDataPump.getData()
        val expandableListTitle = ArrayList<String>(expandableListDetail!!.keys)
        val expandableListAdapter = CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail)
        expandableListView.setAdapter(expandableListAdapter)
        expandableListView.setOnGroupExpandListener{
            fun onGroupExpand(groupPosition : Int) {
                Toast.makeText(applicationContext,
                        expandableListTitle.get(groupPosition) + " List Expanded.",
                        Toast.LENGTH_SHORT).show();
            }
        }

        expandableListView.setOnGroupCollapseListener{
            fun onGroupCollapse(groupPosition: Int) {
                Toast.makeText(applicationContext,
                        expandableListTitle.get(groupPosition) + " List Collapsed.",
                        Toast.LENGTH_SHORT).show()

            }
        }

        expandableListView.setOnChildClickListener{ expandableListView: ExpandableListView, v: View,
                groupPosition: Int, childPosition: Int, id: Long ->
                Toast.makeText(
                        applicationContext,
                        expandableListTitle.get(groupPosition)
                                + " -> "
                                + expandableListDetail.get(
                                expandableListTitle.get(groupPosition))!![childPosition], Toast.LENGTH_SHORT
                ).show()
                false
        }
    }

    private fun openFragment(fragment: Fragment){
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, fragment)
        ft.commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    private fun showCreateOrEditTeamModal()
    {
        val builder = AlertDialog.Builder(this)
        //TODO: Change title based on whether editing or creating team
        //TODO: Prepopulate items as needed
        builder.setTitle("Create Team?")

        val view = LayoutInflater.from(this).inflate(R.layout.create_team_modal, null, false)
        builder.setView(view)

        builder.setPositiveButton("Save") { _, _ ->

        }

        builder.setNegativeButton(android.R.string.cancel, null)

        builder.create().show()
    }

    private fun showCreateProjectModal()
    {
        val builder = AlertDialog.Builder(this)
        //TODO: Change title based on whether editing or creating team
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

    private fun showAddRemoveModal()
    {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add/Remove Member")

        val view = LayoutInflater.from(this).inflate(R.layout.add_remove_members_modal, null, false)
        builder.setView(view)

        //TODO: Add Spinner
        val arrayVal = resources.getStringArray(R.array.member_Permissions)
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayVal)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        view.userPermissionSpinner.adapter = aa

        builder.setPositiveButton(R.string.add) { _, _ ->

        }


        builder.setNeutralButton(android.R.string.cancel, null)

        builder.setNegativeButton("Remove") { _, _ ->

        }
        builder.create().show()
    }
}