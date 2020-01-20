package ru.programminglearning.com.googlemaps.Maps.ViewWindowMarker;

public class Model {
    private double latitude;
    private double longitude;
    private float  distance;
    private String nameCompany;
    private String nameAddress;
    private String modeWorkTime;
    private String prices;
    private String authorID;
    private String idMarker;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getNameCompany() {
        return nameCompany;
    }

    public String getNameAddress() {
        return nameAddress;
    }

    public String getModeWorkTime() {
        return modeWorkTime;
    }

    public String getPrices() {
        return prices;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setIdMarker(String idMarker){
        this.idMarker = idMarker;
    }

    public String getIdMarker() {
        return idMarker;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getDistance() {
        return distance;
    }
    @Override
    public String toString() {
        return "Model{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", nameCompany='" + nameCompany + '\'' +
                ", nameAddress='" + nameAddress + '\'' +
                ", modeWorkTime='" + modeWorkTime + '\'' +
                ", authorID='" + authorID + '\'' +
                ", idMarker='" + idMarker + '\'' +
                ", distance='" + distance + '\'' +
                '}';
    }



}
