package com.senzzzi.shq.command;

import com.senzzzi.shq.repository.QuoteRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.senzzzi.shq.util.PrintUtil.prettifyCentsValue;
import static com.senzzzi.shq.util.PrintUtil.printTable;

@Component
@RequiredArgsConstructor
public class ListCommand implements Command {

    private final QuoteRepository quoteRepository;

    public String getCommand() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "Lists Available Quotes To Buy";
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

        List<List<String>> rows = new ArrayList<>();
        rows.add(List.of("Quote Id", "Price"));
        quoteRepository.findAll().forEach(quote -> {
            if (quote.getAvailable()) {
                rows.add(List.of(quote.getId().toString(), prettifyCentsValue(quote.getPrice())));
            }
        });
        printTable(rows);
    }
}
