package com.searoth.kotlin.mvvm.meals

import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import com.searoth.kotlin.mvvm.data.Meal
import com.searoth.kotlin.mvvm.databinding.MealItemBinding

/**
 * Created by cr on 2/26/2018.
 */
class MealsAdapter(
        private var meals: List<Meal>,
        private var mealsViewModel: MealsViewModel
) : BaseAdapter() {

    fun replaceData(meals: List<Meal>){
        setList(meals)
    }

    override fun getItem(position: Int) = meals[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = meals.size

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View {
        val binding: MealItemBinding
        if (view == null){
            //inflate
            var inflater = LayoutInflater.from(viewGroup.context)
            //create the binding
            binding = MealItemBinding.inflate(inflater, viewGroup, false)
        }else{
            //Recycling view
            binding = DataBindingUtil.getBinding<MealItemBinding>(view)
        }

        val userActionListener = object : MealItemUserActionListener {
            override fun onFavoriteChanged(meal: Meal, v: View) {
                val checked = (v as CheckBox).isChecked
                mealsViewModel.favoriteMeal(meal, checked)
            }

            override fun onMealClicked(meal: Meal) {
                mealsViewModel.openMealEvent.value = meal.id
            }

        }
        with(binding){
            meal = meals[position]
            listener = userActionListener
            executePendingBindings()
        }
        return binding.root
    }

    private fun setList(meals: List<Meal>){
        this.meals = meals
        notifyDataSetChanged()
    }

}