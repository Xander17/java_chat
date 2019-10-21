import java.sql.*;

public class CRUDTest {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Connection connection = null;
        Statement statement = null;
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:DB/main.db");
        statement = connection.createStatement();
        String query = "create table lessons (id integer primary key autoincrement not null unique, lesson text not null)";
        statement.execute(query);
        System.out.println("Добавление таблицы");
        query = "drop table lessons";
        statement.execute(query);
        System.out.println("Удаление таблицы");
        query = "insert into students(name,score) values('John',10)";
        System.out.println("Строк затронуто - " + statement.executeUpdate(query));
        query = "update students set score=50 where name='John'";
        System.out.println("Строк затронуто - " + statement.executeUpdate(query));
        query = "select count(name) from students where score<50";
        ResultSet rs = statement.executeQuery(query);
        int c = 0;
        if (rs.next()) {
            c = rs.getInt(1);
        }
        System.out.println("Студентов с баллами меньше 50 - " + c);
        query = "delete from students where name='John'";
        System.out.println("Строк затронуто - " + statement.executeUpdate(query));
    }
}
