package com.lionasp.controller;

import com.lionasp.connector.Connector;
import com.lionasp.connector.exceptions.ConnectorException;
import com.lionasp.connector.key.Key;
import com.lionasp.connector.value.Value;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

public class MainSceneController {
    @FXML
    private TextField hostnameInput;

    @FXML
    private TextField portInput;

    @FXML
    private TextField dbNumberInput;

    @FXML
    private ListView<Key> redisKeysListView;

    @FXML
    private TextArea redisValueContent;

    @FXML
    private Label statusBar;

    private Connector connector;
    private String selectedKey;
    private ObservableList<Key> redisKeys = FXCollections.observableArrayList();
    private HashMap<String, Value> cache = new HashMap<>();

    public MainSceneController() {

    }

    @FXML
    private void initialize() {
        redisKeysListView.setItems(redisKeys);

        redisKeysListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showValueContent(newValue == null ? "" : newValue.getName()));
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
            return;
        } finally {
            redisKeys.clear();
            cache.clear();
        }
        fillRedisKeysList();
    }

    public void onDeleteKeyClicked() {
        String selectedKey = redisKeysListView.getSelectionModel().getSelectedItem().getName();
        if (selectedKey == null) {
            statusBar.setText("Chose key for delete it");
            return;
        }
        deleteKey(selectedKey);

        try {
            connector.del(selectedKey);
            statusBar.setText("Key \"" + selectedKey + "\" has been deleted");
        } catch (ConnectorException e) {
            String message = "Can't delete key " + selectedKey + " from DB";
            statusBar.setText(message);
            System.out.println(message);
        }

    }

    private void deleteKey(String key) {
        Optional<Key> needed = redisKeys.stream()
                .filter(item -> item.getName().equals(key)).findFirst();
        needed.ifPresent(value -> redisKeys.remove(value));
    }

    private void updateKeyType(String key, String type) {
        Optional<Key> needed = redisKeys.stream()
                .filter(item -> item.getName().equals(key)).findFirst();
        needed.ifPresent(value -> value.setType(type));
    }

    private void fillRedisKeysList() {
        redisKeys.clear();
        try {
            for (String rawKey : connector.keys()) {
                redisKeys.add(new Key(rawKey));
            }
        } catch (ConnectorException e) {
            String message = "Can't fetch keys from DB";
            statusBar.setText(message);
            System.out.println(message);
        }
    }

    private void showValueContent(String key) {
        clearStatusBar();
        if (key.equals("")) {
            redisValueContent.setText("");
            selectedKey = null;
            return;
        }

        selectedKey = key;
        if (!cache.containsKey(key)) {
            Value value;
            String type;
            try {
                type = connector.getType(key);
                value = connector.getValue(key);
            } catch (ConnectorException e) {
                value = null;
                type = null;
            }

            if (value != null) {
                cache.put(key, value);
                updateKeyType(key, type);
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

    public void onRefreshValueClicked() {
        if (selectedKey == null) {
            statusBar.setText("Select key first");
        } else {
            cache.remove(selectedKey);
            showValueContent(selectedKey);
            statusBar.setText("New value has been fetched");
        }
    }

    public void onRefreshKeysClicked() {
        this.selectedKey = null;
        cache.clear();
        this.fillRedisKeysList();
        statusBar.setText("Keys have been fetched");
    }
}
