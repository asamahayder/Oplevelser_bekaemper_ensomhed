package group24.oplevelserbekaemperensomhed.data;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import group24.oplevelserbekaemperensomhed.logic.firebase.DBEvent;
import group24.oplevelserbekaemperensomhed.logic.firebase.DBUser;
import group24.oplevelserbekaemperensomhed.logic.firebase.FirebaseDAO;
import group24.oplevelserbekaemperensomhed.logic.firebase.MyCallBack;

public class DummyData {

    ArrayList<EventDTO> eventList = new ArrayList<>();
    List<SearchHomeItem> searchHomeList = new ArrayList<>();
    ArrayList<String> bannerList = new ArrayList<>();
    LocalData localData = LocalData.INSTANCE;

    public DummyData() {
        ArrayList<EventDTO> events = new ArrayList<>();
        ArrayList<String> pfps = new ArrayList<>();
        ArrayList<String> pfps2 = new ArrayList<>();
        ArrayList<String> pfps3 = new ArrayList<>();
        pfps.add("https://alchetron.com/cdn/albrecht-thaer-3a110342-8ee9-462c-ade0-41fcadf6d35-resize-750.jpg");
        pfps.add("https://www.thestatesman.com/wp-content/uploads/2017/08/1493458748-beauty-face-517.jpg");
        pfps.add("https://goop.com/wp-content/uploads/2020/06/Mask-Group-2.png");
        pfps2.add("https://www.thestatesman.com/wp-content/uploads/2017/08/1493458748-beauty-face-517.jpg");
        pfps3.add("https://goop.com/wp-content/uploads/2020/06/Mask-Group-2.png");

        UserDTO user = new UserDTO("Bob", 45, "Danmark", "Student", "DTU", "Just a random man", "Male", events, pfps);
        UserDTO user2 = new UserDTO("Sarah", 23, "Danmark", "Student", "DTU", "Just a random woman", "Female", events, pfps2);
        UserDTO user3 = new UserDTO("XÆØ A", 1, "Danmark", "Student", "DTU", "Just a random it", "Trans", events, pfps3);
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


        EventDTO event1 = new EventDTO(user, participants, "Come and grab something to eat with us", "Group Dinner", new DateDTO(8,8,2020),13,"Food", "Denmark", "5-15$",event1Pictures);
        EventDTO event2 = new EventDTO(user2, participants, "Lets play some rocket league please", "Rocket League", new DateDTO(8,8,2020),13,"Entertainment", "Gaming Nation", "Free",event2Pictures);
        EventDTO event3 = new EventDTO(user3, participants, "Discover Roskilde with the bois", "Roskilde", new DateDTO(8,8,2020),13,"City", "Denmark", "Free",event3Pictures);


        eventList.add(event1);
        eventList.add(event2);
        eventList.add(event3);

        for (int i = 0; i < 20; i++) {
            String category = "";
            if (i < 1) category = "ONE";
            else if (i < 18) category = "TWO";
            else category = "THREE";
            int counter = 0;
            for (int j = 0; j < 4; j++) {
                EventDTO eventN = null;
                if (counter == 0 || counter == 3) {
                    if (counter == 3) {
                        counter = 1;
                    }
                    eventN = new EventDTO(user,participants,"TESTING1","TESTING1",new DateDTO(12,12,2021),13,category,"FAROE", "1M gp", event2Pictures);
                }
                else if (counter == 1) {
                    eventN = new EventDTO(user2,participants,"TESTING2","TESTING2",new DateDTO(12,12,2021),13,category,"FAROE", "1M gp", event1Pictures);
                }
                else if (counter == 2){
                    eventN = new EventDTO(user3,participants,"TESTING3","TESTING3",new DateDTO(12,12,2021),13,category,"FAROE", "1M gp", event3Pictures);
                }
                counter++;
                eventList.add(eventN);
            }
        }

        for (int i = 0; i < eventList.size(); i++) {
            List<EventDTO> searchHomeListHorizontal = new ArrayList<>();
            String category = eventList.get(i).getCategory();
            boolean test2 = false;
            for (int j = 0; j < eventList.size(); j++) {
                String nextCategory = eventList.get(j).getCategory();
                EventDTO event = eventList.get(j);
                EventDTO item = new EventDTO(event.getEventCreator(), event.getParticipants(), event.getEventDescription(), event.getEventTitle(), event.getEventDate(),event.getEventLikes(), event.getCategory(), event.getAddress(), event.getPrice(), event.getPictures());
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
                SearchHomeItem items = new SearchHomeItem(category, searchHomeListHorizontal);
                searchHomeList.add(items);
            }
        }
        bannerList.add("https://media.istockphoto.com/photos/mountain-landscape-ponta-delgada-island-azores-picture-id944812540?k=6&m=944812540&s=612x612&w=0&h=wRP1lyorzHta0IhG-YIMiqkNsAIre8yQRfY7NaR_r0g=");
        bannerList.add("https://www.stockvault.net/data/2007/03/01/102413/thumb16.jpg");
        bannerList.add("https://www.stockvault.net/data/2008/09/02/106231/thumb16.jpg");
        bannerList.add("https://media.istockphoto.com/photos/perfect-wiev-of-the-sunset-behaind-vestrahorn-mountain-picture-id861625452?k=6&m=861625452&s=612x612&w=0&h=ImXJ9QVeoda-uwDgVe8IyiEUAfWtWdNdU4SfDBPQxHk=");
        bannerList.add("https://c4.wallpaperflare.com/wallpaper/356/733/263/landscape-stock-huawei-mediapad-wallpaper-preview.jpg");
    }

    public List<SearchHomeItem> getSearchHomeList() { return searchHomeList; }

    public ArrayList<EventDTO> getList(){
        return eventList;
    }

    public List<String> getBannerList() { return bannerList; }

}
