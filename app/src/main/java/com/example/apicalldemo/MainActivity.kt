package com.example.apicalldemo

import android.app.Dialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.loader.content.AsyncTaskLoader
import com.example.apicalldemo.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL


class MainActivity : AppCompatActivity() {

    // TODO 9: Create an Instance of the ActivityMainBinding class
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO 10: Comment or delete the next line
        //setContentView(R.layout.activity_main)

        // TODO 11: Perform the following steps to setup the instance of the binding class
        // 11.1. Call the static inflate() method included in the generated binding class
        binding = ActivityMainBinding.inflate(layoutInflater)
        // 11.2. Get a reference to the root view by either calling the getRoot() method
        val view = binding.root
        // 11.3. Pass the root view to setContentView() to make it the active view on the screen
        setContentView(view)

        CallAPILoginAsyncTask().execute()
    }

    // TODO 5: Create class CallAPILoginAsyncTask
    private inner class CallAPILoginAsyncTask(): AsyncTask<Any, Void, String>(){

        // TODO 7: Create a little object that will allow me to create a custom dialog
        private lateinit var customProgressDialog: Dialog


        // TODO 14: Implement onPreExecute()
        // The code inside is executed before doInBackground (before to connect to the internet)
        override fun onPreExecute(){
            super.onPreExecute()
            showProgressDialog()
        }


        // TODO 6: Implement method doInBackground
        override fun doInBackground(vararg params: Any?): String {
            var result: String
            var connection: HttpURLConnection? = null

            try{
                val url = URL("https://run.mocky.io/v3/0ec070bb-3df0-46e5-a9f2-7780e045e103")
                connection = url.openConnection() as HttpURLConnection
                connection.doInput = true   // Do we get data?
                connection.doOutput = true   // Do we send data?

                val httpResult : Int = connection.responseCode

                if(httpResult == HttpURLConnection.HTTP_OK){
                    val inputStream = connection.inputStream

                    val reader = BufferedReader(
                        InputStreamReader(inputStream)
                    )
                    val stringBuilder = StringBuilder()
                    var line: String?
                    try{
                        while(reader.readLine().also {line = it} != null){
                            stringBuilder.append(line + "\n")
                        }

                    }catch (e: IOException){
                        e.printStackTrace()
                    }finally {
                        try{
                            inputStream.close()
                        }catch (e: IOException){
                            e.printStackTrace()
                        }
                    }
                    result = stringBuilder.toString()
                }else{
                    result = connection.responseMessage
                }
            }catch(e: SocketTimeoutException){
                result = "Connection Timeout"
            }catch(e: Exception){
                result = "Error : " + e.message
            }finally {
                connection?.disconnect()
            }

            return result
        }


        // TODO 15: After the connection
        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            cancelProgressDialog()

            Log.i("JSON Reponse Result", result)
        }

        // TODO 12: Create the method showProgressDialog
        private fun showProgressDialog(){
            customProgressDialog = Dialog(this@MainActivity)
            customProgressDialog.setContentView(R.layout.dialog_custom_progress)
            customProgressDialog.show()
        }

        // TODO 13: Create the method cancelProgressDialog
        private fun cancelProgressDialog(){
            customProgressDialog.dismiss()
        }
    }


}