package Commands;

import Data.Company;
import Data.CompanyBuilder;
import Data.CompanyCollection;
import Exceptions.CommandException;
import Exceptions.IllegalCommandArgsException;
import UI.Console.Console;

public class AddCompanyCommand extends AbstractCommand{
    private final CompanyBuilder companyBuilder;
    private final CompanyCollection companyCollection;

    public AddCompanyCommand(CompanyBuilder companyBuilder, CompanyCollection companyCollection) {
        super("add {figi}", "Add company by figi with your parameters");
        this.companyBuilder = companyBuilder;
        this.companyCollection = companyCollection;
    }

    /**
     * @param argument - figi, moneyToTrade, lossPercent, takeProfit
     */
    @Override
    public boolean execute(String argument) throws CommandException {
        try {
            if (argument.isEmpty()) throw new IllegalCommandArgsException();
            double moneyToTrade = companyBuilder.askMoneyToTrade();
            double lossPercent = companyBuilder.askLossPercent();
            double takeProfit = companyBuilder.askTakeProfit();
            Company company = new Company(argument, moneyToTrade, lossPercent, takeProfit, 2);
            companyCollection.putCompanyByFigi(argument, company);
        } catch (IllegalCommandArgsException exception) {
            Console.printError("The command was entered in the wrong format!");
        }

        return true;
    }
}