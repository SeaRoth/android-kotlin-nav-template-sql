package com.searoth.kotlin.mvvm.meals

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.text.method.TextKeyListener.clear
import android.view.Menu

import android.view.MenuItem
import ca.allanwang.kau.searchview.SearchItem
import ca.allanwang.kau.searchview.SearchView
import ca.allanwang.kau.searchview.bindSearchView
import ca.allanwang.kau.utils.toast
import com.facebook.stetho.Stetho
import com.searoth.kotlin.mvvm.R
import com.searoth.kotlin.mvvm.addeditmeal.AddEditMealActivity
import com.searoth.kotlin.mvvm.mealdetail.MealDetailActivity
import com.searoth.kotlin.mvvm.util.obtainViewModel
import com.searoth.kotlin.mvvm.util.replaceFragmentInActivity
import com.searoth.kotlin.mvvm.util.setupActionBar

/**
 * Created by cr on 2/26/2018.
 */
class MealsActivity : AppCompatActivity(), MealItemNavigator, MealsNavigator{

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var viewModel: MealsViewModel

    var searchView: SearchView? = null

    companion object {
        var wordBank: MutableList<String> = mutableListOf("taco","truck")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Stetho.initializeWithDefaults(this)
        setContentView(R.layout.meals_act)

        setupActionBar(R.id.toolbar) {
            setHomeAsUpIndicator(R.drawable.ic_menu)
            setDisplayHomeAsUpEnabled(true)
        }

        setupNavigationDrawer()

        setupViewFragment()

        viewModel = obtainViewModel().apply {
            openMealEvent.observe(this@MealsActivity, Observer<String> { mealId ->
                if (mealId != null) {
                    openMealDetails(mealId)
                }
            })
            newMealEvent.observe(this@MealsActivity, Observer<Void> {
                this@MealsActivity.addNewMeal()
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        if (searchView == null) searchView = bindSearchView(menu!!, R.id.action_search) {

            wordBank.clear()
            viewModel.nameIds.forEach {
                wordBank.add(it.name)
            }

            textCallback = { query, searchView ->
                val items = wordBank.filter { it.contains(query) }.sorted().map { SearchItem(it) }
                searchView.results = items
            }
            searchCallback = { query, _ ->
                toast("Enter pressed for $query")
                true
            }
            textDebounceInterval = 0
            noResultsFound = R.string.kau_no_results_found
            shouldClearOnClose = false
            onItemClick = { _, _, content, searchView ->
                viewModel.currentFiltering = MealsFilterType.CUSTOM
                viewModel.currentFilteringString.set(content)
                viewModel.updateFiltering()
                viewModel.loadMeals(false)
                searchView.revealClose()
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun setupViewFragment() {
        supportFragmentManager.findFragmentById(R.id.contentFrame) ?:
        MealsFragment.newInstance().let {
            replaceFragmentInActivity(it, R.id.contentFrame)
        }
    }

    private fun setupNavigationDrawer() {
        drawerLayout = (findViewById<DrawerLayout>(R.id.drawer_layout)).apply {
            setStatusBarBackground(R.color.colorPrimaryDark)
        }
        setupDrawerContent(findViewById(R.id.nav_view))
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                android.R.id.home -> {
                    // Open the navigation drawer when the home icon is selected from the toolbar.
                    drawerLayout.openDrawer(GravityCompat.START)
                    true
                }

                else -> super.onOptionsItemSelected(item)
            }

    override fun onBackPressed() {
        if (searchView?.onBackPressed() != true) super.onBackPressed()
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.list_navigation_menu_item -> {
                    // Do nothing, we're already on that screen
                }
                R.id.statistics_navigation_menu_item -> {
                    val intent = Intent(this@MealsActivity, MealDetailActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    }
                    startActivity(intent)
                }
            }
            // Close the navigation drawer when an item is selected.
            menuItem.isChecked = true
            drawerLayout.closeDrawers()
            true
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        viewModel.handleActivityResult(requestCode, resultCode)
    }

    override fun openMealDetails(mealId: String) {
        val intent = Intent(this, MealDetailActivity::class.java).apply {
            putExtra(MealDetailActivity.EXTRA_MEAL_ID, mealId)
        }
        startActivityForResult(intent, AddEditMealActivity.REQUEST_CODE)
    }

    override fun addNewMeal() {
        val intent = Intent(this, AddEditMealActivity::class.java)
        startActivityForResult(intent, AddEditMealActivity.REQUEST_CODE)
    }

    fun obtainViewModel(): MealsViewModel = obtainViewModel(MealsViewModel::class.java)

}