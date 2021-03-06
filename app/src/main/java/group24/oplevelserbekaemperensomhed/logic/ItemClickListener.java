package group24.oplevelserbekaemperensomhed.logic;

import android.view.View;

import group24.oplevelserbekaemperensomhed.data.EventDTO;
import group24.oplevelserbekaemperensomhed.data.UserDTO;

public interface ItemClickListener {
    void onEventItemClick(int position, EventDTO event, View title);
    void onProfileItemClick(int position, UserDTO user, View title);
}
