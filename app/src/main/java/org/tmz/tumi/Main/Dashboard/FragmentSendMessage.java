package org.tmz.tumi.Main.Dashboard;

import android.os.Bundle;
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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.tmz.tumi.Adapters.SentMessagesAdapter;
import org.tmz.tumi.Main.Explore.FragmentViewBusiness;
import org.tmz.tumi.Objects.MessageObject;
import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FirebaseMethods;
import org.tmz.tumi.Utils.Miscellaneous;
import org.tmz.tumi.Utils.UniversalImageLoader;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentSendMessage extends Fragment {

    private static final String TAG = "FragmentSendMessage";

    private TextView messageTitle, nullMessages;
    private TextInputEditText messageET;
    private ImageButton messageSend;
    private CircleImageView circleImageView;
    private String selectedBusiness, fromWhere, messageUID, selectedBusinessName, selectedBusinessImageURL, senderBusinessName;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;
    private FirebaseMethods firebaseMethods;
    private ArrayList<MessageObject> sentMessageArrayList, receivedMessagesArrayList;
    private RecyclerView recyclerView;
    private SentMessagesAdapter sentMessagesAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_send_message, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        assert bundle != null;
        selectedBusiness = bundle.getString(getString(R.string.selectedBusiness));
        fromWhere = bundle.getString(getString(R.string.fromWhere));

        circleImageView = view.findViewById(R.id.sendMessageProfilePictureTextView);
        messageET = view.findViewById(R.id.sendMessageEditText);
        messageSend = view.findViewById(R.id.sendMessageButton);
        messageTitle = view.findViewById(R.id.sendMessageTitleTextView);
        nullMessages = view.findViewById(R.id.noMessagesTextView);
        nullMessages.setVisibility(View.GONE);
        progressBar = view.findViewById(R.id.progressBarMessages);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseMethods = new FirebaseMethods();
        recyclerView = view.findViewById(R.id.recyclerViewSendMessages);

        sentMessageArrayList = new ArrayList<>();
        receivedMessagesArrayList = new ArrayList<>();

        messageUID = firebaseMethods.getDatabaseReference().child(getString(R.string.fbMessages)).child(getString(R.string.fbMessagesNode))
                .child(selectedBusiness).child(getString(R.string.fbSent))
                .push().getKey();

        //On Click Listeners
        messageTitle.setOnClickListener(v -> {
            Fragment fragment = new FragmentViewBusiness();
            Bundle bundle1 = new Bundle();
            bundle1.putString(getString(R.string.selectedBusiness), selectedBusiness);
            fragment.setArguments(bundle1);
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

            if (fromWhere.equals(getString(R.string.dashboardActivity))) {
                fragmentManager.beginTransaction().setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out).replace(R.id.frameLayoutDashboard, fragment)
                        .addToBackStack(null)
                        .commit();
            } else if (fromWhere.equals(getString(R.string.exploreActivity))) {
                fragmentManager.beginTransaction().setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out).replace(R.id.frameLayoutExplore, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        //OnClick Listeners
        messageSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!new Miscellaneous().internetAvailable(requireActivity())) {
                    if (!messageET.getText().toString().isEmpty()) {
                        sendMessage();
                        messageET.getText().clear();
                    } else {
                        Toast.makeText(requireActivity(), "Type a message", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireActivity(), "There is no available internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        populatePage();
        getMyBusinessName();
    }

    private void getMyBusinessName() {
        new FirebaseMethods().getBusinessInfoDB().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    progressBar.setVisibility(View.GONE);

                    senderBusinessName = snapshot.child("Name").getValue().toString();
                } else {
                    Toast.makeText(requireActivity(), "Advertising business not listed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        getMessages();
    }

    private void sendMessage() {
        Miscellaneous miscellaneous = new Miscellaneous();
        //Creating the chat
        firebaseMethods.getDatabaseReference().child(getString(R.string.fbMessages)).child(getString(R.string.fbMessagesDetails))
                .child(selectedBusiness).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("name", selectedBusinessName);
                    hashMap.put("key", selectedBusiness);
                    hashMap.put("profilePicture", selectedBusinessImageURL);

                    firebaseMethods.getDatabaseReference().child(getString(R.string.fbMessages)).child(getString(R.string.fbMessagesDetails))
                            .child(selectedBusiness).updateChildren(hashMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        HashMap<String, Object> senderMap = new HashMap<>();
        senderMap.put("messageText", messageET.getText().toString());
        senderMap.put("messageKey", messageUID);
        senderMap.put("senderName", senderBusinessName);
        senderMap.put("date", miscellaneous.getDate());
        senderMap.put("time", miscellaneous.getTime());
        senderMap.put("senderKey", firebaseMethods.getUserID());
        senderMap.put("recipientKey", selectedBusiness);

        HashMap<String, Object> receiverMap = new HashMap<>();
        receiverMap.put("messageText", messageET.getText().toString());
        receiverMap.put("messageKey", messageUID);
        receiverMap.put("date", miscellaneous.getDate());
        receiverMap.put("time", miscellaneous.getTime());
        receiverMap.put("senderKey", selectedBusiness);
        receiverMap.put("recipientKey", firebaseMethods.getUserID());

        //Sender database
        firebaseMethods.getDatabaseReference().child(getString(R.string.fbMessages))
                .child(getString(R.string.fbMessagesNode))
                .child(selectedBusiness).child(getString(R.string.fbSent))
                .child(messageUID).updateChildren(senderMap);
        //Receiver database
        databaseReference.child(getString(R.string.fbUsers))
                .child(selectedBusiness)
                .child(getString(R.string.fbMessages))
                .child(getString(R.string.fbMessagesNode))
                .child(firebaseMethods.getUserID()).child(getString(R.string.fbReceived))
                .child(messageUID).updateChildren(receiverMap);
    }

    private void populatePage() {
        databaseReference.child(getString(R.string.fbBusinesses)).child(selectedBusiness).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    progressBar.setVisibility(View.GONE);

                    selectedBusinessName = snapshot.child("Name").getValue().toString();
                    selectedBusinessImageURL = snapshot.child("profilePicture").getValue().toString();

                    messageTitle.setText(selectedBusinessName);
                    UniversalImageLoader.setImage(selectedBusinessImageURL, circleImageView, null, "");
                } else {
                    Toast.makeText(requireActivity(), "Advertising business not listed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        getMessages();
    }

    //Called from populate page
    private void getMessages() {
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));

        //Getting sent messages
        firebaseMethods.getDatabaseReference().child(getString(R.string.fbMessages))
                .child(getString(R.string.fbMessagesNode))
                .child(selectedBusiness).child(getString(R.string.fbSent)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String text = "", date = "", time = "", key = "";

                        text = dataSnapshot.child("messageText").getValue().toString();
                        date = dataSnapshot.child("date").getValue().toString();
                        if (date.equals(new Miscellaneous().getDate())) {
                            date = "Today";
                        }

                        time = dataSnapshot.child("time").getValue().toString();
                        key = dataSnapshot.child("messageKey").getValue().toString();

                        MessageObject messageObject = new MessageObject(text, key, date, time, null, null, selectedBusinessName);
                        sentMessageArrayList.add(messageObject);
                        sentMessagesAdapter.notifyDataSetChanged();
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

        sentMessagesAdapter = new SentMessagesAdapter(sentMessageArrayList);
        recyclerView.setAdapter(sentMessagesAdapter);
    }
}
