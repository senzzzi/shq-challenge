package com.senzzzi.shq.command;

import com.senzzzi.shq.model.CoinValue;
import com.senzzzi.shq.model.PurchaseDTO;
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
public class AddCoinCommand implements Command {

    private final StateService stateService;

    public String getCommand() {
        return "coin-add";
    }

    @Override
    public String getDescription() {
        return "Adds coin";
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
                .builder("v")
                .desc("coin value in cents")
                .required(true)
                .hasArg(true)
                .longOpt("value")
                .build());
        return options;
    }

    @Override
    public void run(CommandLine commandLine) {
        String id = commandLine.getOptionValue("value");
        Integer coinValue = Integer.valueOf(id);
        PurchaseDTO purchaseDTO = stateService.addCoin(CoinValue.fromValue(coinValue));
        List<List<String>> rows = new ArrayList<>();

        if (purchaseDTO.getRemainingMoney() <= 0) {

            // if enough money was already added, finalize purchase
            purchaseDTO = stateService.finalizePurchase();

            rows.add(List.of("Returning Change.."));
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
            // ask for more money
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
