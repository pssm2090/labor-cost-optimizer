package com.smartstay.laboroptimizer.service;

import com.smartstay.laboroptimizer.model.StaffingRequest;
import com.smartstay.laboroptimizer.model.StaffingResponse;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class StaffingService {

    public StaffingResponse recommend(StaffingRequest request) {
    	
    	System.out.println("VIP=" + request.getVipGuest() + 
                " CITY=" + request.getCityEvent() + 
                " LW=" + request.getLongWeekend());

        int occupancy = request.getOccupancyPercent();
        int rooms     = request.getTotalRooms();
        int covers    = request.getRestaurantCovers();
        List<String> events = request.getEvents();
        String tier   = request.getHotelTier() != null ? request.getHotelTier() : "midscale";

        // ── Special circumstance flags ──
        boolean hasWedding    = events != null && events.stream().anyMatch(e -> e.equalsIgnoreCase("wedding"));
        boolean hasConference = events != null && events.stream().anyMatch(e -> e.equalsIgnoreCase("conference"));
        boolean hasFestival   = events != null && events.stream().anyMatch(e -> e.equalsIgnoreCase("festival"));
        Boolean isVip         = request.getVipGuest();
        Boolean isCityEvent   = request.getCityEvent();
        Boolean isLongWeekend = request.getLongWeekend();

        int occupiedRooms = (int) Math.ceil(rooms * occupancy / 100.0);

        // ── HOTEL TIER: rooms per housekeeper ratio ──
        // Luxury hotels assign fewer rooms per housekeeper — more attentive service
        // Budget hotels run leaner — more rooms per housekeeper
        // Source: AHLEI Housekeeping Operations standard, adapted per tier
        int roomsPerHousekeeper;
        switch (tier) {
            case "luxury"    -> roomsPerHousekeeper = 10;
            case "upscale"   -> roomsPerHousekeeper = 12;
            case "midscale"  -> roomsPerHousekeeper = 14;
            default          -> roomsPerHousekeeper = 16; // budget
        }

        // ── HOUSEKEEPING ──
        // Formula: ceil(occupiedRooms / roomsPerHousekeeper) + 1 supervisor per 20 HKs
        // Supervisors do quality checks, linen management — standard hotel ops ratio
        int housekeepers  = (int) Math.ceil(occupiedRooms / (double) roomsPerHousekeeper);
        int hkSupervisors = (int) Math.ceil(housekeepers / 20.0);
        int housekeeping  = housekeepers + hkSupervisors;

        // Long weekend: +15% housekeeping — more checkout/checkin turnovers same day
        if (isLongWeekend) {
            housekeeping += (int) Math.ceil(housekeeping * 0.15);
        }

        // VIP guest: +2 dedicated floor housekeeping for VIP floor attentiveness
        if (isVip) {
            housekeeping += 2;
        }

        // ── FRONT OFFICE ──
        // Scales with occupancy bands — more guests = more check-ins, concierge requests
        // Tier also matters: luxury guests expect more personalised front desk attention
        int frontOfficeBase;
        if (occupancy >= 80)      frontOfficeBase = 8;
        else if (occupancy >= 60) frontOfficeBase = 6;
        else if (occupancy >= 40) frontOfficeBase = 4;
        else                      frontOfficeBase = 3;

        // Luxury/upscale adds 2 extra — dedicated concierge and guest relations
        int tierFrontOfficeBonus = (tier.equals("luxury") || tier.equals("upscale")) ? 2 : 0;
        int frontOffice = frontOfficeBase + tierFrontOfficeBonus;

        // VIP guest: +2 — dedicated concierge, butler, and protocol handling
        if (isVip) frontOffice += 2;

        // Long weekend: +1 for extra check-in/out volume
        if (isLongWeekend) frontOffice += 1;

        // ── F&B / RESTAURANT ──
        // Formula: 1 server per 8 covers (HRAWI standard for Indian hotel restaurants)
        // City event drives walk-in traffic to bar/restaurant even without hotel bookings
        int fnb = (int) Math.ceil(covers / 8.0);
        fnb = Math.max(fnb, 3); // minimum 3 regardless

        if (isCityEvent)   fnb += 5;  // bar + restaurant walk-in spike from event crowd
        if (isLongWeekend) fnb += 3;  // leisure guests dine in more on long weekends

        // ── BANQUET ──
        // Only activated when there is an on-property event
        // Wedding: ~1 server per 10 guests for 200-pax event + coordinators + runners
        // Conference: lighter — buffet service, structured timings, less ad-hoc
        int banquet = 0;
        if (hasWedding)    banquet += 20;
        if (hasConference) banquet += 12;

        // ── KITCHEN ──
        // Base: 1 kitchen staff per 15 covers (line cook batch capacity)
        // Events add significant kitchen load — custom menus, large volume, odd hours
        int kitchen = (int) Math.ceil(covers / 15.0);
        if (hasWedding)    kitchen += 8;  // custom wedding menu, high volume, late hours
        if (hasConference) kitchen += 4;  // set menus but large batch
        if (isCityEvent)   kitchen += 3;  // bar snacks + room service spike
        if (isLongWeekend) kitchen += 2;  // sustained demand across all meals
        kitchen = Math.max(kitchen, 4);   // minimum 4 regardless

        // ── SECURITY ──
        // Base: 1 guard per floor (BIS IS 14665 standard for 3-star+ hotels)
        // Events, festivals, VIPs, and city events all increase security need
        int floors   = (int) Math.ceil(rooms / 20.0);
        int security = floors + 1; // base: 1 per floor + 1 lobby/gate

        if (hasWedding || hasFestival) security += 4; // crowd management, entry gate
        if (isVip)                     security += 6; // VIP security detail + floor seal
        if (isCityEvent)               security += 2; // spillover crowd from city
        if (isLongWeekend)             security += 1; // higher general footfall

        // ── MAINTENANCE ──
        // Higher occupancy = more breakdown calls, lift usage, AC issues
        // Tier matters: luxury guests report issues faster and expect immediate response
        int maintenance;
        if (occupancy >= 75) maintenance = tier.equals("luxury") ? 7 : 5;
        else                 maintenance = tier.equals("luxury") ? 5 : 3;

        if (isLongWeekend) maintenance += 1; // extra wear during peak usage periods

        // ── Build department map ──
        Map<String, Integer> staff = new LinkedHashMap<>();
        staff.put("Housekeeping",     housekeeping);
        staff.put("Front Office",     frontOffice);
        staff.put("F&B / Restaurant", fnb);
        staff.put("Banquet",          banquet);
        staff.put("Kitchen",          kitchen);
        staff.put("Security",         security);
        staff.put("Maintenance",      maintenance);

        int totalStaff = staff.values().stream().mapToInt(Integer::intValue).sum();

        // ── Overtime risk ──
        // High: wedding or very high occupancy — these reliably cause overtime in India
        // Medium: conference or moderate-high occupancy
        // VIP and city events bump risk up by one level
        String overtimeRisk;
        boolean highRiskBase   = occupancy >= 85 || hasWedding;
        boolean mediumRiskBase = occupancy >= 65 || hasConference;

        if (highRiskBase || (mediumRiskBase && (isVip || isCityEvent))) {
            overtimeRisk = "High";
        } else if (mediumRiskBase || isVip || isCityEvent || isLongWeekend) {
            overtimeRisk = "Medium";
        } else {
            overtimeRisk = "Low";
        }

        // ── Estimated savings vs unplanned scheduling ──
        // Logic: low-demand days have the MOST waste (over-rostering just in case)
        // High-demand days need the staff anyway — savings come from precision, not reduction
        double savings;
        if (overtimeRisk.equals("High"))        savings = 12.5;
        else if (overtimeRisk.equals("Medium")) savings = 17.0;
        else                                    savings = 21.5;

        // ── Build context tags for recommendation message ──
        StringBuilder context = new StringBuilder();
        if (hasWedding)    context.append(" + Wedding");
        if (hasConference) context.append(" + Conference");
        if (hasFestival)   context.append(" + Festival");
        if (isVip)         context.append(" + VIP Guest");
        if (isCityEvent)   context.append(" + City Event");
        if (isLongWeekend) context.append(" + Long Weekend");

        String recommendation = String.format(
            "%s hotel | %d%% occupancy (%d rooms occupied) | %d F&B covers%s | " +
            "Shift: %s | Recommended: %d staff across 7 departments. " +
            "Overtime risk is %s. Estimated %.1f%% cost saving vs unplanned scheduling.",
            capitalize(tier),
            occupancy, occupiedRooms, covers,
            context.toString(),
            request.getShift() != null ? request.getShift() : "not specified",
            totalStaff,
            overtimeRisk,
            savings
        );

        StaffingResponse response = new StaffingResponse();
        response.setDepartmentStaff(staff);
        response.setEstimatedSavingsPercent(savings);
        response.setOvertimeRisk(overtimeRisk);
        response.setRecommendation(recommendation);

        return response;
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}