package com.bignerdranch.android.pract5_2_api

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONObject
import androidx.core.os.BundleCompat

class ListFragment : Fragment(R.layout.fragment_list) {

    private var currentIsbn = ""
    private var currentTitle = ""
    private var currentAuthor = ""
    private var currentCover = ""
    private var editBookId = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val etIsbn = view.findViewById<EditText>(R.id.etIsbn)
        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etAuthor = view.findViewById<EditText>(R.id.etAuthor)
        val btnSearch = view.findViewById<Button>(R.id.btnSearch)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val imgCover = view.findViewById<ImageView>(R.id.imgCover)

        val editBook = arguments?.getSerializable("edit_book") as? BookEntity
        if (editBook != null) {
            editBookId = editBook.id
            currentIsbn = editBook.isbn
            currentTitle = editBook.title
            currentAuthor = editBook.author
            currentCover = editBook.coverUrl

            etIsbn.setText(currentIsbn)
            etTitle.setText(currentTitle)
            etAuthor.setText(currentAuthor)

            if (currentCover.isNotEmpty()) {
                Glide.with(this).load(currentCover).into(imgCover)
            }
            btnSearch.visibility = View.GONE
        }

        btnSearch.setOnClickListener {
            val isbn = etIsbn.text.toString()
            if (isbn.isNotEmpty()) {
                val url = "https://openlibrary.org/api/books?bibkeys=ISBN:$isbn&format=json&jscmd=data"
                val queue = Volley.newRequestQueue(requireContext())

                val request = StringRequest(Request.Method.GET, url,
                    { response ->
                        try {
                            val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                            prefs.edit().putString("last_api_response", response).apply()

                            val obj = JSONObject(response)
                            val key = "ISBN:$isbn"

                            if (obj.has(key)) {
                                val bookObj = obj.getJSONObject(key)

                                currentIsbn = isbn
                                currentTitle = bookObj.getString("title")

                                val authorsArray = bookObj.getJSONArray("authors")
                                currentAuthor = authorsArray.getJSONObject(0).getString("name")

                                if (bookObj.has("cover")) {
                                    currentCover = bookObj.getJSONObject("cover").getString("medium")
                                    Glide.with(this).load(currentCover).into(imgCover)
                                } else {
                                    currentCover = ""
                                }

                                etTitle.setText(currentTitle)
                                etAuthor.setText(currentAuthor)

                            } else {
                                Toast.makeText(context, "Книга не найдена", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Ошибка парсинга JSON", Toast.LENGTH_SHORT).show()
                        }
                    },
                    { _ ->
                        Toast.makeText(context, "Ошибка сети: проверьте интернет", Toast.LENGTH_SHORT).show()
                    }
                )
                queue.add(request)
            } else {
                Toast.makeText(context, "Введите ISBN!", Toast.LENGTH_SHORT).show()
            }
        }

        btnSave.setOnClickListener {
            currentTitle = etTitle.text.toString()
            currentAuthor = etAuthor.text.toString()

            if (currentTitle.isEmpty() || currentAuthor.isEmpty()) {
                Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = AppDatabase.getDatabase(requireContext())
            val book = BookEntity(
                id = if (editBookId == -1) 0 else editBookId,
                isbn = currentIsbn,
                title = currentTitle,
                author = currentAuthor,
                coverUrl = currentCover
            )

            if (editBookId == -1) {
                db.bookDao().insertBook(book)
                Toast.makeText(context, "Книга сохранена!", Toast.LENGTH_SHORT).show()
            } else {
                db.bookDao().updateBook(book)
                Toast.makeText(context, "Книга обновлена!", Toast.LENGTH_SHORT).show()
            }

            parentFragmentManager.popBackStack()
        }
    }
}