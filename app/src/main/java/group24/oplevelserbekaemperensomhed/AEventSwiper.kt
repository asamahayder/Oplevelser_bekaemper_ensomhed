package group24.oplevelserbekaemperensomhed

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yuyakaido.android.cardstackview.*
import group24.oplevelserbekaemperensomhed.data.DummyData
import kotlinx.android.synthetic.main.aeventswiper.*


class AEventSwiper : AppCompatActivity(), CardStackListener {

    private val adapter = EventsAdapter()
    private lateinit var layoutManager: CardStackLayoutManager
    private lateinit var cardStackView: CardStackView
    private lateinit var rewindButton: FloatingActionButton
    private lateinit var profileButton: FloatingActionButton
    private lateinit var infoTextView: TextView
    private lateinit var infoTextArrow: ImageView
    private lateinit var settingsButton: FloatingActionButton
    private var currentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)
        setContentView(R.layout.aeventswiper)

        layoutManager = CardStackLayoutManager(this, this).apply {
            setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
            setOverlayInterpolator(LinearInterpolator())
            setStackFrom(StackFrom.Top)
            setMaxDegree(0.0f)

            setDirections(listOf(Direction.Top, Direction.Right, Direction.Left))

        }

        stack_view.layoutManager = layoutManager
        stack_view.adapter = adapter
        stack_view.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }

        val dummyData = DummyData()
        adapter.setEvents(dummyData.list)

        cardStackView = findViewById(R.id.stack_view)
        rewindButton = findViewById(R.id.rewind_action_button)
        infoTextView = findViewById(R.id.infoTextView)
        infoTextArrow = findViewById(R.id.infoTextArrow)
        rewindButton.setOnClickListener{
            val setting = RewindAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(DecelerateInterpolator())
                .build()
            layoutManager.setRewindAnimationSetting(setting)
            cardStackView.rewind()
        }

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

    override fun onCardDisappeared(view: View?, position: Int) {
        if (position == layoutManager.itemCount-1){
            infoTextView.visibility = View.INVISIBLE
            infoTextArrow.visibility = View.INVISIBLE
        }
        setInfoAndArrowVisibility(position)

    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
    }

    override fun onCardSwiped(direction: Direction?) {
        if (direction == Direction.Top){
            val intent = Intent(this, AEventInfo::class.java)
            intent.putExtra("position", currentPosition)
            startActivity(intent)
        }


    }

    override fun onCardCanceled() {

    }

    override fun onCardAppeared(view: View?, position: Int) {
        infoTextView.visibility = View.VISIBLE
        infoTextArrow.visibility = View.VISIBLE
        currentPosition = position
        if (position == 0){
            rewindButton.visibility = View.INVISIBLE
        }else{
            rewindButton.visibility = View.VISIBLE
        }

    }

    override fun onCardRewound() {

    }

    fun setInfoAndArrowVisibility(position: Int){
        if (position == layoutManager.childCount+1){
            infoTextView.visibility = View.INVISIBLE
            infoTextArrow.visibility = View.INVISIBLE
        }else{
            infoTextView.visibility = View.VISIBLE
            infoTextArrow.visibility = View.VISIBLE
        }
    }
}
