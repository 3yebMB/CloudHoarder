package dev.m13d.cloudhoarder.client;

import dev.m13d.cloudhoarder.common.AuthRequest;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginController {
    @FXML
    TextField login;

    @FXML
    PasswordField password;

    @FXML
    VBox globParent;

    @FXML
    Label errorLabel;

    public int id;

    public MainController backController;

    public void auth(ActionEvent actionEvent) {
        Network.sendMsg(new AuthRequest(login.getText(), password.getText()));

        System.out.println(login.getText() + " " + password.getText());
        System.out.println("id = " + id);
        globParent.getScene().getWindow().hide();
    }

public static class GuiHelper {
    public static void updateUI(Runnable runnable) {
            if (Platform.isFxApplicationThread()) {
                runnable.run();
            } else {
                Platform.runLater(runnable);
            }
        }
    }
}
