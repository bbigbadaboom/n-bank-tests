package API.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FraudCheckResponse extends BaseModel {
    String status;
    String decision;
    double riskScore;
    String reason;
    boolean requiresManualReview;
    boolean additionalVerificationRequired;
}
