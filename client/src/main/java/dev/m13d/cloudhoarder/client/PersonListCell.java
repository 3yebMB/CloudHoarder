package dev.m13d.cloudhoarder.client;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class PersonListCell extends ListCell<Person> {
    private FXMLLoader mLLoader;

    public Label label1;
    public Label labelDescription;
    public HBox vbPane;

    @Override
    public void updateSelected(boolean selected) {
        super.updateSelected(selected);
    }

    @Override
    protected void updateItem(Person person, boolean empty) {
        super.updateItem(person, empty);
        if (empty || person == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("/PersonListViewCell.fxml"));
                mLLoader.setController(this);
                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            label1.setText(person.getName() + " ★");
            labelDescription.setText(person.getEmail());
//            if (!task.isChecked()) {
//                setStyle("-fx-background-color: linear-gradient(#F9FFA1 0%, #fb9d00 100%); -fx-background-radius: 10.0; -fx-border-color: #aa5500; -fx-border-radius: 10; -fx-text-fill: #ff0000; -fx-text-color: #00ff00;");
//            } else
//                setStyle("-fx-background-color: linear-gradient(#f62b2b 0%, #d20202 100%); -fx-background-radius: 10.0; -fx-border-color: #550000; -fx-border-radius: 10;");
            setText(null);
            setGraphic(vbPane);
        }
    }
}
