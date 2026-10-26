package com.bignerdranch.android.pract5_2_api

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.Gson

class LoginFragment : Fragment(R.layout.fragment_login) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val etName = view.findViewById<EditText>(R.id.etName)
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            if (etName.text.toString().isNotEmpty()) {
                val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val user = User(etName.text.toString())
                prefs.edit().putString("user_profile", Gson().toJson(user)).apply()

                (activity as MainActivity).switchFragment(BookFragment())
            } else {
                Toast.makeText(context, "Введите имя!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}