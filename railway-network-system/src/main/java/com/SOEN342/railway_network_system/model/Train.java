package com.SOEN342.railway_network_system.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;


@Data
@Entity
@Table(name = "trains")
@NoArgsConstructor
@AllArgsConstructor
public class Train {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "train_id")
    private int trainId; 

    @Column(name = "route_id", nullable = false)
    private String routeId; 

    @Column(name = "train_type", nullable = false)
    private String trainType; 

    @Column(name = "days_of_operation", nullable = false)
    private String daysOfOperation; // comma-separated days e.g., "Mon,Tue,Wed"

    public Train(String trainType, String daysOfOperation) {
        this.trainType = trainType;
        this.daysOfOperation = daysOfOperation;
    }

    //check if train runs on a specific day (case-insensitive), supports "Daily" and ranges like Mon-Fri, Fri-Sun
    public boolean operatesOn(String day) {
        if (day == null || daysOfOperation == null) return false;
        String target = day.trim();
        if (target.isEmpty()) return false;

        String normalized = daysOfOperation.trim();
        if (normalized.equalsIgnoreCase("Daily")) return true;

        String[] tokens = normalized.split(",");
        for (String raw : tokens) {
            String token = raw.trim();
            if (token.isEmpty()) continue;
            if (token.contains("-")) {
                if (isDayInRange(target, token)) return true;
            } else {
                if (equalsDay(token, target)) return true;
            }
        }
        return false; 
    }

    private boolean equalsDay(String a, String b) {
        return normalizeDay(a).equals(normalizeDay(b));
    }

    private String normalizeDay(String d) {
        String x = d.trim().toLowerCase();
        switch (x) {
            case "mon": case "monday": return "mon";
            case "tue": case "tues": case "tuesday": return "tue";
            case "wed": case "wednesday": return "wed";
            case "thu": case "thur": case "thurs": case "thursday": return "thu";
            case "fri": case "friday": return "fri";
            case "sat": case "saturday": return "sat";
            case "sun": case "sunday": return "sun";
            default: return x;
        }
    }

    private boolean isDayInRange(String target, String rangeToken) {
        String[] parts = rangeToken.split("-");
        if (parts.length != 2) return false;
        String start = normalizeDay(parts[0]);
        String end = normalizeDay(parts[1]);
        String day = normalizeDay(target);

        String[] order = {"mon","tue","wed","thu","fri","sat","sun"};
        int startIdx = indexOf(order, start);
        int endIdx = indexOf(order, end);
        int dayIdx = indexOf(order, day);
        if (startIdx == -1 || endIdx == -1 || dayIdx == -1) return false;

        if (startIdx <= endIdx) {
            return dayIdx >= startIdx && dayIdx <= endIdx;
        } else {
            // wrap-around, e.g., Fri-Sun
            return dayIdx >= startIdx || dayIdx <= endIdx;
        }
    }

    private int indexOf(String[] arr, String v) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(v)) return i;
        }
        return -1;
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
