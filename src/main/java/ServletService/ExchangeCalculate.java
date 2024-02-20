package ServletService;

import DTO.CalculateExchangeDTO;
import DTO.CurrencyDTO;
import Serves.ConnectionDB;
import Serves.ErrorMessage;
import Serves.FindService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLOutput;

public class ExchangeCalculate {
    private PreparedStatement statement;
    private ObjectMapper objectMapper = new ObjectMapper();
    public void calculateCurrency(HttpServletResponse resp, HttpServletRequest req){
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String amount = req.getParameter("amount");
        int baseCurrencyId = FindService.findCurrencyByCode(resp,from);
        int targetCurrencyId = FindService.findCurrencyByCode(resp,to);
        if (amount == null || amount.isEmpty()){
            ErrorMessage.sendErrorMessage(resp,400,"Bad Request");
            return;
        }
        BigDecimal amountD = new BigDecimal(amount);
        if (isStandardExchangeRateExist(resp, baseCurrencyId, targetCurrencyId)) {
            standardChange(resp,req, baseCurrencyId, targetCurrencyId, amountD);
        } else if (isStandardExchangeRateExist(resp,targetCurrencyId,baseCurrencyId)) {
            reverseChange(resp,req,targetCurrencyId,baseCurrencyId,amountD);
        } else if (isCrossExchangeRateExist(resp,baseCurrencyId, targetCurrencyId)) {
            System.out.println(3);
            crossChange(resp, req, baseCurrencyId, targetCurrencyId, amountD);
        } else {
            ErrorMessage.sendErrorMessage(resp, 400, "Exchange rate not found");
        }
    }

    private void reverseChange(HttpServletResponse resp, HttpServletRequest req, int from, int to, BigDecimal amount) {
        ConnectionDB connection = new ConnectionDB();
        try {
            String SQL = "SELECT Rate from exchangerates WHERE BaseCurrencyID = ? AND TargetCurrencyID = ?";
            statement = connection.getConnection().prepareStatement(SQL);
            statement.setInt(1,from);
            statement.setInt(2,to);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                CurrencyDTO currencyFrom = FindService.findCurrencyById(resp,from);
                CurrencyDTO currencyTo = FindService.findCurrencyById(resp,to);
                BigDecimal rate = resultSet.getBigDecimal("Rate");
                BigDecimal one = BigDecimal.ONE;
                BigDecimal inverseRate = one.divide(rate,15,RoundingMode.HALF_UP);
                BigDecimal convertedExchange = amount.multiply(inverseRate).setScale(2, RoundingMode.HALF_UP);
                CalculateExchangeDTO calculateExchangeDTO = new CalculateExchangeDTO(
                        currencyTo,
                        currencyFrom,
                        inverseRate.setScale(2,RoundingMode.HALF_UP),
                        amount,
                        convertedExchange
                );
                String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(calculateExchangeDTO);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                PrintWriter printWriter = resp.getWriter();

                printWriter.println(json);
                printWriter.flush();
            }
        } catch (SQLException | IOException e) {
            ErrorMessage.sendErrorMessage(resp,500,"Internal Server Error");
        }

    }
    private boolean isCrossExchangeRateExist(HttpServletResponse resp, int from, int to) {
        ConnectionDB connection = new ConnectionDB();
        boolean rateExists = false;
        try {
            String SQL = "SELECT COUNT(*) FROM exchangerates WHERE (BaseCurrencyID = ? AND TargetCurrencyID = ?) OR (BaseCurrencyID = ? AND TargetCurrencyID = ?)";
            statement = connection.getConnection().prepareStatement(SQL);
            statement.setInt(1,1);
            statement.setInt(2,from);
            statement.setInt(3,1);
            statement.setInt(4,to);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                int count = resultSet.getInt(1);
                rateExists = count > 1;
            }
        } catch (SQLException e) {
            ErrorMessage.sendErrorMessage(resp,500,"Internal Server Error");
        }
        return rateExists;
    }
    public boolean isStandardExchangeRateExist(HttpServletResponse resp,  int from, int to){
        ConnectionDB connection = new ConnectionDB();
        boolean rateExists = false;
        try {
            String SQL = "SELECT COUNT(*) FROM exchangerates WHERE BaseCurrencyID = ? AND TargetCurrencyID = ?";
            statement = connection.getConnection().prepareStatement(SQL);
            statement.setInt(1,from);
            statement.setInt(2,to);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                int count = resultSet.getInt(1);
                rateExists = count > 0;
            }
        } catch (SQLException e) {
            ErrorMessage.sendErrorMessage(resp,500,"Internal Server Error");
        }
        return rateExists;
    }

    private void crossChange(HttpServletResponse resp, HttpServletRequest req, int from, int to, BigDecimal amount) {
        ConnectionDB connection = new ConnectionDB();
        try {
            String SQLFrom = "SELECT Rate from exchangerates WHERE BaseCurrencyID = ? AND TargetCurrencyID = ?";
            PreparedStatement statementFrom = connection.getConnection().prepareStatement(SQLFrom);
            statementFrom.setInt(1,1);
            statementFrom.setInt(2,from);
            BigDecimal rateFrom = BigDecimal.ZERO;
            ResultSet resultSetFrom = statementFrom.executeQuery();
            if(resultSetFrom.next()){
                rateFrom = resultSetFrom.getBigDecimal("Rate");
            }

            String SQLTo = "SELECT Rate from exchangerates WHERE BaseCurrencyID = ? AND TargetCurrencyID = ?";
            PreparedStatement statementTo = connection.getConnection().prepareStatement(SQLTo);
            statementTo.setInt(1,1);
            statementTo.setInt(2,to);
            BigDecimal rateTo = BigDecimal.ONE;
            ResultSet resultSetTo = statementTo.executeQuery();
            if(resultSetTo.next()){
                rateTo = resultSetTo.getBigDecimal("Rate");
            }
            CurrencyDTO currencyFrom = FindService.findCurrencyById(resp,from);
            CurrencyDTO currencyTo = FindService.findCurrencyById(resp,to);
            BigDecimal rate = rateFrom.divide(rateTo,15,RoundingMode.HALF_UP);
            BigDecimal convertedExchange = amount.divide(rate,2,RoundingMode.UP);
            CalculateExchangeDTO calculateExchangeDTO = new CalculateExchangeDTO(
                    currencyFrom,
                    currencyTo,
                    rate.setScale(2,RoundingMode.HALF_UP),
                    amount,
                    convertedExchange
            );
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(calculateExchangeDTO);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            PrintWriter printWriter = resp.getWriter();

            printWriter.println(json);
            printWriter.flush();
        } catch (SQLException | IOException e) {
            ErrorMessage.sendErrorMessage(resp,500,"Internal Server Error");
        }
    }
    public void standardChange(HttpServletResponse resp, HttpServletRequest req, int from, int to, BigDecimal amount){
        ConnectionDB connection = new ConnectionDB();
        try {
            String SQL = "SELECT Rate from exchangerates WHERE BaseCurrencyID = ? AND TargetCurrencyID = ?";
            statement = connection.getConnection().prepareStatement(SQL);
            statement.setInt(1,from);
            statement.setInt(2,to);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                CurrencyDTO currencyFrom = FindService.findCurrencyById(resp,from);
                CurrencyDTO currencyTo = FindService.findCurrencyById(resp,to);
                BigDecimal rate = resultSet.getBigDecimal("Rate");
                BigDecimal convertedExchange = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
                CalculateExchangeDTO calculateExchangeDTO = new CalculateExchangeDTO(
                        currencyFrom,
                        currencyTo,
                        rate.setScale(2,RoundingMode.HALF_UP),
                        amount,
                        convertedExchange
                );
                String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(calculateExchangeDTO);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                PrintWriter printWriter = resp.getWriter();

                printWriter.println(json);
                printWriter.flush();
            }
        } catch (SQLException | IOException e) {
            ErrorMessage.sendErrorMessage(resp,500,"Internal Server Error");
        }

    }
}
