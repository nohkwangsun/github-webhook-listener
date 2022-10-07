package com.onlinejava.project.adapter.web;

import com.onlinejava.project.application.GithubWebHookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController("/")
public class GithubWebHookController {

    @Value("${repository.pusher.email}")
    private String pusherEmail;
    @Value("${repository.sender.login}")
    private String senderLogin;
    @Value("${repository.cloneUrl}")
    private String repositoryCloneUrl;

    @Autowired
    private GithubWebHookService service;

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

        String body = "message accept";

        try {
            switch (webHook.getAction()) {
                case "requested" -> {
                    log.info("The Github DockerAction was requested : " + webHook.getAction());
                }
                case "in_progress" -> {
                    log.info("The Github DockerAction was started : " + webHook.getAction());
                }
                case "completed" -> {
                    if (!"success".equals(webHook.getWorkflowRun().getConclusion())) {
                        log.error("The result of Github DockerAction is not success was started : " + webHook.getWorkflowRun().getConclusion());
                    }
                    service.dockerRestart();
                    body = "start scripts";
                }
                default -> {
                    log.error("Invalid Action : " + webHook.getAction());
                    body = "unknown action";
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failure");
        }

        log.info("Finish the method, handleDockerActionHook");
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @PostMapping("/push")
    public ResponseEntity<String> handlePushHook(@RequestBody GithubWebHook webHook) {
        log.info("Call the method, handlePushHook");
        boolean isPusherValid = webHook.getPusher().getEmail().equals(pusherEmail);
        boolean isCorrectRepository = webHook.getRepository().getCloneUrl().equals(repositoryCloneUrl);
        if (!isPusherValid || !isCorrectRepository) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Hook : " + webHook);
        }
        log.info("The Github WebHook is valid. [{}]", webHook);


        try {
            service.runScripts();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failure");
        }

        log.info("Finish the method, handlePushHook");
        return ResponseEntity.status(HttpStatus.OK).body("success");
    }
}


