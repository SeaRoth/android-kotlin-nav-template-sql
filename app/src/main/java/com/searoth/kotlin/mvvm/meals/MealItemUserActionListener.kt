package com.searoth.kotlin.mvvm.meals

import android.view.View
import com.searoth.kotlin.mvvm.data.Meal

/**
 * Created by cr on 2/26/2018.
 */
interface MealItemUserActionListener {
    fun onFavoriteChanged(meal: Meal, v: View)

    fun onMealClicked(meal: Meal)
}