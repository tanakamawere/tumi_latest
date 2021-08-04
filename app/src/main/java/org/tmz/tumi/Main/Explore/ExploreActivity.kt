package org.tmz.tumi.Main.Explore

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.nostra13.universalimageloader.core.ImageLoader
import de.hdodenhof.circleimageview.CircleImageView
import org.tmz.tumi.Objects.User
import org.tmz.tumi.R
import org.tmz.tumi.Utils.FirebaseMethods
import org.tmz.tumi.Utils.UniversalImageLoader

class ExploreActivity : AppCompatActivity() {
    private var profilePicture: CircleImageView? = null
    private var firebaseMethods: FirebaseMethods? = null

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore)

        setupActionBarWithNavController(findNavController(R.id.fragmentExploreController))

        profilePicture = findViewById(R.id.exploreProductsProfilePicture)
        firebaseMethods = FirebaseMethods()

        profilePicture?.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(R.id.frameLayoutExplore, FragmentYou())
                .addToBackStack(null)
                .commit()
        }

        //Methods
        initImageLoader()
        setProfileImage()
    }

    private fun initImageLoader() {
        val universalImageLoader = UniversalImageLoader(this)
        ImageLoader.getInstance().init(universalImageLoader.config)
    }

    private fun setProfileImage() {
        val proPic = firebaseMethods!!.getDatabaseReference()
        proPic.child(getString(R.string.fbUserInfo))
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)!!
                    val imgURL = user.profilePicture
                    UniversalImageLoader.setImage(imgURL, profilePicture, null, "")
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }
}