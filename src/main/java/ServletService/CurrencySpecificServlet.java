package ServletService;

import DTO.CurrencyDTO;
import Serves.ConnectionDB;
import Serves.ErrorMessage;
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


public class CurrencySpecificServlet {
    private PreparedStatement statement;
    private ObjectMapper objectMapper = new ObjectMapper();
    public void getSpecificCurrencies(HttpServletResponse resp, HttpServletRequest req){
        ConnectionDB connection = new ConnectionDB();
        String pathInfo = req.getPathInfo();
        List<CurrencyDTO> currencyDTOS = new ArrayList<>();
        if(pathInfo != null && pathInfo.length() > 1){
            String currencyCode = pathInfo.substring(1);
            try {
                String SQL = "SELECT * FROM currencies WHERE Code = ?";
                statement = connection.getConnection().prepareStatement(SQL);
                statement.setString(1,currencyCode);
                ResultSet resultSet = statement.executeQuery();
                boolean found = false;
                while (resultSet.next()){
                    found = true;
                    CurrencyDTO currencyDTO = new CurrencyDTO(
                            resultSet.getInt("ID"),
                            resultSet.getString("FullName"),
                            resultSet.getString("Code"),
                            resultSet.getString("Sign")
                    );
                    currencyDTOS.add(currencyDTO);
                }
                if(!found){
                    ErrorMessage.sendErrorMessage(resp,404,"Not Found");
                    return;
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
        }else{
            ErrorMessage.sendErrorMessage(resp,400,"Bad Request");
        }
    }


}
