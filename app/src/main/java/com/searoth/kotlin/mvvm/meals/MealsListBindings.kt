package com.searoth.kotlin.mvvm.meals

import android.databinding.BindingAdapter
import android.widget.ImageView
import android.widget.ListView
import com.searoth.kotlin.mvvm.R
import com.searoth.kotlin.mvvm.data.Meal
import com.squareup.picasso.Picasso

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

    @BindingAdapter("app:imageUrl")
    @JvmStatic fun loadImage(view: ImageView, imageUrl: String) {
        Picasso.with(view.context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_add)
                .into(view)
    }
}