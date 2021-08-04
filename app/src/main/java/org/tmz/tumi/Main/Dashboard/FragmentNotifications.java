package org.tmz.tumi.Main.Dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.tmz.tumi.Objects.NotificationObject;
import org.tmz.tumi.R;

import java.util.ArrayList;

public class FragmentNotifications extends Fragment {
    //Notifications recycler view
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private ArrayList<NotificationObject> arrayList;
    private ProgressBar progressBar;
    private TextView nullNotifications;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewNotifications);
        arrayList = new ArrayList<>();
        progressBar = view.findViewById(R.id.progressBarNotifications);
        nullNotifications = view.findViewById(R.id.nullNotifications);
        nullNotifications.setVisibility(View.GONE);

    }
}
