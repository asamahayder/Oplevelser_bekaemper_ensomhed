package group24.oplevelserbekaemperensomhed;

import android.view.View;

import group24.oplevelserbekaemperensomhed.data.EventDTO;

public interface EventItemClickListener {
    void onEventItemClick(int position, EventDTO event, View title);
}
