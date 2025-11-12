package com.martdbj;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Main {

    public static List<GitHubEvent> events = new ArrayList<>();

    public static void main(String[] args) {
        try {
            if (args.length != 1){
            System.out.println("Usage: java GitHubActivityCLI <username>");
            return;
        }

            URL URLgithubAPI = new URL("https://api.github.com/users/" + args[0] + "/events");
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

                    events.add(new GitHubEvent(type, repoName));
                }

                Set<String> uniqueRepoNames = filterByRepoName();
                Set<String> uniqueTypes = filteredByType();

                for (String type : uniqueTypes) {
                    for (String repoName : uniqueRepoNames) {
                        long pushCount = events.stream()
                                .filter(object -> object.getType().equals(type))
                                .filter(object -> object.getRepoName().equals(repoName))
                                .count();
                        if (pushCount > 0) {
                            String timeDilemma = pushCount == 1 ? "time" : "times";
                            System.out.println(type + " at " + repoName + " " + pushCount + " " + timeDilemma);
                        }
                    }
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

    public static Set<String> filterByRepoName() {
        Set<String> filteredList = new HashSet<>();
        for (GitHubEvent object : events) {
            filteredList.add(object.getRepoName());
        }
        return filteredList;
    }

    public static Set<String> filteredByType() {
        Set<String> filteredList = new HashSet<>();
        for (GitHubEvent object : events) {
            filteredList.add(object.getType());
        }
        return filteredList;
    }
}
