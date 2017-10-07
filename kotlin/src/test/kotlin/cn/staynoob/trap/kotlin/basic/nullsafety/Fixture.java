package cn.staynoob.trap.kotlin.basic.nullsafety;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class Fixture {

    public static <T> void addElement(Collection<T> collection, T element) {
        collection.add(element);
    }

    private int primitive;
    @Nullable
    private String withNullAbleAnnotation;
    @NotNull
    private String withNotNullAnnotation = "withNotNullAnnotation";
    private String platformType;

    public void fn(String str) {
    }

    @Nullable
    public String getWithNullAbleAnnotation() {
        return withNullAbleAnnotation;
    }

    public void setWithNullAbleAnnotation(@Nullable String withNullAbleAnnotation) {
        this.withNullAbleAnnotation = withNullAbleAnnotation;
    }

    @NotNull
    public String getWithNotNullAnnotation() {
        return withNotNullAnnotation;
    }

    public void setWithNotNullAnnotation(@NotNull String withNotNullAnnotation) {
        this.withNotNullAnnotation = withNotNullAnnotation;
    }

    public String getPlatformType() {
        return platformType;
    }

    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }

    public int getPrimitive() {
        return primitive;
    }

    public void setPrimitive(int primitive) {
        this.primitive = primitive;
    }
}
