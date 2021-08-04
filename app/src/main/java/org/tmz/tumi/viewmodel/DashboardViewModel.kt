package org.tmz.tumi.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import org.tmz.tumi.R
import org.tmz.tumi.Utils.FirebaseMethods
import org.tmz.tumi.Utils.Miscellaneous
import java.util.*

class DashboardViewModel(var context: Context, var progressBar: ProgressBar) : ViewModel() {

    private val TAG = "DashboardViewModel"

    var mContext: Context = context
    var miscellaneous: Miscellaneous = Miscellaneous()
    var databaseReference: DatabaseReference =
        FirebaseMethods().getDatabaseReference().child(context.getString(R.string.fbTumiInfo))
    var salesTotal: Double = 0.0
    var statsSalesTotal: Double = 0.0
    var expensesTotal: Double = 0.0
    var statsExpensesTotal: Double = 0.0
    var debtsTotal: Double = 0.0
    var creditsTotal: Double = 0.0
    var profitLoss: Double = 0.0
    var stockTotal: Double = 0.0
    var quantityTotal: Double = 0.0

    fun displaySalesTotal() {
        databaseReference
            .child(mContext.getString(R.string.fbFinances))
            .child(mContext.getString(R.string.fbSales))
            .child(mContext.getString(R.string.fbSalesDetails))
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot1: DataSnapshot) {
                    if (snapshot1.exists()) {
                        progressBar.visibility = View.GONE
                        for (childSnapShot1 in snapshot1.children) {
                            try {
                                if (Objects.requireNonNull(childSnapShot1.child("date").value)
                                        .toString() == miscellaneous.date
                                ) {
                                    salesTotal =
                                        (salesTotal + Objects.requireNonNull(childSnapShot1.child("total").value)
                                            .toString().toDouble())
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "onDataChange: NPE: $e")
                            }
                            statsSalesTotal =
                                (statsSalesTotal + Objects.requireNonNull(childSnapShot1.child("total").value)
                                    .toString().toDouble())
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        displayStockTotal()
        displayExpensesTotal()
        displayMoney()
        displayProfitTotal(salesTotal, expensesTotal)
    }

    fun displayExpensesTotal() {
        databaseReference
            .child(mContext.getString(R.string.fbFinances))
            .child(mContext.getString(R.string.fbExpenses))
            .child("User Expenses").addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot1: DataSnapshot) {
                    if (snapshot1.exists()) {
                        for (childSnapShot1 in snapshot1.children) {
                            try {
                                if (Objects.requireNonNull(childSnapShot1.child("date").value)
                                        .toString() == miscellaneous.date
                                ) {
                                    expensesTotal = (expensesTotal + Objects.requireNonNull(
                                        childSnapShot1.child("total").value
                                    ).toString().toDouble())
                                    Log.e(TAG, "onDataChange: expenses total: $expensesTotal")
                                }
                            } catch (e: java.lang.Exception) {
                                Log.e(TAG, "onDataChange: No data found: $e")
                            }

                            //All time Stats
                            statsExpensesTotal =
                                (statsExpensesTotal + Objects.requireNonNull(childSnapShot1.child("total").value)
                                    .toString().toDouble())
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun displayStockTotal() {
        //Displaying Total Price of Stock
        databaseReference
            .child(mContext.getString(R.string.fbStock))
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (childSnapShot in snapshot.children) {
                            stockTotal += Objects.requireNonNull(childSnapShot.child("productBuyingPrice").value)
                                .toString().toDouble()
                            quantityTotal += Objects.requireNonNull(childSnapShot.child("productQuantity").value)
                                .toString().toDouble()
                            Log.d(
                                TAG,
                                "onDataChange: display stock total$salesTotal Expenses: $expensesTotal"
                            )
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun displayMoney() {
        val mDB = FirebaseMethods().getDatabaseReference()
            .child(mContext.getString(R.string.fbTumiInfo))
            .child(mContext.getString(R.string.fbMoney))
        mDB.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(sn: DataSnapshot) {
                if (sn.exists()) {
                    for (child in sn.children) {
                        if (child.child("type").value.toString() == mContext.getString(R.string.fbDebtsType)) {
                            debtsTotal =
                                (debtsTotal + child.child("amount").value.toString().toDouble())
                        } else if (child.child("type").value.toString() == mContext.getString(R.string.fbCreditsType)) {
                            creditsTotal =
                                (creditsTotal + child.child("amount").value.toString().toDouble())
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    @SuppressLint("SetTextI18n")
    fun displayProfitTotal(salesTotal: Double, expensesTotal: Double) {
        profitLoss = (salesTotal - expensesTotal)
    }
}