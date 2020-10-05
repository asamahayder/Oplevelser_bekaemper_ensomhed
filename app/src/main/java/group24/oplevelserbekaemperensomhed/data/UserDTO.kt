package group24.oplevelserbekaemperensomhed.Data;

import java.util.ArrayList;

public class UserDTO {

    private String name;
    private int age;
    private String address;
    private String occupation;
    private String education;
    private String about;
    private String gender;
    private ArrayList<EventDTO> eventsCreatedByUser;

    //mangler evt.
    //picture
    //interests
    //Social media integration (Instagram, facebook_common friends)


    public UserDTO(String name, int age, String address, String occupation, String education, String about, String gender, ArrayList<EventDTO> eventsCreatedByUser) {
        this.name = name;
        this.age = age;
        this.address = address;
        this.occupation = occupation;
        this.education = education;
        this.about = about;
        this.gender = gender;
        this.eventsCreatedByUser = eventsCreatedByUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public ArrayList<EventDTO> getEventsCreatedByUser() {
        return eventsCreatedByUser;
    }

    public void setEventsCreatedByUser(ArrayList<EventDTO> eventsCreatedByUser) {
        this.eventsCreatedByUser = eventsCreatedByUser;
    }
}
