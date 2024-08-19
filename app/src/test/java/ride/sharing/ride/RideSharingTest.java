package ride.sharing.ride;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ride.sharing.ride.RideSharing;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class RideSharingTest {

    private RideSharing rideSharing;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        rideSharing = new RideSharing();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void testAddDriver() {
        String command = "ADD_DRIVER D1 1 1";
        rideSharing.parseCommands(command);
        assertTrue(rideSharing.driversMap.containsKey("D1"));
    }

    @Test
    void testAddRider() {
        String command = "ADD_RIDER R1 0 0";
        rideSharing.parseCommands(command);
        assertTrue(rideSharing.ridersMap.containsKey("R1"));
    }

    @Test
    void testMatchNoDriversAvailable() {
        rideSharing.parseCommands("ADD_RIDER R1 0 0");
        rideSharing.parseCommands("MATCH R1");
        assertEquals("NO_DRIVERS_AVAILABLE\n", outContent.toString());
    }

    @Test
    void testMatchDriversAvailable() {
        rideSharing.parseCommands("ADD_DRIVER D1 1 1");
        rideSharing.parseCommands("ADD_DRIVER D2 4 4");
        rideSharing.parseCommands("ADD_RIDER R1 0 0");
        rideSharing.parseCommands("MATCH R1");
        assertTrue(outContent.toString().contains("DRIVERS_MATCHED D1"));
    }

    @Test
    void testStartRide() {
        rideSharing.parseCommands("ADD_DRIVER D1 1 1");
        rideSharing.parseCommands("ADD_RIDER R1 0 0");
        rideSharing.parseCommands("MATCH R1");
        rideSharing.parseCommands("START_RIDE RIDE-001 1 R1");
        assertTrue(rideSharing.rideMap.containsKey("RIDE-001"));
        assertEquals("RIDE_STARTED RIDE-001", outContent.toString().split("\n")[1]);
    }

    @Test
    void testStartRideInvalidRide() {
        rideSharing.parseCommands("START_RIDE RIDE-001 1 R1");
        assertEquals("INVALID_RIDE\n", outContent.toString());
    }

    @Test
    void testStopRide() {
        rideSharing.parseCommands("ADD_DRIVER D1 1 1");
        rideSharing.parseCommands("ADD_RIDER R1 0 0");
        rideSharing.parseCommands("MATCH R1");
        rideSharing.parseCommands("START_RIDE RIDE-001 1 R1");
        rideSharing.parseCommands("STOP_RIDE RIDE-001 4 5 32");
        assertTrue(rideSharing.rideMap.get("RIDE-001").isCompleted());
        assertEquals("RIDE_STOPPED RIDE-001", outContent.toString().split("\n")[2]);
    }

    @Test
    void testStopRideInvalidRide() {
        rideSharing.parseCommands("STOP_RIDE RIDE-001 4 5 32");
        assertEquals("INVALID_RIDE\n", outContent.toString());
    }

    @Test
    void testGenerateBill() {
        rideSharing.parseCommands("ADD_DRIVER D1 1 1");
        rideSharing.parseCommands("ADD_RIDER R1 0 0");
        rideSharing.parseCommands("MATCH R1");
        rideSharing.parseCommands("START_RIDE RIDE-001 1 R1");
        rideSharing.parseCommands("STOP_RIDE RIDE-001 4 5 32");
        rideSharing.parseCommands("BILL RIDE-001");
        assertTrue(outContent.toString().contains("BILL RIDE-001 D1 "));
    }

    @Test
    void testGenerateBillInvalidRide() {
        rideSharing.parseCommands("BILL RIDE-001");
        assertEquals("INVALID_RIDE\n", outContent.toString());
    }

    @Test
    void testGenerateBillRideNotCompleted() {
        rideSharing.parseCommands("ADD_DRIVER D1 1 1");
        rideSharing.parseCommands("ADD_RIDER R1 0 0");
        rideSharing.parseCommands("MATCH R1");
        rideSharing.parseCommands("START_RIDE RIDE-001 1 R1");
        rideSharing.parseCommands("BILL RIDE-001");
        assertEquals("RIDE_NOT_COMPLETED", outContent.toString().split("\n")[2]);
    }
}
