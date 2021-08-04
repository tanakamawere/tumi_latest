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

public class ReceivedMessagesAdapter extends RecyclerView.Adapter<ReceivedMessagesAdapter.ReceivedMessagesViewHolder> {

    private final ArrayList<MessageObject> arrayList;

    public ReceivedMessagesAdapter(ArrayList<MessageObject> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ReceivedMessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_received_message, parent, false);
        return new ReceivedMessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceivedMessagesViewHolder holder, int position) {
        MessageObject object = arrayList.get(position);

        holder.text.setText(object.getMessageText());
        holder.time.setText(object.getTime());
        holder.date.setText(object.getDate());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ReceivedMessagesViewHolder extends RecyclerView.ViewHolder {
        private final TextView time;
        private final TextView date;
        private final TextView text;

        public ReceivedMessagesViewHolder(@NonNull View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.receivedMessageTime);
            date = itemView.findViewById(R.id.receivedMessageDate);
            text = itemView.findViewById(R.id.receivedMessageText);
        }
    }
}
