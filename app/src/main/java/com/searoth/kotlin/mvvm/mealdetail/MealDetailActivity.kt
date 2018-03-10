package com.searoth.kotlin.mvvm.mealdetail

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.searoth.kotlin.mvvm.R
import com.searoth.kotlin.mvvm.addeditmeal.AddEditMealActivity
import com.searoth.kotlin.mvvm.addeditmeal.AddEditMealFragment
import com.searoth.kotlin.mvvm.mealdetail.MealDetailFragment.Companion.REQUEST_EDIT_MEAL
import com.searoth.kotlin.mvvm.util.*

/**
 * Created by cr on 2/26/2018.
 */
class MealDetailActivity : AppCompatActivity(), MealDetailNavigator {

    private lateinit var mealViewModel: MealDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.mealdetail_act)

        setupActionBar(R.id.toolbar) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Meal Name"
        }

        replaceFragmentInActivity(findOrCreateViewFragment(), R.id.contentFrame)

        mealViewModel = obtainViewModel()

        subscribeToNavigationChanges(mealViewModel)
    }

    private fun findOrCreateViewFragment() =
            supportFragmentManager.findFragmentById(R.id.contentFrame) ?:
            MealDetailFragment.newInstance(intent.getStringExtra(EXTRA_MEAL_ID))

    private fun subscribeToNavigationChanges(viewModel: MealDetailViewModel) {
        // The activity observes the navigation commands in the ViewModel
        val activity = this@MealDetailActivity
        viewModel.run {
            editMealCommand.observe(activity,
                    Observer { activity.onStartEditMeal() })
            deleteMealCommand.observe(activity,
                    Observer { activity.onMealDeleted() })
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_EDIT_MEAL) {
            // If the task was edited successfully, go back to the list.
            if (resultCode == ADD_EDIT_RESULT_OK) {
                // If the result comes from the add/edit screen, it's an edit.
                setResult(EDIT_RESULT_OK)
                finish()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onMealDeleted() {
        setResult(DELETE_RESULT_OK)
        // If the task was deleted successfully, go back to the list.
        finish()
    }

    override fun onStartEditMeal() {
        val taskId = intent.getStringExtra(EXTRA_MEAL_ID)
        val intent = Intent(this, AddEditMealActivity::class.java).apply {
            putExtra(AddEditMealFragment.ARGUMENT_EDIT_MEAL_ID, taskId)
        }
        startActivityForResult(intent, REQUEST_EDIT_MEAL)
    }

    fun obtainViewModel(): MealDetailViewModel = obtainViewModel(MealDetailViewModel::class.java)

    companion object {

        const val EXTRA_MEAL_ID = "MEAL_ID"

    }

}
