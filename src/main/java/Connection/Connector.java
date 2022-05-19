package Connection;

import Data.CompanyCollection;
import Exceptions.CompanyNotFoundException;
import Exceptions.OutNumberOfReconnectAttemptsException;
import Proccesor.StreamProcessor;
import com.google.protobuf.Timestamp;
import ru.tinkoff.piapi.contract.v1.Candle;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.TradingDay;
import ru.tinkoff.piapi.core.InvestApi;
import static ru.tinkoff.piapi.core.utils.DateUtils.timestampToString;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Queue;

/**
 * Class for unary requests (initialisations, verifications etc)
 */
public class Connector{
    private final TradeStream tradeStream;
    private final CandleStream candleStream;
    private final InvestApi api;

    public Connector(InvestApi api, CompanyCollection companies) {
        this.tradeStream = new TradeStream(api, companies);
        this.candleStream = new CandleStream(api, companies);
        this.api = api;
    }

    public TradeStream getTradeStream() {
        return this.tradeStream;
    }

    public CandleStream getCandleStream() {
        return this.candleStream;
    }

    public void initializeStreams(StreamProcessor streamProcessor) throws CompanyNotFoundException, OutNumberOfReconnectAttemptsException {
        tradeStream.initialize(streamProcessor);
        candleStream.initialize(streamProcessor);
    }

    public Date timestampToDate(Timestamp timestamp) {
        return new Date(timestamp.getSeconds() * 1000);
    }

    public boolean isAvailableNow() {
        var tradingSchedules =
                api.getInstrumentsService().getTradingScheduleSync("moex", Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS));

        var today = tradingSchedules.getDays(0);
        var now = System.currentTimeMillis() / 1000;
        if (today.getIsTradingDay()
                && now  >= today.getStartTime().getSeconds()
                && now < today.getEndTime().getSeconds()) {
            return true;
        }
        else return false;
    }

    public void printScheduleForThisDay() {
        var tradingSchedules =
                api.getInstrumentsService().getTradingScheduleSync("moex", Instant.now(), Instant.now().plus(5, ChronoUnit.DAYS));

        var today = tradingSchedules.getDays(0);
        if (today.getIsTradingDay()) {
            String startTime = new SimpleDateFormat("HH.mm.ss").format(timestampToDate(today.getStartTime()));
            String endTime = new SimpleDateFormat("HH.mm.ss").format(timestampToDate(today.getEndTime()));

            System.out.println("Расписание работы на сегодня:\nОткрытие: " + startTime + "\nЗакрытие: " + endTime);
        }
        else System.out.println("Сегодня биржа не работает");
    }

    public void printSchedule() {
        var tradingSchedules =
                api.getInstrumentsService().getTradingScheduleSync("moex", Instant.now(), Instant.now().plus(5, ChronoUnit.DAYS));

        for (TradingDay tradingDay : tradingSchedules.getDaysList()) {
            String date = new SimpleDateFormat("yyyy.MM.dd").format(timestampToDate(tradingDay.getDate()));
            String startTime = new SimpleDateFormat("HH.mm.ss").format(timestampToDate(tradingDay.getStartTime()));
            String endTime = new SimpleDateFormat("HH.mm.ss").format(timestampToDate(tradingDay.getEndTime()));

            if (tradingDay.getIsTradingDay()) {
                System.out.printf("Расписание торгов для площадки MOEX. Дата: {%s},  открытие: {%s}, закрытие: {%s}\n", date, startTime, endTime);
            } else {
                System.out.printf("Расписание торгов для площадки MOEX. Дата: {%s}. Выходной день\n", date);
            }

        }
    }


}

/*
-верификации компании по фиги
-получение портфолио
-инициализация стримов свеч и торговли
*/
