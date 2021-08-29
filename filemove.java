///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS info.picocli:picocli:4.5.0
//DEPS org.apache.camel:camel-core:3.11.1
//DEPS ch.qos.logback:logback-classic:1.2.5
//JAVA 11

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "filemove", mixinStandardHelpOptions = true, version = "filemove 0.1",
        description = "filemove made with jbang")
class filemove implements Callable<Integer> {

    @CommandLine.Option(names = "--copy", description = "Copy instead of move. Leaves the file in source directory.", defaultValue = "false")
    private boolean copy;

    public static void main(String... args) {
        int exitCode = new CommandLine(new filemove()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        try (CamelContext camelContext = new DefaultCamelContext()) {
            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    System.out.println("Executing file route with noop=" + copy + " to " + (copy?"copy":"move") + " file from source to destination");
                    from("file:data-inbox?noop=false")
                            .to("file:data-outbox");
                }
            });
            camelContext.start();
            Thread.sleep(5000);
        }
        return 0;
    }
}
