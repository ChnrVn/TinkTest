import Commands.*;
import Connection.Connector;
import Data.*;
import Exceptions.AccountNotFoundException;
import Exceptions.CommandException;
import Proccesor.DataStreamProcessor;
import Proccesor.Trader;
import UI.Console.Console;
import ru.tinkoff.piapi.contract.v1.*;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.exception.ApiRuntimeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    static final String appName = "ChnrVn";

    public static void main(String[] args) {

        try (Scanner userScanner = new Scanner(System.in)) {

            InvestApi api = initializeApi(userScanner);
            String accountId = chooseAccount(api, userScanner);

            CompanyCollection companyCollection = new CompanyCollection();
            Connector connector = new Connector(api, companyCollection, accountId);
            Trader trader = new Trader(connector, api);
            DataStreamProcessor dataProc = new DataStreamProcessor(companyCollection, trader);

            connector.initializeStreams(dataProc);

           // testRealTrade( connector,  companyCollection,  api, connector.getTradeStream());
            CompanyBuilder companyBuilder = new CompanyBuilder(userScanner);
            CommandManager commandManager = new CommandManager(
                    new HelpCommand(),
                    new AddCompanyCommand(companyBuilder, companyCollection, connector),
                    new ChangeCompanyCommand(companyBuilder, companyCollection, connector),
                    new DeleteCommand(companyCollection, connector.getCandleStream()),
                    new PrintScheduleCommand(connector),
                    new PrintScheduleForThisDayCommand(connector),
                    new ShowCommand(companyCollection),
                    new ShowFigisCommand(companyCollection),
                    new StartAllCommand(companyCollection, connector.getCandleStream()),
                    new StartTradeCommand(companyCollection, connector.getCandleStream()),
                    new StopAllCommand(companyCollection, connector.getCandleStream()),
                    new StopTradeCommand(companyCollection, connector.getCandleStream()));

            Console console = new Console(commandManager, userScanner);
            console.interactiveMode();
        } catch (CommandException exception) {
            Console.printError(exception.getMessage());
        } catch (ApiRuntimeException exception) {
            Console.printError("Internal critical error, try to reboot, please");
        }
    }

    private static InvestApi initializeApi(Scanner userScanner) {
        InvestApi api;
        String token;

        while (true) {
            try {
                Console.println("Input your token:");
                Console.print("> ");
                token = userScanner.nextLine().trim();
                if (token.isEmpty()) throw new IllegalArgumentException();

                api = InvestApi.create(token, appName);
                api.getUserService().getAccountsSync();
                break;
            } catch (IllegalArgumentException exception) {
                Console.printError("Invalid token!");
            } catch (ApiRuntimeException exception) {
                Console.printError("Can't create API with this token, try again");
            }
        }

        return api;
    }

    private static String chooseAccount(InvestApi api, Scanner userScanner) {

        List<Account> accounts = api.getUserService().getAccountsSync();
        List<Account> tinkoffAccounts = new ArrayList<>();
        String accountId = "";
        int userInput;
        int number = 1;

        try {
            for (Account account: accounts) {
                if (account.getType() == AccountType.ACCOUNT_TYPE_TINKOFF) {
                    tinkoffAccounts.add(account);
                    Console.println("Account #" + number++
                            + "\nName: " + account.getName()
                            + "\nId: " + account.getId());
                }
            }

            if (tinkoffAccounts.isEmpty()) throw new AccountNotFoundException("Accounts not found");

            while (true) {
                try {
                    Console.println("Account choosing (type number from 1)");
                    Console.print("> ");
                    userInput = Integer.parseInt(userScanner.nextLine());
                    if (userInput < 1 || userInput > tinkoffAccounts.size()) throw new IllegalArgumentException("Invalid number of account, try again");
                    accountId = tinkoffAccounts.get(userInput - 1).getId();
                    if (accountId == "") throw new AccountNotFoundException("Accounts not found");
                    break;
                } catch (IllegalArgumentException exception) {
                    Console.printError(exception.getMessage());
                }

            }
        } catch (AccountNotFoundException exception) {
            Console.printError(exception.getMessage());
        }
        Console.println("Success");
        return accountId;
    }
}



