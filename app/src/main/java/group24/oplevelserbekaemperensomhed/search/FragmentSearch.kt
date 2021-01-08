package group24.oplevelserbekaemperensomhed.search

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import group24.oplevelserbekaemperensomhed.R

class FragmentSearch : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

}