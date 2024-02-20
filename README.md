# Currency Exchange System

This repository contains a currency exchange system implemented in Java. It provides functionalities for calculating exchange rates, adding new currencies, and retrieving currency information.

## Project Structure

The project is structured into several packages:

### DTO

Contains Data Transfer Objects for representing currency exchange data.

- **CalculateExchangeDTO**: Represents exchange calculation data including base currency, target currency, rate, amount, and converted amount.
- **CurrencyDTO**: Represents currency information such as ID, name, code, and sign.
- **ExchangeDTO**: Represents exchange rate information including ID, base currency, target currency, and rate.
- **MessageDTO**: Represents a simple message object.

### Serves

Contains server-side classes responsible for handling requests and database connections.

- **ConnectionDB**: Provides a database connection using JDBC for MySQL.
- **ErrorMessage**: Handles sending error messages as JSON responses.
- **FindService**: Provides methods for finding currency and exchange rate information from the database.

### ServletService

Contains servlets for handling HTTP requests related to currency operations.

### Servlets

Contains servlet classes that handle HTTP requests and delegate operations to service classes in the ServletService package.

#### GettingAllCurrencies

- **Description:** Handles HTTP requests related to retrieving all currencies and adding new currencies.
- **Endpoints:**
  - `GET /currency`: Retrieves all currencies.
  - `POST /currency`: Adds a new currency.
- **Servlet Class:** `GettingAllCurrencies`

#### GettingExchangeRate

- **Description:** Handles HTTP requests related to retrieving all exchange rates and adding new exchange rates.
- **Endpoints:**
  - `GET /exchangeRates`: Retrieves all exchange rates.
  - `POST /exchangeRates`: Adds a new exchange rate.
- **Servlet Class:** `GettingExchangeRate`

#### GettingSpecificCurrency

- **Description:** Handles HTTP requests for retrieving specific currency information.
- **Endpoints:**
  - `GET /currency/*`: Retrieves information about a specific currency.
- **Servlet Class:** `GettingSpecificCurrency`

#### GettingSpecificExchangeRate

- **Description:** Handles HTTP requests for retrieving specific exchange rate information and updating exchange rates.
- **Endpoints:**
  - `GET /exchangeRates/*`: Retrieves information about a specific exchange rate.
  - `PATCH /exchangeRates/*`: Updates an existing exchange rate.
- **Servlet Class:** `GettingSpecificExchangeRate`

#### MakeExchange

- **Description:** Handles HTTP requests for performing currency exchange calculations.
- **Endpoints:**
  - `GET /exchange`: Performs a currency exchange calculation.
- **Servlet Class:** `MakeExchange`

These servlets interact with the service layer to perform the necessary business logic for currency-related operations.

## Setup

- Clone the repository.
- Set up your MySQL database and configure connection details in ConnectionDB.java.
- Build and run the project using your preferred IDE or build tool.

## Usage

- Access the provided servlet endpoints to perform currency operations.
- Use appropriate HTTP methods and endpoints to interact with the system.

## Dependencies

- Java Development Kit (JDK)
- MySQL Server
- Servlet Container (e.g., Apache Tomcat)
- JDBC Driver for MySQL

## Contributing

Contributions are welcome! Please follow the Contribution Guidelines.

