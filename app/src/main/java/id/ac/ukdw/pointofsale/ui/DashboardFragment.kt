package id.ac.ukdw.pointofsale.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import id.ac.ukdw.pointofsale.R
import id.ac.ukdw.pointofsale.databinding.FragmentDashboardBinding
import id.ac.ukdw.pointofsale.viewmodel.SelectedFilterMenuViewModel

class DashboardFragment : Fragment() {

    private var _binding:FragmentDashboardBinding? =null
    private val binding get()= _binding!!
    private val filterViewModel:SelectedFilterMenuViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)


        val sharedPreferences = activity?.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        val nama = sharedPreferences?.getString("nama", "defaultValue")
        binding.nama.text = nama

        binding.semuaMakanan.isChecked = true

        binding.semuaMakanan.setOnClickListener {
            selectChip(binding.semuaMakanan)
            filterViewModel.updateData(1)
        }

        binding.makanan.setOnClickListener {
            selectChip(binding.makanan)
            filterViewModel.updateData(2)
        }

        binding.minuman.setOnClickListener {
            selectChip(binding.minuman)
            filterViewModel.updateData(3)
        }

        binding.snack.setOnClickListener {
            selectChip(binding.snack)
            filterViewModel.updateData(4)
        }



        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.menuContainer, MenuFragment())
                .commit()
        }

        if (savedInstanceState == null) {
            val checkoutFragment = CheckOutFragment()
            childFragmentManager.beginTransaction()
                .replace(R.id.menuCheckout, checkoutFragment)
                .commit()

            // Set elevation after committing the transaction
            val fragmentContainer = checkoutFragment.view?.findViewById<FrameLayout>(R.id.menuCheckout)
            fragmentContainer?.elevation = resources.getDimension(R.dimen.your_elevation_value)
        }

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

}