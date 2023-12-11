package id.ac.ukdw.pointofsale.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.ac.ukdw.pointofsale.R



class DashboardFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        // Load MenuFragment into the FrameLayout
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.menuContainer, MenuFragment())
                .commit()
        }

        return view
    }


}