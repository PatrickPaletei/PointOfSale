package id.ac.ukdw.pointofsale.ui

import android.app.Dialog
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import id.ac.ukdw.pointofsale.MainActivity
import id.ac.ukdw.pointofsale.R
import id.ac.ukdw.pointofsale.viewmodel.EditCheckOutViewModel
import id.ac.ukdw.pointofsale.viewmodel.SharedCheckoutViewModel


class EditCheckOutFragment : DialogFragment() {

    private lateinit var editCheckOutViewModel: EditCheckOutViewModel
    private val checkoutViewModel: SharedCheckoutViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_check_out, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editCheckOutViewModel = (requireActivity() as MainActivity).getCheckOutItemViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val batalButon: Button = view.findViewById(R.id.batal)
        val pilihButon: Button = view.findViewById(R.id.pilih)
        val namaMenu: TextView = view.findViewById(R.id.namaMenu)
        val hargaMenu: TextView = view.findViewById(R.id.hargaMenu)
        val catatanMenu: EditText = view.findViewById(R.id.editCatatan)
        val tambahPesan: ImageButton = view.findViewById(R.id.tambahPesan)
        val kurangPesan: ImageButton = view.findViewById(R.id.kurangPesan)
        val banyakPesanan: TextView = view.findViewById(R.id.banyakPesanan)
        var total =1
        tambahPesan.setOnClickListener {
            total++
            banyakPesanan.text = total.toString()
        }

        // Decrement button logic
        kurangPesan.setOnClickListener {
            if (total > 1) {
                total--
                banyakPesanan.text = total.toString()
            }
        }

        editCheckOutViewModel.checkOutData.observe(viewLifecycleOwner){checkOutData ->
            namaMenu.text = checkOutData.judulMenu
            hargaMenu.text = checkOutData.harga
            val notes = checkOutData.notes
            catatanMenu.setText(notes)
            banyakPesanan.text = checkOutData.jumlah.toString()
        }

        batalButon.setOnClickListener {
            dismiss()
        }

        pilihButon.setOnClickListener {
            val updatedNotes = catatanMenu.text.toString()
            val updatedQuantity = banyakPesanan.text.toString().toInt() // Assuming this is the updated quantity

            val currentCheckOutData = editCheckOutViewModel.checkOutData.value

            currentCheckOutData?.let { checkOutData ->
                checkOutData.notes = updatedNotes
                checkOutData.jumlah = updatedQuantity

                editCheckOutViewModel.updateCheckOutData(checkOutData)

                // Retrieve the current list from the checkOutViewModel
                val checkOutDataList = checkoutViewModel.getDataList()?.toMutableList() ?: mutableListOf()

                // Find the index of the item to update using judulMenu as identifier
                val indexOfItemToUpdate = checkOutDataList.indexOfFirst { it.judulMenu == checkOutData.judulMenu }

                if (indexOfItemToUpdate != -1) {
                    checkOutDataList[indexOfItemToUpdate] = checkOutData // Update the item in the list
                    checkoutViewModel.updateData(checkOutDataList) // Update the data in checkOutViewModel
                }
            }

            dismiss()
        }
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
}