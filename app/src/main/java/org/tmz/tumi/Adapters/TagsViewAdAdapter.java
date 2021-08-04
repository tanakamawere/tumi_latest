package org.tmz.tumi.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.tmz.tumi.R;

import java.util.List;

public class TagsViewAdAdapter extends RecyclerView.Adapter<TagsViewAdAdapter.TagsViewHolder> {

    List<String> arrayList;

    public TagsViewAdAdapter(List<String> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public TagsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_tags_chips, parent, false);

        return new TagsViewAdAdapter.TagsViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final TagsViewHolder holder, final int position) {
        holder.tagText.setText(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class TagsViewHolder extends RecyclerView.ViewHolder {
        public TextView tagText;

        public TagsViewHolder(View view) {
            super(view);

            tagText = view.findViewById(R.id.chipTextView);
        }
    }
}
