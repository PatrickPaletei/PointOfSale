package id.ac.ukdw.pointofsale.viewmodel

import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.ac.ukdw.pointofsale.api.Service.ApiClientInterface
import id.ac.ukdw.pointofsale.api.response.DataSummary
import id.ac.ukdw.pointofsale.api.response.SummaryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class PenjualanViewModel @Inject constructor(
    private val apiClientInterface: ApiClientInterface
) : ViewModel() {

    private val _responseCode = MutableLiveData<Int?>()
    val responseCode: LiveData<Int?> get() = _responseCode

    private val _responseSummaryToday = MutableLiveData<SummaryResponse?>()
    val responseSummaryToday: LiveData<SummaryResponse?> get() = _responseSummaryToday

    fun export(token: String, fileName: String, context: Context) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiClientInterface.instance.export(token).execute()
                }
                if (response.isSuccessful) {
                    val code = response.code()
                    if (code == 200) {
                        _responseCode.value = code
                        response.body()?.let { saveXlsxToDownloads(it, fileName, context) }
                    }
                } else {
                    Log.d("Gagal Export", "Gagal Export")
                }
            } catch (e: IOException) {
                Log.e("PenjualanViewModel", "Error export: ${e.message} ")
            }
        }
    }

    fun getSummary(criteria: String, liveData: MutableLiveData<SummaryResponse?>) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiClientInterface.instance.getSummaryToday(criteria).execute()
                }
                if (response.isSuccessful) {
                    val code = response.code()
                    if (code == 200) {
                        liveData.value = response.body()
                        Log.d("summaryToday", "getTodaySummary: ${response.body()}")
                    } else {
                        Log.d("summaryTodayGagal", "getTodaySummary: $code")
                    }
                }
            } catch (e: IOException) {
                Log.e("PenjualanViewModel", "Error get summary today: ${e.message} ")
            }
        }
    }

    fun getTodaySummary(criteria: String) {
        getSummary(criteria, _responseSummaryToday)
    }



    private fun openXlxsWithDialog(xlxsUri: Uri, context: Context) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setDataAndType(xlxsUri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        }

        val chooserIntent = Intent.createChooser(intent, "Open XLSX")
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            context.startActivity(chooserIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No XLSX viewer app found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveXlsxToDownloads(
        responseBody: ResponseBody,
        fileName: String,
        context: Context
    ) {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val contentUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI
        val uri = resolver.insert(contentUri, contentValues)

        uri?.let { outputStream ->
            resolver.openOutputStream(outputStream).use { outputStream ->
                responseBody.byteStream().use { inputStream ->
                    if (outputStream != null) {
                        inputStream.copyTo(outputStream)
                        openXlxsWithDialog(uri, context)
                    }
                }
            }
        }
    }


}