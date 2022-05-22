package Data;

import Exceptions.IllegalCommandArgsException;
import UI.Console.Console;

import java.math.BigDecimal;
import java.util.Scanner;

public class CompanyBuilder {
    private final Scanner userScanner;

    public CompanyBuilder(Scanner userScanner) {
        this.userScanner = userScanner;
    }

    public BigDecimal askAllowedMoney() {
        BigDecimal value;

        while (true) {
            try {
                Console.println("Input new value of money that you want to trade:");
                Console.print("> ");
                String stringValue = userScanner.nextLine().trim();
                value = new BigDecimal(stringValue);

                if (value.compareTo(BigDecimal.ZERO) < 0) throw new IllegalCommandArgsException();
                break;
            } catch (IllegalCommandArgsException exception) {
                Console.printError("Invalid value");
            }
        }

        return value;
    }

    public double askLossPercent() {
        double loss;

        while (true) {
            try {
                Console.println("Input loss-percent from 0 to 100:");
                Console.print("> ");
                String stringValue = userScanner.nextLine().trim();
                loss = Double.parseDouble(stringValue);

                if (loss < 0 || loss > 100) throw new IllegalCommandArgsException();
                break;
            } catch (IllegalCommandArgsException exception) {
                Console.printError("Invalid value");
            }
        }

        return loss;
    }

    public double askTakeProfit() {
        double takeProfit;

        while (true) {
            try {
                Console.println("Input take-profit:");
                Console.print("> ");
                String stringValue = userScanner.nextLine().trim();
                takeProfit = Double.parseDouble(stringValue);

                if (takeProfit < 0) throw new IllegalCommandArgsException();
                break;
            } catch (IllegalCommandArgsException exception) {
                Console.printError("Invalid value");
            }
        }

        return takeProfit;
    }

    public double askMoneyToTrade() {
        double value;

        while (true) {
            try {
                Console.println("Input value of money-to-trade:");
                Console.print("> ");
                String stringValue = userScanner.nextLine().trim();
                value = Double.parseDouble(stringValue);

                if (value < 0) throw new IllegalCommandArgsException();
                break;
            } catch (IllegalCommandArgsException exception) {
                Console.printError("Invalid value");
            }
        }

        return value;
    }

    public String askNameOfExchange() {
        String name;

        while (true) {
            try {
                Console.println("Input value name of exchange:");
                Console.print("> ");
                name = userScanner.nextLine().trim();

                if (name.isEmpty()) throw new IllegalCommandArgsException();
                break;
            } catch (IllegalCommandArgsException exception) {
                Console.printError("Invalid name (example: moex, spb)");
            }
        }

        return name;
    }
}
