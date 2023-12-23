package id.ac.ukdw.pointofsale.ui.penjualan

import android.R
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import id.ac.ukdw.pointofsale.adapter.SummaryAdapter
import id.ac.ukdw.pointofsale.databinding.FragmentPenjualanBinding
import id.ac.ukdw.pointofsale.viewmodel.PenjualanViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class PenjualanFragment : Fragment() {

    private lateinit var binding: FragmentPenjualanBinding
    private val penjualanViewModel: PenjualanViewModel by viewModels()
    private lateinit var rcyViewToday: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPenjualanBinding.inflate(inflater, container, false)
        initViews()
        return binding.root
    }

    private fun getTokenFromPrefs(): String {
        val sharedPreferences = activity?.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        return sharedPreferences?.getString("token", "") ?: ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get current date and time
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formattedDateTime = currentDateTime.format(formatter)

        rcyViewToday = binding.rcyViewToday
        rcyViewToday.layoutManager = LinearLayoutManager(requireContext())

        penjualanViewModel.responseSummaryToday.observe(viewLifecycleOwner) { summaryResponse ->
            if (summaryResponse!!.pendapatan != 0) {
                // Data is available
                binding.holderHariIni.visibility = View.VISIBLE
                binding.noDataFound.visibility = View.GONE
                binding.lottieToday.visibility = View.GONE
                rcyViewToday.visibility = View.VISIBLE
                val adapter = SummaryAdapter(summaryResponse)
                rcyViewToday.adapter = adapter
            }else{
                binding.holderHariIni.visibility = View.GONE
                binding.noDataFound.visibility = View.VISIBLE
            }
        }

        // Assuming you have a button with ID 'exportButton'
        binding.btnDownload.setOnClickListener {
            val token = "Bearer ${getTokenFromPrefs()}"
            val fileName = "pembukuan $formattedDateTime.xlsx"
            penjualanViewModel.export(token, fileName, requireContext())
            penjualanViewModel.responseCode.observe(viewLifecycleOwner) {
                if (it == 200) {
                    Toast.makeText(context, "Berhasil Unduh Pembukuan", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        context,
                        "Gagal Unduh Pembukuan Coba Beberapa Saat Lagi",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun initViews(){
        val spinnerOptions = listOf("Hari Ini", "Kemarin", "Minggu Ini", "Bulan Ini")
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, spinnerOptions)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        val spinner: Spinner = binding.spiner
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                when (position) {
                    0 -> getTodaySummary("today") // Hari Ini
                    1 -> getTodaySummary("yesterday") // Kemarin
                    2 -> getTodaySummary("this-week") // Minggu Ini
                    3 -> getTodaySummary("this-month") // Bulan Ini
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

    }

    private fun getTodaySummary(criteria1: String) {
        penjualanViewModel.getTodaySummary(criteria1)
    }

}