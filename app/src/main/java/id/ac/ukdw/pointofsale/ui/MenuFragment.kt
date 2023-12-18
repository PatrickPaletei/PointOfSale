package id.ac.ukdw.pointofsale.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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
    ): View? {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)

        getAllMenu()

        return binding.root
    }

    private fun getAllMenu() {
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
                            binding.lottieAnimationView.visibility = View.GONE
                            binding.recyclerViewMenu.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onFailure(call: Call<AllMenuResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
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

        filterViewModel.dataList.observe(viewLifecycleOwner) { selectedFilter ->
            when (selectedFilter) {
                1 -> adapter.submitData(data)
                2 -> adapter.filterByCategory("Makanan")
                3 -> adapter.filterByCategory("Minuman")
                else -> adapter.filterByCategory("Snack")// Default case: show all data if no filter is selected
            }
            binding.recyclerViewMenu.adapter = adapter
        }

    }

}
