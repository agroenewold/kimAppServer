package example;

import com.sun.net.httpserver.HttpServer;
import com.sun.jersey.api.container.httpserver.HttpServerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.StringTokenizer;

import javax.ws.rs.*;

/* @author Lydia Cupery, Beka Agava, Andrew Groenewold, Moses Mangunrahardja
 * The Java class will be hosted at the URI path "/helloworld"
 */
@Path("/kimSQL/")
public class HelloWorld {

    //declare public variables
    private static final String DB_URI = "jdbc:postgresql://localhost:5432/kimSQL";
    private static final String DB_LOGINID = "postgres";
    private static final String DB_PASSWORD = "postgres";

    /* getPlayer gets an account name when given the id
    *  @param id receives the account id
    *  @return the account name as a string
    */
    @GET
    @Path("/account/{id}")
    @Produces("text/plain")
    public String getPlayer(@PathParam("id") int id) {
        String result;
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URI, DB_LOGINID, DB_PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT userID FROM Account WHERE id = " + id);
            if (resultSet.next()) {
                //result = resultSet.getInt(1) + " " + resultSet.getString(3) + " " + resultSet.getString(2);
                result = resultSet.getString(1);
            } else {
                result = "nothing found...";
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }
    /* getStockPriceFromShares gets the current price of a stock when given the amount of shares owned
    *  @param stockID receives the amount of shares owned
    *  @return the current price of the stock
    */
    @GET
    @Path("/stock/{stockid}")
    @Produces("text/plain")
    public String getStockPriceFromShares(@PathParam("stockid") int stockID) {
        String result;
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URI, DB_LOGINID, DB_PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT sector, buyPrice, targetPrice, sharesOwned FROM Stock WHERE id = " + stockID);
            if (resultSet.next()) {
                //result = resultSet.getInt(1) + " " + resultSet.getString(3) + " " + resultSet.getString(2);
                result = resultSet.getString(1) + "\n" + resultSet.getFloat(2) + "\n" + resultSet.getFloat(3) + "\n" + resultSet.getInt(4) + "\n";
            } else {
                result = "0";
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }

    /* getPlayers gets all the account names
    *  @param id receives the account id
    *  @return the account names as strings
    */
    @GET
    @Path("/accounts")
    @Produces("text/plain")
    public String getPlayers(@PathParam("id") int id) {
        String result = "";
            try {
                Class.forName("org.postgresql.Driver");
                Connection connection = DriverManager.getConnection(DB_URI, DB_LOGINID, DB_PASSWORD);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT userID, password FROM Account");
                while (resultSet.next()) {
                    result += resultSet.getString(1) + "\n" + resultSet.getString(2) + "\n";
                }
                resultSet.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }

    /* getStocks gets all the stock tickers
    *  @param id receives the account id
    *  @return the stock tickers as strings
    */
    @GET
    @Path("/stocks")
    @Produces("text/plain")
    public String getStocks(@PathParam("id") int id) {
        String result = "";
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URI, DB_LOGINID, DB_PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT ticker FROM Stock");
            while (resultSet.next()) {
                result += resultSet.getString(1) + "\n";
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }

    /* getStocksIds gets all the stock tickers and IDs
    *  @param id receives the account id
    *  @return the stock tickers and IDs
    */
    @GET
    @Path("/stocksIds")
    @Produces("text/plain")
    public String getStocksIds(@PathParam("id") int id) {
        String result = "";
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URI, DB_LOGINID, DB_PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT ticker, id FROM Stock");
            while (resultSet.next()) {
                result += resultSet.getString(1) + "\n" + resultSet.getString(2) + "\n";
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }

    // The Java method will process HTTP GET requests
    @GET
    // The Java method will produce content identified by the MIME Media type "text/plain"
    @Produces("text/plain")
    public String getClichedMessage() {
        // Return some cliched textual content
        return "Hello World!";
    }

    /**
     * PUT method for creating an instance of account with a given ID - If the
     * account already exists, replace them with the new account field values. We do this
     * because PUT is idempotent, meaning that running the same PUT several
     * times does not change the database.
     *
     * @param playerLine a string representation of the account in the format: username password
     * @return status message
     */
    @PUT
    @Path("/accountPut")
    @Consumes("text/plain")
    @Produces("text/plain")
    public String putPlayer(String playerLine) {
        String result;
        StringTokenizer st = new StringTokenizer(playerLine);
        String name = st.nextToken(), password = st.nextToken();
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URI, DB_LOGINID, DB_PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Account WHERE userID=" + name);
            if (resultSet.next()) {
                statement.executeUpdate("UPDATE Account SET password='" + password + "' WHERE userID=" + name);
                result = "Account " + name + " updated...";
            } else {
                int id = -1;
                ResultSet resultSet2 = statement.executeQuery("SELECT MAX(id) FROM Account");
                if (resultSet2.next()) {
                    id = resultSet.getInt(1) + 1;
                }
                statement.executeUpdate("INSERT INTO Account VALUES (" + id + ", '" + name + "', '" + password + "')");
                result = "Account " + id + " added...";
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }

    /**
     * POST method for creating an instance of Account with a new, unique ID
     * number. We do this because POST is not idempotent, meaning that running
     * the same POST several times creates multiple objects with unique IDs but
     * with the same values.
     * <p/>
     * The method creates a new, unique ID by querying the account table for the
     * largest ID and adding 1 to that. Using a sequence would be a better solution.
     *
     * @param playerLine a string representation of the account in the format: password account
     * @return status message
     */
    @POST
    @Path("/accountPost")
    @Consumes("text/plain")
    @Produces("text/plain")
    public String postPlayer(String playerLine) {
        String result;
        StringTokenizer st = new StringTokenizer(playerLine);
        int id = -1;
        String name = st.nextToken(":"), password = st.nextToken(":");
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URI, DB_LOGINID, DB_PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT MAX(id) FROM Account");
            if (resultSet.next()) {
                id = resultSet.getInt(1) + 1;
            }
            statement.executeUpdate("INSERT INTO Account VALUES (" + id + ", '" + name + "', '" + password + "', " + false + ")");
            resultSet.close();
            statement.close();
            connection.close();
            result = "Account " + id + " added...";
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }

    /**
     * DELETE method for deleting and instance of account with the given ID. If
     * the player doesn't exist, then don't delete anything. DELETE is idempotent, so
     * sending the same command multiple times should result in the same side
     * effect, though the return value may be different.
     *
     * @param id the ID of the account to be returned
     * @return a simple text confirmation message
     */
    @DELETE
    @Path("/account/{id}")
    @Produces("text/plain")
    public String deletePlayer(@PathParam("id") int id) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URI, DB_LOGINID, DB_PASSWORD);
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM Account WHERE id=" + id);
            statement.close();
            connection.close();
        } catch (Exception e) {
            return e.getMessage();
        }
        return "Account " + id + " deleted...";
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServerFactory.create("http://localhost:9998/");
        server.start();

        System.out.println("Server running");
        System.out.println("Visit: http://localhost:9998/kimSQL");
        System.out.println("Visit: http://localhost:9998/kimSQL/account/1");
        System.out.println("Visit: http://localhost:9998/kimSQL/accounts");
        System.out.println("Hit return to stop...");
        System.in.read();
        System.out.println("Stopping server");
        server.stop(0);
        System.out.println("Server stopped");
    }
}
