package com.example.malupet

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class AppointmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.title = "Edit Appointments"

        val pet = intent.getParcelableExtra<Pet>("pet")

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        if (pet != null) {
            layout.addView(TextView(this).apply {
                text = "Appointments for ${pet.name}"
                textSize = 22f
            })

            val feedingField = EditText(this).apply {
                hint = "Feeding Time"
                isFocusable = false
                setOnClickListener {
                    val calendar = Calendar.getInstance()
                    TimePickerDialog(this@AppointmentActivity, { _, hour, minute ->
                        setText("${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}")
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
                }
            }

            val groomingField = EditText(this).apply {
                hint = "Grooming Date"
                isFocusable = false
                setOnClickListener {
                    val calendar = Calendar.getInstance()
                    DatePickerDialog(this@AppointmentActivity, { _, year, month, day ->
                        setText("${month + 1}/$day/$year")
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
                }
            }

            val vetField = EditText(this).apply {
                hint = "Vet Appointment Date"
                isFocusable = false
                setOnClickListener {
                    val calendar = Calendar.getInstance()
                    DatePickerDialog(this@AppointmentActivity, { _, year, month, day ->
                        setText("${month + 1}/$day/$year")
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
                }
            }

            layout.addView(feedingField)
            layout.addView(groomingField)
            layout.addView(vetField)

            val saveButton = Button(this).apply {
                text = "Save"
                setOnClickListener {
                    Toast.makeText(this@AppointmentActivity, "Appointments saved (not persisted).", Toast.LENGTH_SHORT).show()
                }
            }
            layout.addView(saveButton)

        } else {
            layout.addView(TextView(this).apply {
                text = "No pet information available."
                textSize = 20f
            })
        }

        setContentView(layout)
    }
}