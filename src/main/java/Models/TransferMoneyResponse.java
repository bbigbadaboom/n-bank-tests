package Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferMoneyResponse extends BaseModel{
     private int senderAccountId;
     private int receiverAccountId;
     private double amount;
     private String message;
}
