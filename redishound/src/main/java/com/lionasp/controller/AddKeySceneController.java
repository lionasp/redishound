package com.lionasp.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.function.Consumer;

public class AddKeySceneController {

    @FXML
    private Label statusBar;

    @FXML
    private TextField valueInput;

    @FXML
    private TextField keyInput;

    @FXML
    private Button addKeyButton;

    private Consumer<AddKeyResponse> newKeyCallback;

    public void setKeyCallback(Consumer<AddKeyResponse> callback) {
        this.newKeyCallback = callback ;
    }

    @FXML
    private void initialize() {

    }

    public void onAddKeyClicked(ActionEvent actionEvent) {
        statusBar.setText("");
        String key = TextFieldGetter.getValueFromTextField(keyInput);
        String value = TextFieldGetter.getValueFromTextField(valueInput);

        if (Objects.equals(key, "") || Objects.equals(value, "")) {
            statusBar.setText("Key or value is empty");
            return;
        }

        if (newKeyCallback != null) {
            newKeyCallback.accept(new AddKeyResponse(key, value));
            Stage stage = (Stage) addKeyButton.getScene().getWindow();
            stage.close();
        }
    }
}
