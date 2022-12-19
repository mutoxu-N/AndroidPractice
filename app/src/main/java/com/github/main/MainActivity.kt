package com.github.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.renderscript.ScriptGroup
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.core.os.HandlerCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    companion object {
        private const val DEBUG_TAG = "AsyncSample"
        private const val WEATHERINFO_URL = "https://api.openweathermap.org/data/2.5/weather?lang=ja"
        private const val APP_ID = ""
    }

    private var _list: MutableList<MutableMap<String, String>> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        _list = createList()
        val lvCityList = findViewById<ListView>(R.id.lvCityList)
        val from = arrayOf("name")
        val to = intArrayOf(android.R.id.text1)
        val adapter = SimpleAdapter(this@MainActivity, _list, android.R.layout.simple_list_item_1, from, to)
        lvCityList.adapter = adapter
        lvCityList.onItemClickListener = ListItemClickListener()

    }

    private fun createList(): MutableList<MutableMap<String, String>> {
        var list: MutableList<MutableMap<String, String>> = mutableListOf()
        list.add(mutableMapOf("name" to "大阪", "q" to "Osaka"))
        list.add(mutableMapOf("name" to "神戸", "q" to "Kobe"))
        list.add(mutableMapOf("name" to "京都", "q" to "Kyoto"))
        list.add(mutableMapOf("name" to "大津", "q" to "Otsu"))
        list.add(mutableMapOf("name" to "奈良", "q" to "Nara"))
        list.add(mutableMapOf("name" to "和歌山", "q" to "Wakayama"))
        list.add(mutableMapOf("name" to "姫路", "q" to "Himeji"))

        return list
    }

    private inner class ListItemClickListener: AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
            val item = _list.get(pos)
            val q = item.get("q")
            q?.let {
                val urlFull = "$WEATHERINFO_URL&q=$q&appid=$APP_ID"
                receiveWeatherInfo(urlFull)
            }

        }

    }

    @UiThread
    private fun receiveWeatherInfo(urlFull: String) {
        // コルーチン処理の記述
        lifecycleScope.launch {
            val result = weatherInfoBackgroundRunner(urlFull)
            weatherInfoPostRunner(result)
        }
    }

    @WorkerThread
    private suspend fun weatherInfoBackgroundRunner(url: String): String {
        val returnVal = withContext(Dispatchers.IO) {
            var result = ""
            val url = URL(url)
            val con = url.openConnection() as? HttpURLConnection
            con?.let {
                try {
                    it.connectTimeout = 1000
                    it.readTimeout = 1000
                    it.requestMethod = "GET"
                    it.connect()

                    val stream = it.inputStream
                    result = is2String(stream)
                    stream.close()
                } catch (ex: SocketTimeoutException) {
                    Log.w(DEBUG_TAG, "通信タイムアウト", ex)
                }
                it.disconnect()
            }
            result
        }
        return returnVal
    }

    private fun is2String(stream: InputStream): String {
        val sb = StringBuilder()
        val reader = BufferedReader(InputStreamReader(stream, "UTF-8"))
        var line = reader.readLine()
        while(line != null) {
            sb.append(line)
            line = reader.readLine()
        }
        reader.close()
        return sb.toString()
    }

    @UiThread
    private fun weatherInfoPostRunner(result: String) {
        val rootJson = JSONObject(result)
        val cityName = rootJson.getString("name")
        val coordJSON = rootJson.getJSONObject("coord")
        val latitude = coordJSON.getString("lat")
        val longitude = coordJSON.getString("lon")
        val weatherJSONArray = rootJson.getJSONArray("weather")
        val weatherJSON = weatherJSONArray.getJSONObject(0)
        val weather = weatherJSON.getString("description")
        val telop = "${cityName}の天気"
        val desc = "現在は${weather}です。\n井戸は${latitude}度で経度は${longitude}度です。"
        val tvWeatherTelop = findViewById<TextView>(R.id.tvWeatherTelop)
        val tvWeatherDesc = findViewById<TextView>(R.id.tvWeatherDesc)
        tvWeatherTelop.text = telop
        tvWeatherDesc.text = desc

    }
}