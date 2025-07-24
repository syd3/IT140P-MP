package com.example.malupet

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ViewAppointmentsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "All Appointments"

        val petList = intent.getParcelableArrayListExtra<Pet>("pet_list") ?: arrayListOf()

        val scrollView = ScrollView(this)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        if (petList.isEmpty()) {
            layout.addView(TextView(this).apply {
                text = "No appointments available."
                textSize = 20f
            })
        } else {
            for (pet in petList) {
                layout.addView(TextView(this).apply {
                    text = "${pet.name} - Feeding: ${pet.feedingTime ?: "N/A"}, Grooming: ${pet.groomingDate ?: "N/A"}, Vet: ${pet.vetAppointment ?: "N/A"}"
                    textSize = 18f
                    setPadding(0, 12, 0, 12)
                })
            }
        }

        scrollView.addView(layout)
        setContentView(scrollView)
    }
}