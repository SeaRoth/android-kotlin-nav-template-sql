package com.searoth.kotlin.mvvm.addeditmeal

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.support.annotation.StringRes
import com.searoth.kotlin.mvvm.R
import com.searoth.kotlin.mvvm.SingleLiveEvent
import com.searoth.kotlin.mvvm.data.Meal
import com.searoth.kotlin.mvvm.data.source.MealsDataSource
import com.searoth.kotlin.mvvm.data.source.MealsRepository

/**
 * ViewModel for the Add/Edit screen
 *
 * This ViewModel only exposes [ObservableField]s, so it doesn't need to extend
 * [androiddatabinding.BaseObservable] and updates are notified auto.
 *
 *
 */
class AddEditMealViewModel(
        context: Application,
        private val mealsRepository: MealsRepository
) : AndroidViewModel(context), MealsDataSource.GetMealCallback{

    val name = ObservableField<String>()
    val rating = ObservableField<Float>()
    val price = ObservableField<Float>()
    val description = ObservableField<String>()
    val notes = ObservableField<String>()
    val ingredients = ObservableField<String>()
    val imageUrl = ObservableField<String>()

    val dataLoading = ObservableBoolean(false)
    internal val snackbarMessage = SingleLiveEvent<Int>()
    internal val mealUpdateEvent = SingleLiveEvent<Void>()
    private var mealId: String? = ""
    private val isNewMeal
        get() = mealId == null
    private var isDataLoaded = false
    private var mealFavorited = false

    fun start(mealId: String?){
        if(dataLoading.get()){
            //its already loading, ignore
            return
        }
        this.mealId = mealId
        if( isNewMeal || isDataLoaded){
            //no need to pop, its a new task or it already has data
            return
        }
        dataLoading.set(true)
        mealId?.let {
            mealsRepository.getMeal(it, this)
        }
    }

    override fun onMealLoaded(meal: Meal) {
        name.set(meal.name)
        description.set(meal.description)
        mealFavorited = meal.isFavorite
        dataLoading.set(false)
        isDataLoaded = true
        //no need to notify the values changed because we're using ObservableFields
    }

    override fun onDataNotAvailable() {
        dataLoading.set(false)
    }

    //called when clicking on the fab
    fun saveMeal(){
        val meal = Meal(name.get(), rating.get(), price.get(), description.get(), notes.get(), ingredients.get(), imageUrl.get(), mealId!!)
        if (meal.isEmpty){
            showSnackbarMessage(R.string.empty_meal_message)
            return
        }
        if(isNewMeal){
            createMeal(meal)
        } else {
            mealId?.let {
                updateMeal(Meal(name.get(), rating.get(), price.get(), description.get(), notes.get(), ingredients.get(), imageUrl.get(), it)
                        .apply { isFavorite = mealFavorited })
            }
        }
    }

    private fun createMeal(newMeal: Meal){
        mealsRepository.saveMeal(newMeal)
        mealUpdateEvent.call()
    }

    private fun updateMeal(meal: Meal){
        if(isNewMeal){
            throw RuntimeException("updateMeal() was called but meal is new.")
        }
        mealsRepository.saveMeal(meal)
        mealUpdateEvent.call()
    }

    private fun showSnackbarMessage(@StringRes message: Int) {
        snackbarMessage.value = message
    }
}