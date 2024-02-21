package com.dkaminski.base;

import lombok.extern.slf4j.Slf4j;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.SandboxPolicy;
import org.graalvm.polyglot.Value;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import java.io.ByteArrayOutputStream;

@ConfigurationPropertiesScan
@SpringBootApplication
@Slf4j
public class Application implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(final ApplicationArguments args) {

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ByteArrayOutputStream err = new ByteArrayOutputStream();

        final Engine engine = Engine.newBuilder("js")
            .sandbox(SandboxPolicy.UNTRUSTED)
            .err(err)
            .out(out)
            .allowExperimentalOptions(true)
            .logHandler(new SLF4JBridgeHandler())
            .option("engine.WarnInterpreterOnly", "false")
            .option("js.graal-builtin", "false")
            .option("js.load", "false")
            .option("js.console", "false")
            .option("js.print", "false")
            .build();

        final Context.Builder contextBuilder = Context.newBuilder("js")
            .sandbox(SandboxPolicy.UNTRUSTED)
            .logHandler(new SLF4JBridgeHandler())
            .engine(engine);

        try (final Context context = contextBuilder.build()) {

            final Value eval = context.eval("js", """
                    function main() {
                    const foo = Math.floor(Math.random() * 100) + 1;
                    
                    return foo;
                    }
                    main()
                """);

            log.info(STR."EvaluationResult: \{eval}");
        }
    }
}
