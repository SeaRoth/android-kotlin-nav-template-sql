package com.searoth.kotlin.mvvm.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.location.Location

/**
 * Created by cr on 3/13/2018.
 */
/**
 * Immutable model class for a Meal. In order to compile with Room, we can't use @JvmOverloads to
 * generate multiple constructors.
 *
 * @param name
 * @param username
 * @param email
 * @param imageurl
 * @param favoriteMeals []
 * @param favoriteBusinesses []
 * @param lastLocation
 * @param businessAdminOf []
 * @param entryid
 */
@Entity(tableName = "user")
data class User @JvmOverloads constructor(
        @ColumnInfo(name = "name") var name: String = "",
        @ColumnInfo(name = "username") var username: String = "",
        @ColumnInfo(name = "email") var email: String = "",
        @ColumnInfo(name = "imageUrl") var imageUrl: String = "",
        @ColumnInfo(name = "favoriteMeals") var favoriteMeals: ArrayList<String> = arrayListOf(),
        @ColumnInfo(name = "favoriteBusinesses") var favoriteBusinesses: ArrayList<String> = arrayListOf(),
        @ColumnInfo(name = "lastLocation") var lastLocation: String = "",
        @ColumnInfo(name = "businessAdminOf") var businessAdminOf: ArrayList<String> = arrayListOf(),
        @PrimaryKey @ColumnInfo(name = "entryid") var id: String = ""
){
    val isAnAdmin
        get() = businessAdminOf.isEmpty()
}