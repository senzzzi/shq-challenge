package com.senzzzi.shq.command;

import com.senzzzi.shq.model.CoinValue;
import com.senzzzi.shq.model.PurchaseDTO;
import com.senzzzi.shq.model.persistence.QuoteEntity;
import com.senzzzi.shq.service.StateService;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BuyCommandTest {

    private BuyCommand target;

    @Mock
    private StateService stateService;

    private final CommandLineParser commandLineParser = new DefaultParser();

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    public void setUp() {
        if (target == null) {
            target = new BuyCommand(stateService);
        }
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    public void cleanUp() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void testBuyCommand() throws ParseException {
        when(stateService.startBuying(1L)).thenReturn(PurchaseDTO.builder()
                        .remainingMoney(10)
                        .quote(QuoteEntity.builder()
                                .quote("asd")
                                .available(true)
                                .price(100)
                                .id(1L)
                                .build())
                        .coins(Map.of(CoinValue.COIN_25, 1))
                .build());


        CommandLine line = commandLineParser.parse(target.getOptions(), new String[]{"-id", "1"});
        target.run(line);
        assertEquals("Quote Id  Price  Remaining  0.01$  0.05$  0.10$  0.25$  0.5$  1$  \n" +
                "1         1.00$  0.10$      0      0      0      1      0     0   \n" +
                "\n", outContent.toString());

    }

}
