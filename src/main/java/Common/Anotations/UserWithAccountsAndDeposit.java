package Common.Anotations;

import API.Models.TestType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UserWithAccountsAndDeposit {
    int accounts() default 1;
    int accFordep() default 1;
}
