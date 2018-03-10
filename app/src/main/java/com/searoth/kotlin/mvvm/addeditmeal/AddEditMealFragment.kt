package com.searoth.kotlin.mvvm.addeditmeal

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.searoth.kotlin.mvvm.R
import com.searoth.kotlin.mvvm.databinding.AddmealFragBinding
import com.searoth.kotlin.mvvm.util.setupSnackbar

/**
 * Created by cr on 2/26/2018.
 */
class AddEditMealFragment : Fragment(){

    private lateinit var viewDataBinding: AddmealFragBinding

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupFab()
        viewDataBinding.viewmodel?.let {
            view?.setupSnackbar(this, it.snackbarMessage, Snackbar.LENGTH_LONG)
        }
        setupActionBar()
        loadData()
    }

    private fun loadData() {
        // Add or edit an existing task?
        viewDataBinding.viewmodel?.start(arguments?.getString(ARGUMENT_EDIT_MEAL_ID))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.addmeal_frag, container, false)
        viewDataBinding = AddmealFragBinding.bind(root).apply {
            viewmodel = (activity as AddEditMealActivity).obtainViewModel()
        }
        setHasOptionsMenu(true)
        retainInstance = false
        return viewDataBinding.root
    }

    private fun setupFab() {
        activity?.findViewById<FloatingActionButton>(R.id.fab_edit_meal_done).apply {
            this?.setImageResource(R.drawable.ic_done)
            this?.setOnClickListener { viewDataBinding.viewmodel?.saveMeal() }
        }
    }

    private fun setupActionBar() {
        (activity as AppCompatActivity).supportActionBar?.setTitle(
                if (arguments != null && arguments?.get(ARGUMENT_EDIT_MEAL_ID) != null)
                    R.string.edit_meal
                else
                    R.string.add_meal
        )
    }

    companion object {
        const val ARGUMENT_EDIT_MEAL_ID = "EDIT_TASK_ID"

        fun newInstance() = AddEditMealFragment()
    }
}
