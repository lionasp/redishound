package com.lionasp.controller;

import javafx.scene.control.TextField;

public class TextFieldGetter {

    public static String getValueFromTextField(TextField textField) {
        String value = textField.getText();
        if (value == null || "".equals(value)) {
            value = textField.getPromptText();
        }
        return value;
    }

    public static String getValueFromTextField(TextField textField, String defaultValue) {
        String value = textField.getText();
        if (value == null || "".equals(value)) {
            value = textField.getPromptText();
        }

        if (value == null || "".equals(value)) {
            value = defaultValue;
        }

        return value;
    }
}
