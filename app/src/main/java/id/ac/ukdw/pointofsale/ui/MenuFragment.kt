package id.ac.ukdw.pointofsale.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.ac.ukdw.pointofsale.MainActivity
import id.ac.ukdw.pointofsale.R
import id.ac.ukdw.pointofsale.adapter.CardAdapter
import id.ac.ukdw.pointofsale.adapter.SpaceItemDecoration
import id.ac.ukdw.pointofsale.data.CardData
import id.ac.ukdw.pointofsale.viewmodel.SelectedItemViewModel


class MenuFragment : Fragment() {

    private lateinit var selectedItemViewModel: SelectedItemViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedItemViewModel = (requireActivity() as MainActivity).getSelectedItemViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewMenu)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        // Set the spacing (24dp in this case)
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_between_items)
        recyclerView.addItemDecoration(SpaceItemDecoration(spacingInPixels,3))

        val dataList = listOf(
            CardData(R.drawable.ayam, "Title 1", "Description 1"),
            CardData(R.drawable.ayam, "Title 2", "Description 1"),
            CardData(R.drawable.ayam, "Title 3", "Description 1"),
            CardData(R.drawable.ayam, "Title 4", "Description 1"),
            CardData(R.drawable.ayam, "Title 5", "Description 1"),
            CardData(R.drawable.ayam, "Title 6", "Description 1"),
            CardData(R.drawable.ayam, "Title 7", "Description 1"),
            CardData(R.drawable.ayam, "Title 8", "Description 1"),
            CardData(R.drawable.ayam, "Title 9", "Description 1")
            // Add more CardData objects as needed
        )

        val adapter = CardAdapter(dataList) { selectedItem ->
            selectedItemViewModel.setSelectedItem(selectedItem)
        }

        recyclerView.adapter = adapter

        return view
    }

}
