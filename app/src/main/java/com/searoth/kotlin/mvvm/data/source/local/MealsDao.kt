package com.searoth.kotlin.mvvm.data.source.local

import android.arch.persistence.room.*
import com.searoth.kotlin.mvvm.data.Meal

/**
 * Created by cr on 2/26/2018.
 */
@Dao interface MealsDao{
    /**
     * Select all meals from the meals table
     *
     * @return all meals
     */
    @Query("SELECT * FROM Meals")
    fun getMeals(): List<Meal>

    /**
     * Select a meal by id
     *
     * @param mealId the meal id
     * @return the meal wit mealId
     */
    @Query("SELECT * FROM Meals WHERE entryid = :mealId")
    fun getMealById(mealId: String): Meal?

    /**
     * Insert a meal into db, if exists, replace
     *
     * @param meal - meal to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMeal(meal: Meal)

    /**
     * update a meal
     *
     * @param meal - meal to be updated
     * @return the number of meals updated. should always be 1
     */
    @Update fun updateMeal(meal: Meal): Int

    /**
     * update the price of a meal
     */
    @Query("UPDATE Meals SET favorite = :favorite WHERE entryid = :mealId")
    fun updateFavorite(mealId: String, favorite: Boolean)

    /**
     * Add one to the view count of a meal
     *
     * @param mealId id of the meal
     */
    @Query("UPDATE Meals SET views = views + 1 WHERE entryid = :mealId")
    fun viewMeal(mealId: String)

    /**
     * Delete a meal by id
     *
     * @return the number of meals deleted. should be 1
     */
    @Query("DELETE FROM Meals WHERE entryid = :mealId") fun deleteMealsById(mealId: String): Int

    /**
     * Delete all meals
     */
    @Query("DELETE FROM Meals") fun deleteMeals()

    /**
     * Remove all isFavorite meals from the table
     *
     * @return the number of meals unfavorited
     */
    @Query("UPDATE Meals SET favorite = 0 WHERE favorite = 1") fun removeFavoriteMeals(): Int

}