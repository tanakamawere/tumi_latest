package org.tmz.tumi.Adapters;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import org.tmz.tumi.Main.Dashboard.CustomersActivity;
import org.tmz.tumi.Objects.CustomerObject;
import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FirebaseMethods;

import java.util.ArrayList;

public class CustomersMainAdapter extends RecyclerView.Adapter<CustomersMainAdapter.CustomersViewHolder> {

    ArrayList<CustomerObject> arrayList;

    String randomKey = new FirebaseMethods().getDatabaseReference()
            .child("Tumi Information")
            .push().getKey();

    DatabaseReference databaseReference = new FirebaseMethods().getDatabaseReference()
            .child("Tumi Information")
            .child("Customers");

    public CustomersMainAdapter(ArrayList<CustomerObject> customerObjects) {
        this.arrayList = customerObjects;
    }

    @NonNull
    @Override
    public CustomersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_contact, parent, false);
        return new CustomersMainAdapter.CustomersViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomersViewHolder holder, final int position) {
        final CustomerObject customerObject = arrayList.get(position);

        holder.nameTV.setText(customerObject.getName());
        holder.phoneTV.setText(customerObject.getPhone());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("What do you want to do?");
                CharSequence[] options = new CharSequence[]{
                        "Sell to " + arrayList.get(position).getName(),
                        "Open customer profile",
                        "Delete from customers"
                };
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {

                        } else if (which == 1) {

                        } else if (which == 2) {
                            databaseReference.child(customerObject.getCustomerKey())
                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(v.getContext(), customerObject.getName() + " removed from customers.", Toast.LENGTH_SHORT).show();
                                        v.getContext().startActivity(new Intent(v.getContext(), CustomersActivity.class));
                                    } else {
                                        Toast.makeText(v.getContext(), "Failed to remove " + customerObject.getName() + " from customers.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
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

    public class CustomersViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTV, phoneTV;

        public CustomersViewHolder(View view) {
            super(view);

            nameTV = view.findViewById(R.id.contactName);
            phoneTV = view.findViewById(R.id.contactPhoneNumber);
        }
    }
}
