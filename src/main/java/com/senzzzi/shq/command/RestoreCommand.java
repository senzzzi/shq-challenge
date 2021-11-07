package com.senzzzi.shq.command;

import com.senzzzi.shq.service.InitialDataService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.senzzzi.shq.util.PrintUtil.printTable;

@Component
@RequiredArgsConstructor
public class RestoreCommand implements Command {

    private final InitialDataService initialDataService;

    public String getCommand() {
        return "restore";
    }

    @Override
    public String getDescription() {
        return "Restores initial data (both quotes and coins)";
    }

    @Override
    public Options getOptions() {
        Options options = new Options();
        options.addOption(Option
                .builder("h")
                .desc("show helper")
                .required(false)
                .hasArg(false)
                .longOpt("help")
                .build());
        return options;
    }

    @Override
    public void run(CommandLine commandLine) {
        initialDataService.deleteAllData();
        initialDataService.populateInitialData();
        List<List<String>> rows = new ArrayList<>();
        rows.add(List.of("Initial data restored"));
        printTable(rows);
    }
}
