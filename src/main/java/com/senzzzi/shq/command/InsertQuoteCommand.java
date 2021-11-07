package com.senzzzi.shq.command;

import com.senzzzi.shq.exception.QuoteInvalidPriceException;
import com.senzzzi.shq.exception.QuoteWithoutTextException;
import com.senzzzi.shq.model.CoinValue;
import com.senzzzi.shq.model.PurchaseDTO;
import com.senzzzi.shq.model.persistence.QuoteEntity;
import com.senzzzi.shq.repository.QuoteRepository;
import com.senzzzi.shq.service.StateService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.senzzzi.shq.util.PrintUtil.prettifyCentsValue;
import static com.senzzzi.shq.util.PrintUtil.printTable;

@Component
@RequiredArgsConstructor
public class InsertQuoteCommand implements Command {

    private final QuoteRepository quoteRepository;

    public String getCommand() {
        return "quote-add";
    }

    @Override
    public String getDescription() {
        return "Insert a new quote - only for maintenance purposes";
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
        options.addOption(Option
                .builder("q")
                .desc("quote text")
                .required(true)
                .hasArg(true)
                .longOpt("quote")
                .build());
        options.addOption(Option
                .builder("p")
                .desc("price in cents")
                .required(true)
                .hasArg(true)
                .longOpt("price")
                .build());
        return options;
    }

    @Override
    public void run(CommandLine commandLine) {
        String quote = commandLine.getOptionValue("quote");
        int price = Integer.parseInt(commandLine.getOptionValue("price"));

        if (!StringUtils.hasText(quote)) {
            throw new QuoteWithoutTextException("Quote needs to have a text");
        }

        if (price <= 0) {
            throw new QuoteInvalidPriceException("Quote has an invalid price");
        }

        QuoteEntity quoteEntity = quoteRepository.save(QuoteEntity.builder()
                        .quote(quote)
                        .price(price)
                        .available(true)
                        .build());

        List<List<String>> rows = new ArrayList<>();
        rows.add(List.of("Quote Successfully added with id " + quoteEntity.getId()));
        printTable(rows);
    }
}
