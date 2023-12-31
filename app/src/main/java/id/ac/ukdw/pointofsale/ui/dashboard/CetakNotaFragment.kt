package id.ac.ukdw.pointofsale.ui.dashboard

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import id.ac.ukdw.pointofsale.R
import id.ac.ukdw.pointofsale.api.Service.ApiClient
import id.ac.ukdw.pointofsale.databinding.FragmentCetakNotaBinding
import id.ac.ukdw.pointofsale.viewmodel.NotaViewModel
import id.ac.ukdw.pointofsale.viewmodel.SharedCheckoutViewModel
import kotlinx.coroutines.delay
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
    private val checkoutViewModel: SharedCheckoutViewModel by activityViewModels()
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
        binding.balikDashboard.setOnClickListener { dismiss() }

        cetakNotaBtn.setOnClickListener {
            binding.btnCetakNota.startAnimation()
            lifecycleScope.launch {
                val idNota = requireActivity().getPreferences(Context.MODE_PRIVATE).getInt("idNota", 0)
                val success = cetakNota(idNota, requireContext())
                if (success) {
                    showToastAndHandleSuccess()
                } else {
                    Toast.makeText(context, "Transaksi Gagal", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showToastAndHandleSuccess() {
        Toast.makeText(context, "Transaksi Berhasil", Toast.LENGTH_SHORT).show()
        binding.cetakNota.visibility = View.GONE
        binding.sedangCetak.visibility = View.VISIBLE
        lifecycleScope.launch {
            delay(2000)
            binding.sedangCetak.visibility = View.GONE
            binding.berhasilCetak.visibility = View.VISIBLE
            binding.backToDash.setOnClickListener { dismiss() }
        }
    }


    private fun clearCheckOut(){
        checkoutViewModel.clearData()
    }

    override fun onDestroy() {
        super.onDestroy()
        clearCheckOut()
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
                        openPDFWithDialog(uri)
                    }
                }
            }
        }
    }

    private fun openPDFWithDialog(pdfUri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setDataAndType(pdfUri, "application/pdf")
        }

        val chooserIntent = Intent.createChooser(intent, "Open PDF")
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            requireContext().startActivity(chooserIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "No PDF viewer app found", Toast.LENGTH_SHORT).show()
        }
    }


}