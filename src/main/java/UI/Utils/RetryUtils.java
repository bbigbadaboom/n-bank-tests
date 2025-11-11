package UI.Utils;


import com.codeborne.selenide.Selenide;

import java.util.function.BooleanSupplier;

public class RetryUtils {
    public static void retry(BooleanSupplier condition, Runnable action, int retries, long delayMs) {
        for (int attempt = 1; attempt <= retries; attempt++) {
               action.run();
               Selenide.sleep(300);
                if (condition.getAsBoolean()) {
                    return;
                } else {
                    Selenide.sleep(delayMs);

                }
            }

        }

}
