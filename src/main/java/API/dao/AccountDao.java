package API.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDao extends BaseDao {
    private Long id;
    @JsonProperty("accountNumber")
    private String account_number;
    private Double balance;
    private Long customerId;
}
