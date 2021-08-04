package org.tmz.tumi.Adapters;

import android.app.ProgressDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.tmz.tumi.Objects.RequestObject;
import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FirebaseMethods;

import java.util.ArrayList;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.RequestsViewHolder> {

    ArrayList<RequestObject> arrayList;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
            .child("Stories");

    public RequestsAdapter(final ArrayList<RequestObject> requestObjects) {
        this.arrayList = requestObjects;
    }

    @NonNull
    @Override
    public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_user_requests, parent, false);
        return new RequestsAdapter.RequestsViewHolder(myView);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestsViewHolder holder, final int position) {
        final RequestObject storyObject = arrayList.get(position);

        holder.title.setText(storyObject.getStoryTitle());
        holder.description.setText(storyObject.getStoryDescription());
        holder.body.setText(storyObject.getStoryBody());
        holder.date.setText(storyObject.getStoryDate());
        holder.time.setText(storyObject.getStoryTime());
        holder.writer.setText("By " + storyObject.getStoryWriter());

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final ProgressDialog progressDialog = new ProgressDialog(v.getContext());
                progressDialog.setMessage("Please wait while we are updating your info...");
                progressDialog.show();

                String selectedRequestsID = arrayList.get(position).getStoryKey();

                final DatabaseReference userDatabaseReference = new FirebaseMethods().getDatabaseReference()
                        .child("Explore")
                        .child("Stories")
                        .child(selectedRequestsID);
                userDatabaseReference.removeValue();

                databaseReference.child(selectedRequestsID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(v.getContext(), "Deleted your request.", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class RequestsViewHolder extends RecyclerView.ViewHolder {
        public TextView title, description, body, date, time, writer;
        public ImageButton delete;

        public RequestsViewHolder(View view) {
            super(view);

            title = view.findViewById(R.id.pingShowTitleTextView);
            description = view.findViewById(R.id.pingShowDescriptionTextView);
            body = view.findViewById(R.id.pingShowBodyTextView);
            date = view.findViewById(R.id.pingShowDateTextView);
            time = view.findViewById(R.id.pingShowTimeTextView);
            writer = view.findViewById(R.id.pingShowWriterTextView);

            delete = view.findViewById(R.id.deleteRequestButton);
        }
    }
}
