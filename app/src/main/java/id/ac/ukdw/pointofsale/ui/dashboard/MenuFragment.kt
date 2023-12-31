package id.ac.ukdw.pointofsale.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.ac.ukdw.pointofsale.MainActivity
import id.ac.ukdw.pointofsale.R
import id.ac.ukdw.pointofsale.adapter.CardAdapterAllMenu
import id.ac.ukdw.pointofsale.adapter.SpaceItemDecoration
import id.ac.ukdw.pointofsale.api.response.DataSemuaMakanan
import id.ac.ukdw.pointofsale.data.CardData
import id.ac.ukdw.pointofsale.database.MenuItem
import id.ac.ukdw.pointofsale.databinding.FragmentMenuBinding
import id.ac.ukdw.pointofsale.viewmodel.MenuViewModel
import id.ac.ukdw.pointofsale.viewmodel.SelectedFilterMenuViewModel
import id.ac.ukdw.pointofsale.viewmodel.SelectedItemViewModel
import id.ac.ukdw.pointofsale.viewmodel.SharedCheckoutViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!
    private lateinit var selectedItemViewModel: SelectedItemViewModel
    private val menuViewModel: MenuViewModel by viewModels()
    private val filterViewModel: SelectedFilterMenuViewModel by activityViewModels()
    private val checkoutViewModel: SharedCheckoutViewModel by activityViewModels()

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
                //nodata
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
                // You can handle selected items here based on your requirements
            }
        }, checkoutViewModel)

        filterViewModel.combinedFilter.observe(viewLifecycleOwner) { combinedValue ->
            val (intFilter, stringFilter) = combinedValue
            when (intFilter) {
                1 -> {
                    adapter.submitData(data)
                    binding.noMenuInDb.visibility = View.GONE
                }
                2, 3, 4, 5 -> {
                    val category = when (intFilter) {
                        2 -> "Makanan"
                        3 -> "Minuman"
                        4 -> "Snack"
                        else -> "Lain-Lain"
                    }
                    val filteredSize = adapter.filterByCategory(category)
                    updateVisibilityBasedOnFilterResult(filteredSize)
                }

                6 -> {
                    stringFilter?.let { filterString ->
                        val filteredSize = adapter.filterByInput(filterString)
                        if (filteredSize == 0) {
                            binding.recyclerViewMenu.visibility = View.GONE
                            binding.noMenuInDb.visibility = View.GONE
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

    private fun updateVisibilityBasedOnFilterResult(filteredSize: Int) {
        val delayDuration = 100L // Adjust the delay time as needed in milliseconds
        viewLifecycleOwner.lifecycleScope.launch {
            delay(delayDuration)
            if (filteredSize == 0) {
                binding.recyclerViewMenu.visibility = View.GONE
                binding.noMenuInDb.visibility = View.VISIBLE
            } else {
                binding.recyclerViewMenu.visibility = View.VISIBLE
                binding.noMenuInDb.visibility = View.GONE
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        filterViewModel.dataState.observe(viewLifecycleOwner) { state ->
            if (state == true) {
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
        filterViewModel.updateData(1, "")
    }
}

