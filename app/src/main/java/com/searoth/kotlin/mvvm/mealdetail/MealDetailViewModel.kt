package com.searoth.kotlin.mvvm.mealdetail

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.support.annotation.StringRes
import com.searoth.kotlin.mvvm.R
import com.searoth.kotlin.mvvm.R.id.favorite
import com.searoth.kotlin.mvvm.SingleLiveEvent
import com.searoth.kotlin.mvvm.data.Meal
import com.searoth.kotlin.mvvm.data.source.MealsDataSource
import com.searoth.kotlin.mvvm.data.source.MealsRepository

/**
 * Created by cr on 2/26/2018.
 */
class MealDetailViewModel(
        context: Application,
        private val mealsRepository: MealsRepository
) : AndroidViewModel(context), MealsDataSource.GetMealCallback{

    val meal = ObservableField<Meal>()
    val editMealCommand = SingleLiveEvent<Void>()
    val deleteMealCommand = SingleLiveEvent<Void>()
    val snackbarMessage = SingleLiveEvent<Int>()
    var isDataLoading = false
        private set
    val isDataAvailable
        get() = meal.get() != null

    fun deleteMeal(){
        meal.get()?.let {
            mealsRepository.deleteMeal(it.id)
            deleteMealCommand.call()
        }
    }

    fun editMeal(){
        editMealCommand.call()
    }

    fun setFavorite(favorite: Boolean){
        if(isDataLoading){
            return
        }
        val meal = this.meal.get().apply{
            isFavorite = favorite
        }
        if(favorite){
            mealsRepository.favoriteMeal(meal)
            showSnackbarMessage(R.string.meal_marked_favorite)
        }else{
            mealsRepository.unFavoriteMeal(meal)
            showSnackbarMessage(R.string.meal_marked_normal)
        }
    }

    fun start(mealId: String){
        mealId.let {
            isDataLoading = true
            mealsRepository.getMeal(it, this)
            mealsRepository.viewMeal(it)
        }
    }

    fun setMeal(meal: Meal){
        this.meal.set(meal)
        //favorite.set(meal.isFavorite)
    }

    override fun onMealLoaded(meal: Meal) {
        setMeal(meal)
        isDataLoading = false
    }

    override fun onDataNotAvailable() {
        meal.set(null)
        isDataLoading = false
    }

    fun onRefresh(){
        if(meal.get() != null){
            start(meal.get().id)
        }
    }

    private fun showSnackbarMessage(@StringRes message: Int){
        snackbarMessage.value = message
    }

}