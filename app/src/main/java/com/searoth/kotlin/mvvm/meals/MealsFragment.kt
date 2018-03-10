package com.searoth.kotlin.mvvm.meals

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.MenuItemCompat.getActionView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import com.searoth.kotlin.mvvm.R
import com.searoth.kotlin.mvvm.databinding.MealsFragBinding
import com.searoth.kotlin.mvvm.util.setupSnackbar

/**
 * Created by cr on 2/26/2018.
 */
class MealsFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var viewDataBinding: MealsFragBinding
    private lateinit var listAdapter: MealsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewDataBinding = MealsFragBinding.inflate(inflater, container, false).apply {
            viewmodel = (activity as MealsActivity).obtainViewModel()
        }
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onResume() {
        super.onResume()
        viewDataBinding.viewmodel?.start()
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId){
            R.id.menu_filter -> {
                showFilteringPopUpMenu()
                true
            }
            R.id.menu_refresh -> {
                viewDataBinding.viewmodel?.loadMeals(true)
                true
            }
            else -> false
        }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.meals_fragment_menu, menu)

        val searchItem = menu!!.findItem(R.id.action_search)
        (getActionView(searchItem) as SearchView?)?.setOnQueryTextListener(this)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.viewmodel?.let {
            view?.setupSnackbar(this, it.snackbarMessage, Snackbar.LENGTH_LONG)
        }
        setupFab()
        setupListAdapter()
        setupRefreshLayout()
    }

    private fun showFilteringPopUpMenu(){
        context?.let {
            PopupMenu(it, activity?.findViewById<View>(R.id.menu_filter)!!).run{
                menuInflater.inflate(R.menu.filter_meals, menu)

                setOnMenuItemClickListener {
                    viewDataBinding.viewmodel?.run{
                        currentFiltering =
                                when (it.itemId){
                                    R.id.all -> MealsFilterType.ALL_MEALS
                                    R.id.favorite -> MealsFilterType.FAVORITES
                                    R.id.under_five -> MealsFilterType.UNDER_FIVE
                                    R.id.under_ten -> MealsFilterType.UNDER_TEN
                                    R.id.under_twenty -> MealsFilterType.UNDER_TWENTY
                                    else -> MealsFilterType.ALL_MEALS
                                }
                        loadMeals(false)
                    }
                    true
                }
                show()
            }
        }
    }

    private fun setupFab(){
        activity!!.findViewById<FloatingActionButton>(R.id.fab_add_meal).run {
            setImageResource(R.drawable.ic_add)
            setOnClickListener {
                viewDataBinding.viewmodel?.addNewMeal()
            }
        }
    }

    private fun setupListAdapter(){
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null){
            listAdapter = MealsAdapter(ArrayList(0),viewModel)
            viewDataBinding.mealsList.adapter = listAdapter
        }else{
            Log.w(TAG, "ViewModel not init when att to set up adapter")
        }
    }

    private fun setupRefreshLayout(){
        viewDataBinding.refreshLayout.run {
            setColorSchemeColors(
                    ContextCompat.getColor(activity!!, R.color.colorPrimary),
                    ContextCompat.getColor(activity!!, R.color.colorAccent),
                    ContextCompat.getColor(activity!!, R.color.colorPrimaryDark)
            )
            //set scroll view in custom swipe
            scrollUpChild = viewDataBinding.mealsList
        }
    }

    companion object {
        fun newInstance() = MealsFragment()
        private const val TAG = "MealsFragment"
    }


}