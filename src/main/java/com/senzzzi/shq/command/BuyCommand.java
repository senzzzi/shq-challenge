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
public class BuyCommand implements Command {

    private final StateService stateService;

    public String getCommand() {
        return "buy";
    }

    @Override
    public String getDescription() {
        return "Choose which quote to buy";
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
                .builder("i")
                .desc("quote id to buy")
                .required(true)
                .hasArg(true)
                .longOpt("id")
                .build());
        return options;
    }

    @Override
    public void run(CommandLine commandLine) {
        String id = commandLine.getOptionValue("id");

        List<List<String>> rows = new ArrayList<>();
        PurchaseDTO purchaseDTO = stateService.startBuying(Long.valueOf(id));

        if (purchaseDTO.getRemainingMoney() <= 0) {
            purchaseDTO = stateService.finalizePurchase();

            rows = new ArrayList<>();
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
