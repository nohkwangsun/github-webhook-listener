package com.onlinejava.project.adapter.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

@Slf4j
@RestController("/")
public class GithubWebHookController {

    @Value("${repository.pusher.email}")
    private String pusherEmail;
    @Value("${repository.sender.login}")
    private String senderLogin;
    @Value("${repository.workingDirectory}")
    private String processWorkingDirectory;
    @Value("${repository.cloneUrl}")
    private String repositoryCloneUrl;

    @GetMapping
    public String health() {
        return "ok";
    }

    @PostMapping("/docker")
    public ResponseEntity<String> handleDockerActionHook(@RequestBody GithubDockerActionWebHook webHook) {
        log.info("Call the method, handlePushHook");
        boolean isPusherValid = webHook.getSender().getLogin().equals(senderLogin);
        boolean isCorrectRepository = webHook.getRepository().getCloneUrl().equals(repositoryCloneUrl);
        if (!isPusherValid || !isCorrectRepository) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid DockerAction WebHook : " + webHook);
        }
        log.info("The Github DockerAction WebHook is valid. [{}]", webHook);


        try {
            switch (webHook.getAction()) {
                case "in_progress" -> log.info("The Github DockerAction was started : " + webHook.getAction());
                case "completed" -> {
                    if (!"success".equals(webHook.getWorkflowRun().getConclusion())) {
                        log.error("The result of Github DockerAction is not success was started : " + webHook.getWorkflowRun().getConclusion());
                    }
                    dockerRestart();
                }
                default -> log.error("Invalid Action : " + webHook.getAction());
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failure");
        }

        log.info("Finish the method, handleDockerActionHook");
        return ResponseEntity.status(HttpStatus.OK).body("success");
    }

    private void dockerRestart() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(new File(processWorkingDirectory));
        execute(processBuilder, "bin/docker-restart.sh");
    }

//    @PostMapping("/push")
    public ResponseEntity<String> handlePushHook(@RequestBody GithubWebHook webHook) {
        log.info("Call the method, handlePushHook");
        boolean isPusherValid = webHook.getPusher().getEmail().equals(pusherEmail);
        boolean isCorrectRepository = webHook.getRepository().getCloneUrl().equals(repositoryCloneUrl);
        if (!isPusherValid || !isCorrectRepository) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Hook : " + webHook);
        }
        log.info("The Github WebHook is valid. [{}]", webHook);


        try {
            runScripts();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failure");
        }

        log.info("Finish the method, handlePushHook");
        return ResponseEntity.status(HttpStatus.OK).body("success");
    }

    public void runScripts() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(new File(processWorkingDirectory));
        execute(processBuilder, "bin/stop.sh");
        execute(processBuilder, "bin/build.sh");
        execute(processBuilder, "bin/start.sh");
    }

    private void execute(ProcessBuilder processBuilder, String script) throws IOException, InterruptedException {
        log.info("Start to execute [{}] at [{}]", script, processBuilder.directory().getAbsolutePath());
        processBuilder.command(script);
        int exitCode = processBuilder.start().waitFor();
        if (exitCode != 0) {
            String errorMessage = "Error occurs while [" + script + "]. exitCode:[" + exitCode + "]";
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
        log.info("Complete [{}] at [{}]", script, processBuilder.directory().getName());

    }

}


