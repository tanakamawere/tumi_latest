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
import org.tmz.tumi.Objects.ContactObject;
import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FirebaseMethods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {

    ArrayList<ContactObject> arrayList;

    String randomKey = new FirebaseMethods().getDatabaseReference()
            .child("Tumi Information")
            .push().getKey();

    DatabaseReference databaseReference = new FirebaseMethods().getDatabaseReference()
            .child("Tumi Information")
            .child("Customers")
            .child(Objects.requireNonNull(randomKey))
            .child("Details");

    public ContactsAdapter(ArrayList<ContactObject> contactObjects) {
        this.arrayList = contactObjects;
    }

    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_contact, parent, false);

        return new ContactsAdapter.ContactsViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactsViewHolder holder, final int position) {
        ContactObject contactObject = arrayList.get(position);

        holder.nameTV.setText(contactObject.getName());
        holder.phoneTV.setText(contactObject.getPhone());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Contacts List");
                builder.setMessage("Add to Customers?");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String name = arrayList.get(position).getName();
                        String phone = arrayList.get(position).getPhone();

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("name", name);
                        map.put("phoneNumber", phone);
                        map.put("customerKey", randomKey);

                        databaseReference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(v.getContext(), name + " added successfully", Toast.LENGTH_SHORT).show();
                                    v.getContext().startActivity(new Intent(v.getContext(), CustomersActivity.class));
                                } else {
                                    Toast.makeText(v.getContext(), name + " failed to be added", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
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

    public class ContactsViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTV, phoneTV;

        public ContactsViewHolder(View view) {
            super(view);

            nameTV = view.findViewById(R.id.contactName);
            phoneTV = view.findViewById(R.id.contactPhoneNumber);
        }
    }
}
