package id.ac.ukdw.pointofsale.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.ac.ukdw.pointofsale.MainActivity
import id.ac.ukdw.pointofsale.R
import id.ac.ukdw.pointofsale.adapter.CardAdapterAllMenu
import id.ac.ukdw.pointofsale.adapter.SpaceItemDecoration
import id.ac.ukdw.pointofsale.api.Service.ApiClient
import id.ac.ukdw.pointofsale.api.response.AllMenuResponse
import id.ac.ukdw.pointofsale.api.response.DataSemuaMakanan
import id.ac.ukdw.pointofsale.data.CardData
import id.ac.ukdw.pointofsale.database.MenuItem
import id.ac.ukdw.pointofsale.databinding.FragmentMenuBinding
import id.ac.ukdw.pointofsale.viewmodel.MenuViewModel
import id.ac.ukdw.pointofsale.viewmodel.PageMenuViewModel
import id.ac.ukdw.pointofsale.viewmodel.SelectedFilterMenuViewModel
import id.ac.ukdw.pointofsale.viewmodel.SelectedItemViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@AndroidEntryPoint
class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!
    private lateinit var selectedItemViewModel: SelectedItemViewModel
    private val menuViewModel: MenuViewModel by viewModels()
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
        setupRecyclerView()
        observeMenuData()
        menuViewModel.fetchMenuData()
        return binding.root
    }

    private fun observeMenuData() {
        menuViewModel.menuData.observe(viewLifecycleOwner) { menuData ->
            // Handle menuData updates here
            if (menuData.isNotEmpty()) {
                showListSemuaMakanan(mapMenuItemsToDataSemuaMakanan(menuData))
                updateViewsVisibility(200) // Assuming 200 for success
            } else {
                // Handle no data scenario
            }
        }
    }

    private fun mapMenuItemsToDataSemuaMakanan(menuItems: List<MenuItem>): List<DataSemuaMakanan> {
        return menuItems.map { menuItem ->
            DataSemuaMakanan(
                idMenu = menuItem.idMenu,
                namaMenu = menuItem.namaMenu,
                harga = menuItem.harga,
                image = menuItem.image ?: "",
                jumlahStok = menuItem.jumlahStok,
                kategori = menuItem.kategori
                // map other properties accordingly
            )
        }
    }


    private val spacingInPixels by lazy {
        resources.getDimensionPixelSize(R.dimen.spacing_between_items)
    }
    private val spaceItemDecoration by lazy {
        SpaceItemDecoration(spacingInPixels, 3)
    }

    private fun setupRecyclerView() {
        binding.recyclerViewMenu.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerViewMenu.addItemDecoration(spaceItemDecoration)
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

        filterViewModel.combinedFilter.observe(viewLifecycleOwner) { combinedValue ->
            val (intFilter, stringFilter) = combinedValue
            when (intFilter) {
                1 -> adapter.submitData(data)
                2 -> adapter.filterByCategory("Makanan")
                3 -> adapter.filterByCategory("Minuman")
                4 -> adapter.filterByCategory("Snack")
                5 -> adapter.filterByCategory("Lain-Lain")
                6 -> {
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

    override fun onDestroyView() {
        super.onDestroyView()
        filterViewModel.updateData(1,"")
    }
}

