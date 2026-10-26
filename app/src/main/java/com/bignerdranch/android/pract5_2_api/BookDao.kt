package com.bignerdranch.android.pract5_2_api

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface BookDao {
    @Query("SELECT * FROM books_table ORDER BY id DESC")
    fun getAllBooks(): List<BookEntity>

    @Insert
    fun insertBook(book: BookEntity)

    @Update
    fun updateBook(book: BookEntity)

    @Delete
    fun deleteBook(book: BookEntity)
}