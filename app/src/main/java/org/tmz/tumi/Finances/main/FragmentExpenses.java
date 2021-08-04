package org.tmz.tumi.Finances.main;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.tmz.tumi.Adapters.FinancesExpensesAdapter;
import org.tmz.tumi.Finances.FragmentRecordExpenses;
import org.tmz.tumi.Objects.FinanceExpenseObject;
import org.tmz.tumi.R;

import java.util.ArrayList;
import java.util.Objects;

public class FragmentExpenses extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<FinanceExpenseObject> arrayList;
    private TextView nullExpensesTV, nullInternet;
    private ProgressBar progressBar;
    private ExtendedFloatingActionButton recordExpense;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expenses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nullExpensesTV = view.findViewById(R.id.nullStockExpenses);
        nullExpensesTV.setVisibility(View.GONE);
        progressBar = view.findViewById(R.id.progressBarExpenses);
        nullInternet = view.findViewById(R.id.nullInternetExpensesTV);
        nullInternet.setVisibility(View.GONE);

        recordExpense = view.findViewById(R.id.fabRecordExpense);
        recordExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in,  // enter
                                R.anim.fade_out,  // exit
                                R.anim.fade_in,   // popEnter
                                R.anim.slide_out)
                        .replace(R.id.frameLayoutFinances, new FragmentRecordExpenses())
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            }
        });

        checkForInternetConnection();
        arrayList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerViewFinancesExpense);
        initializeRecyclerView();
    }

    private void checkForInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            nullInternet.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void initializeRecyclerView() {
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        //Getting Information from FireBase
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child("Tumi Information")
                .child("Finances")
                .child("Expenses")
                .child("User Expenses");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String type = "",
                            date = "",
                            total = "",
                            expenseID = "";

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.child("type").getValue() != null)
                            type = dataSnapshot.child("type").getValue().toString();
                        if (dataSnapshot.child("date").getValue() != null)
                            date = dataSnapshot.child("date").getValue().toString();
                        if (dataSnapshot.child("total").getValue() != null)
                            total = dataSnapshot.child("total").getValue().toString();
                        if (dataSnapshot.child("expenseID").getValue() != null)
                            expenseID = dataSnapshot.child("expenseID").getValue().toString();

                        FinanceExpenseObject financeExpenseObject = new FinanceExpenseObject(type, date, total, expenseID);
                        arrayList.add(financeExpenseObject);
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                } else {
                    nullExpensesTV.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Adapter
        recyclerViewAdapter = new FinancesExpensesAdapter(arrayList);
        recyclerView.setAdapter(recyclerViewAdapter);
    }
}
