package org.tmz.tumi.Adapters;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.tmz.tumi.Main.Dashboard.FragmentSendMessage;
import org.tmz.tumi.Main.Explore.ExploreActivity;
import org.tmz.tumi.Objects.RequestObject;
import org.tmz.tumi.R;

import java.util.ArrayList;

public class RequestsPublicAdapter extends RecyclerView.Adapter<RequestsPublicAdapter.StoryViewHolder> {

    private static final String TAG = "RequestsPublicAdapter";

    ArrayList<RequestObject> arrayList;

    public RequestsPublicAdapter(ArrayList<RequestObject> requestObjects) {
        this.arrayList = requestObjects;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_requests, parent, false);
        return new RequestsPublicAdapter.StoryViewHolder(myView);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, final int position) {
        final RequestObject storyObject = arrayList.get(position);

        holder.title.setText(storyObject.getStoryTitle());
        holder.description.setText(storyObject.getStoryDescription());
        holder.body.setText(storyObject.getStoryBody());
        holder.date.setText(storyObject.getStoryDate());
        holder.time.setText(storyObject.getStoryTime());
        holder.writer.setText("By " + storyObject.getStoryWriter());

        holder.optionsRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle(v.getContext().getString(R.string.whatDoYouWant));
                CharSequence[] options = new CharSequence[]{v.getContext().getString(R.string.sendMessage), v.getContext().getString(R.string.report)};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Fragment fragment = new FragmentSendMessage();
                            Bundle bundle = new Bundle();
                            bundle.putString(v.getContext().getString(R.string.selectedBusiness), storyObject.getRequesterUID());
                            fragment.setArguments(bundle);
                            ((ExploreActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                                    .replace(R.id.frameLayoutExplore, fragment)
                                    .addToBackStack(null)
                                    .commit();
                        } else if (which == 1) {
                            //TODO
                            //admin control node
                            Toast.makeText(v.getContext(), v.getContext().getString(R.string.complaintsReply), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void filterList(ArrayList<RequestObject> arraySearch) {
        arrayList = arraySearch;
        notifyDataSetChanged();
    }

    public class StoryViewHolder extends RecyclerView.ViewHolder {
        public TextView title, description, body, date, time, writer;
        public ImageButton optionsRequests;

        public StoryViewHolder(View view) {
            super(view);

            title = view.findViewById(R.id.pingShowTitleTextView);
            description = view.findViewById(R.id.pingShowDescriptionTextView);
            body = view.findViewById(R.id.pingShowBodyTextView);
            date = view.findViewById(R.id.pingShowDateTextView);
            time = view.findViewById(R.id.pingShowTimeTextView);
            writer = view.findViewById(R.id.pingShowWriterTextView);

            optionsRequests = view.findViewById(R.id.globalRequestsButton);
        }
    }
}
