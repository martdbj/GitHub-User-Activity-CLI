package com.martdbj;

import java.util.ArrayList;
import java.util.List;

public class GitHubEvent {
    private String type;
    private String repoName;

    public GitHubEvent(String type, String repoName) {
        this.type = type;
        this.repoName = repoName;
    }

    public String getType() {
        return type;
    }

    public String getRepoName() {
        return repoName;
    }

    @Override
    public String toString() {
        return "GitHubEvent{type='" + type + "', repoName='" + repoName + "'}";
    }
}