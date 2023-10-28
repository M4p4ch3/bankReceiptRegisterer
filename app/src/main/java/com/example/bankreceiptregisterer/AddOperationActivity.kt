package com.example.bankreceiptregisterer

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.ComponentActivity
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class AddOperationActivity : ComponentActivity() {

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    private var dateStr: String = dateFormat.format(calendar.time)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_operation)

        findViewById<TextView>(R.id.textViewDateValue).text = "$dateStr"

        val spinner: Spinner = findViewById(R.id.spinnerMode)
        ArrayAdapter.createFromResource(
            this,
            R.array.arrayMode,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    fun pickDate(view: View) {
        val datePickerDialog = DatePickerDialog(
            this, {_, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                calendar.set(year, monthOfYear, dayOfMonth)
                dateStr = dateFormat.format(calendar.time)
                findViewById<TextView>(R.id.textViewDateValue).text = "$dateStr"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    fun cancel(view: View) {
        val builder = AlertDialog.Builder(this)
        builder.setPositiveButton("Yes") { dialog, which ->
            this@AddOperationActivity.finish()
        }
        builder.setNegativeButton("No") { dialog, which ->
            // NOOP
        }

        builder.setTitle("Confirm cancel operation ?")

        val d = builder.create()
        d.show()
    }

    fun addOperation(view: View) {
        val mode = findViewById<Spinner>(R.id.spinnerMode).getSelectedItem().toString()

        val tier = findViewById<EditText>(R.id.editTextTier).text.toString().trimEnd()
        val cat = findViewById<EditText>(R.id.editTextCat).text.toString().trimEnd()
        val desc = findViewById<EditText>(R.id.editTextDesc).text.toString().trimEnd()

        var amount = 0.0
        val amountStr = findViewById<EditText>(R.id.editTextAmount).getText().toString()
        if (amountStr != "") {
            amount = amountStr.toDouble()
        }

        // date,mode,tier,cat,desc,amount
        // 2023-09-19,cb,osteo,health,total reset consult 4,-60.0
        val csvEntry: String = "$dateStr,$mode,$tier,$cat,$desc,$amount\n"
        findViewById<TextView>(R.id.textViewCsvEntry).text = csvEntry

        val builder = AlertDialog.Builder(this)
        builder.setPositiveButton("Yes") { dialog, which ->
            val pathDoc = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            val fileReceiptList: File = File(pathDoc, "receiptList.txt")
            try {
                val writer = FileOutputStream(fileReceiptList, true)
                writer.write(csvEntry.toByteArray())
                writer.close()
                findViewById<TextView>(R.id.textViewWriteStatus).text = "OK"
            } catch (e: IOException) {
                e.printStackTrace()
                findViewById<TextView>(R.id.textViewWriteStatus).text = "FAILED"
            }

            this@AddOperationActivity.finish()
        }
        builder.setNegativeButton("No") { dialog, which ->
            // NOOP
        }

        builder.setTitle("Confirm add operation ?")
        builder.setMessage("Entry : $csvEntry")

        val d = builder.create()
        d.show()
    }
}
