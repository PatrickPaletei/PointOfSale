package id.ac.ukdw.pointofsale.ui.dashboard

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
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import id.ac.ukdw.pointofsale.MainActivity
import id.ac.ukdw.pointofsale.R
import id.ac.ukdw.pointofsale.data.CheckOutData
import id.ac.ukdw.pointofsale.viewmodel.SelectedItemViewModel
import id.ac.ukdw.pointofsale.viewmodel.SharedCheckoutViewModel


class PopUpFragment : DialogFragment() {

    private lateinit var selectedItemViewModel: SelectedItemViewModel
    private val checkoutViewModel: SharedCheckoutViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pop_up, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedItemViewModel = (requireActivity() as MainActivity).getSelectedItemViewModel()
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

        val batalButon: Button = view.findViewById(R.id.batal)
        val pilihButon: Button = view.findViewById(R.id.btn_lanjut_checkOut)
        val namaMenu: TextView = view.findViewById(R.id.namaMenu)
        val hargaMenu: TextView = view.findViewById(R.id.hargaMenu)
        val catatanMenu: EditText = view.findViewById(R.id.editCatatan)
        val tambahPesan: ImageButton = view.findViewById(R.id.tambahPesan)
        val kurangPesan: ImageButton = view.findViewById(R.id.kurangPesan)
        val banyakPesanan: TextView = view.findViewById(R.id.banyakPesanan)


        var total: Int = DEFAULT_TOTAL
        val dataList: MutableList<CheckOutData> =
            checkoutViewModel.dataList.value?.toMutableList() ?: mutableListOf()


        // Observing changes in selected item
        selectedItemViewModel.selectedItem.observe(viewLifecycleOwner) { selectedItem ->
            namaMenu.text = selectedItem.namaMenu
            hargaMenu.text = selectedItem.hargaMenu
        }

        // Increment button logic
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

        batalButon.setOnClickListener {
            dismiss()
        }
        pilihButon.setOnClickListener {
            selectedItemViewModel.selectedItem.value?.let { selectedItem ->
                val catatan = catatanMenu.text.toString()
                val updatedItem =
                    CheckOutData(
                        selectedItem.idMenu,
                        selectedItem.namaMenu,
                        selectedItem.hargaMenu,
                        total,
                        catatan)

                dataList.add(updatedItem)
                checkoutViewModel.updateData(dataList)
            }

            dismiss()
        }
    }

    companion object {
        private const val DEFAULT_TOTAL = 1
    }
}