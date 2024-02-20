package DTO;

public class ExchangeDTO {
    private int id;
    private CurrencyDTO baseCurrencyId;
    private CurrencyDTO targetCurrencyId;
    private double rate;

    public ExchangeDTO(int id, CurrencyDTO baseCurrencyId, CurrencyDTO targetCurrencyId, double rate) {
        this.id = id;
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.rate = rate;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public CurrencyDTO getBaseCurrencyId() {
        return baseCurrencyId;
    }

    public void setBaseCurrencyId(CurrencyDTO baseCurrencyId) {
        this.baseCurrencyId = baseCurrencyId;
    }

    public CurrencyDTO getTargetCurrencyId() {
        return targetCurrencyId;
    }

    public void setTargetCurrencyId(CurrencyDTO targetCurrencyId) {
        this.targetCurrencyId = targetCurrencyId;
    }


    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
