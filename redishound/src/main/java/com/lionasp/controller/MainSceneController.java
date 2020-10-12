package com.lionasp.controller;

import com.lionasp.connector.Connector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class MainSceneController {
    @FXML
    private TextField hostnameInput;

    @FXML
    private TextField portInput;

    @FXML
    private TextField dbNumberInput;

    @FXML
    private ListView<String> redisKeysListView;

    private Connector connector;
    private ObservableList<String> redisKeys = FXCollections.observableArrayList();

    public MainSceneController() {

    }

    @FXML
    private void initialize() {
        redisKeysListView.setItems(redisKeys);
    }

    public void onConnectButtonClicked() {
        this.connector = new Connector(
                TextFieldGetter.getValueFromTextField(hostnameInput),
                Integer.parseInt(TextFieldGetter.getValueFromTextField(portInput)),
                Integer.parseInt(TextFieldGetter.getValueFromTextField(dbNumberInput))
        );

        try {
            System.out.println(this.connector.ping());
        } catch (redis.clients.jedis.exceptions.JedisConnectionException | redis.clients.jedis.exceptions.JedisDataException e) {
            System.out.println("Connection failed");
            redisKeys.clear();
            return;
        }

        fillRedisKeysList();
    }

    public void onDeleteKeyClicked() {
        String selectedKey = redisKeysListView.getSelectionModel().getSelectedItem();
        if (selectedKey == null) {
            return;
        }
        redisKeys.remove(selectedKey);
        connector.del(selectedKey);
    }

    private void fillRedisKeysList() {
        redisKeys.addAll(connector.keys());
    }
}
