package group24.oplevelserbekaemperensomhed

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import group24.oplevelserbekaemperensomhed.data.DateDTO
import group24.oplevelserbekaemperensomhed.data.DummyData
import kotlinx.android.synthetic.main.a_event_info.*
import org.w3c.dom.Text

lateinit var dummyData: DummyData
lateinit var eventNameTextView: TextView
lateinit var eventPlaceTextView: TextView
lateinit var eventPriceTextView: TextView
lateinit var eventTimeTextView: TextView
lateinit var eventBioTextView: TextView
lateinit var eventImage: SimpleDraweeView
lateinit var eventBackButton: ImageView

class AEventInfo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_event_info)

        eventNameTextView = findViewById(R.id.eventName)
        eventPlaceTextView = findViewById(R.id.aevent_info_address)
        eventPriceTextView = findViewById(R.id.aevent_info_price)
        eventTimeTextView = findViewById(R.id.aevent_info_time)
        eventBioTextView = findViewById(R.id.aevent_info_bio)
        eventImage = findViewById(R.id.eventPictureURL)
        eventBackButton = findViewById(R.id.aevent_info_backButton)

        dummyData = DummyData()
        var list = dummyData.list

        var position = intent.getIntExtra("position",0)

        var event = list[position]

        eventNameTextView.text = event.eventTitle
        eventPlaceTextView.text = event.address
        eventPriceTextView.text = event.price

        var dateObject: DateDTO = event.eventDate
        var date: String = dateObject.day.toString() + "/" + dateObject.month.toString() + "/" + dateObject.year.toString()
        eventTimeTextView.text = date

        eventBioTextView.text = event.eventDescription

        eventImage.setImageURI(event.pictureURL)

        aevent_info_backButton.setOnClickListener {
            finish()
        }


    }
}