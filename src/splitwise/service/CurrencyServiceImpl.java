package splitwise.service;

import splitwise.exception.InvalidCurrencyException;
import splitwise.model.Currency;
import splitwise.util.CurrencyConverter;

/**
 * Implementation of CurrencyService.
 * Single Responsibility: Only handles currency conversion operations.
 * Open/Closed: Uses CurrencyConverter utility that can be extended.
 */
public class CurrencyServiceImpl implements CurrencyService {
    private final CurrencyConverter converter;

    public CurrencyServiceImpl() {
        this.converter = new CurrencyConverter();
    }

    public CurrencyServiceImpl(CurrencyConverter converter) {
        this.converter = converter;
    }

    @Override
    public double convert(double amount, Currency from, Currency to) {
        if (!isConversionAvailable(from, to)) {
            throw new InvalidCurrencyException(from.name(), to.name());
        }
        return converter.convert(amount, from, to);
    }

    @Override
    public void updateExchangeRate(Currency from, Currency to, double rate) {
        converter.setExchangeRate(from, to, rate);
    }

    @Override
    public double getExchangeRate(Currency from, Currency to) {
        return converter.getExchangeRate(from, to);
    }

    @Override
    public boolean isConversionAvailable(Currency from, Currency to) {
        try {
            converter.getExchangeRate(from, to);
            return true;
        } catch (InvalidCurrencyException e) {
            return false;
        }
    }

    @Override
    public String format(double amount, Currency currency) {
        return currency.format(amount);
    }
}
