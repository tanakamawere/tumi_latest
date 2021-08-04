package org.tmz.tumi.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import org.tmz.tumi.Finances.SaleCartActivity;
import org.tmz.tumi.Objects.CustomerObject;
import org.tmz.tumi.R;

import java.util.ArrayList;

public class CustomersCartAdapter extends RecyclerView.Adapter<CustomersCartAdapter.CustomersViewHolder> {

    ArrayList<CustomerObject> arrayList;

    public CustomersCartAdapter(ArrayList<CustomerObject> customerObjects) {
        this.arrayList = customerObjects;
    }

    @NonNull
    @Override
    public CustomersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_contact, parent, false);

        return new CustomersCartAdapter.CustomersViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomersViewHolder holder, final int position) {
        final CustomerObject customerObject = arrayList.get(position);

        holder.nameTV.setText(customerObject.getName());
        holder.phoneTV.setText(customerObject.getPhone());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intent = new Intent("action");

                intent.putExtra("mName", customerObject.getName());
                intent.putExtra("mPhone", customerObject.getPhone());
                LocalBroadcastManager.getInstance(v.getContext()).sendBroadcast(intent);

                Toast.makeText(v.getContext(), customerObject.getName() + " selected", Toast.LENGTH_SHORT).show();
                v.getContext().startActivity(new Intent(v.getContext(), SaleCartActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class CustomersViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTV, phoneTV;

        public CustomersViewHolder(View view) {
            super(view);

            nameTV = view.findViewById(R.id.contactName);
            phoneTV = view.findViewById(R.id.contactPhoneNumber);
        }
    }
}
