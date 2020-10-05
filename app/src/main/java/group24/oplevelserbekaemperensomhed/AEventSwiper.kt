package group24.oplevelserbekaemperensomhed

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.recyclerview.widget.DefaultItemAnimator
import com.facebook.drawee.backends.pipeline.Fresco
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.SwipeableMethod
import group24.oplevelserbekaemperensomhed.Data.DummyData
import kotlinx.android.synthetic.main.aeventswiper.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AEventSwiper : AppCompatActivity(), CardStackListener {

    private val adapter = EventsAdapter()
    private lateinit var layoutManager: CardStackLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)
        setContentView(R.layout.aeventswiper)

        layoutManager = CardStackLayoutManager(this, this).apply {
            setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
            setOverlayInterpolator(LinearInterpolator())
        }

        stack_view.layoutManager = layoutManager
        stack_view.adapter = adapter
        stack_view.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }

        //This fetches the girl-profiles
        /*TinderAPI().getProfiles().enqueue(object : Callback<List<Profile>> {
            override fun onFailure(call: Call<List<Profile>>, t: Throwable) {

            }

            override fun onResponse(call: Call<List<Profile>>, response: Response<List<Profile>>) {
                response.body()?.let {
                    adapter.setProfiles(it)
                }
            }
        })*/

        val dummyData = DummyData()
        adapter.setProfiles(dummyData.list)
    }

    override fun onCardDisappeared(view: View?, position: Int) {

    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {

    }

    override fun onCardSwiped(direction: Direction?) {

    }

    override fun onCardCanceled() {

    }

    override fun onCardAppeared(view: View?, position: Int) {

    }

    override fun onCardRewound() {

    }
}
