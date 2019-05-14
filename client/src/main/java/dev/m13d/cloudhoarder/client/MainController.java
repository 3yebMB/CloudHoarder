package dev.m13d.cloudhoarder.client;

import dev.m13d.cloudhoarder.common.AbstractMessage;
import dev.m13d.cloudhoarder.common.FileMessage;
import dev.m13d.cloudhoarder.common.FileRequest;
import dev.m13d.cloudhoarder.common.FileDeleteRequest;
import dev.m13d.cloudhoarder.common.FileListRequest;
import dev.m13d.cloudhoarder.common.FileRenameRequest;
import dev.m13d.cloudhoarder.common.AuthRequest;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    TextField tfFileName;

    @FXML
    ListView<Person> customCellListView;

    @FXML
    ListView<String> filesLList;

    @FXML
    ListView<String> filesRList;

    @FXML
    Button refreshBtn;

    @FXML
    Button deleteBtn;

    @FXML
    Button renameBtn;

    @FXML
    Label errorLabel;

    private ActionEvent actionEvent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Network.start();
//        initializePersonListView();
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    AbstractMessage am = Network.readObject();
                    if (am instanceof FileMessage) {
                        FileMessage fm = (FileMessage) am;
                        Files.write(Paths.get("client_storage/" + fm.getFileName()), fm.getData(), StandardOpenOption.CREATE);
                        refreshLocalFilesList();
                        continue;
                    }
                    if (am instanceof FileListRequest) {
                        FileListRequest fileListRequest = (FileListRequest) am;
                        refreshLocalFilesList();
                        continue;
                    }
                    if (am instanceof AuthRequest) {
                        AuthRequest authRequest = (AuthRequest) am;
                        if (!authRequest.isAuthOk()) {
                            showAuthWindow(authRequest.getAuthError());
                            return;
                        }
                        refreshLocalFilesList();
                        Network.sendMsg(new FileListRequest());
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
        showAuthWindow("");
    }

    private void showAuthWindow(String errorMessage) {
        LoginController.GuiHelper.updateUI(() -> {
            try {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));

                Parent root = loader.load();
                if (!errorMessage.isEmpty()) {
                    LoginController loginController = loader.getController();
                    errorLabel.setVisible(true);
                    errorLabel.setText(errorMessage);
                }
                stage.setTitle("Cloud sign-in");
                stage.setScene(new Scene(root, 400, 200));
                stage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void initializePersonListView() {
        customCellListView.getItems().addAll(new Person("User", "user_mail.@somemail.com"));
        customCellListView.setCellFactory(new Callback<ListView<Person>, ListCell<Person>>() {
            @Override
            public ListCell<Person> call(ListView<Person> studentListView) {
                return new PersonListCell();
            }
        });
    }

    public void pressOnDownloadBtn(ActionEvent actionEvent) {
        if (filesRList.getSelectionModel().getSelectedItem() != null && !filesRList.getSelectionModel().getSelectedItem().equals("")) {
            Network.sendMsg(new FileRequest(filesRList.getSelectionModel().getSelectedItem()));
        }
    }

    public void pressOnSendBtn(ActionEvent actionEvent) throws IOException, NoSuchAlgorithmException {
        if (filesLList.getSelectionModel().getSelectedItem() != null && Files.exists(Paths.get("client_storage/" + filesLList.getSelectionModel().getSelectedItem()))) {
            System.out.println("File is exist");
            Network.sendMsg(new FileMessage(Paths.get("client_storage/" + filesLList.getSelectionModel().getSelectedItem())));
        }
    }

    private void refreshRemoteFilesList(List<String> serverFileList) {
        LoginController.GuiHelper.updateUI(() -> {
            filesRList.getItems().clear();
            filesRList.getItems().addAll(serverFileList);
        });
    }

    public void refreshLocalFilesList() {
        LoginController.GuiHelper.updateUI(() -> {
            try {
                filesLList.getItems().clear();
                Files.list(Paths.get("client/client_storage")).map(p -> p.getFileName().toString()).forEach(o -> filesLList.getItems().add(o));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void pressOnRefreshBtn(ActionEvent actionEvent) {
        Network.sendMsg(new FileListRequest());
        refreshLocalFilesList();
    }

    public void pressOnDeleteBtn(ActionEvent actionEvent) throws IOException {
        if (filesLList.getSelectionModel().getSelectedItem() != null && !filesLList.getSelectionModel().getSelectedItem().equals("")) {
            Files.delete(Paths.get("client/client_storage/" + filesLList.getSelectionModel().getSelectedItem()));
            refreshLocalFilesList();
        } else {
            if (filesRList.getSelectionModel().getSelectedItem() != null && !filesRList.getSelectionModel().getSelectedItem().equals("")) {
                Network.sendMsg(new FileDeleteRequest(filesRList.getSelectionModel().getSelectedItem()));
            }
        }
    }

    public void pressOnRenameBtn(ActionEvent actionEvent) {
        if (filesLList.getSelectionModel().getSelectedItem() != null && !filesRList.getSelectionModel().getSelectedItem().equals("")) {
            String newFileName = askNewFileName(filesRList.getSelectionModel().getSelectedItem());
            if (!newFileName.isEmpty()) {
                Network.sendMsg(new FileRenameRequest(filesRList.getSelectionModel().getSelectedItem(), newFileName));
            }
        }
    }

    private String askNewFileName(String fileName) {
        TextInputDialog nfnDialog = new TextInputDialog();
        nfnDialog.setTitle("Переименование: " + fileName);
        nfnDialog.setHeaderText("Новое имя файла");
        nfnDialog.setContentText("Имя: ");
        Optional<String> result = nfnDialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        }
        return "";
    }

    public void btnExit(ActionEvent actionEvent) {
        System.exit(0);
    }
}
