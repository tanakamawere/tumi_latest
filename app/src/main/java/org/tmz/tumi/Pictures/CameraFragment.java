package org.tmz.tumi.Pictures;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.tmz.tumi.Main.Explore.FragmentCreateAd;
import org.tmz.tumi.R;

import java.util.Objects;

public class CameraFragment extends Fragment {
    private static final String TAG = "CameraFragment";
    private static final int CAMERA_REQUEST_CODE = 263;
    private Button openCamera;
    private String fromLocation, stockSelected, mSelected;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //get intent extras of where I am coming from
        final Intent intentLocate = requireActivity().getIntent();
        fromLocation = intentLocate.getStringExtra(getString(R.string.comingFromWhere));
        stockSelected = intentLocate.getStringExtra(getString(R.string.selectedStockExtra));

        openCamera = view.findViewById(R.id.openCameraButton);
        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bitmap;
        assert data != null;
        bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");

        if (requestCode == CAMERA_REQUEST_CODE) {

            if (fromLocation.equals(getString(R.string.comingFromAd))) {
                Fragment fragment = new FragmentCreateAd();
                Log.e(TAG, "onClick: Show that I clicked: True.");
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putParcelable(getString(R.string.selectedBitmap), bitmap);
                bundle.putString(getString(R.string.selectedStockAd), stockSelected);

                fragment.setArguments(bundle);

                fragmentManager.beginTransaction().replace(R.id.frameLayoutPicture, fragment)
                        .addToBackStack(null)
                        .commit();
            } else if (fromLocation.equals(getString(R.string.comingFromAccount))) {
                try {
                    Intent intent1 = new Intent(requireActivity(), UploadPictureActivity.class);
                    intent1.putExtra(getString(R.string.selectedBitmap), bitmap);
                    startActivity(intent1);
                } catch (NullPointerException e) {
                    Log.e(TAG, "onActivityResult: " + e.getMessage());
                }
            }
        }

    }
}
