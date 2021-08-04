package org.tmz.tumi.Main.Explore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FirebaseMethods;

public class FragmentAddService extends Fragment {
    private static final String TAG = "FragmentYouProducts";

    public FirebaseMethods firebaseMethods;

    private String[] serviceCategory;
    private Spinner addSpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_service, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        addSpinner = view.findViewById(R.id.addServiceSpinner);

        serviceCategory = new String[]{"Catering",
                "Cleaning",
                "Hairdressing",
                "Barber",
                "Plumbing",
                "Landscaping",
                "Rentals",
                "Legal Services",
                "Photography & Media",
                "Printing",
                "Other"};

    }
}
