package id.ac.ukdw.pointofsale.ui.karyawan

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skydoves.androidveil.VeilRecyclerFrameView
import dagger.hilt.android.AndroidEntryPoint
import id.ac.ukdw.pointofsale.MainActivity
import id.ac.ukdw.pointofsale.R
import id.ac.ukdw.pointofsale.adapter.KaryawanAdapter
import id.ac.ukdw.pointofsale.databinding.FragmentEditMenuBinding
import id.ac.ukdw.pointofsale.databinding.FragmentKaryawanBinding
import id.ac.ukdw.pointofsale.viewmodel.KarywawanViewModel
import id.ac.ukdw.pointofsale.viewmodel.SelectedItemViewModel

@AndroidEntryPoint
class KaryawanFragment : Fragment() {

    private val karyawanViewModel:KarywawanViewModel by viewModels()
    private lateinit var selectedItemViewModel: SelectedItemViewModel
    private lateinit var binding:FragmentKaryawanBinding
    private lateinit var veil:VeilRecyclerFrameView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedItemViewModel = (requireActivity() as MainActivity).getSelectedItemViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentKaryawanBinding.inflate(inflater, container, false)
        karyawanViewModel.getAllUser()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeUserResponse()
        tambahPopUpListener()

        binding.swipeRefreshLayout.setOnRefreshListener {
            veil.veil()
            karyawanViewModel.getAllUser()
            observeUserResponse()
            stopRefreshing()
            veil.unVeil()
        }
    }

    private fun tambahPopUpListener(){
        binding.btnRegis.setOnClickListener {
            selectedItemViewModel.setCallPopUpRegisUser(true)
        }
    }

    private fun setupRecyclerView() {
        veil = binding.rcyViewKaryawan
        veil.setLayoutManager(LinearLayoutManager(requireContext()))
        veil.addVeiledItems(6)
    }
    private fun observeUserResponse() {
        sharedPreferences = requireActivity().getSharedPreferences("dataEdit", Context.MODE_PRIVATE)
        karyawanViewModel.responseUser.observe(viewLifecycleOwner) { response ->
            response?.let {
                if (it.statusCode == 200) {
                    Log.d("responseKarywan", "onViewCreated: $it")
                    val adapter = KaryawanAdapter(it) { idUser,Nama ->
                        val editor = sharedPreferences.edit()
                        editor.putString("namaKaryawan",Nama)
                        editor.putInt("idKaryawan",idUser)
                        editor.apply()
                        selectedItemViewModel.setCallPopUpDeleteUser(true)
                    }
                    veil.setAdapter(adapter)
                    veil.unVeil()
                }
            }
        }
    }

    private fun stopRefreshing() {
        binding.swipeRefreshLayout.isRefreshing = false
    }
}