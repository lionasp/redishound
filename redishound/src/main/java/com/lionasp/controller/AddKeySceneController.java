package com.lionasp.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.function.Consumer;

public class AddKeySceneController {
    @FXML
    private TextField valueInput;

    @FXML
    private TextField keyInput;

    private Consumer<AddKeyResponse> newKeyCallback;

    public void setKeyCallback(Consumer<AddKeyResponse> callback) {
        this.newKeyCallback = callback ;
    }

    @FXML
    private void initialize() {

    }

    public void onAddKeyClicked(ActionEvent actionEvent) {
        String key = TextFieldGetter.getValueFromTextField(keyInput);
        String value = TextFieldGetter.getValueFromTextField(valueInput);

        if (newKeyCallback != null) {
            newKeyCallback.accept(new AddKeyResponse(key, value));
        }
    }
}
