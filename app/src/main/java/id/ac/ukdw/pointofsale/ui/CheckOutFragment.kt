package id.ac.ukdw.pointofsale.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.ac.ukdw.pointofsale.MainActivity
import id.ac.ukdw.pointofsale.R
import id.ac.ukdw.pointofsale.adapter.CheckOutAdapter
import id.ac.ukdw.pointofsale.data.CheckOutData
import id.ac.ukdw.pointofsale.viewmodel.EditCheckOutViewModel
import id.ac.ukdw.pointofsale.viewmodel.SharedCheckoutViewModel


class CheckOutFragment : Fragment() {

    private val checkoutViewModel: SharedCheckoutViewModel by activityViewModels()
    private lateinit var adapter: CheckOutAdapter
    private lateinit var editCheckoutViewModel: EditCheckOutViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editCheckoutViewModel = (requireActivity() as MainActivity).getCheckOutItemViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_check_out, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.rcyViewCheckOut)
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

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val totalMenuTxt: TextView = view.findViewById(R.id.totalMenu)
        val btnClear: Button = view.findViewById(R.id.btn_clear)
        val subtotalTxt: TextView = view.findViewById(R.id.subtotal)
        val totalKeseluruhanTxt: TextView = view.findViewById(R.id.totalKeseluruhan)

        checkoutViewModel.dataList.observe(viewLifecycleOwner) { dataList ->
            val totalMenu = "(${dataList.size} menu)"
            val totalPrice = dataList.sumOf { item ->
                // Filter out non-digit characters from harga, convert it to Int or default to 0
                val hargaInt = item.harga.filter { it.isDigit() }.toIntOrNull()

                // Multiply harga (price) with jumlah (quantity) for each item
                hargaInt?.times(item.jumlah) ?: 0
            }
            val subTotal = "IDR $totalPrice"

            totalMenuTxt.text = totalMenu
            subtotalTxt.text = totalPrice.toString()
            totalKeseluruhanTxt.text = subTotal
        }

        btnClear.setOnClickListener {
            checkoutViewModel.clearData()
        }
    }


    private fun incrementItem(position: Int) {
        val currentList = checkoutViewModel.getDataList()?.toMutableList() ?: mutableListOf()
        currentList.getOrNull(position)?.let { item ->
            item.jumlah++
            checkoutViewModel.updateData(currentList)
        }
    }

    private fun decrementItem(position: Int) {
        val currentList = checkoutViewModel.getDataList()?.toMutableList() ?: mutableListOf()
        currentList.getOrNull(position)?.let { item ->
            if (item.jumlah > 0) {
                item.jumlah--
                checkoutViewModel.updateData(currentList)
            }
        }
        checkoutViewModel.removeIfEmpty() // Call removeIfEmpty after updating data
    }
}