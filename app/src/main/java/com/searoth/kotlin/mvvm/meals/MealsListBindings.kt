package com.searoth.kotlin.mvvm.meals

import android.databinding.BindingAdapter
import android.widget.ListView
import com.searoth.kotlin.mvvm.data.Meal

/**
 * Created by cr on 2/26/2018.
 */
object MealsListBindings {
    @BindingAdapter("app:items")
    @JvmStatic fun setItems(listView: ListView, items: List<Meal>){
        with(listView.adapter as MealsAdapter){
            replaceData(items)
        }
    }
}