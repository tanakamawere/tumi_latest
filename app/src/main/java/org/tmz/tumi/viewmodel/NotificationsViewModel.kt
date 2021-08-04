package org.tmz.tumi.viewmodel

import android.content.Context
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import org.tmz.tumi.Adapters.NotificationsAdapter
import org.tmz.tumi.Objects.NotificationObject
import org.tmz.tumi.R
import org.tmz.tumi.Utils.FirebaseMethods

class NotificationsViewModel(
    var context: Context,
    var progressBar: ProgressBar,
    var nullText: TextView,
    var recyclerView: RecyclerView
) : ViewModel() {

    private lateinit var arrayList: ArrayList<NotificationObject>

    fun notificationsRecyclerView() {
        val adapter = NotificationsAdapter(context)

        val nDB = FirebaseMethods().getDatabaseReference()
            .child(context.getString(R.string.fbTumiInfo))
            .child(context.getString(R.string.fbNotifications))
        nDB.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    progressBar.visibility = View.GONE
                    var title = ""
                    var quantity = ""
                    var key = ""
                    var description = ""
                    var date = ""
                    var time = ""
                    for (dataSnapshot in snapshot.children) {
                        title = dataSnapshot.child("title").value.toString()
                        quantity = dataSnapshot.child("quantity").value.toString()
                        key = dataSnapshot.child("key").value.toString()
                        description = dataSnapshot.child("description").value.toString()
                        date = dataSnapshot.child("date").value.toString()
                        time = dataSnapshot.child("time").value.toString()
                        val `object` =
                            NotificationObject(title, quantity, key, description, date, time)
                        arrayList.add(`object`)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    nullText.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        adapter.setData(arrayList)
        recyclerView.adapter = adapter
    }
}