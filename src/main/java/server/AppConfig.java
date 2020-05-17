package server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import server.service.*;

import javax.sql.DataSource;

//@Configuration
public class AppConfig {

    @Bean
    public MainServer mainServer(DatabaseService databaseService, MessageFormatting messageFormatting) {
        return new MainServer(databaseService, messageFormatting);
    }

    @Bean
    public MessageFormatting messageFormatting() {
        return new MessageFormatting();
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/java_chat?serverTimezone=UTC");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        return dataSource;
    }

    @Bean
    public DatabaseService databaseService(ChatHistory ch, Database db, AuthService as, BlacklistService bs) {
        return new DatabaseService(db, ch, as, bs);
    }

    @Bean
    public Database database(DataSource dataSource) {
        return new Database(dataSource);
    }

    @Bean
    public AuthService authService(Database databaseSql) {
        return new AuthService(databaseSql);
    }

    @Bean
    public ChatHistory chatHistory(Database db, MessageFormatting mf, AuthService as) {
        return new ChatHistory(db, mf, as);
    }

    @Bean
    public BlacklistService blacklistService(AuthService as, Database db) {
        return new BlacklistService(db, as);
    }
}
