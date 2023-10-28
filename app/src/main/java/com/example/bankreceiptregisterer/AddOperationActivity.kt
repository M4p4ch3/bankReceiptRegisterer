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
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar


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

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm add operation ?")
        builder.setMessage("Operation : $csvEntry")
        builder.setPositiveButton("Yes") { dialog, which ->
            val builder2 = AlertDialog.Builder(this)
            builder.setMessage("Operation : $csvEntry")
            builder2.setPositiveButton("OK") { dialog, which ->
                this@AddOperationActivity.finish()
            }

            val pathDoc = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            val fileReceiptList: File = File(pathDoc, "receiptList.txt")
            try {
                val writer = FileOutputStream(fileReceiptList, true)
                writer.write(csvEntry.toByteArray())
                writer.close()
                builder2.setTitle("Add operation OK")
            } catch (e: IOException) {
                e.printStackTrace()
                builder2.setTitle("Add operation FAILED")
            }

            val d2 = builder2.create()
            d2.show()
        }
        builder.setNegativeButton("No") { dialog, which ->
            // NOOP
        }

        val d = builder.create()
        d.show()
    }
}
