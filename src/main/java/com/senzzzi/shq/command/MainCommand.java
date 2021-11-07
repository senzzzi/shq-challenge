package com.senzzzi.shq.command;

import org.apache.commons.cli.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.senzzzi.shq.util.PrintUtil.printTable;

@Component
public class MainCommand implements CommandLineRunner {

    private final Map<String, Command> commandMap = new HashMap<>();
    private final HelpFormatter helpFormatter = new HelpFormatter();
    private final CommandLineParser commandLineParser = new DefaultParser();

    public MainCommand(List<Command> commands) {
        commands.forEach((v) -> commandMap.put(v.getCommand(), v));
    }

    @Override
    public void run(String... args) {

        // print welcome message
        List<List<String>> rows = new ArrayList<>();
        rows.add(List.of("Welcome to this awesome quote vending machine!"));
        rows.add(List.of("type help for available commands"));
        printTable(rows);

        // continuously check for new input on stdin
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String cmd = scanner.nextLine();
            String[] arguments = cmd.split(" ");

            // if input is empty, show helper
            if (arguments.length == 0 || !commandMap.containsKey(arguments[0])) {
                commandMap.forEach((k, v) -> showHelper(v.getCommand(), new Options(), v.getDescription()));
            } else {
                Command command = commandMap.get(arguments[0]);
                try {

                    // if command argument -h or --help is sent, show helper of that command
                    if (Arrays.asList(arguments).contains("-h") || Arrays.asList(arguments).contains("--help")) {
                        showHelper(command.getCommand(), command.getOptions(), command.getDescription());
                    } else {

                        // run specific command
                        CommandLine line = commandLineParser.parse(command.getOptions(), arguments);
                        try {
                            command.run(line);
                        } catch (Exception e) {
                            rows = new ArrayList<>();
                            rows.add(List.of("Error"));
                            rows.add(List.of(e.getMessage()));
                            printTable(rows);
                        }
                    }
                } catch (Exception e) {
                    showHelper(e.getMessage() + "\n" + command.getCommand(), command.getOptions(), command.getDescription());
                }
            }
        }
    }

    private void showHelper(String command, Options options, String description) {
        helpFormatter.printHelp(command, description, options,"", true);
    }
}
