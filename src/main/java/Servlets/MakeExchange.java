package Servlets;

import ServletService.ExchangeCalculate;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/exchange")
public class MakeExchange extends HttpServlet {
    ExchangeCalculate actionServlet = new ExchangeCalculate();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        actionServlet.calculateCurrency(resp,req);
    }
}
