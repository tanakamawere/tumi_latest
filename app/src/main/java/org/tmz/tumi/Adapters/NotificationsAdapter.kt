package org.tmz.tumi.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import org.tmz.tumi.Main.Dashboard.DashboardActivity
import org.tmz.tumi.Objects.NotificationObject
import org.tmz.tumi.R
import org.tmz.tumi.Stock.StockActivity
import org.tmz.tumi.Utils.FirebaseMethods

class NotificationsAdapter(var context: Context) :
    RecyclerView.Adapter<NotificationsAdapter.NotificationsViewHolder>() {

    private var list = emptyList<NotificationObject>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_holder_notifications, parent, false)
        return NotificationsViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NotificationsViewHolder, position: Int) {
        val `object` = list[position]

        holder.time.text = `object`.time
        holder.date.text = `object`.date
        holder.description.text = `object`.description
        if (`object`.description == context.getString(R.string.lowStock)) {
            holder.text.text = "Seems like stock for " + `object`.title + " is getting" +
                    " low. There are " + `object`.quantity + " items left."
        } else {
            holder.text.text =
                ("Someone has taken your number. They were viewing your product title "
                        + `object`.title)
        }
        holder.itemView.setOnClickListener { v ->
            val builder = AlertDialog.Builder(v.context)
            val options = arrayOf<CharSequence>(
                "Go to stock", "Remove notification"
            )
            builder.setTitle("What do you want to do?")
            builder.setItems(options) { dialog, which ->
                if (which == 0) {
                    context.startActivity(Intent(context, StockActivity::class.java))
                } else if (which == 1) {
                    val nDB = FirebaseMethods().getDatabaseReference()
                        .child(context.getString(R.string.fbTumiInfo))
                        .child(context.getString(R.string.fbNotifications))
                        .child(`object`.key)
                    nDB.removeValue().addOnSuccessListener {
                        Toast.makeText(context, "Notification removed", Toast.LENGTH_SHORT).show()
                        context.startActivity(Intent(context, DashboardActivity::class.java))
                    }.addOnFailureListener {
                        Toast.makeText(
                            context,
                            "Failed to remove notification",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            builder.show()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class NotificationsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView
        val description: TextView
        val time: TextView
        val date: TextView

        init {
            description = itemView.findViewById(R.id.notificationsDescriptionTextView)
            text = itemView.findViewById(R.id.notificationsTextView)
            time = itemView.findViewById(R.id.notificationsTimeTextView)
            date = itemView.findViewById(R.id.notificationsDateTextView)
        }
    }

    fun setData(list: List<NotificationObject>) {
        this.list = list
        notifyDataSetChanged()
    }
}