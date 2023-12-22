package id.ac.ukdw.pointofsale.ui.menu

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import id.ac.ukdw.pointofsale.R
import id.ac.ukdw.pointofsale.databinding.FragmentHapusMenuBinding
import id.ac.ukdw.pointofsale.databinding.FragmentPopUpEditMenuBinding
import id.ac.ukdw.pointofsale.viewmodel.PageMenuViewModel

@AndroidEntryPoint
class PopUpHapusMenuFragment : DialogFragment() {
    private lateinit var binding : FragmentHapusMenuBinding
    private val pageMenuViewModel: PageMenuViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHapusMenuBinding.inflate(inflater, container, false)
        return binding.root
    }
    private fun getTokenFromPrefs(): String {
        val sharedPreferences = activity?.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        return sharedPreferences?.getString("token", "") ?: ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val token = "Bearer ${getTokenFromPrefs()}"
        val sharedPreferences = requireActivity().getSharedPreferences("dataEdit", Context.MODE_PRIVATE)
        val judulMenu = sharedPreferences.getString("judulMenuDelete", "")
        val idDeleteMenu = sharedPreferences.getInt("idMenuDelete", 0)

        binding.namaMenu.text = judulMenu
        binding.btnDeleteMenu.setOnClickListener {
            binding.btnDeleteMenu.startAnimation()
            if (idDeleteMenu != 0){
                pageMenuViewModel.deleteMenu(idDeleteMenu,token)
                pageMenuViewModel.menuDeleted.observe(viewLifecycleOwner){
                    if (it){
                        Toast.makeText(context, "Berhasil Menghapus Menu", Toast.LENGTH_SHORT).show()
                        dismiss()
                    }else{
                        Toast.makeText(context, "Gagal Mengahpus Menu", Toast.LENGTH_SHORT).show()

                    }
                }
            }else{
                Toast.makeText(context, "Gagal Mengahpus Menu Harap Cek Koneksi Internet!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onStart() {
        super.onStart()
        setDialogDimensions(450, 400)
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