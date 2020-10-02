package com.example.converter1

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL


class MainActivity : AppCompatActivity() {
    var baseCurrency = "EUR"
    var convertedCurrency = "USD"
    var conversionRate = 0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        spinnerSetup()
        txtChanged()
    }
    private fun txtChanged()
    {
        et_firstConversion.addTextChangedListener(object: TextWatcher
        {
            override fun afterTextChanged(p0: Editable?) {
                try
                {
                    getApiResult()
                }
                catch(e: Exception)
                {
                    Log.e("Main","$e")
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("Main","Before Text Changed")
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("Main","On Text Changed")
            }
        })
    }
    private fun getApiResult()
    {
        if(et_firstConversion !=null&&et_firstConversion.text.isNotEmpty()&&et_firstConversion.text.isNotBlank())
        {
            val API = "https://api.ratesapi.io/api/latest?base=$baseCurrency&symbols=$convertedCurrency"
            if(baseCurrency==convertedCurrency)
            {
                Toast.makeText(applicationContext, "Can not convert the same currency", Toast.LENGTH_SHORT).show()
            }
            else
            {
                GlobalScope.launch(Dispatchers.IO)
                {
                    try
                    {
                        val apiResult= URL(API).readText()
                        val jsonObject = JSONObject(apiResult)
                        conversionRate = jsonObject.getJSONObject("rates").getString(convertedCurrency).toFloat()
                        Log.d("Main", "$conversionRate")
                        Log.d("Main",apiResult)
                        withContext(Dispatchers.Main)
                        {
                            val text = ((et_firstConversion.text.toString().toFloat())*conversionRate).toString()
                            et_secondConversion?.setText(text)
                        }
                    }
                    catch(e: Exception)
                    {
                        Log.e("Main","$e")
                    }
                }
            }
        }
    }
    private fun spinnerSetup()
    {
        val spinner: Spinner = findViewById(R.id.spinner_firstConversion)
        val spinner2: Spinner = findViewById(R.id.spinner_secondConversion)
        ArrayAdapter.createFromResource(
            this,
            R.array.currencies,
            android.R.layout.simple_spinner_item
        ).also{adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        ArrayAdapter.createFromResource(
                this,
        R.array.currencies2,
        android.R.layout.simple_spinner_item
        ).also{adapter ->
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner2.adapter = adapter
        }
        spinner.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                baseCurrency = p0?.getItemAtPosition(p2).toString()
                getApiResult()
            }
        })
        spinner2.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                convertedCurrency = p0?.getItemAtPosition(p2).toString()
                getApiResult()
            }
        })
        val reverseButton: Button = findViewById(R.id.button2)
        reverseButton.setOnClickListener()
        {
           // Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
            var str = baseCurrency
            baseCurrency = convertedCurrency
            convertedCurrency = str
            spinner.setSelection(
                (spinner.getAdapter() as ArrayAdapter<String?>).getPosition(
                    baseCurrency
                )
            )
            spinner2.setSelection(
                (spinner2.getAdapter() as ArrayAdapter<String?>).getPosition(
                    convertedCurrency
                )
            )
        }
    }
}