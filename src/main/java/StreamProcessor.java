import Connection.CandleStream;
import ru.tinkoff.piapi.contract.v1.Candle;

public class StreamProcessor {
    CompaniesForTrading companies;
    CandleStream candleStream;



    public void process(Candle candle)  {

        try {
            //���������� � ��������, ��� ������� ������ �����
            Company comp = companies.getByFigi(candle.getFigi());

            //������� ������� ��� ������� �����
           // comp.setIndexValue(IndexType.RSI,
            //        RSICalculator.calculateIndex(comp, candle));


        } catch (CompanyNotFoundException e) {
            e.printStackTrace();
        }
    }
}
