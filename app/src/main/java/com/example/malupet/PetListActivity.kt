package com.example.malupet

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PetListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Pet List"

        val petList = intent.getParcelableArrayListExtra<Pet>("pet_list") ?: arrayListOf()

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        val viewAppointmentsButton = Button(this).apply {
            text = "View All Appointments"
            setOnClickListener {
                val intent = Intent(this@PetListActivity, ViewAppointmentsActivity::class.java)
                intent.putParcelableArrayListExtra("pet_list", petList)
                startActivity(intent)
            }
        }

        layout.addView(viewAppointmentsButton)

        if (petList.isEmpty()) {
            layout.addView(TextView(this).apply {
                text = "No pets found."
                textSize = 20f
            })
        } else {
            for (pet in petList) {
                val petInfo = TextView(this).apply {
                    text = "${pet.name} - ${pet.type} (${pet.breed}), Age ${pet.age}"
                    textSize = 18f
                    setPadding(0, 16, 0, 16)
                    setOnClickListener {
                        val intent = Intent(this@PetListActivity, AppointmentActivity::class.java)
                        intent.putExtra("pet", pet)
                        startActivity(intent)
                    }
                }
                layout.addView(petInfo)
            }
        }

        val scrollView = ScrollView(this)
        scrollView.addView(layout)
        setContentView(scrollView)
    }
}