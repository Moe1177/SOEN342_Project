package com.SOEN342.railway_network_system.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Train {
    private String trainType; 
    private String daysOfOperation; // comma-separated days e.g., "Mon,Tue,Wed"

    //check if train runs on a specific day (case-insensitive)
    public boolean operatesOn(String day) {
        if (day == null || daysOfOperation == null) return false;
        String target = day.trim().toLowerCase();
        String[] tokens = daysOfOperation.split(",");
        for (String token : tokens) {
            if (token.trim().toLowerCase().equals(target)) {
                return true;
            }
        }
        return false; 
    }

    //check if train type matches user's criteria (case-insensitive)
    public boolean matchesType(String type){
        if (type == null || trainType == null) return false;
        return trainType.equalsIgnoreCase(type); 
    }

    public String displayTrainInfo(){
        StringBuilder sb = new StringBuilder();
        sb.append("Train type: ").append(trainType == null ? "" : trainType)
          .append(" | Days: ").append(daysOfOperation == null ? "" : daysOfOperation);
        return sb.toString();
    }
}
