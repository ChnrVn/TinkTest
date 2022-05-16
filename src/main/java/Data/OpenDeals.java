package Data;

import java.util.Comparator;
import java.util.List;

public class OpenDeals {
    private List<Deal> openDeals;

    public OpenDeals() {
        this.openDeals = List.of();
    }

    public void addDeal(Deal deal) {
        openDeals.add(deal);
    }

    private void deleteDeal(Deal deal) {
        openDeals.remove(deal);
    }

    public void deletePartly(Deal deal, int numberOfSold) {
        addDeal(new Deal(deal.getShareNumber() - numberOfSold, deal.getPrice(), deal.getStopPrice()));
        deleteDeal(deal);
    }

    public List<Deal> getOpenDeals() {
        return this.openDeals;
    }

    public void sortByPrices() {
        this.openDeals.sort(Comparator.comparing(Deal::getPrice));
    }

    public double getAveragePrice() {
        double price = 0;
        for (Deal deal: openDeals) {
            price += deal.getPrice();
        }
        return price;
    }

    @Override
    public String toString() {
        return "Кол-во сделок: " + this.openDeals.size()
                + "Средняя стоимость всех акций: " + getAveragePrice();
    }
}