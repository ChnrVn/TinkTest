package Proccesor;

import Data.Company;
import Data.CompanyCollection;
import Exceptions.CompanyNotFoundException;
import Data.IndexType;
import Exceptions.NotEnoughMoneyToTradeException;
import ru.tinkoff.piapi.contract.v1.Candle;
import ru.tinkoff.piapi.contract.v1.MarketDataResponse;

/**
 * Class used for process responses from exchange. Used in CandleStream.
 * CandleStream call method process on each response async.
 * In case response is candle, and time from last candle for current company more than given time,
 * Method processes calculate all indexes, Solver calculates probability to buy/sell, based on indexes
 * and after all company with updated indexes and calculated probability will go to trader, that will
 * buy/sell some amount of stocks
 * Final pipeline: CandleStream -> IndexCalculators -> Solver -> Trader
 */
public class DataStreamProcessor {
    CompanyCollection companies;
    Trader trader;

    Candle curCandle;
    Company curCandleCompany;
    IndexCalculators i;

    public DataStreamProcessor(CompanyCollection companies, Trader trader){
        this.companies = companies;
        this.trader = trader;
        this.i = new IndexCalculators();
    }

    /**
    * Method processes calculate all indexes, Solver calculates probability to buy/sell, based on indexes
    * and after all company with updated indexes and calculated probability will go to trader, that will
     * buy/sell some amount of stocks
     * Final pipeline: CandleStream -> IndexCalculators -> Solver -> Trader
     */
    public void process(MarketDataResponse marketDataResponse) {
            if(marketDataResponse.hasCandle()) {
                curCandle = marketDataResponse.getCandle();
                try {
                    curCandleCompany = companies.getByFigi(curCandle.getFigi()); //get company of candle

                    //Check if candle is on new timestamp for some index. Different indexes has different timeframes
                    if (checkIfNewCandleForIndex(IndexType.RSI, marketDataResponse, curCandleCompany)) {

                        //Sell if some deals reached stopLoss
                        trader.sellIfStopPrice(curCandleCompany, curCandle);

                        //Calculate each index if candle is on new timestamp for this index
                        //todo: calculate index only if candle is on new timestamp for this index
                        for (IndexType index : IndexType.values()) {
                            curCandleCompany.setIndexValue(index
                                    , i.getCalcByIndex(index).calculateIndex(curCandleCompany, curCandle));
                        }


                        trader.trade(curCandleCompany, curCandle,
                                Solver.solution(curCandleCompany)); //solver calculates probability to buy/sell based on indexes


                    }


                } catch (CompanyNotFoundException | NotEnoughMoneyToTradeException | io.grpc.StatusRuntimeException e ) {
                    e.printStackTrace();
                }
            }

    }

    /**
     * Check if new candle older that previous on the index timestap
     */
    private boolean checkIfNewCandleForIndex(IndexType type, MarketDataResponse marketDataResponse, Company company){

        switch(type){
            case RSI:
                return company.getIndexByType(IndexType.RSI)
                        .getTimeOfLastEl() != marketDataResponse.getCandle().getTime().getSeconds();
            case NVI:
            case PVI:
                return false; //todo: проверка, что инфа за новый день пришла
            default:
                return false;
        }
    }
}


