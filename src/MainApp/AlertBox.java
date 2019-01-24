package MainApp;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

public class AlertBox {
    private boolean answer = false;



   //Adapted from :
   // https://github.com/buckyroberts/Source-Code-from-Tutorials/tree/master/JavaFX/005_creatingAlertBoxes
   // Retrieved Nov 7, 2017.
  /**
   * Displays an error message.
   *
   * @param title the title of the display window
   * @param message the error message
   */
  public boolean display(String title, String message) {


    Stage window = new Stage();

    // Block events to other windows
    window.initModality(Modality.APPLICATION_MODAL);
    window.setTitle(title);
    window.setMinWidth(400);

    Label label = new Label();
    label.setText(message);
    Button closeButton = new Button("OK");
    closeButton.setOnAction(event -> {
        answer = true;
        window.close();
    });


    VBox layout = new VBox(20);
    layout.getChildren().addAll(label, closeButton);
    layout.setAlignment(Pos.CENTER);

    // Display window and wait for it to be closed before returning
    Scene scene = new Scene(layout);
    window.setScene(scene);
    window.showAndWait();

    return answer;
  }
}
