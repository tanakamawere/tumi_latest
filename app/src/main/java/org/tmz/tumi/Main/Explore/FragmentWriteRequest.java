package org.tmz.tumi.Main.Explore;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.tmz.tumi.Objects.BusinessObject;
import org.tmz.tumi.Objects.User;
import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FirebaseMethods;
import org.tmz.tumi.Utils.Miscellaneous;

import java.util.HashMap;
import java.util.Objects;

public class FragmentWriteRequest extends Fragment {

    private static final String TAG = "FragmentWriteRequest";

    private TextInputEditText title, description, body;
    private Button post;
    private DatabaseReference databaseReference;
    private String storyKey;
    private HashMap<String, Object> map;
    private String mTitle, mDescription, mBody, storyWriter, requesterPhone;
    private FirebaseAuth mAuth;
    private Miscellaneous dateTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_write_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title = view.findViewById(R.id.storyTitleEditText);
        description = view.findViewById(R.id.storyDescriptionEditText);
        body = view.findViewById(R.id.storyBodyEditText);

        map = new HashMap<>();
        dateTime = new Miscellaneous();
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storyKey = FirebaseDatabase.getInstance().getReference().child("Stories").push().getKey();

        post = view.findViewById(R.id.postStoryButton);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postStory();
            }
        });

        getWriterName();
    }

    private void getWriterName() {
        DatabaseReference accountInfoDatabase = new FirebaseMethods().getDatabaseReference();

        accountInfoDatabase.child("User Information").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d(TAG, "onDataChange: Going into database");
                    User user = snapshot.getValue(User.class);

                    assert user != null;
                    storyWriter = user.getFullName();
                    Log.d(TAG, "onDataChange: I got it");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        accountInfoDatabase.child("Business Information").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    BusinessObject object = snapshot.getValue(BusinessObject.class);
                    assert object != null;

                    requesterPhone = object.getPhoneNumber();
                    Log.e(TAG, "onDataChange: Request Phone: " + requesterPhone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void populateHashMap() {
        map.put("storyTitle", mTitle);
        map.put("storyDescription", mDescription);
        map.put("storyBody", mBody);
        map.put("storyWriter", storyWriter);
        map.put("storyKey", storyKey);
        map.put("storyTime", dateTime.getTime());
        map.put("storyDate", dateTime.getDate());
        map.put("requesterPhone", requesterPhone);
        map.put("requesterUID", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
    }

    private void postStory() {
        mTitle = Objects.requireNonNull(title.getText()).toString();
        mDescription = Objects.requireNonNull(description.getText()).toString();
        mBody = Objects.requireNonNull(body.getText()).toString();

        populateHashMap();

        if (mTitle.equals("") && mDescription.equals("") && mBody.equals("")) {
            Toast.makeText(getActivity(), "Please fill in all the fields.", Toast.LENGTH_SHORT).show();
        } else {
            postToUserNode();
            databaseReference.child("Stories").child(storyKey).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Story posted successfully.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), ExploreActivity.class));
                        requireActivity().finishAffinity();
                    } else {
                        Toast.makeText(getActivity(), "Failed to post. Check your internet connection.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void postToUserNode() {
        FirebaseMethods firebaseMethods = new FirebaseMethods();

        firebaseMethods.getDatabaseReference().child("Explore")
                .child("Stories")
                .child(storyKey).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e(TAG, "onSuccess: Posted to user node successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: Failed to post to user node");
            }
        });
    }
}
