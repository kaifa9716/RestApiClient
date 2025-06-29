import java.io.*;
import java.net.*;
import java.util.*;

// Weather app using WeatherAPI.com - seemed easier than OpenWeatherMap
class WeatherApp {
    
    // Get free API key from weatherapi.com - just need to sign up
    static String API_KEY = "7cdfedd1065048c094f190302252706"; // My api key 
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        System.out.println("\t\t\t******Simple Weather App*******");
        System.out.print("Enter city name: ");
        String city = sc.nextLine();
        
        try {
            String weatherData = fetchWeather(city);
            displayWeather(weatherData);
        } catch (Exception e) {
            System.out.println("Oops, something went wrong: " + e.getMessage());
        }
    }
    
    // Get weather data from WeatherAPI.com
    static String fetchWeather(String city) throws Exception {
        // I am using weatther api for the demonstration and provided key with it with 5mil tokens
        String apiUrl = "http://api.weatherapi.com/v1/current.json?key=" + API_KEY + "&q=" + city + "&aqi=no";
        
        URL url = new URL(apiUrl);
      
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        
        // Check if request worked
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            if (responseCode == 400) {
                throw new Exception("Invalid city name or api key issue");
            } else {
                throw new Exception("API request failed with code: " + responseCode);
            }
        }
        
        // Read the JSON response
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        conn.disconnect();
        
        return response.toString();
    }
    
    // Parse JSON and show weather info
    static void displayWeather(String json) {
        System.out.println("\n--- Weather Report ---");
        
        try {
            // location info
            String cityName = extractJsonValue(json, "name");
            String region = extractJsonValue(json, "region");
            String country = extractJsonValue(json, "country");
            
            // current weather
            String tempC = extractJsonValue(json, "temp_c");
            String tempF = extractJsonValue(json, "temp_f");
            String condition = extractJsonValue(json, "text");
            String humidity = extractJsonValue(json, "humidity");
            String windKph = extractJsonValue(json, "wind_kph");
            String windDir = extractJsonValue(json, "wind_dir");
            String feelsLikeC = extractJsonValue(json, "feelslike_c");
            
            // Display the weather info
            System.out.println("Location: " + cityName + ", " + region + ", " + country);
            System.out.println("Temperature: " + tempC + "°C (" + tempF + "°F)");
            System.out.println("Feels like: " + feelsLikeC + "°C");
            System.out.println("Condition: " + condition);
            System.out.println("Humidity: " + humidity + "%");
            System.out.println("Wind: " + windKph + " km/h " + windDir);
            
            

            
        } catch (Exception e) {
            System.out.println("Error parsing weather data: " + e);
        }
    }
    
    static String extractJsonValue(String json, String key) {
        try {
            String searchPattern = "\"" + key + "\":";
            int startIndex = json.indexOf(searchPattern);
            
            if (startIndex == -1) {
                return ""; // Key not found
            }
            
            startIndex = json.indexOf(":", startIndex) + 1;
            while (startIndex < json.length() && 
                   (json.charAt(startIndex) == ' ' || json.charAt(startIndex) == '"')) {
                startIndex++;
            }
            int endIndex = startIndex;
            boolean inQuotes = json.charAt(startIndex - 1) == '"';
            
            while (endIndex < json.length()) {
                char c = json.charAt(endIndex);
                if (inQuotes && c == '"') {
                    break;
                } else if (!inQuotes && (c == ',' || c == '}' || c == ']')) {
                    break;
                }
                endIndex++;
            }
            
            return json.substring(startIndex, endIndex).trim();
            
        } catch (Exception e) {
            return "";
        }
    }
}

