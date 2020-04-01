package junit;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MyAnnotation {

    //定义了两个成员变量
    String username() default "zicheng";
    int age() default 23;
}
