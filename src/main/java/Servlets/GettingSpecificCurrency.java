package Servlets;

import ServletService.CurrencySpecificServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/currency/*")
public class GettingSpecificCurrency extends HttpServlet {
    CurrencySpecificServlet actionServlet = new CurrencySpecificServlet();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        actionServlet.getSpecificCurrencies(resp, req);
    }
}
