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

public class ExchangeSpecificServlet {
    private PreparedStatement statement;
    private ObjectMapper objectMapper = new ObjectMapper();
    public void getSpecificExchangeRate(HttpServletResponse resp, HttpServletRequest req){
        ConnectionDB connection = new ConnectionDB();
        String pathInfo = req.getPathInfo();
        List<ExchangeDTO> exchangeDTOS = new ArrayList<>();
        // /USDEUR
        if(pathInfo.length() == 7 && pathInfo != null){
            String baseCurrency = pathInfo.substring(1,4);
            String targetCurrency = pathInfo.substring(4,7);
            int baseCurrencyId = FindService.findCurrencyByCode(resp,baseCurrency);
            int targetCurrencyId = FindService.findCurrencyByCode(resp,targetCurrency);
            if(baseCurrencyId < 0 || targetCurrencyId < 0) {
                ErrorMessage.sendErrorMessage(resp, 404, "Not Found");
            }
            try {
                String SQL = "SELECT * FROM exchangerates WHERE BaseCurrencyID = ? AND TargetCurrencyID = ?";
                statement = connection.getConnection().prepareStatement(SQL);
                statement.setInt(1,baseCurrencyId);
                statement.setInt(2,targetCurrencyId);
                CurrencyDTO baseDTO = FindService.findCurrencyById(resp,baseCurrencyId);
                CurrencyDTO targetDTO = FindService.findCurrencyById(resp,targetCurrencyId);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()){
                    ExchangeDTO exchangeDTO = new ExchangeDTO(
                            resultSet.getInt("ID"),
                            baseDTO,
                            targetDTO,
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
        } else {
            ErrorMessage.sendErrorMessage(resp,400,"Bad Request");
        }
    }

    public void updateSpecificExchangeRate(HttpServletResponse resp, HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        String rate = req.getParameter("rate");
        if(rate.isEmpty() || rate == null){
            ErrorMessage.sendErrorMessage(resp,400,"Bad Request");
            return;
        }
        double newRate = Double.parseDouble(rate);
        ConnectionDB connection = new ConnectionDB();
        if(pathInfo.length() == 7 && pathInfo != null) {
            String baseCurrency = pathInfo.substring(1, 4);
            String targetCurrency = pathInfo.substring(4, 7);
            int baseCurrencyId = FindService.findCurrencyByCode(resp, baseCurrency);
            int targetCurrencyId = FindService.findCurrencyByCode(resp, targetCurrency);
            if (baseCurrencyId < 0 || targetCurrencyId < 0) {
                ErrorMessage.sendErrorMessage(resp, 404, "Not Found");
            }
            try {
                String SQL = "UPDATE exchangerates SET Rate = ? WHERE BaseCurrencyID = ? AND TargetCurrencyID = ?";
                statement = connection.getConnection().prepareStatement(SQL);
                statement.setDouble(1,newRate);
                statement.setInt(2,baseCurrencyId);
                statement.setInt(3,targetCurrencyId);

                CurrencyDTO baseCurrencyDTO = FindService.findCurrencyById(resp,baseCurrencyId);
                CurrencyDTO targetCurrencyDTO = FindService.findCurrencyById(resp,targetCurrencyId);

                int rowsAffected = statement.executeUpdate();
                if(rowsAffected > 0 ){
                    int ID = FindService.findExchangeRateId(resp,baseCurrencyId,targetCurrencyId);
                    resp.setStatus(200);
                    ExchangeDTO exchangeDTO = new ExchangeDTO(
                            ID,
                            baseCurrencyDTO,
                            targetCurrencyDTO,
                            newRate
                    );
                    String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exchangeDTO);
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    PrintWriter printWriter = resp.getWriter();

                    printWriter.println(json);
                    printWriter.flush();
                }

            } catch (SQLException | IOException e) {
                ErrorMessage.sendErrorMessage(resp,500,"Internal Server Error");
            }
        }else{
            ErrorMessage.sendErrorMessage(resp,400,"Bad Request");
        }
    }
}
