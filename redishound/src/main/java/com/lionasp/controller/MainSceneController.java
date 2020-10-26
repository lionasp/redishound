package com.lionasp.controller;

import com.lionasp.connector.Connector;
import com.lionasp.connector.exceptions.ConnectorException;
import com.lionasp.connector.value.Value;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.HashMap;

public class MainSceneController {
    @FXML
    private TextField hostnameInput;

    @FXML
    private TextField portInput;

    @FXML
    private TextField dbNumberInput;

    @FXML
    private ListView<String> redisKeysListView;

    @FXML
    private TextArea redisValueContent;

    @FXML
    private Label statusBar;

    private Connector connector;
    private String selectedKey;
    private ObservableList<String> redisKeys = FXCollections.observableArrayList();
    private HashMap<String, Value> cache = new HashMap<>();

    public MainSceneController() {

    }

    @FXML
    private void initialize() {
        redisKeysListView.setItems(redisKeys);

        redisKeysListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showValueContent(newValue));
    }

    public void onConnectButtonClicked() {
        this.connector = new Connector(
                TextFieldGetter.getValueFromTextField(hostnameInput),
                Integer.parseInt(TextFieldGetter.getValueFromTextField(portInput)),
                Integer.parseInt(TextFieldGetter.getValueFromTextField(dbNumberInput))
        );

        try {
            System.out.println(this.connector.ping());
            statusBar.setText("Successfully connected");
        } catch (ConnectorException e) {
            System.out.println("Connection failed");
            statusBar.setText("Connection failed");
            redisKeys.clear();
            return;
        }

        fillRedisKeysList();
    }

    public void onDeleteKeyClicked() {
        String selectedKey = redisKeysListView.getSelectionModel().getSelectedItem();
        if (selectedKey == null) {
            statusBar.setText("Chose key for delete it");
            return;
        }
        redisKeys.remove(selectedKey);
        try {
            connector.del(selectedKey);
            statusBar.setText("Key \"" + selectedKey + "\" has deleted");
        } catch (ConnectorException e) {
            String message = "Can't delete key " + selectedKey + " from DB";
            statusBar.setText(message);
            System.out.println(message);
        }

    }

    private void fillRedisKeysList() {
        redisKeys.clear();
        try {
            redisKeys.addAll(connector.keys());
        } catch (ConnectorException e) {
            String message = "Can't fetch keys from DB";
            statusBar.setText(message);
            System.out.println(message);
        }
    }

    private void showValueContent(String key) {
        clearStatusBar();
        selectedKey = key;
        if (!cache.containsKey(key)) {
            Value value;
            try {
                value = connector.getValue(key);
            } catch (ConnectorException e) {
                value = null;
            }

            if (value != null) {
                cache.put(key, value);
            }
        }

        if (cache.containsKey(key)) {
            redisValueContent.setText(cache.get(key).toString());
        } else {
            String message = "Can't read value for key: " + key;
            statusBar.setText(message);
            System.out.println(message);
        }
    }

    private void clearStatusBar() {
        statusBar.setText("");
    }

    public void onRefreshKeyClicked() {
        if (selectedKey == null) {
            statusBar.setText("Select key first");
        } else {
            cache.remove(selectedKey);
            showValueContent(selectedKey);
            statusBar.setText("New value has been fetched");
        }
    }
}
