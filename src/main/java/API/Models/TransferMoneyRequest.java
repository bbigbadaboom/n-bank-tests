package API.Models;

import Common.Anotations.NumericPattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferMoneyRequest extends BaseModel{
     @NumericPattern(min = 1, max = 100)
     private long senderAccountId;
     @NumericPattern(min = 1, max = 100)
     private long receiverAccountId;
     @NumericPattern(min = 1, max = 100, scale = 2)
     private double amount;
}
