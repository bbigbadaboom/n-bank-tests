package API;

import API.Models.*;
import API.Models.Comparisons.ModelAssertions;
import Common.Anotations.Mock;
import Common.Anotations.UserWithAccountsAndDeposit;
import Common.Extensions.DepositExtension;
import Common.Extensions.FraudCheckWireMockExtension;
import Common.Storage.AccountsStorage;
import Common.Storage.SessionStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

import static Common.Common.randomDouble;

@ExtendWith({DepositExtension.class, FraudCheckWireMockExtension.class})
public class TransferWithFraudCheckTest extends BaseTest {
    

    @Test
    @UserWithAccountsAndDeposit(accounts = 2)
    @Mock(responseClass = FraudCheckResponse.class,
            endpoint = "/fraud-check",
            overrides = {"status = SUCCESS",
            "decision = APPROVED",
            "riskScore = 0.2",
            "reason = Low risk transaction",
            "requiresManualReview = false",
            "additionalVerificationRequired = false"
            }
    )
    public void testTransferWithFraudCheck() {

        double amount = randomDouble(1000, 2000);
        long secondAccountId = AccountsStorage.getAccounts().get(1).getId();
        long accountId = AccountsStorage.getAccounts().get(0).getId();

        TransferResponse transferResponse = SessionStorage.getSteps().transferWithFraudCheck(
                accountId,
                secondAccountId,
                amount
        );

        assertNotNull(transferResponse);
        
        TransferResponse expectedResponse = TransferResponse.builder()
                .status("APPROVED")
                .message("Transfer approved and processed immediately")
                .amount(amount)
                .senderAccountId(accountId)
                .receiverAccountId(secondAccountId)
                .fraudRiskScore(0.2)
                .fraudReason("Low risk transaction")
                .requiresManualReview(false)
                .requiresVerification(false)
                .build();
        
         ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();
    }

}
