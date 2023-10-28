package com.example.bankreceiptregisterer

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.ComponentActivity
import java.io.*
import java.util.*


class MainActivity : ComponentActivity() {

    private val opStrList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        updateOperationList()
    }

    override fun onResume() {
        super.onResume()
        updateOperationList()
    }

//    override fun onTopResumedActivityChanged(isTopResumedActivity: Boolean) {
//        super.onTopResumedActivityChanged(isTopResumedActivity)
//        updateOperationList()
//    }

    private fun updateOperationList() {
        val pathDoc = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val fileReceiptList: File = File(pathDoc, "receiptList.txt")
        try {
            val reader = FileInputStream(fileReceiptList)
            opStrList.clear()
            reader.bufferedReader().forEachLine { opStrList.add(it) }
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        findViewById<ListView>(R.id.listOperation).adapter =
            ArrayAdapter<String>(
                applicationContext,
                android.R.layout.simple_list_item_1,
                opStrList)
    }

    fun switchActAddOperation(view: View) {
        val myIntent = Intent(this@MainActivity, AddOperationActivity::class.java)
        this@MainActivity.startActivity(myIntent)
    }
}
