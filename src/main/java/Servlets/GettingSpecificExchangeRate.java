package Servlets;

import ServletService.ExchangeSpecificServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/exchangeRates/*")
public class GettingSpecificExchangeRate extends HttpServlet {

    ExchangeSpecificServlet actionServlet = new ExchangeSpecificServlet();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        actionServlet.getSpecificExchangeRate(resp,req);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if(!method.equals("PATCH")){
            super.service(req,resp);
            return;
        }
        this.doPatch(req,resp);
    }
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        actionServlet.updateSpecificExchangeRate(resp,req);
    }
}
