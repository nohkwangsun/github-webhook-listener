package com.onlinejava.project.adapter.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class GithubWebHook {
    private Repository repository;
    private User pusher;

    @Getter
    @ToString
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
    @ToString
    public class User {
        private long id;
        private String name;
        private String email;
    }
}
