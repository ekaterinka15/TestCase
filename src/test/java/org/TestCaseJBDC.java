package org;

import org.junit.jupiter.api.*;


import java.sql.*;

import java.sql.*;

@Disabled("Временно отключено — база не доступна")


public class TestCaseJBDC {

    private Connection connection;
    private Statement statement;
     @BeforeEach
              void setupDatabase() throws SQLException {
         String url = "jdbc:h2:tcp://localhost:9092/mem:testdb";
         String user = "user";
         String pass = "pass";

         connection = DriverManager.getConnection(url, user, pass);
         statement = connection.createStatement();

        }
        @Test

         void testCase() throws SQLException {
            statement.executeUpdate("INSERT INTO food (food_name, food_type, food_exotic) VALUES ('морковь', 'овощ', 0)");
            System.out.println("Добавлена морковь");
                ResultSet resultSet = statement.executeQuery("SELECT * FROM food");
                System.out.println("Список товаров:");
                while (resultSet.next()) {
                    System.out.println("- " + resultSet.getString("food_name"));
                }

                ResultSet resultSet1 = statement.executeQuery("SELECT * FROM food WHERE food_name = 'морковь'");
                if (resultSet1.next()) {
                    System.out.println("Товар 'морковь' найден в БД");
                } else {
                    System.out.println("Товар 'морковь' не найден");
                }


                int deleted = statement.executeUpdate("DELETE FROM food WHERE food_name = 'морковь'");
                System.out.println("Товар 'морковь' удален");
                // Проверяем удаление
                ResultSet resultSet2 = statement.executeQuery("SELECT * FROM food WHERE food_name = 'морковь'");
                if (!resultSet2.next()) {
                    System.out.println("Товар 'морковь' успешно удалён");
                } else {
                    System.out.println("Товар 'морковь' всё ещё в базе.");
                }

            }
            }





