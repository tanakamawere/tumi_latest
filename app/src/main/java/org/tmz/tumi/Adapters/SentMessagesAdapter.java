package org.tmz.tumi.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.tmz.tumi.Objects.MessageObject;
import org.tmz.tumi.R;

import java.util.ArrayList;

public class SentMessagesAdapter extends RecyclerView.Adapter<SentMessagesAdapter.SentMessagesViewHolder> {

    private final ArrayList<MessageObject> arrayList;

    public SentMessagesAdapter(ArrayList<MessageObject> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public SentMessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_sent_message, parent, false);
        return new SentMessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SentMessagesViewHolder holder, int position) {
        MessageObject object = arrayList.get(position);

        holder.text.setText(object.getMessageText());
        holder.time.setText(object.getTime());
        holder.date.setText(object.getDate());
        holder.name.setText(object.getSenderName());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class SentMessagesViewHolder extends RecyclerView.ViewHolder {
        private final TextView time;
        private final TextView date;
        private final TextView text;
        private final TextView name;

        public SentMessagesViewHolder(@NonNull View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.sentMessageTime);
            date = itemView.findViewById(R.id.sentMessageDate);
            text = itemView.findViewById(R.id.sentMessageText);
            name = itemView.findViewById(R.id.sentMessageName);
        }
    }
}
