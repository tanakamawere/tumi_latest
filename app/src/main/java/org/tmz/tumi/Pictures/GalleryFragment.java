package org.tmz.tumi.Pictures;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.tmz.tumi.Main.Account.FragmentAccount;
import org.tmz.tumi.Main.Explore.FragmentCreateAd;
import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FilePaths;
import org.tmz.tumi.Utils.FileSearch;
import org.tmz.tumi.Utils.GridImageAdapter;
import org.tmz.tumi.Utils.UniversalImageLoader;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    private static final String TAG = "GalleryFragment";
    private static final int NUM_GRID_COLUMNS = 5;
    public String mAppend = "file:/";
    private GridView gridView;
    private ImageView imageView;
    private ImageButton cancelButton;
    private Spinner fileSpinner;
    private Button shareButton;
    private ArrayList<String> directories;
    private String mSelected;
    private String fromLocation, stockSelected;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cancelButton = view.findViewById(R.id.cancelPictureButton);
        fileSpinner = view.findViewById(R.id.filePaths);
        shareButton = view.findViewById(R.id.finishTextView);
        directories = new ArrayList<>();

        gridView = view.findViewById(R.id.gridViewGallery);
        imageView = view.findViewById(R.id.galleryImageView);
        init();
        initImageLoader();

        //get intent extras of where I am coming from
        final Intent intentLocate = requireActivity().getIntent();
        fromLocation = intentLocate.getStringExtra(getString(R.string.comingFromWhere));
        stockSelected = intentLocate.getStringExtra(getString(R.string.selectedStockExtra));

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), FragmentAccount.class));
                requireActivity().finishAffinity();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromLocation.equals(getString(R.string.comingFromAd))) {
                    Fragment fragment = new FragmentCreateAd();
                    Log.e(TAG, "onClick: Show that I clicked: True.");
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    Bundle bundle = new Bundle();
                    bundle.putString(getString(R.string.selectedImage), mSelected);
                    bundle.putString(getString(R.string.selectedStockAd), stockSelected);
                    bundle.putString(getString(R.string.fromWhere), getString(R.string.selectedPictureAdvertise));

                    fragment.setArguments(bundle);

                    fragmentManager.beginTransaction().replace(R.id.frameLayoutPicture, fragment)
                            .addToBackStack(null)
                            .commit();
                } else if (fromLocation.equals(getString(R.string.comingFromAccount))) {
                    Intent intent1 = new Intent(v.getContext(), UploadPictureActivity.class);
                    intent1.putExtra(getString(R.string.selectedImage), mSelected);
                    startActivity(intent1);
                }
            }
        });

    }
    //Methods to select the image for profile picture

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(requireActivity());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    public void init() {
        FilePaths filePaths = new FilePaths();
        if (FileSearch.getDirectoryPaths(filePaths.PICTURES) != null) {
            directories = FileSearch.getDirectoryPaths(filePaths.PICTURES);
        }

        ArrayList<String> directoryNames = new ArrayList<>();
        directories.add(filePaths.CAMERA);

        for (int i = 0; i < directories.size(); i++) {
            int index = directories.get(i).lastIndexOf("/");
            String string = directories.get(i).substring(index);
            directoryNames.add(string);
        }

        ArrayAdapter<String> arrayAdapter = new
                ArrayAdapter<String>(requireActivity(),
                R.layout.spinner_item, directoryNames);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        fileSpinner.setAdapter(arrayAdapter);

        fileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setUpGridView(directories.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setUpGridView(String selectedDirectory) {
        Log.e(TAG, "setUpGridView: directory chosen" + selectedDirectory);
        final ArrayList<String> imgUrl = FileSearch.getFilePaths(selectedDirectory);

        //Settint the grid image width
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth / NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        //Use the grid adapter to adapt images to image view
        GridImageAdapter gridImageAdapter = new GridImageAdapter(getActivity(), R.layout.view_holder_explore_image_view, mAppend, imgUrl);
        gridView.setAdapter(gridImageAdapter);


        try {
            setImage(imgUrl.get(0), imageView, mAppend);
            mSelected = imgUrl.get(0);
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(getActivity(), "There's no file in that directory.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "setUpGridView: That pesky error messing up with the Picture Activity.");
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setImage(imgUrl.get(position), imageView, mAppend);
                mSelected = imgUrl.get(position);
            }
        });
    }

    public void setImage(String imgURL, ImageView image, String append) {
        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(append + imgURL, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }
}
