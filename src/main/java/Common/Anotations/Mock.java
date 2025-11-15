package Common.Anotations;

import API.Models.BaseModel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Mock {

    Class<? extends BaseModel> responseClass();

    int port() default 8080;

    String endpoint() default "";

    int statusCode() default 200;

    /**
     * Поля для переопределения в ответе
     * Например: "decision=APPROVED", "riskScore=0.3"
     */
    String[] overrides() default {};
}
