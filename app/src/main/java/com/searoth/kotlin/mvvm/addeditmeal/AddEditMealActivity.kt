package com.searoth.kotlin.mvvm.addeditmeal

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.searoth.kotlin.mvvm.R
import com.searoth.kotlin.mvvm.util.ADD_EDIT_RESULT_OK
import com.searoth.kotlin.mvvm.util.obtainViewModel
import com.searoth.kotlin.mvvm.util.replaceFragmentInActivity
import com.searoth.kotlin.mvvm.util.setupActionBar


/**
 * Created by cr on 2/26/2018.
 */
class AddEditMealActivity : AppCompatActivity(), AddEditMealNavigator {

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onMealSaved(){
        setResult(ADD_EDIT_RESULT_OK)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addmeal_act)

        setupActionBar(R.id.toolbar){
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        replaceFragmentInActivity(obtainViewFragment(), R.id.contentFrame)

        subscribeToNavigationChanges()
    }

    private fun subscribeToNavigationChanges(){
        //act observes the nav events in the vm
        obtainViewModel().mealUpdateEvent.observe(this, Observer{
            this@AddEditMealActivity.onMealSaved()
        })
    }

    private fun obtainViewFragment() = supportFragmentManager.findFragmentById(R.id.contentFrame) ?:
            AddEditMealFragment.newInstance().apply {
                arguments = Bundle().apply {
                    putString(AddEditMealFragment.ARGUMENT_EDIT_MEAL_ID,
                            intent.getStringExtra(AddEditMealFragment.ARGUMENT_EDIT_MEAL_ID))
                }
            }

    fun obtainViewModel(): AddEditMealViewModel = obtainViewModel(AddEditMealViewModel::class.java)

    companion object {
        const val REQUEST_CODE = 1
    }
}