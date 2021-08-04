package org.tmz.tumi.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.tmz.tumi.Finances.FinancesActivity;
import org.tmz.tumi.Objects.FinanceExpenseObject;
import org.tmz.tumi.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

public class FinancesExpensesAdapter extends RecyclerView.Adapter<FinancesExpensesAdapter.ExpensesViewHolder> {

    ArrayList<FinanceExpenseObject> arrayList;

    public FinancesExpensesAdapter(ArrayList<FinanceExpenseObject> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ExpensesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_finances_expenses_fragment, parent, false);
        return new ExpensesViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ExpensesViewHolder holder, final int position) {
        FinanceExpenseObject financeExpenseObject = arrayList.get(position);
        DecimalFormat decimalFormat = new DecimalFormat("####0.00");

        holder.typeTV.setText(financeExpenseObject.getType());
        holder.dateTV.setText("Date:                 " + financeExpenseObject.getDate());
        holder.amountTV.setText("Amount:        USD $" + financeExpenseObject.getTotal());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Are you sure you want to delete this record?");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        final ProgressDialog progressDialog = new ProgressDialog(v.getContext());
                        progressDialog.setMessage("Please wait while we are updating your info...");
                        progressDialog.show();

                        String selectedExpenseID = arrayList.get(position).getExpenseID();

                        //Getting Information from FireBase
                        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                                .child("Users")
                                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                .child("Tumi Information")
                                .child("Finances")
                                .child("Expenses")
                                .child("User Expenses")
                                .child(selectedExpenseID);

                        databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(v.getContext(), "Success!", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(v.getContext(), FinancesActivity.class);
                                    v.getContext().startActivity(intent);
                                }
                            }
                        });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ExpensesViewHolder extends RecyclerView.ViewHolder {
        public TextView typeTV, dateTV, amountTV;

        public ExpensesViewHolder(@NonNull View view) {
            super(view);
            typeTV = view.findViewById(R.id.expenseTypeTextView);
            dateTV = view.findViewById(R.id.expenseDateTextView);
            amountTV = view.findViewById(R.id.expenseAmountTextView);
        }
    }
}
