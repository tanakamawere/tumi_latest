package org.tmz.tumi.Main.Explore

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.database.*
import com.nostra13.universalimageloader.core.ImageLoader
import org.tmz.tumi.Adapters.ExploreBusinessAdapter
import org.tmz.tumi.Adapters.ExploreProductsAdapter
import org.tmz.tumi.Finances.RecordSaleActivity
import org.tmz.tumi.Main.Dashboard.DashboardActivity
import org.tmz.tumi.Objects.AdvertisementsObject
import org.tmz.tumi.Objects.BusinessObject
import org.tmz.tumi.R
import org.tmz.tumi.Utils.UniversalImageLoader
import org.tmz.tumi.databinding.FragmentExploreBinding
import java.util.*

class FragmentExplore : Fragment() {

    private val TAG = "FragmentExplore"

    private var recyclerView: RecyclerView? = null
    private var recyclerViewBusiness: RecyclerView? = null
    private var adapter: ExploreProductsAdapter? = null
    private var businessAdapter: ExploreBusinessAdapter? = null

    //Arrays
    private var photo: ArrayList<AdvertisementsObject?>? = null
    private var businessObjects: ArrayList<BusinessObject>? = null

    //Widgets
    private var openSearchBox: ImageButton? = null
    private var refreshButton: ImageButton? = null
    private var progressBar: ProgressBar? = null
    private var nullRequests: TextView? = null
    private var chipGroup: ChipGroup? = null
    private var databaseReference: DatabaseReference? = null
    private var picture: String? = null

    //Strings for Business DataSnapshot
    var name = ""
    var type = ""
    var category = ""
    var key = ""

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding

    //Views
    private var chip: Chip? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)

        nullRequests = binding?.nullExploreProducts
        recyclerView = binding?.recyclerViewExploreProducts
        recyclerViewBusiness = binding?.recyclerViewExploreBusinesses
        progressBar = binding?.progressBarExploreProducts
        chipGroup = binding?.chipGroupExplore

        val bottomNavigationView: BottomNavigationView? =
            binding?.bottomBar?.bottomNavigationViewMain
        bottomNavigationView?.menu?.findItem(R.id.explore)?.isChecked = true
        bottomNavigationView?.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.dashboard -> startActivity(
                    Intent(
                        requireContext(),
                        DashboardActivity::class.java
                    )
                )
                R.id.explore -> startActivity(Intent(requireContext(), ExploreActivity::class.java))
                R.id.sell -> startActivity(Intent(requireContext(), RecordSaleActivity::class.java))
            }
            true
        }

        setHasOptionsMenu(true)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photo = ArrayList()
        businessObjects = ArrayList()

        //Widgets initializations
        chipGroup?.setOnCheckedChangeListener { _, checkedId ->
            chip = view.findViewById(checkedId)
            if (chip != null) {
                filterAdRecyclerView(chip!!.text.toString())
                filterBusinessRecyclerView(chip!!.text.toString())
            }
        }

        try {
            initImageLoader()
            initializingAdRecyclerView()
            initializingBusinessRecyclerView()
        } catch (e: Exception) {
            Log.e(TAG, "onCreate: $e")
        }

    }

    private fun initImageLoader() {
        val universalImageLoader = UniversalImageLoader(requireActivity())
        ImageLoader.getInstance().init(universalImageLoader.config)
    }

    private fun initializingAdRecyclerView() {
        recyclerView!!.isNestedScrollingEnabled = false
        recyclerView!!.setHasFixedSize(false)
        recyclerView!!.layoutManager = GridLayoutManager(requireActivity(), NUMBER_GRID_COLUMNS)
        val databaseReference = FirebaseDatabase.getInstance().reference
        val query: Query = databaseReference.child("Advertisements")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var URL = ""
                    var adKey = ""
                    var adProductTitle = ""
                    var adProductDescription = ""
                    var adCategory = ""
                    for (snapshot1 in snapshot.children) {
                        //photo.add(snapshot1.getValue(AdvertisementsObject.class));
                        try {
                            URL = snapshot1.child("imageURL").value.toString()
                            adKey = snapshot1.child("adKey").value.toString()
                            adProductTitle = snapshot1.child("adProductTitle").value.toString()
                            adProductDescription =
                                snapshot1.child("adProductDescription").value.toString()
                            adCategory = snapshot1.child("adCategory").value.toString()
                        } catch (e: NullPointerException) {
                            Log.e(TAG, "onDataChange: Failed to retrieve a child: $e")
                        }
                        val `object` = AdvertisementsObject(
                            adProductTitle,
                            null,
                            adProductDescription,
                            adKey,
                            null,
                            null,
                            null,
                            URL,
                            null,
                            null,
                            adCategory
                        )
                        photo!!.add(`object`)
                        Collections.shuffle(photo)
                        adapter!!.notifyDataSetChanged()
                    }
                } else {
                    nullRequests!!.visibility = View.VISIBLE
                }
                progressBar!!.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        adapter = ExploreProductsAdapter(photo, requireActivity())
        recyclerView!!.adapter = adapter
    }

    private fun initializingBusinessRecyclerView() {
        recyclerViewBusiness!!.setHasFixedSize(false)
        recyclerViewBusiness!!.isNestedScrollingEnabled = false
        recyclerViewBusiness!!.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        databaseReference!!.child(getString(R.string.fbBusinesses))
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (childSnap in snapshot.children) {
                            key = childSnap.child("Key").value.toString()

                            //Getting the user's profile picture
                            databaseReference!!.child(getString(R.string.fbUsers))
                                .child(key).child(getString(R.string.fbUserInfo))
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists()) {
                                            picture =
                                                snapshot.child("profilePicture").value.toString()
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {}
                                })

                            //Getting the details from the user's personal node
                            databaseReference!!.child(getString(R.string.fbUsers))
                                .child(key).child(getString(R.string.fbBusinessInfo))
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists()) {
                                            name = snapshot.child("Name").value.toString()
                                            type = snapshot.child("Type").value.toString()
                                            category = snapshot.child("Category").value.toString()
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {}
                                })
                            val `object` = BusinessObject(
                                null,
                                null,
                                name,
                                null,
                                type,
                                category,
                                picture,
                                key,
                                null
                            )
                            businessObjects!!.add(`object`)
                            businessAdapter!!.notifyDataSetChanged()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        businessAdapter = ExploreBusinessAdapter(requireActivity(), businessObjects)
        recyclerViewBusiness!!.adapter = businessAdapter
    }

    private fun filterAdRecyclerView(text: String) {
        val searchedAdList = ArrayList<AdvertisementsObject?>()
        for (`object` in photo!!) {
            if (`object`!!.adProductTitle.toLowerCase().contains(text.toLowerCase())) {
                searchedAdList.add(`object`)
            }
            if (`object`.adProductDescription.toLowerCase().contains(text.toLowerCase())) {
                searchedAdList.add(`object`)
            }
            if (`object`.adCategory.toLowerCase().contains(text.toLowerCase())) {
                searchedAdList.add(`object`)
            }
        }
        adapter!!.filterList(searchedAdList)
    }

    private fun filterBusinessRecyclerView(text: String) {
        val searchedList = ArrayList<BusinessObject>()
        for (`object` in businessObjects!!) {
            if (`object`.name.toLowerCase().contains(text.toLowerCase())) {
                searchedList.add(`object`)
            }
            if (`object`.category.toLowerCase().contains(text.toLowerCase())) {
                searchedList.add(`object`)
            }
        }
        businessAdapter!!.filterList(searchedList)
    }

    companion object {
        private const val TAG = "FragmentExplore"
        private const val NUMBER_GRID_COLUMNS = 3
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.explore_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.exploreMenuProfile -> findNavController().navigate(R.id.action_fragmentExplore2_to_MBNavigationExplore)
        }
        return super.onOptionsItemSelected(item)
    }
}