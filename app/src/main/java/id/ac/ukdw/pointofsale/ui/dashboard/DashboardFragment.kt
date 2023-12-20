package id.ac.ukdw.pointofsale.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import id.ac.ukdw.pointofsale.R
import id.ac.ukdw.pointofsale.databinding.FragmentDashboardBinding
import id.ac.ukdw.pointofsale.viewmodel.SelectedFilterMenuViewModel

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val filterViewModel: SelectedFilterMenuViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        binding.nama.text = getUserNameFromPrefs()

        setChipClickListener(binding.semuaMakanan, 1)
        setChipClickListener(binding.makanan, 2)
        setChipClickListener(binding.minuman, 3)
        setChipClickListener(binding.snack, 4)

        setSearchButtonClickListener()

        replaceFragment(savedInstanceState, R.id.menuContainer, MenuFragment())
        replaceFragment(
            savedInstanceState,
            R.id.menuCheckout,
            CheckOutFragment(),
            resources.getDimension(R.dimen.your_elevation_value)
        )

        return binding.root
    }

    private fun selectChip(chip: Chip) {
        // Reset all chips to default state
        binding.semuaMakanan.isChecked = false
        binding.makanan.isChecked = false
        binding.minuman.isChecked = false
        binding.snack.isChecked = false

        // Set only the clicked chip to selected state
        chip.isChecked = true
        // Update the appearance of the chip here, change background color or other visual indicators
    }

    private fun getUserNameFromPrefs(): String {
        val sharedPreferences = activity?.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        return sharedPreferences?.getString("nama", "defaultValue") ?: "defaultValue"
    }

    private fun setChipClickListener(chip: Chip, id: Int, searchText: String = "") {
        chip.setOnClickListener {
            selectChip(chip)
            filterViewModel.updateData(id, searchText)
            filterViewModel.updateUi(true)
            binding.txtSearch.setText("")
        }
    }

    private fun setSearchButtonClickListener() {
        binding.btnSearch.setOnClickListener {
            val searchString = binding.txtSearch.text.toString()
            if (searchString.isNotEmpty()) {
                filterViewModel.updateData(5, searchString)
                binding.semuaMakanan.isChecked = false
            }
        }
    }

    private fun replaceFragment(
        savedInstanceState: Bundle?,
        containerId: Int,
        fragment: Fragment,
        elevation: Float? = null
    ) {
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .commit()

            elevation?.let {
                val fragmentContainer = fragment.view?.findViewById<FrameLayout>(containerId)
                fragmentContainer?.elevation = resources.getDimension(R.dimen.your_elevation_value)
            }
        }
    }


}