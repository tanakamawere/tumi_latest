package org.tmz.tumi.Finances;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;

import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FirebaseMethods;

import java.util.HashMap;

public class FragmentNewExpenseType extends BottomSheetDialogFragment {

    private TextInputEditText type;
    private String typeID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_expense_type, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        typeID = new FirebaseMethods().getDatabaseReference().child(getString(R.string.fbTumiInfo))
                .child(getString(R.string.fbFinances))
                .child("Expenses")
                .child("Expense Types").push().getKey();

        Button confirmBtn = view.findViewById(R.id.newExpenseBtn);
        type = view.findViewById(R.id.newExpenseTypeEditText);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInfo();
            }
        });
    }

    private void updateInfo() {
        DatabaseReference databaseReference = new FirebaseMethods().getDatabaseReference().child(getString(R.string.fbTumiInfo))
                .child(getString(R.string.fbFinances))
                .child("Finances")
                .child("Expenses")
                .child("Expense Types")
                .child(typeID);

        HashMap<String, Object> map = new HashMap<>();
        map.put("type", type.getText().toString());

        databaseReference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getContext(), "Uploaded new type successfully. Now you can enter the expense.", Toast.LENGTH_LONG).show();
                dismiss();
            }
        });
    }
}
