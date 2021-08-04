package org.tmz.tumi.StartUp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.tmz.tumi.R;

import java.util.Objects;

public class FragmentCreateAccountError extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account_creation_failure, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button sendEmail = view.findViewById(R.id.sendEmailBtnAccountFailure);
        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);

                emailIntent.setType("message/rfc822");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"tanakamawere15@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Failure to create a Tumi Account");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");

                Objects.requireNonNull(getActivity()).startActivity(Intent.createChooser(emailIntent, "send"));
            }
        });
    }
}
