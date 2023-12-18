package id.ac.ukdw.pointofsale.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import id.ac.ukdw.pointofsale.MainActivity
import id.ac.ukdw.pointofsale.adapter.CheckOutAdapter
import id.ac.ukdw.pointofsale.databinding.FragmentCheckOutBinding
import id.ac.ukdw.pointofsale.viewmodel.EditCheckOutViewModel
import id.ac.ukdw.pointofsale.viewmodel.SelectedItemViewModel
import id.ac.ukdw.pointofsale.viewmodel.SharedCheckoutViewModel

class CheckOutFragment : Fragment() {

    private var _binding:FragmentCheckOutBinding? = null
    private val binding get() = _binding!!

    private val checkoutViewModel: SharedCheckoutViewModel by activityViewModels()
    private lateinit var adapter: CheckOutAdapter
    private lateinit var editCheckoutViewModel: EditCheckOutViewModel
    private lateinit var selectedItemViewModel: SelectedItemViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editCheckoutViewModel = (requireActivity() as MainActivity).getCheckOutItemViewModel()
        selectedItemViewModel = (requireActivity() as MainActivity).getSelectedItemViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCheckOutBinding.inflate(inflater, container, false)

        val recyclerView = binding.rcyViewCheckOut
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = CheckOutAdapter(
            incrementClickListener = { position ->
                incrementItem(position)
            },
            decrementClickListener = { position ->
                decrementItem(position)
            },
            ubahSemuaClickListener = { dataList ->
                editCheckoutViewModel.setSelectedItem(dataList)
                editCheckoutViewModel.setPopUpFlag()
            }
        )

        recyclerView.adapter = adapter

        checkoutViewModel.dataList.observe(viewLifecycleOwner) { newDataList ->
            adapter.updateData(newDataList)
        }



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val totalMenuTxt = binding.totalMenu
        val btnClear = binding.btnClear
        val subtotalTxt = binding.subtotal
        val totalKeseluruhanTxt = binding.totalKeseluruhan
        val containerRcy = binding.hide1
        val hide2 = binding.hide2
        val containerSub = binding.subTotalHide
        val containerNoItem = binding.containerNoItem
        val checkOutBtn = binding.btnCheckOut
        val namaPelanggan = binding.namaPelanggan
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)

        checkoutViewModel.dataList.observe(viewLifecycleOwner) { dataList ->
            val totalMenu = "(${dataList.size} menu)"
            val totalPrice = dataList.sumOf { item ->
                // Filter out non-digit characters from harga, convert it to Int or default to 0
                val hargaInt = item.harga.filter { it.isDigit() }.toIntOrNull()

                // Multiply harga (price) with jumlah (quantity) for each item
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

            if (dataList.isEmpty()){
                containerRcy.visibility = View.GONE
                hide2.visibility = View.GONE
                containerSub.visibility = View.GONE
                checkOutBtn.visibility = View.GONE
                containerNoItem.visibility =View.VISIBLE
            }else{
                containerNoItem.visibility =View.GONE
                containerRcy.visibility = View.VISIBLE
                hide2.visibility = View.VISIBLE
                checkOutBtn.visibility = View.VISIBLE
                containerSub.visibility = View.VISIBLE
            }
        }



        checkOutBtn.setOnClickListener {
            selectedItemViewModel.setcallPopUp(true)
            val namaCust = namaPelanggan.text.toString()
            with(sharedPref.edit()) {
                putString("namaPelanggan", namaCust)
                apply()
            }

        }

        btnClear.setOnClickListener {
            checkoutViewModel.clearData()
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
}