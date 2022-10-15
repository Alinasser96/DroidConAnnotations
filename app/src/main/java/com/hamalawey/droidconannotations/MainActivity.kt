package com.hamalawey.droidconannotations

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recycler = findViewById<RecyclerView>(R.id.persons_RV)

        val persons = listOf(
            Person("Aly Hamalawey", "10th of Ramadan"),
            Person("Ata Ata Ety monty", "My horty")
        )


    }
}