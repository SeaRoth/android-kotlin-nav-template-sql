package com.searoth.kotlin.mvvm

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.support.annotation.VisibleForTesting
import com.searoth.kotlin.mvvm.addeditmeal.AddEditMealViewModel
import com.searoth.kotlin.mvvm.data.source.MealsRepository
import com.searoth.kotlin.mvvm.mealdetail.MealDetailViewModel
import com.searoth.kotlin.mvvm.meals.MealsViewModel


/**
 * Created by cr on 2/26/2018.
 */
/**
 * A creator is used to inject the product ID into the ViewModel
 *
 *
 * This creator is to showcase how to inject dependencies into ViewModels. It's not
 * actually necessary in this case, as the product ID can be passed in a public method.
 */
class ViewModelFactory private constructor(
        private val application: Application,
        private val tasksRepository: MealsRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
            with(modelClass) {
                when {
                    isAssignableFrom(MealDetailViewModel::class.java) ->
                        MealDetailViewModel(application, tasksRepository)
                    isAssignableFrom(AddEditMealViewModel::class.java) ->
                        AddEditMealViewModel(application, tasksRepository)
                    isAssignableFrom(MealsViewModel::class.java) ->
                        MealsViewModel(application, tasksRepository)
                    else ->
                        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            } as T

    companion object {

        @SuppressLint("StaticFieldLeak")
        @Volatile private var INSTANCE: ViewModelFactory? = null

        fun getInstance(application: Application) =
                INSTANCE ?: synchronized(ViewModelFactory::class.java) {
                    INSTANCE ?: ViewModelFactory(application,
                            Injection.provideMealsRepository(application.applicationContext))
                            .also { INSTANCE = it }
                }


        @VisibleForTesting
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
