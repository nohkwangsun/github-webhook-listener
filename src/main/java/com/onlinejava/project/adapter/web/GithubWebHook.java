package com.onlinejava.project.adapter.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class GithubWebHook {
    @JsonProperty("hook_id")
    private long hookId;
    private Hook hook;
    private Repository repository;
    private User sender;

    @Getter
    public class Hook {
        private long id;
        private String type;
        private String name;
        private boolean active;
        private String[] events;
    }

    @Getter
    public class Repository {
        private long id;
        private String name;

        @JsonProperty("full_name")
        private String fullName;

        @JsonProperty("git_url")
        private String gitUrl;

        @JsonProperty("clone_url")
        private String cloneUrl;
    }

    @Getter
    public class User {
        private long id;
        private String login;
        private String type;
    }
}
