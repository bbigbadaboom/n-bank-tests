package DB.database;

import API.Configs.Config;
import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class DBRequest {
    private RequestType requestType;
    private String table;
    private List<Condition> conditions;
    private Class<?> extractAsClass;

    public enum RequestType {
        SELECT, INSERT, UPDATE, DELETE
    }

    //    Устанавливает класс, в который будем маппить результат,
//
//    Вызывает метод executeQuery, который реально делает запрос в базу и возвращает объект типа T.
    public <T> T extractAs(Class<T> clazz) {
        this.extractAsClass = clazz;
        return executeQuery(clazz);
    }

    //ВЫПОЛНЕНИЕ ЗАПРОСА и подставление вместо ?
    private <T> T executeQuery(Class<T> clazz) {
        String sql = buildSQL();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Set parameters for conditions
            if (conditions != null) {
                for (int i = 0; i < conditions.size(); i++) {
                    statement.setObject(i + 1, conditions.get(i).getValue());
                }
            }

            try (ResultSet resultSet = statement.executeQuery()) {//выполнение запроса
                if (resultSet.next()) { // если есть данные
                    return mapToObject(resultSet, clazz);
                }
                return null;
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Database query failed", e);
        }
    }

    //поля мапятся в объект
    private <T> T mapToObject(ResultSet resultSet, Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance(); //рефлексия создается новый объект

            ResultSetMetaData metaData = resultSet.getMetaData(); //получение информации о колонках
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnLabel(i); //для каждой колонки ищем имя колонки из SQL
                Object columnValue = resultSet.getObject(i);

                try {
                    Field field = clazz.getDeclaredField(columnName); //берется имя колонки
                    if (columnValue instanceof BigDecimal && field.getType().equals(Double.class)) {
                        columnValue = ((BigDecimal) columnValue).doubleValue();//преобразование типов
                    }
                    field.setAccessible(true); //для того чтобы поле можно было проставить
                    field.set(instance, columnValue);
                } catch (NoSuchFieldException ignored) {
                    // если в DAO нет поля с таким именем — просто пропускаем
                }
            }

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to map " + clazz.getSimpleName(), e);
        }
    }

    //СОБИРАЕТСЯ ЗАПРОС ? подставляется для избежания инъекций
    private String buildSQL() {
        StringBuilder sql = new StringBuilder();

        switch (requestType) {
            case SELECT:
                sql.append("SELECT * FROM ").append(table);
                if (conditions != null && !conditions.isEmpty()) {
                    sql.append(" WHERE ");
                    for (int i = 0; i < conditions.size(); i++) {
                        if (i > 0) sql.append(" AND ");
                        sql.append(conditions.get(i).getColumn()).append(" ").append(conditions.get(i).getOperator()).append(" ?");
                    }
                }
                break;
            default:
                throw new UnsupportedOperationException("Request type " + requestType + " not implemented");
        }

        return sql.toString();
    }
    //Создается конекшен
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                Config.getProperties("db.url"),
                Config.getProperties("db.username"),
                Config.getProperties("db.password")
        );
    }

    public static DBRequestBuilder builder() {
        return new DBRequestBuilder();
    }//новый экземпляр билдера

    public static class DBRequestBuilder {
        private RequestType requestType;
        private String table;
        private List<Condition> conditions = new ArrayList<>();
        private Class<?> extractAsClass;

        //МЕтоды билдера
        public DBRequestBuilder requestType(RequestType requestType) {
            this.requestType = requestType;
            return this;
        }

        public DBRequestBuilder table(String table) {
            this.table = table;
            return this;
        }

        public DBRequestBuilder where(Condition condition) {
            this.conditions.add(condition);
            return this;
        }


        //собирается дбреквест
        public <T> T extractAs(Class<T> clazz) {
            this.extractAsClass = clazz;
            DBRequest request = DBRequest.builder()
                    .requestType(requestType)
                    .table(table)
                    .conditions(conditions)
                    .extractAsClass(extractAsClass) // параметры подставляются сюда из степов
                    .build();
            return request.extractAs(clazz);//и выполняется запрос и получаем готовый объект
        }
    }
}
