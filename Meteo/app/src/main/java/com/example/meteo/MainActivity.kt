package com.example.meteo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var editTextCity: EditText
    private lateinit var buttonSearch: Button
    private lateinit var weatherContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        editTextCity = findViewById(R.id.editTextCity)
        buttonSearch = findViewById(R.id.buttonSearch)
        weatherContainer = findViewById(R.id.weatherContainer)
    }

    private fun setupClickListeners() {
        buttonSearch.setOnClickListener {
            val cityName = editTextCity.text.toString().trim()
            if (cityName.isNotEmpty()) {
                Toast.makeText(
                    this,
                    "Recherche pour: $cityName",
                    Toast.LENGTH_SHORT
                ).show()
                lifecycleScope.launch {
                    val list = getDataCity(cityName);
                    if (list.length() > 0) {
                        displaySampleData(list.getJSONObject(0))
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Ville n'existe pas",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(
                    this,
                    "Veuillez entrer le nom d'une ville",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    suspend fun getDataCity(cityName: String): JSONArray {
        return withContext(Dispatchers.IO) {
            try {
                //val urlService = "https://api.openweathermap.org/data/2.5/weather?q=$cityName,uk&APPID=af0148bccec6ed63f27e2776720cd5ae"
                val urlService = "https://api.openweathermap.org/data/2.5/weather?q=$cityName,uk&APPID=3aed5994bdf03d38f905f7778ecd3842"
                val url = URL(urlService)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val stream = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonObject = JSONObject(stream)
                    JSONArray().put(jsonObject)
                } else {
                    JSONArray()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                JSONArray()
            }
        }
    }


    private fun displaySampleData(item : JSONObject) {
        weatherContainer.removeAllViews()

        val main = item.getJSONObject("main")
        val weather = item.getJSONArray("weather").getJSONObject(0)
        val dateSecondes = item.getString("dt")
        val dateMuliSecondes = Date(dateSecondes.toLong() * 1000)
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val date = format.format(dateMuliSecondes)

        addWeatherItem(
            date,
            main.getDouble("temp_min").toInt(),
            main.getDouble("temp_max").toInt(),
            main.getInt("pressure"),
            main.getInt("humidity"),
            weather.getString("icon")
        )

    }

    private fun addWeatherItem(
        date: String,
        minTemp: Int,
        maxTemp: Int,
        pressure: Int,
        humidity: Int,
        weatherType: String
    ) {
        val itemView = layoutInflater.inflate(R.layout.weather_item, null)

        val dateText = itemView.findViewById<TextView>(R.id.dateText)
        val weatherIcon = itemView.findViewById<ImageView>(R.id.weatherIcon)
        val minTempText = itemView.findViewById<TextView>(R.id.minTempText)
        val maxTempText = itemView.findViewById<TextView>(R.id.maxTempText)
        val pressureText = itemView.findViewById<TextView>(R.id.pressureText)
        val humidityText = itemView.findViewById<TextView>(R.id.humidityText)

        dateText.text = date
        minTempText.text = "${(minTemp-273.15).toInt()}°C"
        maxTempText.text = "${(maxTemp-273.15).toInt()}°C"
        pressureText.text = "${pressure} hPa"
        humidityText.text = "${humidity} %"

        when (weatherType.lowercase()) {
            "01d", "02d" -> weatherIcon.setImageResource(R.drawable.ic_sunny)
            "09d", "10d", "11d" -> weatherIcon.setImageResource(R.drawable.ic_rainy)
            "03d", "04d" -> weatherIcon.setImageResource(R.drawable.ic_cloudy)
            "13d", "50d" -> weatherIcon.setImageResource(R.drawable.ic_snowy)
            else -> weatherIcon.setImageResource(R.drawable.ic_sunny)
        }

        weatherContainer.addView(itemView)
    }
}