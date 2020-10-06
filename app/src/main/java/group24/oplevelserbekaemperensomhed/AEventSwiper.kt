package group24.oplevelserbekaemperensomhed

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.recyclerview.widget.DefaultItemAnimator
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yuyakaido.android.cardstackview.*
import group24.oplevelserbekaemperensomhed.data.DummyData
import kotlinx.android.synthetic.main.aeventswiper.*
import java.util.*


class AEventSwiper : AppCompatActivity(), CardStackListener {

    private val adapter = EventsAdapter()
    private lateinit var layoutManager: CardStackLayoutManager
    private lateinit var cardStackView: CardStackView
    private lateinit var rewindButton: FloatingActionButton
    private lateinit var profileButton: FloatingActionButton

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
        adapter.setProfiles(dummyData.list)

        cardStackView = findViewById(R.id.stack_view)
        rewindButton = findViewById(R.id.rewind_action_button)
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

    }

    override fun onCardDisappeared(view: View?, position: Int) {

    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
    }

    override fun onCardSwiped(direction: Direction?) {
        if (direction == Direction.Top){
            val currentPosition = cardStackView.top
            val intent = Intent(this, AEventInfo::class.java)
            intent.putExtra("position", currentPosition)
            startActivity(intent)
        }



    }

    override fun onCardCanceled() {

    }

    override fun onCardAppeared(view: View?, position: Int) {
        if (position == 0){
            rewindButton.visibility = View.INVISIBLE
        }else{
            rewindButton.visibility = View.VISIBLE
        }

    }

    override fun onCardRewound() {

    }
}
