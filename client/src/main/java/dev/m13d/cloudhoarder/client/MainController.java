package dev.m13d.cloudhoarder.client;

import dev.m13d.cloudhoarder.common.AbstractMessage;
import dev.m13d.cloudhoarder.common.FileMessage;
import dev.m13d.cloudhoarder.common.FileRequest;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    TextField tfFileName;

    @FXML
    ListView<String> filesLList;

    @FXML
    ListView<String> filesRList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Network.start();
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    AbstractMessage am = Network.readObject();
                    if (am instanceof FileMessage) {
                        FileMessage fm = (FileMessage) am;
                        Files.write(Paths.get("client_storage/" + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
                        refreshLocalFilesList();
                    }
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            } finally {
                Network.stop();
            }
        });
        t.setDaemon(true);
        t.start();
        refreshLocalFilesList();
    }

    public void pressOnDownloadBtn(ActionEvent actionEvent) {
        if (tfFileName.getLength() > 0) {
            Network.sendMsg(new FileRequest(tfFileName.getText()));
            tfFileName.clear();
        }
    }

    public void pressOnSendData(ActionEvent actionEvent) throws IOException {
        if (filesLList.getSelectionModel().getSelectedItem() != null && Files.exists(Paths.get("client_storage/" + filesLList.getSelectionModel().getSelectedItem()))) {
            System.out.println("File is exist");
            Network.sendMsg(new FileMessage(Paths.get("client_storage/" + filesLList.getSelectionModel().getSelectedItem())));
        }
    }

    public void refreshLocalFilesList() {
        if (Platform.isFxApplicationThread()) {
            try {
                filesLList.getItems().clear();
                Files.list(Paths.get("client_storage/")).map(p -> p.getFileName().toString()).forEach(o -> filesLList.getItems().add(o));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Platform.runLater(() -> {
                try {
                    filesLList.getItems().clear();
                    Files.list(Paths.get("client_storage/")).map(p -> p.getFileName().toString()).forEach(o -> filesLList.getItems().add(o));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
