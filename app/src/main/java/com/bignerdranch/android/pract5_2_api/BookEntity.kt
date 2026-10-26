package com.bignerdranch.android.pract5_2_api

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "books_table")
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val isbn: String,
    val title: String,
    val author: String,
    val coverUrl: String
) : Serializable