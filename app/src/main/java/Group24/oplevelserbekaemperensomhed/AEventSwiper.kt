package group24.oplevelserbekaemperensomhed

import Group24.oplevelserbekaemperensomhed.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import com.yuyakaido.android.cardstackview.*


class AEventSwiper : AppCompatActivity(), CardStackListener {

    private val cardStackView by lazy { findViewById<CardStackView>(R.id.card_stack_view) }
    private val manager by lazy { CardStackLayoutManager(this, this) }
    private val adapter by lazy { CardStackAdapter(createSpots()) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aeventswiper)
        initialize()

        val cardStackView = findViewById<CardStackView>(R.id.card_stack_view)
        cardStackView.layoutManager = CardStackLayoutManager



    }

    private fun initialize() {
        manager.setStackFrom(StackFrom.None)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
        cardStackView.layoutManager = manager
        cardStackView.adapter = adapter
        cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }

    override fun onCardDisappeared(view: View?, position: Int) {
        TODO("Not yet implemented")
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
        TODO("Not yet implemented")
    }

    override fun onCardSwiped(direction: Direction?) {
        TODO("Not yet implemented")
    }

    override fun onCardCanceled() {
        TODO("Not yet implemented")
    }

    override fun onCardAppeared(view: View?, position: Int) {
        TODO("Not yet implemented")
    }

    override fun onCardRewound() {
        TODO("Not yet implemented")
    }


}
