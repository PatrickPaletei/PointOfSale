package id.ac.ukdw.pointofsale.ui.karyawan

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import id.ac.ukdw.pointofsale.R
import id.ac.ukdw.pointofsale.databinding.FragmentDeleteKaryawanBinding
import id.ac.ukdw.pointofsale.viewmodel.KarywawanViewModel

@AndroidEntryPoint
class DeleteKaryawanFragment : DialogFragment() {

    private lateinit var binding: FragmentDeleteKaryawanBinding
    private val karyawanViewModel: KarywawanViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDeleteKaryawanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences =
            requireActivity().getSharedPreferences("dataEdit", Context.MODE_PRIVATE)
        val namaKaryawan = sharedPreferences.getString("namaKaryawan", "")
        val idUser = sharedPreferences.getInt("idKaryawan", 0)
        binding.namaKaryawan.text = namaKaryawan
        binding.batal.setOnClickListener {
            dismiss()
        }
        binding.btnDeleteKaryawan.setOnClickListener {
            binding.btnDeleteKaryawan.startAnimation()
            karyawanViewModel.deleteUser(idUser)
            karyawanViewModel.responseCodeDelete.observe(viewLifecycleOwner) {
                if (it == 200) {
                    dismiss()
                    Toast.makeText(context, "Berhasil Hapus User Harap Refresh", Toast.LENGTH_SHORT).show()
                } else {
                    dismiss()
                    Toast.makeText(context, "Gagal Hapus User", Toast.LENGTH_SHORT).show()
                }
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