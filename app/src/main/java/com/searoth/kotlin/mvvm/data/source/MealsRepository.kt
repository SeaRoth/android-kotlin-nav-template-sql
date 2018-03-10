package com.searoth.kotlin.mvvm.data.source

import com.searoth.kotlin.mvvm.data.Meal

/**
 * Created by cr on 2/26/2018.
 */
class MealsRepository(
        val mealsRemoteDataSource: MealsDataSource,
        val mealsLocalDataSource: MealsDataSource
) : MealsDataSource {

    /**
     * Access for tests
     */
    var cachedMeals: LinkedHashMap<String, Meal> = LinkedHashMap()

    /**
     * marks cache as invalid, forces update next time data is requested.
     */
    var cacheIsDirty = false

    /**
     * Gets meals from cache, local data source (SQLite) or remote data source, whichever is
     * available first.
     *
     *
     * Note: [LoadMealssCallback.onDataNotAvailable] is fired if all data sources fail to
     * get the data.
     */
    override fun getMeals(callback: MealsDataSource.LoadMealsCallback) {
        if(cachedMeals.isNotEmpty() && !cacheIsDirty){
            callback.onMealsLoaded(ArrayList(cachedMeals.values))
            return
        }

        if(cacheIsDirty){
            //fetch from network
            getMealsFromRemoteDataSource(callback)
        }else{
            //Query local storage
            mealsLocalDataSource.getMeals(object : MealsDataSource.LoadMealsCallback {
                override fun onDataNotAvailable() {
                    getMealsFromRemoteDataSource(callback)
                }

                override fun onMealsLoaded(meals: List<Meal>) {
                    refreshCache(meals)
                    callback.onMealsLoaded(ArrayList(cachedMeals.values))
                }
            })
        }

    }

    override fun saveMeal(meal: Meal) {
        //do in mem cache update to keep the app ui up to date
        cacheAndPerform(meal){
            mealsRemoteDataSource.saveMeal(meal)
            mealsLocalDataSource.saveMeal(meal)
        }
    }

    override fun favoriteMeal(meal: Meal) {
        // Do in memory cache update to keep the app UI up to date
        cacheAndPerform(meal) {
            it.isFavorite = true
            mealsRemoteDataSource.favoriteMeal(it)
            mealsLocalDataSource.favoriteMeal(it)
        }
    }

    override fun favoriteMeal(mealId: String) {
        getMealWithId(mealId)?.let {
            favoriteMeal(it)
        }
    }

    override fun unFavoriteMeal(meal: Meal) {
        cacheAndPerform(meal) {
            it.isFavorite = false
            mealsRemoteDataSource.unFavoriteMeal(it)
            mealsLocalDataSource.unFavoriteMeal(it)
        }
    }

    override fun unFavoriteMeal(mealId: String) {
        getMealWithId(mealId)?.let {
            unFavoriteMeal(it)
        }
    }

    override fun viewMeal(meal: Meal) {
        //in mem cache update to keep app UI up to date
        cacheAndPerform(meal){
            it.views++
            mealsRemoteDataSource.viewMeal(meal)
            mealsLocalDataSource.viewMeal(meal)
        }
    }

    override fun viewMeal(mealId: String) {
        getMealWithId(mealId)?.let{
            viewMeal(it)
        }
    }

    override fun clearFavoriteMeals() {
        mealsRemoteDataSource.clearFavoriteMeals()
        mealsLocalDataSource.clearFavoriteMeals()

        cachedMeals = cachedMeals.filterValues {
            !it.isMyFavorite
        } as LinkedHashMap<String, Meal>
    }

    /**
     * Gets meals from local data source (sqlite) unless the table is new or empty. In that case it uses the network
     * data source.
     *
     * Note: [GetMealCallback.onDataNotAvailable] is fired if both data sources fail to get the data.
     */
    override fun getMeal(mealId: String, callback: MealsDataSource.GetMealCallback) {
        val mealInCache = getMealWithId(mealId)

        //repond immediately with cache if avail
        if(mealInCache != null){
            callback.onMealLoaded(mealInCache)
            return
        }

        //Query local then remote
        mealsLocalDataSource.getMeal(mealId, object : MealsDataSource.GetMealCallback{
            override fun onMealLoaded(meal: Meal) {
                //In mem cache update to keep app UI up to date
                cacheAndPerform(meal){
                    callback.onMealLoaded(it)
                }
            }

            override fun onDataNotAvailable() {
                mealsRemoteDataSource.getMeal(mealId, object : MealsDataSource.GetMealCallback {
                    override fun onMealLoaded(meal: Meal) {
                        //do in mem cache update to keep the app ui up to date
                        cacheAndPerform(meal){
                            callback.onMealLoaded(it)
                        }
                    }

                    override fun onDataNotAvailable() {
                        callback.onDataNotAvailable()
                    }
                })
            }
        })
    }

    override fun refreshMeals() {
        cacheIsDirty = true
    }

    override fun deleteAllMeals() {
        mealsRemoteDataSource.deleteAllMeals()
        mealsLocalDataSource.deleteAllMeals()
        cachedMeals.clear()
    }

    override fun deleteMeal(mealId: String) {
        mealsRemoteDataSource.deleteMeal(mealId)
        mealsLocalDataSource.deleteMeal(mealId)
        cachedMeals.remove(mealId)
    }

    private fun getMealsFromRemoteDataSource(callback: MealsDataSource.LoadMealsCallback){
        mealsRemoteDataSource.getMeals(object : MealsDataSource.LoadMealsCallback {
            override fun onMealsLoaded(meals: List<Meal>) {
                refreshCache(meals)
                refreshLocalDataSource(meals)
                callback.onMealsLoaded(ArrayList(cachedMeals.values))
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    private fun refreshCache(meals: List<Meal>){
        cachedMeals.clear()
        meals.forEach{
            cacheAndPerform(it){}
        }
        cacheIsDirty = false
    }

    private fun refreshLocalDataSource(meals: List<Meal>){
        mealsLocalDataSource.deleteAllMeals()
        for(meal in meals){
            mealsLocalDataSource.saveMeal(meal)
        }
    }

    private fun getMealWithId(id: String) = cachedMeals[id]

    private inline fun cacheAndPerform(meal: Meal, perform: (Meal) -> Unit){
        val cachedMeal = Meal(meal.name, meal.rating, meal.price, meal.description, meal.notes, meal.ingredients, meal.imageurl, meal.id).apply{
            name = meal.name
        }
        cachedMeals[cachedMeal.id] = cachedMeal
        perform(cachedMeal)
    }

    companion object {
        private var INSTANCE: MealsRepository? = null

        /**
         * Returns the single instance of this class, creates if necc
         *
         * @param mealsRemoteDataSource - the backend data
         * @param mealsLocalDataSource - the device storage
         *
         * @return the [MealsRepository] instance
         */

        @JvmStatic fun getInstance(mealsRemoteDataSource: MealsDataSource,
                                   mealsLocalDataSource: MealsDataSource) =
                INSTANCE ?: synchronized(MealsRepository::class.java){
                    INSTANCE ?: MealsRepository(mealsRemoteDataSource, mealsLocalDataSource)
                            .also { INSTANCE = it }
                }
        /**
         * Forces [getInstance] to create new next time
         */
        @JvmStatic fun destroyInstance(){
            INSTANCE = null
        }
    }




}