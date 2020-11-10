package group24.oplevelserbekaemperensomhed.data;

import java.util.ArrayList;

public class DummyData {

    ArrayList<EventDTO> eventList = new ArrayList<>();
    LocalData localData = LocalData.INSTANCE;

    public DummyData() {
        ArrayList<EventDTO> events = new ArrayList<>();
        ArrayList<String> pfps = new ArrayList<>();
        pfps.add("https://alchetron.com/cdn/albrecht-thaer-3a110342-8ee9-462c-ade0-41fcadf6d35-resize-750.jpg");
        pfps.add("https://www.thestatesman.com/wp-content/uploads/2017/08/1493458748-beauty-face-517.jpg");
        pfps.add("https://goop.com/wp-content/uploads/2020/06/Mask-Group-2.png");
        UserDTO user = new UserDTO("Sarah", 23, "Danmark", "Student", "DTU", "Just a random woman", "Female", events, pfps);
        localData.setUserData(user);
        ArrayList<UserDTO> participants = new ArrayList<>();
        participants.add(user);
        EventDTO event1 = new EventDTO(user, participants, "Come and grab something to eat with us", "Group Dinner", new DateDTO(8,8,2020),"Food", "Denmark", "5-15$","https://www.tasteofhome.com/wp-content/uploads/2018/01/One-Pot-Dinner_EXPS_OPBZ18_10388_E06_07_2b-1-696x696.jpg");
        EventDTO event2 = new EventDTO(user, participants, "Lets play some rocket league please", "Rocket League", new DateDTO(8,8,2020),"Entertainment", "Gaming Nation", "Free","https://image-cdn.essentiallysports.com/wp-content/uploads/20200519223847/rl_platform_keyart_2019.309bf22bd29c2e411e9dd8eb07575bb1-1600x900.jpg");
        EventDTO event3 = new EventDTO(user, participants, "Discover Roskilde with the bois", "Roskilde Adventure ", new DateDTO(8,8,2020),"City", "Denmark", "Free","https://upload.wikimedia.org/wikipedia/commons/6/6b/Roskilde_Cathedral_aerial.jpg");


        eventList.add(event1);
        eventList.add(event2);
        eventList.add(event3);

    }

    public ArrayList<EventDTO> getList(){
        return eventList;
    }

}
