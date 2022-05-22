package Proccesor;

import java.math.BigDecimal;
import java.math.RoundingMode;

import Connection.Connector;
import Connection.TradeStream;
import Data.Company;
import Data.CompanyCollection;
import Data.Deal;
import Exceptions.NotEnoughMoneyToTradeException;
import ru.tinkoff.piapi.contract.v1.Candle;
import ru.tinkoff.piapi.core.InvestApi;

/**
 * Class that calculate from probability, how many stock it can buy/sell and call TradeStream methods for buying/selling
 * if user hasn't got enough money/stocks for trading, trader will give a signal
 *
 *
 */

public class Trader {
    private final TradeStream tradeStream;
    private final CompanyCollection companies;
    private final InvestApi api;

    public Trader(Connector connector, InvestApi api){
        this.tradeStream = connector.getTradeStream();
        this.api = api;
        this.companies = connector.getCompanies();
    }

    //todo: ������ ��������

    public void trade(Company company, Candle candle, double probability) throws NotEnoughMoneyToTradeException {
        System.out.println("--GOING TO TRADE--");
        System.out.println(probability);

        if(probability > 0) buyLots(probability, candle, company);

        if(probability < 0) sellLots((-1) * probability, candle, company);


    }




    public void sellIfStopPrice(Company company, Candle candle){
        BigDecimal close = MoneyQuotationProcessor.convertFromQuation(candle.getClose());
        for( Deal d  : company.getOpenDeals().getDealsAsList()){
            //���� ������� ���� ���� ����-����, �� ������� ��� ����
            if(d.getStopPrice().compareTo(close) >= 0){
                tradeStream.sellStock((d.getLotNumber()), candle.getClose(), company.getFigi(), d);
            }
        }
    }


   public void buyLots(double probability, Candle candle, Company company) throws NotEnoughMoneyToTradeException {
        long lots;

       BigDecimal closePrice = MoneyQuotationProcessor.convertFromQuation(candle.getClose());
       // ���� ���� = ���� �������� * �������� �����������
       BigDecimal lotPrice = closePrice.multiply(BigDecimal.valueOf(company.getLot()));

       BigDecimal freeMoney =  BigDecimal.valueOf(company.getFreeMoney());
       BigDecimal probab = BigDecimal.valueOf(probability);

       //���� ������� ������ �� 1 ��� � ����������� ������ 60, ����� 1 ���
       if(freeMoney.divide(lotPrice, 9, RoundingMode.HALF_DOWN).intValue() == 1 && probability > 0.6) lots =  1;
       /*��������� ������ / ���� ���� = ��������� ���������� �����, ���������� �� ����������� ��������
       ���������� ����� ���������������� �����������.
       �.�. ��� ����������� 50% ��� ����� ������������ �� 50% �� ���������� ��������� �����
       (� ��������� �� ��������, ���������� ���� � ������� �������� ���������� �����
        */
       else lots = freeMoney.divide(lotPrice, 9, RoundingMode.HALF_DOWN).multiply(probab).intValue();

       /*���� ���������� ������ �� ������ ����, � �� ����� �� ���� ������� �� ������ ����, �� ������������ ������������
       � ������������ ��������
        */
       if(lots == 0 && company.getOpenDeals().getDealsAsList().isEmpty()) throw new NotEnoughMoneyToTradeException();

       //������ �� �������
       if(lots > 0) tradeStream.buyStock(
               lots,
               candle.getClose(),
               company.getFigi()
       );

   }


    public void sellLots(double probability, Candle candle, Company company) {
        BigDecimal close = MoneyQuotationProcessor.convertFromQuation(candle.getClose());
        // ��������� ��� ��������� ���� �����������
        BigDecimal  companyTakeprofit = BigDecimal.valueOf(1 + (company.getTakeProfit() / 100));


        for(Deal d : company.getOpenDeals().getDealsAsList()){
            //���� ��������� �������� ���� ����������
            if( (d.getPrice().multiply(companyTakeprofit)).compareTo(close) <= 0){
                /*
                 � ������ ����������� 1 ���� ������� �������, ����� ���� ������������� ����� �����������
                 (������� ����� �����, ���������� � ������� �������
                 */
                if(d.getLotNumber() == 1)  tradeStream.sellStock(1, candle.getClose(), company.getFigi(), d);
                if (d.getLotNumber() > 1){
                    tradeStream.sellStock(
                            (long)(d.getLotNumber() * probability), //todo:���������� ������ ����� �����
                            candle.getClose(),
                            company.getFigi(),
                            d
                    );
                }
            }
        }
    }

}
