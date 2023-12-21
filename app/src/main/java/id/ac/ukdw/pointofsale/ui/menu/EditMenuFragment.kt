package id.ac.ukdw.pointofsale.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import id.ac.ukdw.pointofsale.R
import id.ac.ukdw.pointofsale.adapter.MenuPageAdapter
import id.ac.ukdw.pointofsale.databinding.FragmentEditMenuBinding
import id.ac.ukdw.pointofsale.viewmodel.PageMenuViewModel

@AndroidEntryPoint
class EditMenuFragment : Fragment() {

    private val viewModel: PageMenuViewModel by viewModels()
    private lateinit var binding: FragmentEditMenuBinding // Replace with your actual binding class name
    private lateinit var menuPageAdapter: MenuPageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditMenuBinding.inflate(inflater, container, false)
        // Fetch menu items when the fragment is created or as needed
//        lifecycleScope.launch {
//            viewModel.getMenuItemsFromDB()
//        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.semuaMakanan.isChecked = true
        setupChipsListeners()
        menuPageAdapter = MenuPageAdapter { menuItem ->
            // Handle item click here
            // For example, navigate to a detail screen with the selected menu item
            // Or perform any other action based on the clicked item

        }

        binding.rcyView.apply {
            adapter = menuPageAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.menuItems.observe(viewLifecycleOwner) { menuItems ->
            menuPageAdapter.submitList(menuItems)
        }


    }

    private fun setupChipsListeners() {
        binding.semuaMakanan.setOnClickListener {
            observeAndCheckChip(binding.semuaMakanan) {
                viewModel.menuItems.observe(viewLifecycleOwner) { allData ->
                    allData?.let {
                        menuPageAdapter.submitList(it)
                    }
                }
            }
            updateUIVisibility(View.VISIBLE, View.GONE)
        }

        binding.makanan.setOnClickListener { filterAndCheckChip(binding.makanan, getString(R.string.makanan)) }
        binding.minuman.setOnClickListener { filterAndCheckChip(binding.minuman, getString(R.string.minuman)) }
        binding.snack.setOnClickListener { filterAndCheckChip(binding.snack, getString(R.string.snack)) }

        binding.search.setOnClickListener {
            val query = binding.querySearch.text.toString().trim()
            val filteredSize = menuPageAdapter.filterByInput(query)
            unCheckAllChips()
            updateUIVisibility(if (filteredSize == 0) View.GONE else View.VISIBLE, if (filteredSize == 0) View.VISIBLE else View.GONE)
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
        menuPageAdapter.filterByCategory(category)
    }

    private fun unCheckAllChips() {
        binding.semuaMakanan.isChecked = false
        binding.makanan.isChecked = false
        binding.minuman.isChecked = false
        binding.snack.isChecked = false
    }

    private fun updateUIVisibility(rcyViewVisibility: Int, noItemFoundVisibility: Int) {
        binding.rcyView.visibility = rcyViewVisibility
        binding.noItemFound.visibility = noItemFoundVisibility
    }
}
