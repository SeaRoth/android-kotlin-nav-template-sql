package com.searoth.kotlin.mvvm.mealdetail

import android.databinding.BindingAdapter
import android.widget.ImageView
import com.searoth.kotlin.mvvm.R
import com.squareup.picasso.Picasso

/**
 * Created by cr on 3/10/2018.
 */
object MealDetailBindings {
    @BindingAdapter("app:imageUrl")
@JvmStatic fun loadImage(view: ImageView, imageUrl: String){
        Picasso.with(view.context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_add)
                .into(view)
    }
}