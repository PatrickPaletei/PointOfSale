package id.ac.ukdw.pointofsale.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import id.ac.ukdw.pointofsale.MainActivity
import id.ac.ukdw.pointofsale.R
import id.ac.ukdw.pointofsale.api.Service.ApiClient
import id.ac.ukdw.pointofsale.api.request.ItemTransaksi
import id.ac.ukdw.pointofsale.api.request.TambahTransaksiRequest
import id.ac.ukdw.pointofsale.api.response.TransaksiResponse
import id.ac.ukdw.pointofsale.databinding.FragmentPopUpPembayaranBinding
import id.ac.ukdw.pointofsale.viewmodel.SelectedItemViewModel
import id.ac.ukdw.pointofsale.viewmodel.SharedCheckoutViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class PopUpPembayaranFragment : DialogFragment() {


    private var _binding: FragmentPopUpPembayaranBinding? = null
    private val binding get() = _binding!!
    private val checkoutViewModel: SharedCheckoutViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPopUpPembayaranBinding.inflate(inflater, container, false)
        return binding.root
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


        binding.batal.setOnClickListener {
            dismiss()
        }
        binding.btnLanjutCheckOut.setOnClickListener {
            val itemCheckOut = checkoutViewModel.getDataList()
            val itemTransaksi =
                itemCheckOut?.map { ItemTransaksi(it.id_menu, it.jumlah_pesanan, it.catatan) }

            val checkedRadioButtonId = radioGroup.checkedRadioButtonId
            val radioButton = view.findViewById<RadioButton>(checkedRadioButtonId)?.text.toString()
            binding.holderTitle.visibility = View.GONE
            binding.materialDivider.visibility = View.GONE
            binding.holderPembayaran.visibility = View.GONE
            binding.materialDivider2.visibility = View.GONE
            binding.holderBtn.visibility = View.GONE
            binding.totalBayarCheckOut.visibility = View.GONE
            binding.pembayaranDiproses.visibility = View.VISIBLE
            lifecycleScope.launch {
                if (id_user != null && !itemTransaksi.isNullOrEmpty()) {
                    val responseCode = nama_pelanggan?.let {
                        tambahTransaksi(
                            id_user.toInt(), itemTransaksi, radioButton, it
                        )
                    }
                    if (responseCode == 201) {
                        binding.holderTitle.visibility = View.GONE
                        binding.materialDivider.visibility = View.GONE
                        binding.holderPembayaran.visibility = View.GONE
                        binding.materialDivider2.visibility = View.GONE
                        binding.holderBtn.visibility = View.GONE
                        binding.totalBayarCheckOut.visibility = View.GONE
                        binding.pembayaranDiproses.visibility = View.GONE
                        binding.pembayaranBerhasil.visibility = View.VISIBLE
                        delay(3000)
                        dismiss()
                    } else {
                        Log.d("statusTransaksi", "gagal $responseCode")
                    }
                }
            }
        }
    }

    private suspend fun tambahTransaksi(
        id_user: Int,
        items: List<ItemTransaksi>,
        metode_pembayaran: String,
        nama_pelanggan: String
    ): Int? {
        return suspendCoroutine { continuation ->
            val request = TambahTransaksiRequest(
                idUser = id_user,
                items = items,
                metodePembayaran = metode_pembayaran,
                namaPelanggan = nama_pelanggan
            )

            ApiClient.instance.tambahTransaksi(request)
                .enqueue(object : Callback<TransaksiResponse> {
                    override fun onResponse(
                        call: Call<TransaksiResponse>,
                        response: Response<TransaksiResponse>
                    ) {
                        val code = response.code()
                        val body =response.body()
                        if (code == 201) {
                            val sharedPrefIdNota = requireActivity().getPreferences(Context.MODE_PRIVATE)
                            with(sharedPrefIdNota.edit()) {
                                putString("id_nota", body?.data?.idTransaksi.toString())
                                apply()
                            }
                            Log.d("Transaksi", "onResponse: $response ")
                            continuation.resume(code)
                        } else {
                            continuation.resume(null)
                        }
                    }

                    override fun onFailure(call: Call<TransaksiResponse>, t: Throwable) {
                        Log.d("Transaksi", "onResponse: $t ")
                        continuation.resume(null)
                    }

                })
        }
    }
}