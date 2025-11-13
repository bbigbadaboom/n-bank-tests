package API.Models;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class UserAccount extends BaseModel {
    private Long id;
    private String accountNumber;
    private double balance;
    private List<Transaction> transactions;
}
