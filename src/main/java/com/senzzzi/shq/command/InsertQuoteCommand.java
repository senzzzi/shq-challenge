package com.senzzzi.shq.command;

import com.senzzzi.shq.model.CoinValue;
import com.senzzzi.shq.model.PurchaseDTO;
import com.senzzzi.shq.repository.QuoteRepository;
import com.senzzzi.shq.service.StateService;
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
                .builder("-q")
                .desc("quote text")
                .required(true)
                .hasArg(true)
                .longOpt("quote")
                .build());
        options.addOption(Option
                .builder("-p")
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
        String price = commandLine.getOptionValue("price");

        Integer coinValue = Integer.valueOf(id);
        PurchaseDTO purchaseDTO = stateService.addCoin(CoinValue.fromValue(coinValue));
        List<List<String>> rows = new ArrayList<>();

        if (purchaseDTO.getRemainingMoney() <= 0) {

            purchaseDTO = stateService.finalizePurchase();

            rows.add(List.of("Giving back coins.."));
            printTable(rows);
            rows = new ArrayList<>();
            rows.add(List.of("0.01$", "0.05$", "0.10$", "0.25$", "0.5$", "1$"));
            rows.add(List.of(String.valueOf(purchaseDTO.getCoins().getOrDefault(CoinValue.COIN_1, 0)),
                    String.valueOf(purchaseDTO.getCoins().getOrDefault(CoinValue.COIN_5, 0)),
                    String.valueOf(purchaseDTO.getCoins().getOrDefault(CoinValue.COIN_10, 0)),
                    String.valueOf(purchaseDTO.getCoins().getOrDefault(CoinValue.COIN_25, 0)),
                    String.valueOf(purchaseDTO.getCoins().getOrDefault(CoinValue.COIN_50, 0)),
                    String.valueOf(purchaseDTO.getCoins().getOrDefault(CoinValue.COIN_100, 0))));
            printTable(rows);

            rows = new ArrayList<>();
            rows.add(List.of("Here is your quote, have a great day!"));
            rows.add(List.of(purchaseDTO.getQuote().getQuote()));
            printTable(rows);


        } else {
            rows.add(List.of("Quote Id", "Price" , "Remaining", "0.01$", "0.05$", "0.10$", "0.25$", "0.5$", "1$"));
            rows.add(List.of(String.valueOf(purchaseDTO.getQuote().getId()),
                    prettifyCentsValue(purchaseDTO.getQuote().getPrice()),
                    prettifyCentsValue(purchaseDTO.getRemainingMoney()),
                    String.valueOf(purchaseDTO.getCoins().getOrDefault(CoinValue.COIN_1, 0)),
                    String.valueOf(purchaseDTO.getCoins().getOrDefault(CoinValue.COIN_5, 0)),
                    String.valueOf(purchaseDTO.getCoins().getOrDefault(CoinValue.COIN_10, 0)),
                    String.valueOf(purchaseDTO.getCoins().getOrDefault(CoinValue.COIN_25, 0)),
                    String.valueOf(purchaseDTO.getCoins().getOrDefault(CoinValue.COIN_50, 0)),
                    String.valueOf(purchaseDTO.getCoins().getOrDefault(CoinValue.COIN_100, 0))));
            printTable(rows);
        }

    }
}
