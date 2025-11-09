package Common;

import API.Models.FieldType;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import Common.Anotations.NumericPattern;
import Common.Anotations.RegexPattern;
import com.mifmif.common.regex.Generex;

public class Common {
    public static String generatePassword(int length) {

        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&";

        String all = upper + lower + digits + special;

        Random random = new Random();
        StringBuilder password = new StringBuilder();

        // Гарантируем, что есть хотя бы по одному нужному символу
        password.append(upper.charAt(random.nextInt(upper.length())));
        password.append(lower.charAt(random.nextInt(lower.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(special.charAt(random.nextInt(special.length())));

        // Остальные символы случайные
        for (int i = 4; i < length; i++) {
            password.append(all.charAt(random.nextInt(all.length())));
        }

        return password.toString();
    }

    public static void repeat(int times, Runnable action) {
        for (int i = 0; i < times; i++) {
            action.run();
        }
    }

    public static int randomDouble(int first, int last) {

        return
                ThreadLocalRandom.current().nextInt(first, last);
    }
    public static String generateString(String regexp) {
        Generex generex = new Generex(regexp);
        return generex.random();

    }

    public static <T> T generate(Class<T> clazz) {
        Random random = new Random();
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                RegexPattern annotation = field.getAnnotation(RegexPattern.class);

                if (annotation != null && field.getType().equals(String.class)) {
                    String value;
                    if (annotation.type() == FieldType.PASSWORD) {
                        // Генерируем сложный пароль
                        value = generatePassword(10);
                    } else {
                        // Генерируем по regex
                        value = new Generex(annotation.value()).random();
                    }
                    field.set(instance, value);
                }
                NumericPattern num = field.getAnnotation(NumericPattern.class);
                if (num != null) {
                    double min = num.min();
                    double max = num.max();
                    int scale = num.scale();
                    double val = min + (max - min) * random.nextDouble();
                    double factor = Math.pow(10, scale);
                    val = Math.round(val * factor) / factor;

                    if (field.getType() == int.class || field.getType() == Integer.class) {
                        field.set(instance, (int) val);
                    } else if (field.getType() == double.class || field.getType() == Double.class) {
                        field.set(instance, val);
                    }
                }
            }



            return instance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
