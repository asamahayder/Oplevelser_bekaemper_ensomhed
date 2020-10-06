package group24.oplevelserbekaemperensomhed

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class AEventInfo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_event_info)

        var position = intent.getIntExtra("position",0)
    }
}