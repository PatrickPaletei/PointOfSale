package id.ac.ukdw.pointofsale.ui.karyawan

import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import id.ac.ukdw.pointofsale.R
import id.ac.ukdw.pointofsale.databinding.FragmentRegisBinding
import id.ac.ukdw.pointofsale.viewmodel.KarywawanViewModel
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class RegisFragment : DialogFragment() {

    private val karyawanViewModel: KarywawanViewModel by viewModels()
    private lateinit var binding: FragmentRegisBinding

    private var selectedImageUri: Uri? = null
    private var isNewImageSelected: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.batal.setOnClickListener {
            dismiss()
        }
        binding.tambahGambar.setOnClickListener {
            uploadPicFromGallery()
        }
        binding.tambahGambarCam.setOnClickListener {
            uploadPicFromCamera()
        }

        binding.btnRegis.setOnClickListener {
            val username = binding.username.text.toString().trim()
            val nama = binding.namaKaryawan.text.toString().trim()
            val pass = binding.password.text.toString()
            val konfirmPass = binding.konfirmasiPassword.text.toString()

            if (pass == konfirmPass && pass.length >= 6 && nama.isNotEmpty() && username.isNotEmpty()) {
                val token = "Bearer ${getTokenFromPrefs()}"
                Log.d("xmxx", "onViewCreated: $pass $konfirmPass")
                regisUser(token, nama, pass, username)
            } else {
                when {
                    nama.isEmpty() -> {
                        Toast.makeText(context, "Nama tidak boleh kosong!", Toast.LENGTH_SHORT)
                            .show()
                    }

                    username.isEmpty() -> {
                        Toast.makeText(context, "Username tidak boleh kosong!", Toast.LENGTH_SHORT)
                            .show()
                    }

                    pass != konfirmPass -> {
                        Toast.makeText(context, "Password tidak sama!", Toast.LENGTH_SHORT).show()
                    }

                    pass.length <= 6 -> {
                        Toast.makeText(
                            context,
                            "Password harus lebih dari 6 karakter!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        karyawanViewModel.responseCodeRegister.observe(viewLifecycleOwner) { responseCode ->
            if (responseCode == 201) {
                Toast.makeText(context, "Registrasi Berhasil!", Toast.LENGTH_SHORT).show()
                dismiss()
            } else {
                Toast.makeText(
                    context,
                    "Registrasi Gagal. Harap coba username lain!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun convertImageToFile(bitmap: Bitmap): File {
        val contextWrapper = ContextWrapper(requireContext())
        val file = File(contextWrapper.cacheDir, "tempImage.jpg")
        file.createNewFile()

        // Convert bitmap to file
        val fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()

        return file
    }
    private fun uploadPicFromGallery() {
        pickImages.launch("image/*")
    }

    private fun uploadPicFromCamera() {
        takePicture.launch(null)
    }
    private val pickImages =
        registerForActivityResult(ActivityResultContracts.GetContent()) { selectedImageUri ->
            selectedImageUri?.let {
                binding.imageContainer.setImageURI(selectedImageUri)
                this.selectedImageUri = selectedImageUri
                isNewImageSelected = true// Assign the selected URI to the member variable
            }
        }

    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { result ->
            result?.let { imageBitmap ->
                binding.imageContainer.setImageBitmap(imageBitmap)
                // Assuming you're converting the captured image to URI and assigning it to selectedImageUri
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, "image_${System.currentTimeMillis()}.jpg")
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                }
                val uri = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                uri?.let {
                    this.selectedImageUri = uri
                    val outputStream = requireContext().contentResolver.openOutputStream(uri)
                    if (outputStream != null) {
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    }
                    outputStream?.close()
                    isNewImageSelected = true
                }
            }
        }

    private fun regisUser(token: String, nama: String, password: String, username: String) {
        val imageFile = selectedImageUri?.let { uri ->
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            convertImageToFile(bitmap)
        }

        if (imageFile != null || isNewImageSelected) {
            val file = imageFile ?: run {
                val drawable = binding.imageContainer.drawable
                val bitmap = (drawable as BitmapDrawable).bitmap
                convertImageToFile(bitmap)
            }

            // Start animation before making the API call
            binding.btnRegis.startAnimation()

            karyawanViewModel.regisUser(
                file,
                username = username,
                nama_karyawan = nama,
                passwrod = password,
                token = token
            )
        } else {
            // Handle case when no image is selected
            Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
        }
    }





    private fun getTokenFromPrefs(): String {
        val sharedPreferences = activity?.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        return sharedPreferences?.getString("token", "") ?: ""
    }

    override fun onStart() {
        super.onStart()
        setDialogDimensions(500, 550)
    }

    private fun setDialogDimensions(widthInDp: Int, heightInDp: Int) {
        val widthInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, widthInDp.toFloat(), resources.displayMetrics
        ).toInt()

        val heightInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, heightInDp.toFloat(), resources.displayMetrics
        ).toInt()

        dialog?.window?.setLayout(widthInPixels, heightInPixels)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_dialog)
        return dialog
    }
}