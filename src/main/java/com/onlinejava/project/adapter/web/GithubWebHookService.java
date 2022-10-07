package com.onlinejava.project.adapter.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
public class GithubWebHookService {
    @Value("${repository.workingDirectory}")
    private String processWorkingDirectory;

    @Async
    public void dockerRestart() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(new File(processWorkingDirectory));
        execute(processBuilder, "bin/docker-restart.sh");
    }

    @Async
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


