package id.ac.ukdw.pointofsale.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import id.ac.ukdw.pointofsale.R
import id.ac.ukdw.pointofsale.databinding.FragmentCetakNotaBinding


class CetakNotaFragment : DialogFragment() {

    private var _binding:FragmentCetakNotaBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding= FragmentCetakNotaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val widthInDp = 480// Replace with your desired width in dp
        val heightInDp = 480 // Replace with your desired height in dp

        val widthInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            widthInDp.toFloat(),
            resources.displayMetrics
        ).toInt()

        val heightInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            heightInDp.toFloat(),
            resources.displayMetrics
        ).toInt()

        dialog?.window?.setLayout(widthInPixels, heightInPixels)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_dialog)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPrefIdNota = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val cetakNota = binding.btnCetakNota
        val dashBoard = binding.balikDashboard
        val idNota = sharedPrefIdNota.getString("id_nota","")
        Log.d("idNota", "onViewCreated: $idNota")
    }
}