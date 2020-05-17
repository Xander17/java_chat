package server.service;


import lombok.Getter;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

@Service
public class Database {

    private Connection connection;
    @Getter
    private Statement statement;
    private DataSource dataSource;

    public Database(DataSource dataSource) {
        this.dataSource = dataSource;
        connect();
    }

    public void connect() {
        try {
            this.connection = dataSource.getConnection();
            this.statement = connection.createStatement();
            LogService.SERVER.info("Clients DB connected.");
        } catch (Exception e) {
            LogService.SERVER.error("Clients DB connection failed", Arrays.toString(e.getStackTrace()));
        }
    }

    public void shutdown() {
        try {
            connection.close();
            LogService.SERVER.info("Clients DB stopped.");
        } catch (SQLException e) {
            LogService.SERVER.error("DB", Arrays.toString(e.getStackTrace()));
        }
    }

    public PreparedStatement getPreparedStatement(String query) throws SQLException {
        return connection.prepareStatement(query);
    }

    public void closePreparedStatement(PreparedStatement preparedStatement) {
        try {
            if (preparedStatement != null) preparedStatement.close();
        } catch (SQLException e) {
            LogService.SERVER.error("DB", Arrays.toString(e.getStackTrace()));
        }
    }
}
