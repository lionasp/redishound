package com.lionasp.controller;

import com.lionasp.connector.Connector;
import com.lionasp.connector.exceptions.ConnectorException;
import com.lionasp.connector.key.Key;
import com.lionasp.connector.value.Value;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.Console;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

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
            this.connector = null;
            return;
        } finally {
            redisKeys.clear();
            cache.clear();
        }
        fillRedisKeysList();
    }

    public void onDeleteKeyClicked() {
        if (!isConnectionSet()) {
            return;
        }

        String selectedKey = redisKeysListView.getSelectionModel().getSelectedItem().getName();
        if (selectedKey == null) {
            statusBar.setText("Select a key to delete it");
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
        if (!isConnectionSet()) {
            return;
        }

        if (selectedKey == null) {
            statusBar.setText("Select a key first");
        } else {
            cache.remove(selectedKey);
            showValueContent(selectedKey);
            statusBar.setText("New value has been fetched");
        }
    }

    public void onRefreshKeysClicked() {
        if (!isConnectionSet()) {
            return;
        }
        this.selectedKey = null;
        cache.clear();
        this.fillRedisKeysList();
        statusBar.setText("Keys have been fetched");
    }


    public void onAddKeyClicked(ActionEvent event) throws IOException {
        if (!isConnectionSet()) {
            return;
        }
        clearStatusBar();
        Stage dialog = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/addKeyScene.fxml")
        );
        Parent root = fxmlLoader.load();

        AddKeySceneController childController = fxmlLoader.getController();
        childController.setKeyCallback(response -> {
            String key = response.getKey();
            String value = response.getValue();

            try {
                connector.set(key, value);
            } catch (ConnectorException e) {
                statusBar.setText("Can't set the key");
                return;
            }

            statusBar.setText("Key was set successfully");
            this.fillRedisKeysList();

        });

        dialog.setScene(new Scene(root));
        dialog.setTitle("Add new key");
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(((Node)event.getSource()).getScene().getWindow());
        dialog.show();
    }


    private boolean isConnectionSet() {
        if (connector == null) {
            statusBar.setText("Please, connect to a server before any other operation");
            return false;
        }
        return true;
    }
}
