package org.tmz.tumi.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.tmz.tumi.R;

import java.text.DecimalFormat;
import java.util.Objects;

public class AccountingCalculations {

    private static final String TAG = "AccountingCalculations";
    public double expensesTotal2, salesTotal2;
    public DecimalFormat decimalFormat = new DecimalFormat("####0.00");
    //Calculating cost of goods sold
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
            .child("Users")
            .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
            .child("Tumi Information");
    private String costOfGoodsSoldString;

    public String costOfGoodsSold() {
        databaseReference.child("Stock").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double costOfGoodsSold = 0;
                    for (DataSnapshot childSnapShot : snapshot.getChildren()) {
                        costOfGoodsSold = costOfGoodsSold + Double.parseDouble(Objects.requireNonNull(childSnapShot.child("productValue").getValue()).toString());
                    }
                    costOfGoodsSoldString = decimalFormat.format(costOfGoodsSold);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        Log.e(TAG, "costOfGoodsSold: Is it returning null or a value?: " + costOfGoodsSoldString);
        return costOfGoodsSoldString;
    }

    public void salesTotal(final TextView textView, final Context context) {
        Log.d(TAG, "salesTotal: Going into sales node");

        databaseReference.child("Finances")
                .child("Sales")
                .child("Finished Sales").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                if (snapshot1.exists()) {
                    for (DataSnapshot childSnapShot1 : snapshot1.getChildren()) {
                        double salesTotal = 0;
                        for (DataSnapshot child : childSnapShot1.getChildren()) {
                            salesTotal = salesTotal +
                                    Double.parseDouble
                                            (Objects.requireNonNull(child.child("productTotal").getValue()).toString());
                            salesTotal2 = salesTotal;
                            textView.setText(context.getString(R.string.currencyIdentifier) + decimalFormat.format(salesTotal));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void expensesTotal(final TextView textView, final Context context) {
        databaseReference.child("Finances")
                .child("Expenses")
                .child("User Expenses").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                if (snapshot1.exists()) {
                    double expensesTotal = 0;
                    for (DataSnapshot childSnapShot1 : snapshot1.getChildren()) {
                        expensesTotal = expensesTotal +
                                Double.parseDouble
                                        (Objects.requireNonNull(childSnapShot1.child("total").getValue()).toString());
                        expensesTotal2 = expensesTotal;
                        textView.setText(context.getString(R.string.currencyIdentifier) + decimalFormat.format(expensesTotal));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public String netProfitTotal() {
        double profitLoss = salesTotal2 - expensesTotal2;
        return Double.toString(profitLoss);
    }

    public void grossProfitTotal(final TextView textView) {
        databaseReference.child("Tumi Information").child("Stock");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double grossTotal = 0;
                    for (DataSnapshot childSnapShot : snapshot.getChildren()) {
                        grossTotal = grossTotal + Double.parseDouble(Objects.requireNonNull(childSnapShot.child("productGrossProfit").getValue()).toString());
                        textView.setText(decimalFormat.format(grossTotal));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
