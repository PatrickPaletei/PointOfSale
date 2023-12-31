package id.ac.ukdw.pointofsale.ui.menu

import android.app.Dialog
import android.content.Context
import android.os.Bundle
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
import id.ac.ukdw.pointofsale.databinding.FragmentPopUpEditMenuBinding
import id.ac.ukdw.pointofsale.viewmodel.PageMenuViewModel

@AndroidEntryPoint
class PopUpEditMenuFragment : DialogFragment() {
    private val pageMenuViewModel: PageMenuViewModel by viewModels()
    private lateinit var binding : FragmentPopUpEditMenuBinding


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
            binding.btnEditMenu.startAnimation()
            if(namaMenuBaru.isNotEmpty() && hargaBaru.isNotEmpty()){
                if (kategori != null) {
                    pageMenuViewModel.editData(namaMenuBaru,hargaBaru.toInt(),kategori,token,idEditMenu)
                    pageMenuViewModel.menuUpdated.observe(viewLifecycleOwner) { menuAdded ->
                        if (menuAdded) {
                            Toast.makeText(context, "Berhasil Mengubah Menu", Toast.LENGTH_SHORT).show()
                            dismiss()
                        } else {
                            Toast.makeText(context, "Gagal Mengubah menu", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

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
            }
        }

    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { result ->
            result?.let { image ->
                binding.imageContainer.setImageBitmap(image)
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