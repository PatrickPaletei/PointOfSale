package id.ac.ukdw.pointofsale.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import id.ac.ukdw.pointofsale.MainActivity
import id.ac.ukdw.pointofsale.adapter.CheckOutAdapter
import id.ac.ukdw.pointofsale.data.CheckOutData
import id.ac.ukdw.pointofsale.databinding.FragmentCheckOutBinding
import id.ac.ukdw.pointofsale.viewmodel.SharedCheckoutViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CheckOutFragment : Fragment() {

    private lateinit var binding: FragmentCheckOutBinding
    private val checkoutViewModel: SharedCheckoutViewModel by activityViewModels()
    private val editCheckoutViewModel by lazy { (requireActivity() as MainActivity).getCheckOutItemViewModel() }
    private val selectedItemViewModel by lazy { (requireActivity() as MainActivity).getSelectedItemViewModel() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCheckOutBinding.inflate(inflater, container, false)
        setupRecyclerView()
        setupObservers()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCurrentTime()
        setupClickListener()
    }

    private fun setupRecyclerView() {
        binding.rcyViewCheckOut.layoutManager = LinearLayoutManager(requireContext())
        val adapter = CheckOutAdapter(
            incrementClickListener = { position -> incrementItem(position) },
            decrementClickListener = { position -> decrementItem(position) },
            ubahSemuaClickListener = { dataList ->
                editCheckoutViewModel.setSelectedItem(dataList)
                editCheckoutViewModel.setPopUpFlag()
            }
        )
        binding.rcyViewCheckOut.adapter = adapter
    }

    private fun setupObservers() {
        checkoutViewModel.dataList.observe(viewLifecycleOwner) { newDataList ->
            updateUI(newDataList)
            (binding.rcyViewCheckOut.adapter as? CheckOutAdapter)?.updateData(newDataList)
        }
    }

    private fun getCurrentTime(){
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val current = LocalDateTime.now().format(formatter)
        binding.tanggal.text = current
    }

    private fun setupClickListener() {
        binding.btnCheckOut.setOnClickListener { handleCheckoutButton() }

        binding.btnClear.setOnClickListener {
            checkoutViewModel.clearData()
            binding.namaPelanggan.setText("")
        }

        checkoutViewModel.dataList.observe(viewLifecycleOwner) { dataList ->
            if (dataList.isNotEmpty()){
                updateUI(dataList)
            }else{
                //clear after cetak nota
                binding.namaPelanggan.setText("")
            }
        }
    }

    private fun handleCheckoutButton() {
        val namaCust = binding.namaPelanggan.text.toString()
        if (namaCust.isNotEmpty()) {
            selectedItemViewModel.setcallPopUp(true)
            with(requireActivity().getPreferences(Context.MODE_PRIVATE).edit()) {
                putString("namaPelanggan", namaCust)
                apply()
            }
        } else {
            Toast.makeText(context, "Harap Isi Nama Pelanggan", Toast.LENGTH_LONG).show()
        }
    }


    private fun incrementItem(position: Int) {
        val currentList = checkoutViewModel.getDataList()?.toMutableList() ?: mutableListOf()
        currentList.getOrNull(position)?.let { item ->
            item.jumlah_pesanan++
            checkoutViewModel.updateData(currentList)
        }
    }

    private fun decrementItem(position: Int) {
        val currentList = checkoutViewModel.getDataList()?.toMutableList() ?: mutableListOf()
        currentList.getOrNull(position)?.let { item ->
            if (item.jumlah_pesanan > 0) {
                item.jumlah_pesanan--
                checkoutViewModel.updateData(currentList)
            }
        }
        checkoutViewModel.removeIfEmpty() // Call removeIfEmpty after updating data
    }

    private fun updateUI(dataList: List<CheckOutData>) {
        val totalMenuTxt = binding.totalMenu
        val subtotalTxt = binding.subtotal
        val totalKeseluruhanTxt = binding.totalKeseluruhan
        val containerRcy = binding.hide1
        val hide2 = binding.hide2
        val containerSub = binding.subTotalHide
        val containerNoItem = binding.containerNoItem
        val checkOutBtn = binding.btnCheckOut
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)

        val totalMenu = "(${dataList.size} menu)"
        val totalPrice = dataList.sumOf { item ->
            val hargaInt = item.harga.filter { it.isDigit() }.toIntOrNull()
            hargaInt?.times(item.jumlah_pesanan) ?: 0
        }
        val subTotal = "IDR $totalPrice"

        with(sharedPref.edit()) {
            putString("subtotalKey", subTotal)
            apply()
        }

        totalMenuTxt.text = totalMenu
        subtotalTxt.text = totalPrice.toString()
        totalKeseluruhanTxt.text = subTotal

        if (dataList.isEmpty()) {
            containerRcy.visibility = View.GONE
            hide2.visibility = View.GONE
            containerSub.visibility = View.GONE
            checkOutBtn.visibility = View.GONE
            containerNoItem.visibility = View.VISIBLE
        } else {
            containerNoItem.visibility = View.GONE
            containerRcy.visibility = View.VISIBLE
            hide2.visibility = View.VISIBLE
            checkOutBtn.visibility = View.VISIBLE
            containerSub.visibility = View.VISIBLE
        }
    }

}