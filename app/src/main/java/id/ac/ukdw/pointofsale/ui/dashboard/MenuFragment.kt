package id.ac.ukdw.pointofsale.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import id.ac.ukdw.pointofsale.MainActivity
import id.ac.ukdw.pointofsale.R
import id.ac.ukdw.pointofsale.adapter.CardAdapterAllMenu
import id.ac.ukdw.pointofsale.adapter.SpaceItemDecoration
import id.ac.ukdw.pointofsale.api.Service.ApiClient
import id.ac.ukdw.pointofsale.api.response.AllMenuResponse
import id.ac.ukdw.pointofsale.api.response.DataSemuaMakanan
import id.ac.ukdw.pointofsale.data.CardData
import id.ac.ukdw.pointofsale.databinding.FragmentMenuBinding
import id.ac.ukdw.pointofsale.viewmodel.SelectedFilterMenuViewModel
import id.ac.ukdw.pointofsale.viewmodel.SelectedItemViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!
    private lateinit var selectedItemViewModel: SelectedItemViewModel
    private val filterViewModel: SelectedFilterMenuViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedItemViewModel = (requireActivity() as MainActivity).getSelectedItemViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        var responseCode: Int?
        lifecycleScope.launch {
            responseCode = getAllMenu()
            responseCode?.let { nonNullableCode ->
                updateViewsVisibility(nonNullableCode)
            }
        }
        return binding.root
    }

    private suspend fun getAllMenu(): Int? {
        return suspendCoroutine { continuation ->
            ApiClient.instance.getAllMenu()
                .enqueue(object : Callback<AllMenuResponse> {
                    override fun onResponse(
                        call: Call<AllMenuResponse>,
                        response: Response<AllMenuResponse>
                    ) {
                        val body = response.body()
                        val code = response.code()
                        if (code == 200) {
                            if (body != null) {
                                showListSemuaMakanan(body.data)
                                continuation.resume(code)
                            }
                        } else {
                            continuation.resume(null)
                        }
                    }

                    override fun onFailure(call: Call<AllMenuResponse>, t: Throwable) {
                        Toast.makeText(context, "Harap Coba Beberapa Saat Lagi Server Sibuk", Toast.LENGTH_SHORT).show()
                    }

                })
        }
    }

    private fun showListSemuaMakanan(data: List<DataSemuaMakanan>) {
        val adapter = CardAdapterAllMenu(object : CardAdapterAllMenu.OnClickListener {
            override fun onClickItem(dataSemuaMakanan: DataSemuaMakanan) {
                val formattedHarga = dataSemuaMakanan.harga.removeSuffix(".00")
                val hargaWithIDR = "IDR $formattedHarga"
                selectedItemViewModel.setSelectedItem(
                    CardData(
                        dataSemuaMakanan.idMenu,
                        dataSemuaMakanan.namaMenu,
                        hargaWithIDR
                    )
                )
            }
        })

        binding.recyclerViewMenu.layoutManager = GridLayoutManager(requireContext(), 3)
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_between_items)
        binding.recyclerViewMenu.addItemDecoration(SpaceItemDecoration(spacingInPixels, 3))

        filterViewModel.combinedFilter.observe(viewLifecycleOwner) { combinedValue ->
            val (intFilter, stringFilter) = combinedValue
            when (intFilter) {
                1 -> adapter.submitData(data)
                2 -> adapter.filterByCategory("Makanan")
                3 -> adapter.filterByCategory("Minuman")
                4 -> adapter.filterByCategory("Snack")
                5 -> {
                    stringFilter?.let { filterString ->
                        val filteredSize = adapter.filterByInput(filterString)
                        if (filteredSize == 0) {
                            binding.recyclerViewMenu.visibility = View.GONE
                            binding.noItemFound.visibility = View.VISIBLE
                        } else {
                            binding.recyclerViewMenu.visibility = View.VISIBLE
                            binding.noItemFound.visibility = View.GONE
                        }
                    }
                }
            }
            binding.recyclerViewMenu.adapter = adapter
            // Perform view updates based on the filtered size
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        filterViewModel.dataState.observe(viewLifecycleOwner){state->
            if (state == true){
                binding.recyclerViewMenu.visibility = View.VISIBLE
                binding.noItemFound.visibility = View.GONE
            }
        }
    }

    private fun updateViewsVisibility(code: Int?) {
        if (code == 200) {
            binding.lottieAnimationView.visibility = View.GONE
            binding.recyclerViewMenu.visibility = View.VISIBLE
            binding.noItemFound.visibility = View.GONE
        }
    }
}
