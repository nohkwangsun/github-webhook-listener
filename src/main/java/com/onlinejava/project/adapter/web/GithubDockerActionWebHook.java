package com.onlinejava.project.adapter.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class GithubDockerActionWebHook {
    private String action;
    private @JsonProperty("workflow_run") WorkflowRun workflowRun;
    private Repository repository;
    private User sender;

    @Getter
    @ToString
    static public class WorkflowRun {
        private long id;
        private String name;
        private @JsonProperty("head_branch") String headBranch;
        private String conclusion;
    }

    @Getter
    @ToString
    static public class Repository {
        private long id;
        private String name;
        private @JsonProperty("full_name") String fullName;
        private @JsonProperty("git_url") String gitUrl;
        private @JsonProperty("clone_url") String cloneUrl;
    }

    @Getter
    @ToString
    static public class User {
        private long id;
        private String login;
    }
}
