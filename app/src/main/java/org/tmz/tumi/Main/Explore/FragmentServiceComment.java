package org.tmz.tumi.Main.Explore;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.tmz.tumi.Adapters.CommentsAdapter;
import org.tmz.tumi.Objects.CommentObject;
import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FirebaseMethods;
import org.tmz.tumi.Utils.Miscellaneous;

import java.util.ArrayList;
import java.util.HashMap;

public class FragmentServiceComment extends Fragment {
    private static final String TAG = "FragmentServiceComment";

    private TextView nullComments;
    private TextInputEditText CommentET;
    private ImageButton Comment;
    private String selectedService, commenterName, commentUID;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;
    private FirebaseMethods firebaseMethods;
    private ArrayList<CommentObject> commentArraylist;
    private RecyclerView recyclerView;
    private CommentsAdapter commentsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_service_comment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        assert bundle != null;
        selectedService = bundle.getString(getString(R.string.selectedService));

        CommentET = view.findViewById(R.id.CommentEditText);
        Comment = view.findViewById(R.id.CommentButton);
        nullComments = view.findViewById(R.id.noCommentTextView);
        nullComments.setVisibility(View.GONE);
        progressBar = view.findViewById(R.id.progressBarComments);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseMethods = new FirebaseMethods();
        recyclerView = view.findViewById(R.id.recyclerViewComments);
        commentArraylist = new ArrayList<>();

        Comment.setOnClickListener(v -> {
            /*if (new Miscellaneous().internetAvailable(requireActivity())){

            }
            else{
                Toast.makeText(requireActivity(), "There is no available internet connection", Toast.LENGTH_SHORT).show();
            }*/

            if (!CommentET.getText().toString().isEmpty()) {
                sendComment();
                CommentET.getText().clear();
            } else {
                Toast.makeText(requireActivity(), "Type a comment", Toast.LENGTH_SHORT).show();
            }
        });

        try {
            commentUID = databaseReference.child(getString(R.string.fbServices))
                    .child(selectedService)
                    .child(getString(R.string.fbComments))
                    .child(commentUID).push().getKey();
        } catch (NullPointerException nullPointerException) {
            Log.e(TAG, "onViewCreated: " + nullPointerException.toString());
        }

        getMyBusinessName();
        getComments();
    }

    private void getMyBusinessName() {
        new FirebaseMethods().getBusinessInfoDB().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    progressBar.setVisibility(View.GONE);

                    commenterName = snapshot.child("Name").getValue().toString();
                } else {
                    Toast.makeText(requireActivity(), "Advertising business not listed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendComment() {
        Miscellaneous miscellaneous = new Miscellaneous();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("text", CommentET.getText().toString());
        hashMap.put("key", selectedService);
        hashMap.put("date", miscellaneous.getDate());
        hashMap.put("time", miscellaneous.getTime());
        hashMap.put("commenter", commenterName);

        databaseReference.child(getString(R.string.fbServices))
                .child(selectedService)
                .child(getString(R.string.fbComments))
                .child(commentUID).updateChildren(hashMap);
    }

    private void getComments() {
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));

        //Getting sent messages
        databaseReference.child(getString(R.string.fbServices))
                .child(selectedService)
                .child(getString(R.string.fbComments))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String text = "", date = "", time = "", key = "", commenter = "";

                                text = dataSnapshot.child("text").getValue().toString();
                                date = dataSnapshot.child("date").getValue().toString();
                                if (date.equals(new Miscellaneous().getDate())) {
                                    date = "Today";
                                }

                                time = dataSnapshot.child("time").getValue().toString();
                                key = dataSnapshot.child("key").getValue().toString();
                                commenter = dataSnapshot.child(commenter).getValue().toString();

                                CommentObject commentObject = new CommentObject(text, key, date, time, commenter);
                                commentArraylist.add(commentObject);
                                commentsAdapter.notifyDataSetChanged();
                            }
                        } else {
                            nullComments.setVisibility(View.VISIBLE);
                        }
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        commentsAdapter = new CommentsAdapter(commentArraylist);
        recyclerView.setAdapter(commentsAdapter);
    }
}
