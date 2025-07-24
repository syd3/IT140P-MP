package com.example.malupet

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Pet(
    val id: Int = 0,
    val name: String,
    val type: String,
    val breed: String,
    val age: Int,
    val feedingTime: String? = null,
    val groomingDate: String? = null,
    val vetAppointment: String? = null
) : Parcelable

class MainActivity : AppCompatActivity() {

    private lateinit var nameField: EditText
    private lateinit var typeField: EditText
    private lateinit var breedField: EditText
    private lateinit var ageField: EditText
    private lateinit var addButton: Button
    private lateinit var viewPetsButton: Button
    private lateinit var petListView: RecyclerView

    private val pets = mutableListOf<Pet>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nameField = findViewById(R.id.nameField)
        typeField = findViewById(R.id.typeField)
        breedField = findViewById(R.id.breedField)
        ageField = findViewById(R.id.ageField)
        addButton = findViewById(R.id.addButton)
        viewPetsButton = findViewById(R.id.viewPetsButton)
        petListView = findViewById(R.id.petListView)

        petListView.layoutManager = LinearLayoutManager(this)
        val adapter = PetAdapter(pets)
        petListView.adapter = adapter

        addButton.setOnClickListener {
            val name = nameField.text.toString()
            val type = typeField.text.toString()
            val breed = breedField.text.toString()
            val age = ageField.text.toString().toIntOrNull() ?: 0

            if (name.isBlank() || type.isBlank() || breed.isBlank()) {
                Toast.makeText(this, "Please complete all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val pet = Pet(
                name = name,
                type = type,
                breed = breed,
                age = age
            )
            pets.add(pet)
            adapter.notifyItemInserted(pets.size - 1)
            clearFields()
        }

        viewPetsButton.setOnClickListener {
            val intent = Intent(this, PetListActivity::class.java)
            intent.putParcelableArrayListExtra("pet_list", ArrayList(pets))
            startActivity(intent)
        }
    }

    private fun clearFields() {
        nameField.text.clear()
        typeField.text.clear()
        breedField.text.clear()
        ageField.text.clear()
    }

    inner class PetAdapter(private val pets: List<Pet>) :
        RecyclerView.Adapter<PetAdapter.PetViewHolder>() {

        inner class PetViewHolder(val view: TextView) : RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): PetViewHolder {
            val textView = TextView(parent.context).apply {
                textSize = 16f
                setPadding(20, 20, 20, 20)
                setBackgroundColor(android.graphics.Color.parseColor("#E0F2F1"))
                setTextColor(android.graphics.Color.parseColor("#004D40"))
            }
            return PetViewHolder(textView)
        }

        override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
            val pet = pets[position]
            holder.view.text = "${pet.name} - ${pet.type} (${pet.breed}), Age ${pet.age}"
            holder.view.setOnClickListener {
                val intent = Intent(this@MainActivity, AppointmentActivity::class.java)
                intent.putExtra("pet", pet)
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int = pets.size
    }
}