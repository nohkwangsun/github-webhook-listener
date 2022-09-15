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
import java.util.Arrays;

@Slf4j
@RestController("/")
public class GithubWebHookController {

    @Value("${repository.workingDirectory}")
    private static String processWorkingDirectory;
    @Value("${repository.cloneUrl}")
    private static String repositoryCloneUrl;

    @GetMapping
    public String health() {
        return "ok";
    }

    @PostMapping()
    public ResponseEntity<String> handlePushHook(@RequestBody GithubWebHook webHook) {
        boolean isPush = Arrays.asList(webHook.getHook().getEvents()).contains("push");
        boolean isCorrectRepository = webHook.getRepository().getCloneUrl().equals(repositoryCloneUrl);
        if (!isPush || !isCorrectRepository) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Hook : " + webHook);
        }

        try {
            process();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failure");
        }
        return ResponseEntity.status(HttpStatus.OK).body("success");
    }

    public void process() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(new File(processWorkingDirectory));
        execute(processBuilder, "bin/stop.sh");
        execute(processBuilder, "bin/build.sh");
        Thread.sleep(3000);
        execute(processBuilder, "bin/start.sh");
    }

    private void execute(ProcessBuilder processBuilder, String script) throws IOException, InterruptedException {
        processBuilder.command(script);
        int exitCode = processBuilder.start().waitFor();
        if (exitCode != 0) {
            log.error("Error occurs while [" + script + "]. exitCode:[" + exitCode + "]");
        }
    }

}


