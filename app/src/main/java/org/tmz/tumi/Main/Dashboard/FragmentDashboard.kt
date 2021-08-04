package org.tmz.tumi.Main.Dashboard

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import com.nostra13.universalimageloader.core.ImageLoader
import org.tmz.tumi.Finances.FinancesActivity
import org.tmz.tumi.Objects.User
import org.tmz.tumi.R
import org.tmz.tumi.Stock.StockActivity
import org.tmz.tumi.Utils.FirebaseMethods
import org.tmz.tumi.Utils.Miscellaneous
import org.tmz.tumi.Utils.UniversalImageLoader
import org.tmz.tumi.databinding.FragmentDashboardBinding
import org.tmz.tumi.viewmodel.DashboardViewModel
import java.text.DecimalFormat
import java.util.*

class FragmentDashboard : Fragment() {
    private var databaseReference: DatabaseReference? = null
    private var exchangeDB: DatabaseReference? = null
    private var expensesTotal = 0.0
    private var salesTotal = 0.0
    private var debtsTotal = 0.0
    private var creditsTotal = 0.0
    private var rate = 0.0
    var WEEK_END_STATS = 0
    private var calendar: Calendar? = null
    private var decimalFormat: DecimalFormat? = null
    private var sharedPreferences: SharedPreferences? = null
    private var dashboardViewModel: DashboardViewModel? = null
    private var miscellaneous: Miscellaneous? = null
    private var exchangeHouse: String? = null
    private var currencySelected: String? = null

    private val TAG = "FragmentDashboard"

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        decimalFormat = DecimalFormat("####0.00")
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        miscellaneous = Miscellaneous()
        calendar = Calendar.getInstance()

        dashboardViewModel =
            binding?.dashboardProgressBar?.let { DashboardViewModel(requireContext(), it) }


        //On Click Listeners
        binding?.goToStock?.setOnClickListener {
            startActivity(
                Intent(
                    requireActivity(),
                    StockActivity::class.java
                )
            )
        }
        binding?.goToMoney?.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in,  // enter
                    R.anim.fade_out,  // exit
                    R.anim.fade_in,  // popEnter
                    R.anim.slide_out // popExit
                ).replace(R.id.frameLayoutDashboard, FragmentMoney())
                .addToBackStack(null).commit()
        }
        binding?.goToFinances?.setOnClickListener {
            startActivity(
                Intent(
                    requireActivity(),
                    FinancesActivity::class.java
                )
            )
        }
        binding?.goToCustomers?.setOnClickListener {
            startActivity(
                Intent(
                    requireActivity(),
                    CustomersActivity::class.java
                )
            )
        }

        //Database referencing
        databaseReference = FirebaseMethods().getDatabaseReference()
            .child(requireActivity().getString(R.string.fbTumiInfo))
        exchangeDB =
            FirebaseDatabase.getInstance().reference.child(requireActivity().getString(R.string.fbCurrency))

        //Method calling
        try {
            populatePage()
            checkForInternetConnection()
            initImageLoader()
            displayTotal()
        } catch (e: Exception) {
            Log.e(TAG, "onCreate: " + e.message)
        }
    }

    private fun displayTotal() {
        dashboardViewModel!!.displaySalesTotal()
        binding?.totalProfitHomeDisplay?.text = dashboardViewModel!!.profitLoss.toString()
        binding?.totalDebtsHomeDisplay?.text = dashboardViewModel!!.debtsTotal.toString()
        binding?.totalCreditDisplay?.text = dashboardViewModel!!.creditsTotal.toString()
        binding?.totalStockHomeDisplay?.text = dashboardViewModel!!.stockTotal.toString()
        binding?.totalSalesHomeDisplay?.text = dashboardViewModel!!.salesTotal.toString()
        binding?.totalExpensesHomeDisplay?.text = dashboardViewModel!!.expensesTotal.toString()

        binding?.salesDashboardStatsTextView?.text = dashboardViewModel!!.statsSalesTotal.toString()
        binding?.statsProfitDashboard?.text =
            (dashboardViewModel!!.statsSalesTotal - dashboardViewModel!!.statsExpensesTotal - dashboardViewModel!!.stockTotal).toString()
        binding?.expensesDashboardStatsTextView?.text =
            dashboardViewModel!!.statsExpensesTotal.toString()
    }

    val personalExchangeRates: Unit
        get() {
            databaseReference!!.child(requireActivity().getString(R.string.fbFinances))
                .child(requireActivity().getString(R.string.fbCurrency))
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            when (currencySelected) {
                                requireActivity().getString(R.string.defaultCurrencyValue) -> {
                                    rate =
                                        snapshot.child(requireActivity().getString(R.string.fbTumiDefault)).value.toString()
                                            .toDouble()
                                }
                                requireActivity().getString(R.string.usdValue) -> {
                                    rate = 1.0
                                }
                                requireActivity().getString(R.string.randValue) -> {
                                    rate =
                                        snapshot.child(requireActivity().getString(R.string.fbRand)).value.toString()
                                            .toDouble()
                                }
                                requireActivity().getString(R.string.zwlValue) -> {
                                    rate =
                                        snapshot.child(requireActivity().getString(R.string.fbZwl)).value.toString()
                                            .toDouble()
                                }
                            }
                        } else {
                            Toast.makeText(
                                requireActivity(),
                                "You haven't enabled Exchange Rate Editing",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    private val publicExchangeRates: Unit
        get() {
            if (exchangeHouse == requireActivity().getString(R.string.interBankExchangeRateValue)) {
                exchangeDB!!.child(requireActivity().getString(R.string.fbInterbank))
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                when (currencySelected) {
                                    requireActivity().getString(R.string.defaultCurrencyValue) -> {
                                        rate =
                                            snapshot.child(requireActivity().getString(R.string.fbTumiDefault)).value.toString()
                                                .toDouble()
                                    }
                                    requireActivity().getString(R.string.usdValue) -> {
                                        rate = 1.0
                                    }
                                    requireActivity().getString(R.string.randValue) -> {
                                        rate =
                                            snapshot.child(requireActivity().getString(R.string.fbRand)).value.toString()
                                                .toDouble()
                                    }
                                    requireActivity().getString(R.string.zwlValue) -> {
                                        rate =
                                            snapshot.child(requireActivity().getString(R.string.fbZwl)).value.toString()
                                                .toDouble()
                                    }
                                }
                            } else {
                                Log.e(
                                    TAG,
                                    "onDataChange-get public rate-Interbank: Some error happened"
                                )
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
            } else if (exchangeHouse == requireActivity().getString(R.string.blackMarketExchangeRateValue)) {
                exchangeDB!!.child(requireActivity().getString(R.string.fbBlackMarket))
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                if (currencySelected == requireActivity().getString(R.string.defaultCurrencyValue)) {
                                    rate =
                                        snapshot.child(requireActivity().getString(R.string.fbTumiDefault)).value.toString()
                                            .toDouble()
                                } else if (currencySelected == requireActivity().getString(R.string.usdValue)) {
                                    rate = 1.0
                                } else if (currencySelected == requireActivity().getString(R.string.randValue)) {
                                    rate =
                                        snapshot.child(requireActivity().getString(R.string.fbRand)).value.toString()
                                            .toDouble()
                                } else if (currencySelected == requireActivity().getString(R.string.zwlValue)) {
                                    rate =
                                        snapshot.child(requireActivity().getString(R.string.fbZwl)).value.toString()
                                            .toDouble()
                                }
                            } else {
                                Log.e(
                                    TAG,
                                    "onDataChange-get public rate-Black Market: Some error happened"
                                )
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
            }
        }

    private fun populatePage() {
        try {
            setProfileImage()
        } catch (e: Exception) {
            Log.e(TAG, "populatePage: $e")
        }
    }

    private fun initImageLoader() {
        val universalImageLoader = UniversalImageLoader(requireActivity())
        ImageLoader.getInstance().init(universalImageLoader.config)
    }

    //method called from populate page();
    private fun setProfileImage() {
        Log.d(TAG, "setProfileImage: Setting Profile Image")
        val proPic = FirebaseMethods().getDatabaseReference()
        proPic.child(requireActivity().getString(R.string.fbUserInfo))
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)!!
                    val imgURL = user.profilePicture
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun uploadWeeklyStats() {
        try {
            val weekString = sharedPreferences!!.getString(
                requireActivity().getString(R.string.weekEndDate),
                requireActivity().getString(R.string.saturdayValue)
            )
            if (weekString == requireActivity().getString(R.string.fridayValue)) {
                WEEK_END_STATS = 6
            } else if (weekString == requireActivity().getString(R.string.saturdayValue)) {
                WEEK_END_STATS = 7
            } else if (weekString == requireActivity().getString(R.string.sundayValue)) {
                WEEK_END_STATS = 1
            }
            if (miscellaneous!!.internetAvailable(requireActivity())) {
                if (miscellaneous!!.day == WEEK_END_STATS) {
                    val financesKey = miscellaneous!!.date + miscellaneous!!.time
                    val map = HashMap<String, Any>()
                    map["Sales"] = salesTotal
                    map["Expenses"] = expensesTotal
                    map["Debts"] = debtsTotal
                    map["Credits"] = creditsTotal
                    map["Time"] = miscellaneous!!.time
                    map["Date"] = miscellaneous!!.date
                    map["Key"] = financesKey
                    databaseReference!!.child(requireActivity().getString(R.string.fbStatistics))
                        .child(requireActivity().getString(R.string.fbFinances))
                        .child(financesKey).updateChildren(map).addOnSuccessListener { }
                        .addOnFailureListener {
                            Log.e(
                                TAG,
                                "onFailure: Failed to back up statistics"
                            )
                        }
                }
            }
        } catch (e: IllegalStateException) {
            Toast.makeText(
                requireActivity(),
                "There has been an error. Please try again",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun checkForInternetConnection() {

    }

    companion object {
        private const val TAG = "FragmentDashboard"
    }
}