package com.bignerdranch.android.pract5_2_api

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class BookFragment : Fragment(R.layout.fragment_book) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var db: AppDatabase

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.getDatabase(requireContext())

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val btnAdd = view.findViewById<Button>(R.id.btnAddBook)
        btnAdd.setOnClickListener {
            (activity as MainActivity).switchFragment(ListFragment())
        }

        loadData()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        val books = db.bookDao().getAllBooks()
        recyclerView.adapter = BookAdapter(books,
            onEdit = { book ->
                val fragment = ListFragment()
                val bundle = Bundle().apply { putSerializable("edit_book", book) }
                fragment.arguments = bundle
                (activity as MainActivity).switchFragment(fragment)
            },
            onDelete = { book ->
                db.bookDao().deleteBook(book)
                loadData()
            }
        )
    }
}

class BookAdapter(
    val list: List<BookEntity>,
    val onEdit: (BookEntity) -> Unit,
    val onDelete: (BookEntity) -> Unit
) : RecyclerView.Adapter<BookAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView = v.findViewById(R.id.imgItemCover)
        val title: TextView = v.findViewById(R.id.tvItemTitle)
        val author: TextView = v.findViewById(R.id.tvItemAuthor)
        val btnEdit: Button = v.findViewById(R.id.btnItemEdit)
        val btnDelete: Button = v.findViewById(R.id.btnItemDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val book = list[position]
        holder.title.text = book.title
        holder.author.text = "Автор: ${book.author}"

        if (book.coverUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context).load(book.coverUrl).into(holder.img)
        } else {
            holder.img.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        holder.btnEdit.setOnClickListener { onEdit(book) }
        holder.btnDelete.setOnClickListener { onDelete(book) }
    }

    override fun getItemCount() = list.size
}