package Models;

import Common.NumericPattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositMoneyRequest extends BaseModel {
    @NumericPattern(min = 1, max = 100)
    private int id;
    @NumericPattern(min = 0.01, max = 4999.99, scale = 2)
    private double balance;
}
