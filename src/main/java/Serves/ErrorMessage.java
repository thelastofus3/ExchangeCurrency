package Serves;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class ErrorMessage {
    public static void sendErrorMessage(HttpServletResponse resp,int statusCode,String message){
        resp.setStatus(statusCode);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        String json = "{\"message\": \"" + message + "\"}";
        try (PrintWriter writer = resp.getWriter()){
            writer.println(json);
        } catch (IOException e) {
            resp.setStatus(500);
        }
    }
}
