package org.tmz.tumi.Main.Dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.tmz.tumi.Adapters.MessagesMainAdapter;
import org.tmz.tumi.Objects.MessageMainObject;
import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FirebaseMethods;

import java.util.ArrayList;

public class FragmentMessages extends Fragment {

    private static final String TAG = "FragmentMessages";

    //Widgets
    private ProgressBar progressBar;
    private TextView nullMessages;
    private RelativeLayout relativeLayout;
    private RecyclerView recyclerView;
    private MessagesMainAdapter adapter;
    private ArrayList<MessageMainObject> arrayList;

    private FirebaseMethods firebaseMethods;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        relativeLayout = view.findViewById(R.id.relativeLayoutSBMessages);
        recyclerView = view.findViewById(R.id.recyclerViewMessages);
        firebaseMethods = new FirebaseMethods();
        arrayList = new ArrayList<>();
        nullMessages = view.findViewById(R.id.nullMessagesMain);
        nullMessages.setVisibility(View.GONE);
        progressBar = view.findViewById(R.id.progressBarMessagesMain);

        //OnClickListeners
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new FragmentMessagingBusinesses();
                Bundle bundle = new Bundle();
                fragment.setArguments(bundle);

                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out).replace(R.id.frameLayoutDashboard, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        getChats();
    }

    private void getChats() {
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, true));

        databaseReference = firebaseMethods.getDatabaseReference();

        databaseReference.child(getString(R.string.fbMessages))
                .child(getString(R.string.fbMessagesDetails)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        try {
                            String name = "", key = "", profilePicture = "";

                            name = snapshot1.child("name").getValue().toString();
                            key = snapshot1.child("key").getValue().toString();
                            profilePicture = snapshot1.child("profilePicture").getValue().toString();

                            MessageMainObject object = new MessageMainObject(name, key, profilePicture);
                            arrayList.add(object);
                            adapter.notifyDataSetChanged();
                        } catch (NullPointerException e) {
                            Log.e(TAG, "onDataChange: " + e.toString());
                        }
                    }
                } else {
                    nullMessages.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter = new MessagesMainAdapter(arrayList, requireActivity());
        recyclerView.setAdapter(adapter);
    }
}
