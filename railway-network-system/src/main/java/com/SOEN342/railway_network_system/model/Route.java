package com.SOEN342.railway_network_system.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Route {
    private String routeID; 
    private String departureCity;
    private String arrivalCity; 
    private Date departureTime;
    private Date arrivalTime; 
    private String totalDuration; 

    //calculate total duration of train ride 
    public void calculateDuration(){
        if (departureTime == null || arrivalTime == null) {
            totalDuration = null;
            return;
        }
        long millis = arrivalTime.getTime() - departureTime.getTime();
        if (millis < 0) {
            // assume arrival is next day if negative
            millis += TimeUnit.DAYS.toMillis(1);
        }
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(hours);
        StringBuilder sb = new StringBuilder();
        if (hours < 10) sb.append('0');
        sb.append(hours).append(":");
        if (minutes < 10) sb.append('0');
        sb.append(minutes);
        totalDuration = sb.toString();
    }

    //check to see if map matches user's criteria
    public boolean matchesCriteria(Map<String, String> criteria){
            if (criteria == null || criteria.isEmpty()) {
                return true;
            }
            for (Map.Entry<String, String> entry : criteria.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (value == null) continue;
                switch (key) {
                    case "routeID":
                        if (!value.equalsIgnoreCase(routeID)) return false; break;
                    case "departureCity":
                        if (!value.equalsIgnoreCase(departureCity)) return false; break;
                    case "arrivalCity":
                        if (!value.equalsIgnoreCase(arrivalCity)) return false; break;
                    case "totalDuration":
                        if (totalDuration == null) calculateDuration();
                        if (totalDuration == null || !totalDuration.equalsIgnoreCase(value)) return false; break;
                    default:
                        // unrecognized criteria: ignore
                        break;
                }
            }
            return true;
    }

    //display route info 
    public String displayRouteInfo(){
        if (totalDuration == null) {
            calculateDuration();
        }
        SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm", Locale.US);
        StringBuilder sb = new StringBuilder();
        sb.append("Route ").append(routeID)
          .append(" - ")
          .append(departureCity).append(" -> ").append(arrivalCity)
          .append(" | Depart: ").append(departureTime != null ? timeFmt.format(departureTime) : "N/A")
          .append(" | Arrive: ").append(arrivalTime != null ? timeFmt.format(arrivalTime) : "N/A")
          .append(" | Duration: ").append(totalDuration);
        return sb.toString();
    }


}
