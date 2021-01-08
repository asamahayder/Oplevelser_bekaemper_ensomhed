package group24.oplevelserbekaemperensomhed.data;

import java.util.ArrayList;
import java.util.List;

import group24.oplevelserbekaemperensomhed.search.SearchHomeItemHorizontal;
import group24.oplevelserbekaemperensomhed.search.SearchHomeItemVertical;

public class DummyData {

    ArrayList<EventDTO> eventList = new ArrayList<>();
    List<SearchHomeItemVertical> searchHomeList = new ArrayList<>();
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

        ArrayList<String> event1Pictures = new ArrayList<>();
        event1Pictures.add("https://www.tasteofhome.com/wp-content/uploads/2018/01/One-Pot-Dinner_EXPS_OPBZ18_10388_E06_07_2b-1-696x696.jpg");

        ArrayList<String> event2Pictures = new ArrayList<>();
        event2Pictures.add("https://image-cdn.essentiallysports.com/wp-content/uploads/20200519223847/rl_platform_keyart_2019.309bf22bd29c2e411e9dd8eb07575bb1-1600x900.jpg");
        event2Pictures.add("https://rocketleague.media.zestyio.com/rl_cross-play_asset_rl_1920.309bf22bd29c2e411e9dd8eb07575bb1.jpg");
        event2Pictures.add("https://rocketleague.media.zestyio.com/rl_s2_core_1920x1080_no-logos.jpg");


        ArrayList<String> event3Pictures = new ArrayList<>();
        event3Pictures.add("https://upload.wikimedia.org/wikipedia/commons/6/6b/Roskilde_Cathedral_aerial.jpg");


        EventDTO event1 = new EventDTO(user, participants, "Come and grab something to eat with us", "Group Dinner", new DateDTO(8,8,2020),"Food", "Denmark", "5-15$",event1Pictures);
        EventDTO event2 = new EventDTO(user, participants, "Lets play some rocket league please", "Rocket League", new DateDTO(8,8,2020),"Entertainment", "Gaming Nation", "Free",event2Pictures);
        EventDTO event3 = new EventDTO(user, participants, "Discover Roskilde with the bois", "Roskilde Adventure ", new DateDTO(8,8,2020),"City", "Denmark", "Free",event3Pictures);


        eventList.add(event1);
        eventList.add(event2);
        eventList.add(event3);

        for (int i = 0; i < 12; i++) {
            String category = "";
            if (i < 5) category = "ONE";
            else if (i < 10) category = "TWO";
            else category = "THREE";
            for (int j = 0; j < 6; j++) {
                EventDTO eventN = new EventDTO(user,participants,"TESTING","TESTING",new DateDTO(12,12,2021),category,"FAROE", "1M gp", event2Pictures);
                eventList.add(eventN);
            }
        }
        for (int i = 0; i < eventList.size(); i++) {
            List<SearchHomeItemHorizontal> searchHomeListHorizontal = new ArrayList<>();
            String category = eventList.get(i).getCategory();
            boolean test2 = false;
            for (int j = 0; j < eventList.size(); j++) {
                String nextCategory = eventList.get(j).getCategory();
                EventDTO event = eventList.get(i);
                SearchHomeItemHorizontal item = new SearchHomeItemHorizontal(event.getEventTitle(), event.getEventCreator().getName(), event.getAddress(), event.getPictures().get(0), event.getEventCreator().getProfilePictures().get(0));
                if (category.equals(nextCategory)) {
                    boolean test = true;
                    for (int k = 0; k < searchHomeList.size(); k++) {
                        if (searchHomeList.get(k).getCategory().equals(category)){
                            test = false;
                            break;
                        }
                    }
                    if (test || searchHomeList.size() == 0) {
                        test2 = true;
                        searchHomeListHorizontal.add(item);
                    }
                }
            }
            if (test2){
                SearchHomeItemVertical items = new SearchHomeItemVertical(category, searchHomeListHorizontal);
                searchHomeList.add(items);
            }
        }
        System.out.println("asd");
    }

    public List<SearchHomeItemVertical> getSearchHomeList() { return searchHomeList; }

    public ArrayList<EventDTO> getList(){
        return eventList;
    }

}
