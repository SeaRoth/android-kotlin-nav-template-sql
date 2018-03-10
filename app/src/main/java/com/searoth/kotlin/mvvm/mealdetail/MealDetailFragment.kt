package com.searoth.kotlin.mvvm.mealdetail

import android.content.ContentValues.TAG
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import android.widget.CheckBox
import com.searoth.kotlin.mvvm.R
import com.searoth.kotlin.mvvm.databinding.MealdetailFragBinding
import com.searoth.kotlin.mvvm.util.setupSnackbar

/**
 * Created by cr on 2/26/2018.
 */
class MealDetailFragment : Fragment() {

    private lateinit var viewDataBinding: MealdetailFragBinding

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupFab()
        viewDataBinding.viewmodel?.let {
            view?.setupSnackbar(this, it.snackbarMessage, Snackbar.LENGTH_LONG)
        }
    }

    private fun setupFab(){
        activity?.findViewById<View>(R.id.fab_edit_task)?.setOnClickListener{
            viewDataBinding.viewmodel?.editMeal()
            Log.i(TAG, "pressed button")
        }
    }

    override fun onResume(){
        super.onResume()
        viewDataBinding.viewmodel?.start(arguments?.getString(ARGUMENT_MEAL_ID)!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.mealdetail_frag, container, false)

        viewDataBinding = MealdetailFragBinding.bind(view).apply {
            viewmodel = (activity as MealDetailActivity).obtainViewModel()
            listener = object : MealDetailUserActionsListener {
                override fun onFavoriteChanged(v: View) {
                    viewmodel?.setFavorite((v as CheckBox).isChecked)
                }
            }
        }
        setHasOptionsMenu(true)
        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.menu_delete -> {
                viewDataBinding.viewmodel?.deleteMeal()
                return true
            }
            else -> return false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.mealdetail_fragment_menu, menu)
    }

    companion object {
        const val ARGUMENT_MEAL_ID = "MEAL_ID"
        const val REQUEST_EDIT_MEAL = 1

        fun newInstance(mealId: String) = MealDetailFragment().apply {
            arguments = Bundle().apply {
                putString(ARGUMENT_MEAL_ID, mealId)
            }
        }


    }
}