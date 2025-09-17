package com.example.todo;

import org.slf4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev-notused") // Only active with --spring.profiles.active=dev
public class ScriptRunner {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(ScriptRunner.class);

    @Bean
    public CommandLineRunner runBashScript() {
        return args -> {
            try {
                logger.info("Executing Bash Script for initial data setup...");
                String scriptPath = "./createTodos.bash";
                String logPath = "./createTodos-script-output.log";

                // ProcessBuilder to run script, redirect output and error to log file
                ProcessBuilder builder = new ProcessBuilder("bash", scriptPath);
                builder.redirectOutput(ProcessBuilder.Redirect.to(new java.io.File(logPath)));
                builder.redirectError(ProcessBuilder.Redirect.appendTo(new java.io.File(logPath)));

                Process process = builder.start();
                int exitCode = process.waitFor();
                System.out.println("[ScriptRunner] Bash script completed with exit code: " + exitCode);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                System.err.println("[ScriptRunner] Failed to run bash script: " + e.getMessage());
                //e.printStackTrace();
            }
        };
    }
}