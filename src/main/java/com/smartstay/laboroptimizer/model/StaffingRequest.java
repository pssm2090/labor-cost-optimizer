package com.smartstay.laboroptimizer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class StaffingRequest {

    private int occupancyPercent;
    private int totalRooms;
    private List<String> events;
    private int restaurantCovers;
    private String shift;
    private String hotelTier;

    @JsonProperty("vipGuest")
    private Boolean vipGuest;

    @JsonProperty("cityEvent")
    private Boolean cityEvent;

    @JsonProperty("longWeekend")
    private Boolean longWeekend;

    public int getOccupancyPercent() { return occupancyPercent; }
    public void setOccupancyPercent(int occupancyPercent) { this.occupancyPercent = occupancyPercent; }

    public int getTotalRooms() { return totalRooms; }
    public void setTotalRooms(int totalRooms) { this.totalRooms = totalRooms; }

    public List<String> getEvents() { return events; }
    public void setEvents(List<String> events) { this.events = events; }

    public int getRestaurantCovers() { return restaurantCovers; }
    public void setRestaurantCovers(int restaurantCovers) { this.restaurantCovers = restaurantCovers; }

    public String getShift() { return shift; }
    public void setShift(String shift) { this.shift = shift; }

    public String getHotelTier() { return hotelTier; }
    public void setHotelTier(String hotelTier) { this.hotelTier = hotelTier; }

    // Renamed getters from isX() to getX() — fixes Jackson deserialization confusion
    public Boolean getVipGuest() { return vipGuest != null && vipGuest; }
    public void setVipGuest(Boolean vipGuest) { this.vipGuest = vipGuest; }

    public Boolean getCityEvent() { return cityEvent != null && cityEvent; }
    public void setCityEvent(Boolean cityEvent) { this.cityEvent = cityEvent; }

    public Boolean getLongWeekend() { return longWeekend != null && longWeekend; }
    public void setLongWeekend(Boolean longWeekend) { this.longWeekend = longWeekend; }
}