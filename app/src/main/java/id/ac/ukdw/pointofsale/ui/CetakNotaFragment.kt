package id.ac.ukdw.pointofsale.ui

import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import id.ac.ukdw.pointofsale.R
import id.ac.ukdw.pointofsale.api.Service.ApiClient
import id.ac.ukdw.pointofsale.databinding.FragmentCetakNotaBinding
import id.ac.ukdw.pointofsale.viewmodel.NotaViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class CetakNotaFragment : DialogFragment() {

    private var _binding: FragmentCetakNotaBinding? = null
    private val binding get() = _binding!!
    private val notaViewModel: NotaViewModel by viewModels({ requireActivity() })
    var idNota = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCetakNotaBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val widthInDp = 400// Replace with your desired width in dp
        val heightInDp = 500 // Replace with your desired height in dp

        val widthInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            widthInDp.toFloat(),
            resources.displayMetrics
        ).toInt()

        val heightInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            heightInDp.toFloat(),
            resources.displayMetrics
        ).toInt()

        dialog?.window?.setLayout(widthInPixels, heightInPixels)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_dialog)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cetakNotaBtn = binding.btnCetakNota
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val idNota = sharedPref.getInt("idNota",0)
        binding.balikDashboard.setOnClickListener {
            dismiss()
        }

        cetakNotaBtn.setOnClickListener {
            Log.d("tol", "onViewCreated: $idNota")
            lifecycleScope.launch {
                val cetakNota = cetakNota(idNota,requireContext())
                if (cetakNota == true){
                    Toast.makeText(context, "Berhasil donwload nota", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context, "Gagal donwload nota", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun cetakNota(idNota: Int, context: Context): Boolean {
        return suspendCoroutine { continuation ->
            ApiClient.instance.cetakNota(idNota)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            if (responseBody != null) {
                                savePdfToDownloads(responseBody, "nota_$idNota.pdf", context)
                                continuation.resume(true) // Successfully saved PDF
                            } else {
                                continuation.resume(false) // Response body is null
                            }
                        } else {
                            continuation.resume(false) // Unsuccessful response
                        }

                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        continuation.resume(false) // Network request failure
                    }
                })
        }
    }

    private fun savePdfToDownloads(responseBody: ResponseBody, fileName: String, context: Context) {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val contentUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI
        val uri = resolver.insert(contentUri, contentValues)

        uri?.let { outputStream ->
            resolver.openOutputStream(outputStream).use { outputStream ->
                responseBody.byteStream().use { inputStream ->
                    if (outputStream != null) {
                        inputStream.copyTo(outputStream)
                    }
                }
            }
        }
    }


}