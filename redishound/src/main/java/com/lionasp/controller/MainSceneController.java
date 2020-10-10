package com.lionasp.controller;

import com.lionasp.connector.Connector;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class MainSceneController {
    @FXML
    private TextField hostnameInput;

    @FXML
    private TextField portInput;

    @FXML
    private TextField dbNumberInput;

    private Connector connector;

    public MainSceneController() {
    }

    public void onConnectButtonClicked() {
        String port = portInput.getText();
        if (port.equals("")) {
            port = portInput.getPromptText();
        }

        String dbNumber = dbNumberInput.getText();
        if (dbNumber.equals("")) {
            dbNumber = dbNumberInput.getPromptText();
        }

        String hostname = hostnameInput.getText();
        if (hostname.equals("")) {
            hostname = hostnameInput.getPromptText();
        }

        this.connector = new Connector(
                hostname,
                Integer.parseInt(port),
                Integer.parseInt(dbNumber)
        );

        try {
            System.out.println(this.connector.ping());
        } catch (redis.clients.jedis.exceptions.JedisConnectionException | redis.clients.jedis.exceptions.JedisDataException e) {
            System.out.println("Connection failed");
        }
    }
}
