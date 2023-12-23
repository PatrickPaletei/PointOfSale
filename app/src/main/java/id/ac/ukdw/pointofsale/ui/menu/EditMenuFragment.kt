package id.ac.ukdw.pointofsale.ui.menu

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import id.ac.ukdw.pointofsale.MainActivity
import id.ac.ukdw.pointofsale.R
import id.ac.ukdw.pointofsale.adapter.MenuPageAdapter
import id.ac.ukdw.pointofsale.data.DataEditHelper
import id.ac.ukdw.pointofsale.databinding.FragmentEditMenuBinding
import id.ac.ukdw.pointofsale.viewmodel.MenuViewModel
import id.ac.ukdw.pointofsale.viewmodel.PageMenuViewModel
import id.ac.ukdw.pointofsale.viewmodel.SelectedItemViewModel
import kotlinx.coroutines.delay

@AndroidEntryPoint
class EditMenuFragment : Fragment() {

    private val pageMenuViewModel: PageMenuViewModel by viewModels()
    private lateinit var binding: FragmentEditMenuBinding // Replace with your actual binding class name
    private lateinit var menuPageAdapter: MenuPageAdapter
    private lateinit var selectedItemViewModel: SelectedItemViewModel
    private val menuViewModel: MenuViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedItemViewModel = (requireActivity() as MainActivity).getSelectedItemViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.semuaMakanan.isChecked = true
        setupChipsListeners()
        sharedPreferences = requireActivity().getSharedPreferences("dataEdit", Context.MODE_PRIVATE)
        menuPageAdapter = MenuPageAdapter(
            onItemClick = { judulMenu, kategori, harga, id ->
                val hargaWithoutSuffix = harga.removeSuffix(".00")
                val hargaAsInt =
                    hargaWithoutSuffix.toIntOrNull() ?: 0 // Default to 0 if parsing fails
                val editor = sharedPreferences.edit()
                editor.putString("judulMenu", judulMenu)
                editor.putString("kategori", kategori)
                editor.putInt("harga", hargaAsInt)
                editor.putInt("id", id)
                Log.d("idUntaaa", "onViewCreated: $id")
                editor.apply()

                selectedItemViewModel.setCallPopUpEditMenu(true)
            },
            onItemDeleteClick = { idMenu,namaMenu ->
                val editor = sharedPreferences.edit()
                editor.putString("judulMenuDelete", namaMenu)
                editor.putInt("idMenuDelete",idMenu)
                editor.apply()
                selectedItemViewModel.setCallPopUpDeleteMenu(true)
            }
        )

        binding.swipeRefreshLayout.setOnRefreshListener {
            menuViewModel.fetchMenuData()
            pageMenuViewModel.menuItems.observe(viewLifecycleOwner) { menuItems ->
                menuPageAdapter.submitList(menuItems)
                stopRefreshing()
                unCheckAllChips()
                binding.semuaMakanan.isChecked = true
            }
        }

        binding.rcyView.apply {
            adapter = menuPageAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        pageMenuViewModel.menuItems.observe(viewLifecycleOwner) { menuItems ->
            menuPageAdapter.submitList(menuItems)
        }

        binding.btnTambahTransaksi.setOnClickListener {
            selectedItemViewModel.setCallPopUpTambahMenu(true)

        }

    }


    private fun stopRefreshing() {
        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun setupChipsListeners() {
        binding.semuaMakanan.setOnClickListener {
            observeAndCheckChip(binding.semuaMakanan) {
                pageMenuViewModel.menuItems.observe(viewLifecycleOwner) { allData ->
                    allData?.let {
                        menuPageAdapter.submitList(it)
                    }
                }
            }
            updateUIVisibility(View.VISIBLE, View.GONE)
        }

        binding.makanan.setOnClickListener {
            filterAndCheckChip(
                binding.makanan,
                getString(R.string.makanan)
            )
        }
        binding.minuman.setOnClickListener {
            filterAndCheckChip(
                binding.minuman,
                getString(R.string.minuman)
            )
        }
        binding.snack.setOnClickListener {
            filterAndCheckChip(
                binding.snack,
                getString(R.string.snack)
            )
        }
        binding.lain.setOnClickListener {
            filterAndCheckChip(
                binding.lain,
                getString(R.string.lain_lain)
            )
        }

        binding.search.setOnClickListener {
            val query = binding.querySearch.text.toString().trim()
            val filteredSize = menuPageAdapter.filterByInput(query)
            unCheckAllChips()
            updateUIVisibility(
                if (filteredSize == 0) View.GONE else View.VISIBLE,
                if (filteredSize == 0) View.VISIBLE else View.GONE
            )

        }
    }

    private fun observeAndCheckChip(chip: Chip, action: () -> Unit) {
        unCheckAllChips()
        chip.isChecked = true
        action.invoke()
    }

    private fun filterAndCheckChip(chip: Chip, category: String) {
        unCheckAllChips()
        chip.isChecked = true
        val filterResult = menuPageAdapter.filterByCategory(category)
        updateUIVisibilityNoDataDb(
            if (filterResult == 0) View.GONE else View.VISIBLE, // Update RecyclerView visibility
            if (filterResult == 0) View.VISIBLE else View.GONE // Update No Item Found TextView visibility
        )
    }

    private fun unCheckAllChips() {
        binding.semuaMakanan.isChecked = false
        binding.makanan.isChecked = false
        binding.minuman.isChecked = false
        binding.snack.isChecked = false
        binding.lain.isChecked = false
    }

    private fun updateUIVisibility(rcyViewVisibility: Int, noItemFoundVisibility: Int) {
        binding.swipeRefreshLayout.visibility = rcyViewVisibility
        binding.noItemFound.visibility = noItemFoundVisibility
    }

    private fun updateUIVisibilityNoDataDb(rcyViewVisibility: Int, noItemFoundVisibility: Int) {
        binding.swipeRefreshLayout.visibility = rcyViewVisibility
        binding.noDataFoundInDb.visibility = noItemFoundVisibility
    }
}
