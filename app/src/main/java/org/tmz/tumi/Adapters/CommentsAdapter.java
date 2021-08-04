package org.tmz.tumi.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.tmz.tumi.Objects.CommentObject;
import org.tmz.tumi.R;

import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.commentVHsViewHolder> {

    private final ArrayList<CommentObject> arrayList;

    public CommentsAdapter(ArrayList<CommentObject> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public commentVHsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_sent_message, parent, false);
        return new commentVHsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull commentVHsViewHolder holder, int position) {
        CommentObject object = arrayList.get(position);

        holder.text.setText(object.getText());
        holder.time.setText(object.getTime());
        holder.date.setText(object.getDate());
        holder.name.setText(object.getCommenter());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class commentVHsViewHolder extends RecyclerView.ViewHolder {
        private final TextView time;
        private final TextView date;
        private final TextView text;
        private final TextView name;

        public commentVHsViewHolder(@NonNull View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.commentVHTime);
            date = itemView.findViewById(R.id.commentVHDate);
            text = itemView.findViewById(R.id.commentVHText);
            name = itemView.findViewById(R.id.commentVHName);
        }
    }
}
