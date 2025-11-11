package com.martdbj;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Main {

    public static ArrayList<String[]> events = new ArrayList<>();

    public static void main(String[] args) {
        try {
            URL URLgithubAPI = new URL("https://api.github.com/users/" + "martdbj" + "/events");

            HttpURLConnection connectionGithubAPI = (HttpsURLConnection) URLgithubAPI.openConnection(); //Creation of the HttpURLConnection
            
            connectionGithubAPI.connect(); //Making the connection effective 

            //Getting the response code
            int responseCode = connectionGithubAPI.getResponseCode();

            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {

                String inline = "";
                Scanner scanner = new Scanner(URLgithubAPI.openStream());

                //Write all the response data into a string
                while (scanner.hasNext()) {
                    inline += scanner.nextLine();
                }

                scanner.close();

                //Convert the string we obtained into a  JSONArray
                JsonArray githubEvents = JsonParser.parseString(inline).getAsJsonArray();

                for (JsonElement eventElement : githubEvents) {
                    JsonObject event = eventElement.getAsJsonObject();

                    String type = event.get("type").getAsString();
                    String repoName = getRepoName(event);
                    String[] singleEvent = {type, repoName};
                    events.add(singleEvent);
                }
                
                ArrayList<String> urlEvent = new ArrayList<>();
                for (String[] event : events) {
                    int counter = 0;
                    for (String name : event) {
                        if (counter == 0) {
                            counter++;
                        } else {
                            urlEvent.add(name);
                            counter = 0;
                        }
                    }
                }

                // Collection of unique elements
                Set<String> urlEventNoDuplicate = new HashSet<>(urlEvent);
                System.out.println(urlEventNoDuplicate);
                for (String urlNoDuplicate :urlEventNoDuplicate) {
                    int frecuencia = Collections.frequency(urlEvent, urlNoDuplicate);
                    System.out.println("Pushed " + frecuencia + " to " + urlEventNoDuplicate);
                }  
            }

        } catch (IOException | RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    public static String getRepoName(JsonObject event) {
        JsonObject repo = event.getAsJsonObject("repo");
        String repoName = repo.get("name").getAsString();
        return repoName;
    }

    public static int counterRepo() {
        int i = 0;
        i++;
        return i;
    }

    // public static String[] getNumberPush() {
    //     events.get("PushEvent");
    // }
}
