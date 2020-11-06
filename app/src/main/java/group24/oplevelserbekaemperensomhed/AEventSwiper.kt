package group24.oplevelserbekaemperensomhed

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import group24.oplevelserbekaemperensomhed.data.DummyData
import group24.oplevelserbekaemperensomhed.data.EventDTO
import group24.oplevelserbekaemperensomhed.profile.AProfile


class AEventSwiper : AppCompatActivity(), EventItemClickListener {
    private lateinit var viewPager: ViewPager2
    private lateinit var rewindButton: FloatingActionButton
    private lateinit var profileButton: FloatingActionButton
    private lateinit var infoTextView: TextView
    private lateinit var infoTextArrow: ImageView
    private lateinit var settingsButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aeventswiper)

        val dummyData = DummyData()

        val adapter = EventsAdapter(dummyData.list,this)

        viewPager = findViewById(R.id.view_pager_main)
        viewPager.adapter = adapter

        rewindButton = findViewById(R.id.rewind_action_button)
        infoTextView = findViewById(R.id.infoTextView)
        infoTextArrow = findViewById(R.id.infoTextArrow)

        profileButton = findViewById(R.id.profile_action_button);
        profileButton.setOnClickListener {
            var intent = Intent(this, AProfile::class.java)
            startActivity(intent)
        }

        settingsButton = findViewById(R.id.settings_action_button)
        settingsButton.setOnClickListener {
            var intent = Intent(this, Indstillinger::class.java)
            startActivity(intent)
        }


    }


    override fun onEventItemClick(position: Int, event: EventDTO, title: View) {
        var intent = Intent(this, AEventInfo::class.java).apply {
            putExtra("position", position)
        }

        startActivity(intent)
    }
}
