package API.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    private int id;
    private double amount;
    private String type;
    private String timestamp;
    private int relatedAccountId;
    private UserAccount relatedAccount;
    private String timestampAsString;
    private double amountAsDouble;

}
