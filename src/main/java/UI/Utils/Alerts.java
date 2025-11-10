package UI.Utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Alerts {
SUCCES_TRANSFER("✅ Successfully transferred $"
),
    UNSUCCES_TRANSFER("❌ Error: Invalid transfer: insufficient funds or invalid accounts"),
    SUCCES_DEPOSIT("✅ Successfully deposited $"),
    UNSUCCES_DEPOSIT("❌ Please enter a valid amount."),
    UPDATE_NAME("✅ Name updated successfully!"),
    UNSUCCES_UPDATE_NAME("Name must contain two words with letters only"),
    USER_CREATED("✅ User created successfully!"),
    USER_NOT_CREATED("Username must be between 3 and 15 characters"),
    ACCOUNT_CREATED("✅ New Account Created! Account Number:");
private final String alert;
}
