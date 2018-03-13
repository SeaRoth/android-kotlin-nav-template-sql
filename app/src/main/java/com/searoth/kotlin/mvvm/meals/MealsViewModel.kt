package com.searoth.kotlin.mvvm.meals

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.Context
import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableList
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.util.Log
import com.searoth.kotlin.mvvm.R
import com.searoth.kotlin.mvvm.SingleLiveEvent
import com.searoth.kotlin.mvvm.addeditmeal.AddEditMealActivity
import com.searoth.kotlin.mvvm.data.Meal
import com.searoth.kotlin.mvvm.data.source.MealsDataSource
import com.searoth.kotlin.mvvm.data.source.MealsRepository
import com.searoth.kotlin.mvvm.other.Models
import com.searoth.kotlin.mvvm.util.ADD_EDIT_RESULT_OK
import com.searoth.kotlin.mvvm.util.DELETE_RESULT_OK
import com.searoth.kotlin.mvvm.util.EDIT_RESULT_OK


/**
 * Created by cr on 2/26/2018.
 */
class MealsViewModel(
        context: Application,
        private val mealsRepository: MealsRepository
) : AndroidViewModel(context){

    private val isDataLoadingError = ObservableBoolean(false)
    private val context: Context = context.applicationContext //avoid leaks

    internal val openMealEvent = SingleLiveEvent<String>()

    //observable fields will update Views auto
    val items: ObservableList<Meal> = ObservableArrayList()
    var nameIds: ObservableArrayList<Models.NameId> = ObservableArrayList()
    var mealNames: ObservableList<String> = ObservableArrayList()
    var dataLoading = ObservableBoolean(false)
    var currentFilteringLabel = ObservableField<String>()
    var currentFilteringString = ObservableField<String>()
    var noMealsLabel = ObservableField<String>()
    val noMealsIconRes = ObservableField<Drawable>()
    val empty = ObservableBoolean()
    val mealsAddViewVisible = ObservableBoolean()
    val snackbarMessage = SingleLiveEvent<Int>()
    val newMealEvent = SingleLiveEvent<Void>()

    var currentFiltering = MealsFilterType.ALL_MEALS
        set(value){
            field = value
            //depending on filter, set label, etc
            updateFiltering()
        }
    fun start(){
        loadMeals(false)
    }

    fun loadMeals(forceUpdate: Boolean){
        loadMeals(forceUpdate, true)
    }

    fun updateFiltering(){
        when(currentFiltering){
            MealsFilterType.ALL_MEALS -> {
                setFilter(R.string.label_all, R.string.no_meals_active,
                        R.drawable.ic_assignment_turned_in_24dp, true)
            }
            MealsFilterType.FAVORITES -> {
                setFilter(R.string.label_favorite, R.string.no_meals_active,
                        R.drawable.ic_assignment_turned_in_24dp, false)
            }
            MealsFilterType.UNDER_FIVE -> {
                setFilter(R.string.label_under_five, R.string.no_meals_active,
                        R.drawable.ic_check_circle_24dp, false)
            }
            MealsFilterType.UNDER_TEN -> {
                setFilter(R.string.label_under_ten, R.string.no_meals_active,
                        R.drawable.ic_verified_user_24dp, false)
            }
            MealsFilterType.UNDER_TWENTY -> {
                setFilter(R.string.label_under_twenty, R.string.no_meals_active,
                        R.drawable.ic_verified_user_24dp, false)
            }
            MealsFilterType.CUSTOM -> {
                Log.i("MVM", currentFilteringString.toString())
                setFilter(R.string.label_custom, R.string.search_custom,
                        R.drawable.ic_verified_user_24dp, false)
            }
        }
    }

    private fun setFilter(@StringRes filteringLabelString: Int, @StringRes noMealsLabelString: Int,
                          @DrawableRes noMealIconDrawable: Int, mealsAddVisible: Boolean){
        with(context.resources){
            currentFilteringLabel.set(context.getString(filteringLabelString))
            noMealsLabel.set(getString(noMealsLabelString))
            noMealsIconRes.set(getDrawable(noMealIconDrawable))
            mealsAddViewVisible.set(mealsAddVisible)
        }
    }

    private fun showSnackbarMessage(message: Int){
        snackbarMessage.value = message
    }

    fun favoriteMeal(meal: Meal, favorite: Boolean){
        meal.isFavorite = favorite

        if(favorite){
            mealsRepository.favoriteMeal(meal)
            showSnackbarMessage(R.string.meal_marked_favorite)
        }else{
            mealsRepository.unFavoriteMeal(meal)
            showSnackbarMessage(R.string.favorite_meals_cleared)
        }
    }


    /**
     * Called by Data Binding lib and the FAB's click listener
     */
    fun addNewMeal(){
        newMealEvent.call()
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int){
        if(AddEditMealActivity.REQUEST_CODE == requestCode){
            snackbarMessage.value =
                    when (resultCode){
                        EDIT_RESULT_OK ->
                                R.string.successfully_saved_meal_message
                        ADD_EDIT_RESULT_OK ->
                                R.string.successfully_added_meal_message
                        DELETE_RESULT_OK ->
                                R.string.successfully_deleted_meal_message
                        else -> return
                    }
        }
    }

    /**
     * @param forceUpdate Pass in true to refresh teh data in the [MealsDataSource]
     *
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private fun loadMeals(forceUpdate: Boolean, showloadingUI: Boolean){
        if(showloadingUI)
            dataLoading.set(true)
        if(forceUpdate) {
            showSnackbarMessage(R.string.refresh_remote_source)
            mealsRepository.refreshMeals()
        }

        mealsRepository.getMeals(object : MealsDataSource.LoadMealsCallback {
            override fun onMealsLoaded(meals: List<Meal>) {
                val mealsToShow: List<Meal>

                //We filter the tasks based on the requestType
                when (currentFiltering){
                    MealsFilterType.ALL_MEALS ->
                            mealsToShow = meals
                    MealsFilterType.FAVORITES ->
                            mealsToShow = meals.filter { it.isFavorite }
                    MealsFilterType.UNDER_FIVE ->
                            mealsToShow = meals.filter { it.price < 5.0f }
                    MealsFilterType.UNDER_TEN ->
                            mealsToShow = meals.filter { it.price < 10.0f }
                    MealsFilterType.UNDER_TWENTY ->
                        mealsToShow = meals.filter { it.price < 20.0f }
                    MealsFilterType.CUSTOM ->
                        mealsToShow = meals.filter { !it.name.contains(currentFilteringString.toString())}
                }

                if (showloadingUI)
                    dataLoading.set(false)
                isDataLoadingError.set(false)

                with(items){
                    clear()
                    addAll(mealsToShow)
                    empty.set(isEmpty())
                }
                //make the models
                nameIds.clear()
                mealNames.clear()
                items.forEach {
                    val nameId = Models.NameId(it.name,it.id)
                    nameIds.add(nameId)
                    mealNames.add(it.name)
                }
            }

            override fun onDataNotAvailable() {
                isDataLoadingError.set(true)
            }

        })
    }








}