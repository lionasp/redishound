package com.lionasp.controller;

import com.lionasp.connector.Connector;
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
        } catch (redis.clients.jedis.exceptions.JedisConnectionException | redis.clients.jedis.exceptions.JedisDataException e) {
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
        connector.del(selectedKey);
        statusBar.setText("Key \"" + selectedKey + "\" has deleted");
    }

    private void fillRedisKeysList() {
        redisKeys.clear();
        redisKeys.addAll(connector.keys());
    }

    private void showValueContent(String key) {
        clearStatusBar();
        if (!cache.containsKey(key)) {
            Value value = connector.getValue(key);
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
}
