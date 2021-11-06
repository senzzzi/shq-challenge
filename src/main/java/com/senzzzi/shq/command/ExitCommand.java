package com.senzzzi.shq.command;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.senzzzi.shq.util.PrintUtil.printTable;

@Component
public class ExitCommand implements Command {

    public String getCommand() {
        return "exit";
    }

    @Override
    public String getDescription() {
        return "Shut Down Vending Machine";
    }

    @Override
    public Options getOptions() {
        return new Options();
    }

    @Override
    public void run(CommandLine commandLine) {
        List<List<String>> rows = new ArrayList<>();
        rows.add(List.of("Shutting down vending machine"));
        printTable(rows);
        System.exit(0);
    }
}
