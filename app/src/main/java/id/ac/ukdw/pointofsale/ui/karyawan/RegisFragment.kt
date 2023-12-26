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
import id.ac.ukdw.pointofsale.databinding.FragmentRegisBinding
import id.ac.ukdw.pointofsale.viewmodel.KarywawanViewModel

@AndroidEntryPoint
class RegisFragment : DialogFragment() {

    private val karyawanViewModel: KarywawanViewModel by viewModels()
    private lateinit var binding: FragmentRegisBinding
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
        binding.btnRegis.setOnClickListener {
            val username = binding.username.text.toString().trim()
            val nama = binding.namaKaryawan.text.toString().trim()
            val pass = binding.password.text.toString()
            val konfirmPass = binding.konfirmasiPassword.text.toString()

            if (pass == konfirmPass && pass.length >= 6 && nama.isNotEmpty() && username.isNotEmpty()) {
                val token = "Bearer ${getTokenFromPrefs()}"
                binding.btnRegis.startAnimation()
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
            if (responseCode == 200) {
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


    private fun regisUser(token: String, nama: String, password: String, username: String) {
        karyawanViewModel.regisUser(
            token,
            namaKaryawan = nama,
            password = password, username = username
        )
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