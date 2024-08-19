package ride.sharing;

import ride.sharing.ride.RideSharing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Geektrust {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Geektrust <absolute_path_to_input_file>");
            return;
        }

        String inputFilePath = args[0];
        try {
            readFileAndProcess(inputFilePath);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    private static void readFileAndProcess(String filePath) throws IOException {
        RideSharing rideSharing = new RideSharing();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                rideSharing.parseCommands(line);
            }
        }
    }
}

