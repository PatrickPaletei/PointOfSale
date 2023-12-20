package id.ac.ukdw.pointofsale.ui.dashboard

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import id.ac.ukdw.pointofsale.MainActivity
import id.ac.ukdw.pointofsale.R
import id.ac.ukdw.pointofsale.api.request.ItemTransaksi
import id.ac.ukdw.pointofsale.databinding.FragmentPopUpPembayaranBinding
import id.ac.ukdw.pointofsale.viewmodel.NotaViewModel
import id.ac.ukdw.pointofsale.viewmodel.SelectedItemViewModel
import id.ac.ukdw.pointofsale.viewmodel.SharedCheckoutViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PopUpPembayaranFragment : DialogFragment() {


    private var _binding: FragmentPopUpPembayaranBinding? = null
    private val binding get() = _binding!!
    private val checkoutViewModel: SharedCheckoutViewModel by activityViewModels()
    private lateinit var selectedItemViewModel: SelectedItemViewModel
    private lateinit var notaViewModel: NotaViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPopUpPembayaranBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedItemViewModel = (requireActivity() as MainActivity).getSelectedItemViewModel()
    }

    override fun onStart() {
        super.onStart()

        val widthInDp = 480// Replace with your desired width in dp
        val heightInDp = 480 // Replace with your desired height in dp

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

        val radioGroup = binding.radios
        val totalHarga = binding.totalBayarCheckOut
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val subtotalValue = sharedPref.getString("subtotalKey", "")
        //sf id user
        val sharedPreferences = activity?.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        val id_user = sharedPreferences?.getString("id", "")
        totalHarga.text = subtotalValue
        val nama_pelanggan = sharedPref.getString("namaPelanggan", "")
        notaViewModel = ViewModelProvider(this)[NotaViewModel::class.java]

        binding.batal.setOnClickListener {
            dismiss()
        }

        binding.btnLanjutCheckOut.setOnClickListener {
            val itemCheckOut = checkoutViewModel.getDataList()
            val itemTransaksi =
                itemCheckOut?.map { ItemTransaksi(it.id_menu, it.jumlah_pesanan, it.catatan) }

            val checkedRadioButtonId = radioGroup.checkedRadioButtonId
            val radioButton = view.findViewById<RadioButton>(checkedRadioButtonId)?.text.toString()
            if (checkedRadioButtonId != -1) {
                // Call the API using the ViewModel function
                lifecycleScope.launch {
                    binding.btnLanjutCheckOut.startAnimation()
                    notaViewModel.tambahTransaksi(
                        id_user?.toInt() ?: 0, // Convert id_user to Int or provide a default value
                        itemTransaksi ?: emptyList(),
                        radioButton,
                        nama_pelanggan ?: ""
                    )
                    delay(2000)
                    selectedItemViewModel.setCallPopUpNota(true)
                    dismiss()
                }
            } else {
                Toast.makeText(context, "Harap Isi Metode Pembayaran", Toast.LENGTH_SHORT).show()
            }

            notaViewModel.responseBody.observe(viewLifecycleOwner) { id ->
                id?.let {
                    with(sharedPref.edit()) {
                        putInt("idNota", it)
                        apply()
                    }
                }
            }
        }
    }
}