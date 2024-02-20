package ServletService;

import DTO.CurrencyDTO;
import DTO.ExchangeDTO;
import Serves.ConnectionDB;
import Serves.ErrorMessage;
import Serves.FindService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateServlet {
    private PreparedStatement statement;
    private ObjectMapper objectMapper = new ObjectMapper();

    public void getAllExchangeRates(HttpServletRequest req, HttpServletResponse resp) {
        ConnectionDB connection = new ConnectionDB();
        List<ExchangeDTO> exchangeDTOS = new ArrayList<>();
        try {
            String SQL = "SELECT * FROM exchangerates";
            statement = connection.getConnection().prepareStatement(SQL);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                int baseCurrencyId = resultSet.getInt("BaseCurrencyID");
                int targetCurrencyId = resultSet.getInt("TargetCurrencyID");

                // Получаем информацию о базовой и целевой валютах из предварительно загруженного словаря
                CurrencyDTO baseCurrency = FindService.findCurrencyById(resp,baseCurrencyId);
                CurrencyDTO targetCurrency = FindService.findCurrencyById(resp,targetCurrencyId);
                ExchangeDTO exchangeDTO = new ExchangeDTO(
                        resultSet.getInt("ID"),
                        baseCurrency,
                        targetCurrency,
                        resultSet.getDouble("Rate")
                );
                exchangeDTOS.add(exchangeDTO);
            }
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exchangeDTOS);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            PrintWriter printWriter = resp.getWriter();

            printWriter.println(json);
            printWriter.flush();
        } catch (SQLException | IOException e) {
            ErrorMessage.sendErrorMessage(resp,500,"Internal Server Error");

        }
    }

    public void addNewExchangeRates(HttpServletRequest req, HttpServletResponse resp) {
        ConnectionDB connection = new ConnectionDB();
        String baseCurrency = req.getParameter("baseCurrencyCode");
        String targetCurrency = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");
        if(baseCurrency == null || targetCurrency == null || rate == null){
            ErrorMessage.sendErrorMessage(resp,400,"Bad Request");
        }

        int baseCurrencyId = FindService.findCurrencyByCode(resp,baseCurrency);
        int targetCurrencyId = FindService.findCurrencyByCode(resp,targetCurrency);
        double rateId = Double.parseDouble(rate);
        if(baseCurrencyId < 0 || targetCurrencyId < 0){
            ErrorMessage.sendErrorMessage(resp,404,"Not Found");
        }

        if(exchangeRateExists(resp,baseCurrencyId,targetCurrencyId)){
            ErrorMessage.sendErrorMessage(resp,409,"Conflict");
            return;
        }
        try {
            String SQL = "INSERT INTO exchangerates VALUES(?,?,?,?)";
            PreparedStatement statement = connection.getConnection().prepareStatement(SQL);



            statement.setInt(1,lastIdInsert());
            statement.setInt(2,baseCurrencyId);
            statement.setInt(3, targetCurrencyId);
            statement.setDouble(4,rateId);

            CurrencyDTO baseCurrencyDTO = FindService.findCurrencyById(resp,baseCurrencyId);
            CurrencyDTO targetCurrencyDTO = FindService.findCurrencyById(resp,targetCurrencyId);

            int rowsAffected = statement.executeUpdate();
            if(rowsAffected > 0 ){
                resp.setStatus(201);
                ExchangeDTO exchangeDTO = new ExchangeDTO(
                        lastIdInsert()-1,
                        baseCurrencyDTO,
                        targetCurrencyDTO,
                        rateId
                );
                String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exchangeDTO);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                PrintWriter printWriter = resp.getWriter();

                printWriter.println(json);
                printWriter.flush();
            }
        } catch (SQLException | IOException e) {
            ErrorMessage.sendErrorMessage(resp,500, "Internal Server Error");
        }

    }
    public boolean exchangeRateExists(HttpServletResponse resp, int baseCurrencyId, int targetCurrencyId){
        ConnectionDB connectionDB = new ConnectionDB();
        boolean returnValue = false;
        try{
            String SQL = "SELECT COUNT(*) FROM exchangerates WHERE BaseCurrencyID = ? AND TargetCurrencyID = ?";
            statement = connectionDB.getConnection().prepareStatement(SQL);
            statement.setInt(1,baseCurrencyId);
            statement.setInt(2,targetCurrencyId);

            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                returnValue = resultSet.getInt(1) > 0;
            }

        } catch (SQLException e) {
            ErrorMessage.sendErrorMessage(resp,500,"Internal Server Error");
        }
        return returnValue;
    }
    public int lastIdInsert(){
        ConnectionDB connection = new ConnectionDB();
        int lastId = -1;
        try {
            String SQL = "SELECT MAX(ID) FROM exchangerates";
            statement = connection.getConnection().prepareStatement(SQL);

            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                lastId = resultSet.getInt(1) ;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lastId+1;
    }
}
