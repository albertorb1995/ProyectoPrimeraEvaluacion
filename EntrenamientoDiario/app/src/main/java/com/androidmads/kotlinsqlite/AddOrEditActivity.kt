package com.androidmads.kotlinsqlite

import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.androidmads.kotlinsqlite.R.id.*
import com.androidmads.kotlinsqlite.db.DatabaseHandler
import com.androidmads.kotlinsqlite.models.Tasks
import kotlinx.android.synthetic.main.activity_add_edit.*
import java.text.SimpleDateFormat
import java.util.*

class AddOrEditActivity : AppCompatActivity() {

    var dbHandler: DatabaseHandler? = null
    var isEditMode = false

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initDB()
        initOperations()
        supportActionBar!!.title = "Ejercicios"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        val textView: TextView = findViewById<TextView>(R.id.input_date)
        textView.text = SimpleDateFormat("dd/MM/yyyy").format(System.currentTimeMillis())

        var cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "dd/MM/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            textView.text = sdf.format(cal.time)

        }

        textView.setOnClickListener {
            DatePickerDialog(this@AddOrEditActivity, dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        val myStrings = arrayOf("Elegir ejercicio...", "Abdominales", "Piernas", "Oblicuos", "Dominadas", "Flexiones", "Espalda", "Pecho")
        mySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, myStrings)


    }

    fun addExercise (view: View) =
            if(mySpinner.selectedItem.equals("Elegir ejercicio...")) {
                Toast.makeText(this, "Elija un ejercicio", Toast.LENGTH_LONG).show()

            }else{

                var areaExercise = input_name

                if(areaExercise.text.toString().equals("")){
                    areaExercise.append(this.mySpinner.selectedItem.toString())
                }else{
                    areaExercise.append(", " + this.mySpinner.selectedItem.toString())
                }

                var areaExercise2 = input_desc

                areaExercise2.append(this.mySpinner.selectedItem.toString()+ ": " + "\n" + "\n")


                val myStrings = arrayOf("Elegir ejercicio...", "Abdominales", "Piernas", "Oblicuos", "Dominadas", "Flexiones", "Espalda", "Pecho")
                mySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, myStrings)

            }



    private fun initDB() {
        dbHandler = DatabaseHandler(this)
        btn_delete.visibility = View.INVISIBLE
        swt_completed.visibility = View.INVISIBLE
        if (intent != null && intent.getStringExtra("Mode") == "E") {
            isEditMode = true
            val tasks: Tasks = dbHandler!!.getTask(intent.getIntExtra("Id",0))
            input_date.setText(tasks.date)
            input_name.setText(tasks.name)
            input_desc.setText(tasks.desc)
            swt_completed.isChecked = tasks.completed == "Y"
            btn_delete.visibility = View.VISIBLE
            swt_completed.visibility = View.VISIBLE
        }
    }

    private fun initOperations() {
        btn_save.setOnClickListener({
            var success: Boolean = false

            if(input_name.text.toString().equals("")) {
                Toast.makeText(this, "Realice una modificación", Toast.LENGTH_LONG).show()
                } else if (!isEditMode) {
                val tasks: Tasks = Tasks()
                tasks.date = input_date.text.toString()
                tasks.name = input_name.text.toString()
                tasks.desc = input_desc.text.toString()

                if (swt_completed.isChecked){
                    tasks.completed = "Y"
                /*dbHandler?.task()*/
                }else
                    tasks.completed = "N"
                success = dbHandler?.addTask(tasks) as Boolean
            } else {
                val tasks: Tasks = Tasks()
                tasks.id = intent.getIntExtra("Id", 0)
                tasks.date = input_date.text.toString()
                tasks.name = input_name.text.toString()
                tasks.desc = input_desc.text.toString()
                if (swt_completed.isChecked){
                    tasks.completed = "Y"
                /*dbHandler?.task()*/
                }
                else
                    tasks.completed = "N"
                success = dbHandler?.updateTask(tasks) as Boolean
            }
            if(swt_completed.isChecked){
                //fdnkfndskfnsdkfsdf
            val tasks: Tasks = Tasks()
            tasks.date = input_date.text.toString()
            tasks.name = input_name.text.toString()
            tasks.desc = input_desc.text.toString()
                dbHandler?.addTask(tasks)
            }

            if (success)
                finish()
        })

        btn_delete.setOnClickListener({
            val dialog = AlertDialog.Builder(this).setTitle("Info").setMessage("¿Desea borrar la actividad?")
                    .setPositiveButton("SI", { dialog, i ->
                        val success = dbHandler?.deleteTask(intent.getIntExtra("Id", 0)) as Boolean
                        if (success)
                            finish()
                        dialog.dismiss()
                    })
                    .setNegativeButton("NO", { dialog, i ->
                        dialog.dismiss()
                    })
            dialog.show()
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
