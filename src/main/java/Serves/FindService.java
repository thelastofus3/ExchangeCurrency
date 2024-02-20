package Serves;

import DTO.CurrencyDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class FindService {
    public static CurrencyDTO findCurrencyById(HttpServletResponse resp, int id){
        ConnectionDB connection = new ConnectionDB();
        String SQL = "SELECT * FROM currencies WHERE ID = ?";
        CurrencyDTO currencyDTO = null;
        try (PreparedStatement statement = connection.getConnection().prepareStatement(SQL)) {
            statement.setInt(1,id);

            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                currencyDTO = new CurrencyDTO(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4)
                );
            }
        } catch (SQLException e) {
            ErrorMessage.sendErrorMessage(resp,500,"Internal Server Error");
        }
        return  currencyDTO;
    }
    public static int findCurrencyByCode(HttpServletResponse resp, String code){
        ConnectionDB connection = new ConnectionDB();
        int valueOfCurrency = -1;
        String SQL = "SELECT ID FROM currencies WHERE Code = ?";
        try (PreparedStatement statement = connection.getConnection().prepareStatement(SQL)){
            statement.setString(1,code);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                valueOfCurrency = resultSet.getInt(1);
                return valueOfCurrency;
            }
        } catch (SQLException e) {
            ErrorMessage.sendErrorMessage(resp,500,"Internal Server Error");
        }
        return -2;
    }
    public static int findExchangeRateId(HttpServletResponse resp, int baseCurrency,int targetCurrency){
        ConnectionDB connection = new ConnectionDB();
        int valueOfCurrency = -1;
        String SQL = "SELECT ID FROM exchangerates WHERE BaseCurrencyID = ? AND TargetCurrencyID = ?";
        try(PreparedStatement statement = connection.getConnection().prepareStatement(SQL)) {
            statement.setInt(1,baseCurrency);
            statement.setInt(2,targetCurrency);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                valueOfCurrency = resultSet.getInt(1);
                return valueOfCurrency;
            }
        } catch (SQLException e) {
            ErrorMessage.sendErrorMessage(resp,500,"Internal Server Error");
        }
        return -2;
    }
    public static int lastIdInsert(){
        ConnectionDB connection = new ConnectionDB();
        int lastId = 0;
        try {
            String SQL = "SELECT MAX(ID) FROM exchangerates";
            PreparedStatement statement = connection.getConnection().prepareStatement(SQL);

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
