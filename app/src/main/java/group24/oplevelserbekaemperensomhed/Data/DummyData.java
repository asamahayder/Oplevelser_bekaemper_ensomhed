package group24.oplevelserbekaemperensomhed.Data;

import java.util.ArrayList;

public class DummyData {

    ArrayList<Event> eventList = new ArrayList<>();

    public DummyData() {
        UserDTO user = new UserDTO("Asama", 23, "Danmark", "Student", "DTU", "Just a random dude", "Male", null);
        Event event1 = new Event(user, null, "Come and grab something to eat with us", "Group Dinner", new DateDTO(8,8,2020),"Food", "Denmark", "5-15$","https://www.tasteofhome.com/wp-content/uploads/2018/01/One-Pot-Dinner_EXPS_OPBZ18_10388_E06_07_2b-1-696x696.jpg");

        eventList.add(event1);
    }

    public ArrayList<Event> getList(){
        return eventList;
    }

}
