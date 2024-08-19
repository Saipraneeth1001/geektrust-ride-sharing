package ride.sharing.ride;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static ride.sharing.ride.Constants.*;

public class RideSharing {
    Map<String, Driver> driversMap = new HashMap<>();
    Map<String, Rider> ridersMap = new HashMap<>();
    Map<String, List<Driver>> matchedDriversMap = new HashMap<>();
    Map<String, Ride> rideMap = new HashMap<>();

    public void parseCommands(String command) {
        String []words = command.trim().split(" ");
        String prefix = words[0];
        executeCommand(prefix, words);

    }

    private void executeCommand(String prefix, String []words) {
        switch(prefix) {
            case ADD_RIDER:
                addRider(words);
                break;
            case ADD_DRIVER:
                addDriver(words);
                break;
            case MATCH:
                match(words);
                break;
            case START_RIDE:
                startRide(words);
                break;
            case STOP_RIDE:
                stopRide(words);
                break;
            case BILL:
                generateBill(words);
                break;
            default:
                break;

        }
    }

    private void generateBill(String []words) {
        String rideId = words[1];
        if (!rideMap.containsKey(rideId)) {
            System.out.println("INVALID_RIDE");
        }
        else if (rideMap.containsKey(rideId) && !rideMap.get(rideId).isCompleted()) {
            System.out.println("RIDE_NOT_COMPLETED");
        }
        else {
            Ride ride = rideMap.get(rideId);
            double baseFare = 50;
            double distance = round(Math.sqrt(Math.pow(ride.getEndX() - ride.getStartX(), 2) +
                    Math.pow(ride.getEndY() - ride.getStartY(), 2)), 2);
            double distanceFare = round(distance * 6.5, 2);
            int timeFare = ride.getTimeTaken() * 2;
            double serviceTax = round((baseFare + distanceFare + timeFare) * 0.2, 2);
            double finalAmount = baseFare + distanceFare + timeFare + serviceTax;
            System.out.println("BILL "+rideId+" "+ride.getDriver().getId()+" "+finalAmount);
        }

    }


    private void stopRide(String []words) {
        String rideId = words[1];
        if (!rideMap.containsKey(rideId) || rideMap.get(rideId).isCompleted()) {
            System.out.println("INVALID_RIDE");
        } else {
            double destinationX = Double.parseDouble(words[2]);
            double destinationY = Double.parseDouble(words[3]);
            int timeTaken = Integer.parseInt(words[4]);
            Ride ride = rideMap.get(rideId);
            ride.setCompleted(true);
            ride.setEndX(destinationX);
            ride.setEndY(destinationY);
            ride.setTimeTaken(timeTaken);
            rideMap.put(rideId, ride);
            System.out.println("RIDE_STOPPED "+rideId);
        }

    }

    private void startRide(String []words) {
        String rideId = words[1];
        int n = Integer.parseInt(words[2]);
        String riderId = words[3];
        List<Driver> matchedDrivers = matchedDriversMap.get(riderId);
        if (null == matchedDrivers || matchedDrivers.size() < 1 || rideMap.containsKey(rideId) || n - 1 >= matchedDrivers.size()) {
            System.out.println("INVALID_RIDE");
        } else {
            Driver matchedDriver = matchedDrivers.get(n-1);
            Rider rider = ridersMap.get(riderId);
            rideMap.put(rideId, new Ride(rideId, rider, matchedDriver));
            System.out.println("RIDE_STARTED "+rideId);
        }
    }


    private void match(String []words) {
        String riderId = words[1];
        Rider riderDetails = ridersMap.get(riderId);
        List<Driver> matchedDrivers = new ArrayList<>();
        for (Driver driver: driversMap.values()) {
            double distance = calculateDistance(riderDetails, driver);
            if (distance <= 5) {
                matchedDrivers.add(driver);
            }
        }
        Collections.sort(matchedDrivers, new Comparator<Driver>() {
            @Override
            public int compare(Driver driver1, Driver driver2) {
                double d1 = calculateDistance(riderDetails, driver1);
                double d2 = calculateDistance(riderDetails, driver2);
                if (d1 == d2) {
                    return driver1.getId().compareTo(driver2.getId());
                }
                return Double.compare(d1, d2);
            }
        });

        if (matchedDrivers.size() < 1) {
            System.out.println("NO_DRIVERS_AVAILABLE");
        } else {
            matchedDriversMap.put(riderId, matchedDrivers);
            StringBuilder result = new StringBuilder("DRIVERS_MATCHED");
            for (int i = 0; i < Math.min(5, matchedDrivers.size()); i++) {
                result.append(" ").append(matchedDrivers.get(i).getId());
            }
            System.out.println(result);
        }

    }

    private double calculateDistance(Rider rider, Driver driver)
    {
        double x1 = rider.getX();
        double x2 = driver.getX();
        double y1 = rider.getY();
        double y2 = driver.getY();
        double xVar = Math.abs(x2 - x1);
        double yVar = Math.abs(y2 - y1);
        return round(Math.sqrt((xVar * xVar) + (yVar * yVar)), 2);
    }

    private void addDriver(String []words) {
        String driverId = words[1];
        double xc = Double.parseDouble(words[2]);
        double yc = Double.parseDouble(words[3]);
        Driver driver = new Driver(driverId, xc, yc);
        driversMap.put(driverId, driver);

    }

    private void addRider(String []words) {
        String riderId = words[1];
        double xc = Double.parseDouble(words[2]);
        double yc = Double.parseDouble(words[3]);
        Rider rider = new Rider(riderId, xc, yc);
        ridersMap.put(riderId, rider);

    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
