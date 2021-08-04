package org.tmz.tumi.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.tmz.tumi.Objects.MoneyObject;
import org.tmz.tumi.R;

import java.util.ArrayList;

public class MoneyAdapter extends RecyclerView.Adapter<MoneyAdapter.MoneyDebtsViewHolder> {

    private final ArrayList<MoneyObject> arrayList;

    public MoneyAdapter(ArrayList<MoneyObject> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MoneyAdapter.MoneyDebtsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_debts, parent, false);
        return new MoneyDebtsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoneyDebtsViewHolder holder, int position) {
        MoneyObject object = arrayList.get(position);

        holder.name.setText(object.getName());
        holder.phone.setText(object.getPhone());
        holder.amount.setText("$" + object.getAmount());
        holder.date.setText(object.getDate());
        holder.type.setText(object.getType());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MoneyDebtsViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView phone;
        private final TextView date;
        private final TextView amount;
        private final TextView type;

        public MoneyDebtsViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.nameDebtTextView);
            phone = itemView.findViewById(R.id.phoneDebtTextView);
            date = itemView.findViewById(R.id.dateDebtTextView);
            amount = itemView.findViewById(R.id.amountDebtTextView);
            type = itemView.findViewById(R.id.typeDebtTextView);
        }
    }
}
