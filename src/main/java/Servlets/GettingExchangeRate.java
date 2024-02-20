package Servlets;

import ServletService.ExchangeRateServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/exchangeRates")
public class GettingExchangeRate extends HttpServlet {
    ExchangeRateServlet actionServlet = new ExchangeRateServlet();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        actionServlet.getAllExchangeRates(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        actionServlet.addNewExchangeRates(req,resp);
    }
}
