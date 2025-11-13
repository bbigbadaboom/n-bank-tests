package Common.Extensions;

import org.junit.jupiter.api.extension.*;

import java.util.HashMap;
import java.util.Map;

public class TimingExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
    private Map<String, Long> startTimes = new HashMap<>();

    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
        String testName = extensionContext.getRequiredTestClass().getPackageName() + "." + extensionContext.getDisplayName();
        startTimes.put(testName, System.currentTimeMillis());
        System.out.println("Thread " + Thread.currentThread().getName() + ": Test started " + testName);
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
        String testName = extensionContext.getRequiredTestClass().getPackageName() + "." + extensionContext.getDisplayName();
        long testDuration = System.currentTimeMillis() - startTimes.get(testName);
        System.out.println("Thread " + Thread.currentThread().getName() + ": Test finished " + testName + ", test duration " + testDuration + " ms");
    }
}
