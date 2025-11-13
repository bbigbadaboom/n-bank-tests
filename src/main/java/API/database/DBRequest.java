package API.database;

import API.Configs.Config;
import API.dao.AccountDao;
import API.dao.UserDao;
import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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

    public <T> T extractAs(Class<T> clazz) {
        this.extractAsClass = clazz;
        return executeQuery(clazz);
    }

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
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToObject(resultSet, clazz);
                }
                return null;
                }
        }
              catch (SQLException e) {
            throw new RuntimeException("Database query failed", e);
        }
    }

    private <T> T mapToObject(ResultSet resultSet, Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnLabel(i); // имя колонки из SQL
                Object columnValue = resultSet.getObject(i);

                try {
                    Field field = clazz.getDeclaredField(columnName);
                    if (columnValue instanceof BigDecimal && field.getType().equals(Double.class)) {
                        columnValue = ((BigDecimal) columnValue).doubleValue();
                    }
                    field.setAccessible(true);
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

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                Config.getProperties("db.url"),
                Config.getProperties("db.username"),
                Config.getProperties("db.password")
        );
    }

    public static DBRequestBuilder builder() {
        return new DBRequestBuilder();
    }

    public static class DBRequestBuilder {
        private RequestType requestType;
        private String table;
        private List<Condition> conditions = new ArrayList<>();
        private Class<?> extractAsClass;

        public DBRequestBuilder requestType(RequestType requestType) {
            this.requestType = requestType;
            return this;
        }

        public DBRequestBuilder where(Condition condition) {
            this.conditions.add(condition);
            return this;
        }

        public DBRequestBuilder table(String table) {
            this.table = table;
            return this;
        }

        public <T> T extractAs(Class<T> clazz) {
            this.extractAsClass = clazz;
            DBRequest request = DBRequest.builder()
                    .requestType(requestType)
                    .table(table)
                    .conditions(conditions)
                    .extractAsClass(extractAsClass)
                    .build();
            return request.extractAs(clazz);
        }
    }
}
