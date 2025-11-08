package Common.Extensions;

import Common.Anotations.Browsers;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Arrays;

public class BrowsersExtension implements ExecutionCondition {
    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        Browsers annotation = context.getElement()
                .map(el -> el.getAnnotation(Browsers.class))
                .orElse(null);

        if (annotation == null) {
            return ConditionEvaluationResult.enabled("Нет ограничений по браузеру");
        }

        String current = Configuration.browser;
        String[] allowed = annotation.value();

        return Arrays.asList(allowed).contains(current)
                ? ConditionEvaluationResult.enabled("Браузер подходит: " + current)
                : ConditionEvaluationResult.disabled(
                "Тест пропущен: " + current + " не в списке " + Arrays.toString(allowed)
        );
    }
}
