package com.searoth.kotlin.mvvm.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Immutable model class for a Meal. In order to compile with Room, we can't use @JvmOverloads to
 * generate multiple constructors.
 *
 * @param name
 * @param rating
 * @param price
 * @param description
 * @param notes
 * @param ingredients
 * @param imageurl
 * @param entryid
 */
@Entity(tableName = "meals")
data class Meal @JvmOverloads constructor(
        @ColumnInfo(name = "name") var name: String = "",
        @ColumnInfo(name = "rating") var rating: Float = 0.0f,
        @ColumnInfo(name = "price") var price: Float = 0.0f,
        @ColumnInfo(name = "description") var description: String = "",
        @ColumnInfo(name = "notes") var notes: String = "",
        @ColumnInfo(name = "ingredients") var ingredients: String = "",
        @ColumnInfo(name = "imageurl") var imageurl: String = "",
        @PrimaryKey @ColumnInfo(name = "entryid") var id: String = ""
){
    @ColumnInfo(name = "favorite") var isFavorite = false
    @ColumnInfo(name = "views") var views: Int = 0

    val isEmpty
        get() = name.isEmpty() && description.isEmpty()
}