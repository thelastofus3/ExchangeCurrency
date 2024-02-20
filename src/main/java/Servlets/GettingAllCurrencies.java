package Servlets;

import ServletService.CurrenciesServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/currency")
public class GettingAllCurrencies extends HttpServlet {
    CurrenciesServlet actionServlet = new CurrenciesServlet();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        actionServlet.getAllCurrencies(resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        actionServlet.addNewCurrency(req,resp);
    }
}
