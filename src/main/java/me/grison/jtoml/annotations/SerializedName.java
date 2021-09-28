package me.grison.jtoml.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Created by Zachary

/**
 * Like Gson:
 * It indicates this member should be serialized to Toml with
 * the provided name value as its field name.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SerializedName {
    String value();
}
