package ServletService;

import DTO.CurrencyDTO;
import Serves.ConnectionDB;
import Serves.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CurrenciesServlet {
    private PreparedStatement statement;
    private ObjectMapper objectMapper = new ObjectMapper();


    public void getAllCurrencies(HttpServletResponse resp) {
        ConnectionDB connection = new ConnectionDB();
        List<CurrencyDTO> currencyDTOS = new ArrayList<>();
        try {
            String SQL = "SELECT * FROM currencies";
            statement = connection.getConnection().prepareStatement(SQL);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                CurrencyDTO currencyDTO = new CurrencyDTO(
                       resultSet.getInt("ID"),
                       resultSet.getString("FullName"),
                        resultSet.getString("Code"),
                        resultSet.getString("Sign")
                );
                currencyDTOS.add(currencyDTO);
            }
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(currencyDTOS);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            PrintWriter printWriter = resp.getWriter();

            printWriter.println(json);
            printWriter.flush();
        } catch (SQLException | IOException e) {
            ErrorMessage.sendErrorMessage(resp,500,"Internal Server Error");

        }
    }

    public void addNewCurrency(HttpServletRequest req, HttpServletResponse resp) {
        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");
        if (name == null || code == null || sign ==null){
            ErrorMessage.sendErrorMessage(resp,400,"Bad Request");
            return;
        }
        if(currencyWithCodeExists(resp,code)){
            ErrorMessage.sendErrorMessage(resp,409,"Conflict");
        }
        ConnectionDB connection = new ConnectionDB();
        try {
            String SQL = "INSERT INTO currencies VALUES (?,?,?,?)";
            PreparedStatement statement = connection.getConnection().prepareStatement(SQL);

            statement.setInt(1,lastIdInsert());
            statement.setString(2,code);
            statement.setString(3,name);
            statement.setString(4,sign);

            int rowsAffected =  statement.executeUpdate();
            if(rowsAffected > 0){
                resp.setStatus(201);
                CurrencyDTO currencyDTO = new CurrencyDTO(
                        lastIdInsert()-1,name,code,sign
                );
                String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(currencyDTO);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                PrintWriter printWriter = resp.getWriter();

                printWriter.println(json);
                printWriter.flush();
            }else{
                ErrorMessage.sendErrorMessage(resp,500,"Internal Server Error");

            }
        } catch (SQLException | IOException e) {
            ErrorMessage.sendErrorMessage(resp,500,"Internal Server Error");

        }
    }
    public boolean currencyWithCodeExists(HttpServletResponse resp, String code){
        ConnectionDB connection = new ConnectionDB();
        boolean returnValue = false;
        try {
            String SQL = "SELECT COUNT(*) FROM currencies WHERE code = ?";
            statement = connection.getConnection().prepareStatement(SQL);
            statement.setString(1,code);

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
            String SQL = "SELECT MAX(ID) FROM currencies";
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
