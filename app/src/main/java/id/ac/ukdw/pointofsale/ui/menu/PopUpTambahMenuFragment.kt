package id.ac.ukdw.pointofsale.ui.menu

import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresExtension
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import id.ac.ukdw.pointofsale.R
import id.ac.ukdw.pointofsale.databinding.FragmentPopUpTambahMenuBinding
import id.ac.ukdw.pointofsale.viewmodel.PageMenuViewModel
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class PopUpTambahMenuFragment : DialogFragment() {
    private val pageMenuViewModel: PageMenuViewModel by viewModels()
    private lateinit var binding: FragmentPopUpTambahMenuBinding
    private var selectedSpinnerValue: String = ""
    private var selectedImageUri: Uri? = null
    private var isNewImageSelected: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPopUpTambahMenuBinding.inflate(inflater, container, false)
        initializeViews()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtonClicks()

        binding.tambahGambar.setOnClickListener {
            uploadPicFromGallery()
        }
        binding.tambahGambarCam.setOnClickListener {
            uploadPicFromCamera()
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

    private fun initializeViews() {
        val spinnerOptions = listOf("Pilih Kategori", "Makanan", "Minuman", "Snack", "Lain-Lain")


        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinner: Spinner = binding.spinnerKategori
        spinner.adapter = adapter
        spinner.setSelection(0, false)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedSpinnerValue = if (position != 0) spinnerOptions[position] else ""
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedSpinnerValue = ""
            }
        }
    }

    private fun getTokenFromPrefs(): String {
        val sharedPreferences = activity?.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        return sharedPreferences?.getString("token", "") ?: ""
    }

    private fun setupButtonClicks() {
        val btnTambah = binding.btnTambahMenu
        btnTambah.setOnClickListener {
            val namaMenu = binding.namaMenu.text.toString()
            val hargaText = binding.hargaMenu.text.toString()

            if (selectedSpinnerValue.isNotEmpty() && namaMenu.isNotEmpty() && hargaText.isNotEmpty()) {
                try {
                    val harga = hargaText.toInt()

                    val token = getTokenFromPrefs()
                    val bearer = "Bearer $token"

                    val imageFile = selectedImageUri?.let { uri ->
                        // Retrieve the file from the selected URI
                        val inputStream = requireContext().contentResolver.openInputStream(uri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        convertImageToFile(bitmap)
                    }

                    if (imageFile != null || isNewImageSelected) {
                        // If a new image is selected or none, use it or use the existing image
                        val file = imageFile ?: run {
                            val drawable = binding.imageContainer.drawable
                            val bitmap = (drawable as BitmapDrawable).bitmap
                            convertImageToFile(bitmap)
                        }
                        btnTambah.startAnimation()
                        pageMenuViewModel.addData(namaMenu, harga, selectedSpinnerValue, bearer, file)
                        pageMenuViewModel.menuAdded.observe(viewLifecycleOwner) { menuAdded ->
                            if (menuAdded) {
                                Toast.makeText(context, "Berhasil Menambahkan Menu", Toast.LENGTH_SHORT)
                                    .show()
                                dismiss()
                            } else {
                                Toast.makeText(context, "Gagal menambahkan menu", Toast.LENGTH_SHORT)
                                    .show()
                                dismiss()
                            }
                            btnTambah.clearAnimation()
                        }
                    } else {
                        // Handle case when no image is selected
                        Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
                        btnTambah.clearAnimation()
                    }
                } catch (e: NumberFormatException) {
                    Toast.makeText(context, "Harga harus angka yang valid", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(context, "Harap Lengkapi Semua Pilihan", Toast.LENGTH_SHORT).show()
            }
        }

        binding.batal.setOnClickListener { dismiss() }
    }

    override fun onStart() {
        super.onStart()
        setDialogDimensions(500, 500)
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
