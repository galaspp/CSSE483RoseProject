package edu.rosehulman.galaspp.roseproject

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.widget.ArrayAdapter
import android.widget.Spinner
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
import kotlinx.android.synthetic.main.add_remove_members_modal.view.*
import kotlinx.android.synthetic.main.create_edit_task_modal.*
import kotlinx.android.synthetic.main.create_edit_task_modal.view.*

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
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
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