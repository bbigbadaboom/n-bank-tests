package API.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAccount extends BaseModel {
    public int id;
    public String accountNumber;
    public double balance;
    public List<Transaction> transactions;
}
