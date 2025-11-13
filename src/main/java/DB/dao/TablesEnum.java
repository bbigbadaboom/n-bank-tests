package DB.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TablesEnum {
    CUSTOMERS(
            "customers"
    ),
    ACCOUNTS(
           "accounts"
    );
    private final String tableName;
}
