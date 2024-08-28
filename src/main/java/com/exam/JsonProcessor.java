package com.exam;




import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonProcessor {

    public static void main(String[] args) {
        // Step 1: Check if the user provided the correct number of arguments
        if (args.length != 2) {
            System.out.println("Please provide the PRN Number and the JSON file path.");
            return; // Exit the program if the arguments are not correct
        }

        // Step 2: Get the PRN Number and JSON file path from the command line arguments
        String prnNumber = args[0].toLowerCase().replaceAll("\\s+", ""); // Convert PRN to lowercase and remove spaces
        String jsonFilePath = args[1];

        // Step 3: Read the JSON file
        ObjectMapper objectMapper = new ObjectMapper(); // Create an ObjectMapper to read the JSON file
        JsonNode rootNode = null;
        try {
            rootNode = objectMapper.readTree(new File(jsonFilePath)); // Load the JSON file into rootNode
        } catch (IOException e) {
            System.out.println("Error reading the JSON file.");
            e.printStackTrace();
            return; // Exit if there's an error reading the file
        }

        // Step 4: Traverse the JSON to find the first "destination" key
        String destinationValue = null;
        destinationValue = findDestination(rootNode); // Call the method to find the "destination" key
        if (destinationValue == null) {
            System.out.println("The key 'destination' was not found in the JSON file.");
            return; // Exit if the "destination" key is not found
        }

        // Step 5: Generate a random alphanumeric string of length 8
        String randomString = generateRandomString(8);

        // Step 6: Concatenate PRN, destination value, and random string
        String stringToHash = prnNumber + destinationValue + randomString;

        // Step 7: Generate the MD5 hash of the concatenated string
        String hash = null;
        try {
            hash = generateMD5Hash(stringToHash);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error generating MD5 hash.");
            e.printStackTrace();
            return; // Exit if there's an error generating the hash
        }

        // Step 8: Print the final output in the required format
        System.out.println(hash + ";" + randomString);
    }

    // Method to find the first "destination" key in the JSON
    private static String findDestination(JsonNode node) {
        if (node.isObject()) {
            // If the current node is an object, iterate over its fields
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                if (field.getKey().equals("destination")) {
                    return field.getValue().asText(); // Return the value if "destination" key is found
                }
                // Recursively search in the current field's value
                String value = findDestination(field.getValue());
                if (value != null) return value;
            }
        } else if (node.isArray()) {
            // If the current node is an array, iterate over its elements
            for (JsonNode arrayItem : node) {
                String value = findDestination(arrayItem);
                if (value != null) return value;
            }
        }
        return null; // Return null if "destination" key is not found
    }

    // Method to generate a random alphanumeric string of a given length
    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random(); // Create a Random object
        StringBuilder sb = new StringBuilder(length); // Use StringBuilder to build the random string
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString(); // Convert StringBuilder to string and return
    }

    // Method to generate an MD5 hash of a given string
    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5"); // Get an MD5 MessageDigest instance
        byte[] digest = md.digest(input.getBytes()); // Compute the hash of the input string
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b)); // Convert each byte to a hexadecimal string
        }
        return sb.toString(); // Convert StringBuilder to string and return
    }
}

