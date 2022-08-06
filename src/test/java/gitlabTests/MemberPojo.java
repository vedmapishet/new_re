package gitlabTests;

import java.util.ArrayList;

public class MemberPojo {
    private String name;
    private String agency;
    private String image;
    private String wikipedia;
    private ArrayList<String> launches;
    private String status;
    private String id;

    public MemberPojo(String name, String agency, String image, String wikipedia, ArrayList<String> launches, String status, String id) {
        this.name = name;
        this.agency = agency;
        this.image = image;
        this.wikipedia = wikipedia;
        this.launches = launches;
        this.status = status;
        this.id = id;
    }

    public MemberPojo() {
    }

    public String getName() {
        return name;
    }

    public String getAgency() {
        return agency;
    }

    public String getImage() {
        return image;
    }

    public String getWikipedia() {
        return wikipedia;
    }

    public ArrayList<String> getLaunches() {
        return launches;
    }

    public String getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }
}
