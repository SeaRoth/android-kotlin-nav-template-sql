package com.searoth.kotlin.mvvm.util

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.databinding.BindingAdapter
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import com.searoth.kotlin.mvvm.ScrollChildSwipeRefreshLayout
import com.searoth.kotlin.mvvm.SingleLiveEvent
import com.searoth.kotlin.mvvm.meals.MealsViewModel
/**
 * Transforms static java function Snackbar.make() to an extension function on View.
 */
fun View.showSnackBar(snackbarText: String, timeLength: Int){
    Snackbar.make(this, snackbarText, timeLength).show()
}
/**
 * Triggers a snackbar message when the value contained by snackbarTaskMessageLiveEvent is modified.
 */
fun View.setupSnackbar(lifecycleOwner: LifecycleOwner,
                      snackBarMessageLiveEvent: SingleLiveEvent<Int>, timeLength: Int){
    snackBarMessageLiveEvent.observe(lifecycleOwner, Observer{
        it?.let{
            showSnackBar(context.getString(it), timeLength)}
    })
}
/**
 * Reloads the data when the pull-to-refresh is triggered.
 *
 * Creates the `android:onRefresh` for a [SwipeRefreshLayout].
 */
@BindingAdapter("android:onRefresh")
fun ScrollChildSwipeRefreshLayout.setSwipeRefreshLayoutOnRefreshListener(
        viewModel: MealsViewModel){
    setOnRefreshListener{viewModel.loadMeals(true)}
}
