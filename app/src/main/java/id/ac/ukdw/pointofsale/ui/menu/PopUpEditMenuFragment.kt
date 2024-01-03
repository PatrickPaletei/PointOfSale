package id.ac.ukdw.pointofsale.ui.menu

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
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import id.ac.ukdw.pointofsale.R
import id.ac.ukdw.pointofsale.adapter.TextDrawable
import id.ac.ukdw.pointofsale.databinding.FragmentPopUpEditMenuBinding
import id.ac.ukdw.pointofsale.viewmodel.PageMenuViewModel
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class PopUpEditMenuFragment : DialogFragment() {
    private val pageMenuViewModel: PageMenuViewModel by viewModels()
    private lateinit var binding : FragmentPopUpEditMenuBinding

    private var selectedImageUri: Uri? = null
    private var isNewImageSelected: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPopUpEditMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun getTokenFromPrefs(): String {
        val sharedPreferences = activity?.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        return sharedPreferences?.getString("token", "") ?: ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val token = "Bearer ${getTokenFromPrefs()}"
        binding.batal.setOnClickListener {
            dismiss()
        }

        val sharedPreferences = requireActivity().getSharedPreferences("dataEdit", Context.MODE_PRIVATE)
        val judulMenu = sharedPreferences.getString("judulMenu", "")
        val kategori = sharedPreferences.getString("kategori", "")
        val harga = sharedPreferences.getInt("harga", 0)
        val idEditMenu = sharedPreferences.getInt("id", 0)
        val image = sharedPreferences.getString("image","")

        Glide.with(this)
            .load(image)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(6)))
            .placeholder(R.drawable.ic_blank) // Placeholder while loading
            .error(R.drawable.ic_blank)
            .into(binding.imageContainer)

        binding.tambahGambar.setOnClickListener {
            uploadPicFromGallery()
        }
        binding.tambahGambarCam.setOnClickListener {
            uploadPicFromCamera()
        }

        binding.namaMenu.setText(judulMenu)
        binding.hargaMenu.setText(harga.toString())

        binding.btnEditMenu.setOnClickListener {
            val namaMenuBaru = binding.namaMenu.text.toString()
            val hargaBaru = binding.hargaMenu.text.toString()

            if (namaMenuBaru.isNotEmpty() && hargaBaru.isNotEmpty()) {
                val imageUri = selectedImageUri // Retrieve the selected image URI

                binding.btnEditMenu.startAnimation()
                if (kategori != null) {
                    // Use the selected image URI or the existing image in the container
                    val file = if (isNewImageSelected && imageUri != null) {
                        val inputStream = requireContext().contentResolver.openInputStream(imageUri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        convertImageToFile(bitmap)
                    } else {
                        // Use the existing image shown in the imageContainer ImageView
                        val drawable = binding.imageContainer.drawable
                        // Convert drawable to bitmap (assuming it's a BitmapDrawable)
                        val bitmap = (drawable as BitmapDrawable).bitmap
                        convertImageToFile(bitmap)
                    }

                    pageMenuViewModel.editData(file, namaMenuBaru, hargaBaru.toInt(), kategori, token, idEditMenu)
                    pageMenuViewModel.menuUpdated.observe(viewLifecycleOwner) { menuAdded ->
                        if (menuAdded) {
                            Toast.makeText(context, "Berhasil Mengubah Menu", Toast.LENGTH_SHORT).show()
                            dismiss()
                        } else {
                            Toast.makeText(context, "Gagal Mengubah menu", Toast.LENGTH_SHORT).show()
                        }
                        binding.btnEditMenu.clearAnimation()
                    }
                }
            } else {
                // Handle case when menu details are empty
                Toast.makeText(context, "Nama menu dan harga harus diisi", Toast.LENGTH_SHORT).show()
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