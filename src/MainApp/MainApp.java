package MainApp;

import javafx.application.Application;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;

import javafx.scene.control.*;
import javafx.event.EventHandler;

import javafx.scene.control.Button;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.scene.image.Image;
import ImageTagApp.AppManager;
import ImageTagApp.ImageFile;
import ImageTagApp.ImageTag;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.util.ArrayList;

 import java.util.logging.Level;

public class MainApp extends Application {

  // ******************** Components ********************
  private Stage window;
  private Scene scene1, scene2;
  private TextField textField;
  private GridPane grid1;
  private ImageView selectedImageView;
  private File selectedFile;
  private ArrayList<String> imageDatesOfTags;
  private ArrayList<ImageTag> listOfImageTags;
  private ArrayList<String> imageNameOfTags;
  private String pathOfDir = null;
  private String nameOfFile = null;
  private ImageFile image = null;
  private AppManager appManager;
  private ComboBox<String> dropDownTagsByDate;
  private ComboBox<String> dropDownTagsByName;
  private ComboBox<String> dropDownOldNames;
  private Label listOfTags = new Label();

  public static void main(String[] args) {

    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    window = primaryStage;
    grid1 = new GridPane();
    selectedImageView = new ImageView();
    appManager = new AppManager();
    dropDownTagsByDate = new ComboBox<>(FXCollections.observableArrayList());
    dropDownTagsByName = new ComboBox<>();
    dropDownOldNames = new ComboBox<>();
    setItems(dropDownTagsByName);
    setItems(dropDownTagsByDate);
    setItems(dropDownOldNames);

    window.setTitle("Image Tag Manager");
    //  appManager.deserializeDirectories();

    // ******************** Scene 1 ********************

    Button sameDirButton = new Button();
    sameDirButton.setText("Choose from same directory");
    sameDirButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
    setItems(sameDirButton);

    sameDirButton.setDisable(true);

    sameDirButton.setOnAction(
            event -> {
              FileChooser fileChooser = new FileChooser();
              fileChooser.setTitle("Open Resource File");
              fileChooser.setInitialDirectory(selectedFile.getParentFile());
              fileChooser
                      .getExtensionFilters()
                      .addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
              File selected = fileChooser.showOpenDialog(window);
              setImage(selected);
              textField.setText(selectedFile.getName());
              setItems(sameDirButton);
            });

    Button addTagButton = new Button();
    addTagButton.setText("Start Modifying Tags");

    // No file has been selected: it is not possible to add tags yet
    addTagButton.setDisable(true);

    Button chooseFileButton = new Button();
    chooseFileButton.setText("Choose a File");
    chooseFileButton.setOnAction(
        event -> {
          FileChooser fileChooser = new FileChooser();
          fileChooser.setTitle("Open Resource File");
          fileChooser
              .getExtensionFilters()
              .addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
          File selected = fileChooser.showOpenDialog(window);
          if (selected != null){
            addTagButton.setDisable(false);
            sameDirButton.setDisable(false);
            setImage(selected);
          }
            textField.setText(selectedFile.getName());
            setItems(chooseFileButton);
          });

    update(selectedFile);

    textField = new TextField();
    textField.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
    setItems(textField);


    addTagButton.setOnAction(
        event -> {
          setDropDowns();
          window.setScene(scene2);
        });
    setItems(addTagButton);

    // ******************** Scene 1 ********************
    //        GridPane grid1 = new GridPane();
    grid1.setPadding(new Insets(20, 20, 20, 20));
    grid1.setHgap(8);
    grid1.setVgap(8);

    GridPane.setConstraints(chooseFileButton, 25, 2);
    GridPane.setConstraints(addTagButton, 25, 35);
    GridPane.setConstraints(textField, 25, 20);
    GridPane.setConstraints(sameDirButton, 30, 20);

    grid1.getChildren().addAll(chooseFileButton, addTagButton, textField, sameDirButton);

    scene1 = new Scene(grid1, 700, 700);

    // ******************** Scene 2 ********************
    update(selectedFile);

    // Display the tag corresponding to the selected date
    Label thisDateTags = new Label();
    dropDownTagsByDate
        .getSelectionModel()
        .selectedIndexProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (imageDatesOfTags != null && imageDatesOfTags.size() != 0) {
                String tagName = "";
                if (dropDownTagsByDate.getValue() != null) {
                  for (ImageTag tag : listOfImageTags) {
                    if (tag.getTagDate().toString().equals(dropDownTagsByDate.getValue())) {
                      tagName = tag.getTagName();
                      break;
                    }
                  }
                  thisDateTags.setText(tagName);
                } else {
                  thisDateTags.setText(null);
                }
              } else {
                thisDateTags.setText(null);
              }
            });

    // info on current tags
    Label currentTags = new Label();
    currentTags.setText("Current Tags:");
    setItems(currentTags);

    // ******************** Add Button ********************
    TextField addTagField = new TextField();
    addTagField.setPromptText("Type in your new tag");
    addTagField.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
    setItems(addTagField);

    Button addButton = new Button();
    addButton.setText("Add This Tag");
    addButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
    setItems(addButton);

    // Add the tags inputted in the addTagField
    addButton.setOnAction(
        event -> {
          update(selectedFile);
          boolean checkAdding = false;
          if (addTagField.getText().length() != 0) {

            checkAdding = appManager.manageAddingTag(image, addTagField.getText());
          } else {
            AlertBox addBox = new AlertBox();
            addBox.display("No tag is provided", "Please provide a tag to add.");
          }

          if (image != null && checkAdding) {
            try {

              appManager.renameFile(selectedFile, image.getNameOfImageFile());

              selectedFile = new File(pathOfDir + File.separator, image.getNameOfImageFile());
              appManager.logRenaming(image);
              //      update(selectedFile);
              //      setDropDowns();

              AlertBox okBox = new AlertBox();
              okBox.display(
                  "Tag Added",
                  "The tag \"@" + addTagField.getText() + "\" " + "was successfully added.");


              addTagField.setText(""); // Empty the text field
              setDropDowns();
            } catch (IOException e) {
              appManager.logExceptions(e);
            }
          } else {
            addTagField.setText(""); // Empty the text field
          }
        });

    // ******************** Delete Button ********************
    TextField deleteTagField = new TextField();
    deleteTagField.setPromptText("Type in the tag to delete");
    deleteTagField.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
    setItems(deleteTagField);

    Button deleteButton = new Button();
    deleteButton.setText("Delete This Tag");
    deleteButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
    setItems(deleteButton);

    // Add the handler to the button
    deleteButton.setOnAction(
        event -> {
          update(selectedFile);
          boolean check = false;
          if (deleteTagField.getText().length() != 0) {

            check = appManager.manageDeletingTag(image, deleteTagField.getText());
          } else {
            AlertBox addBox = new AlertBox();
            addBox.display("No tag is provided", "Please provide a tag to delete.");
          }

          if (image != null && check) {
            try {

              appManager.renameFile(selectedFile, image.getNameOfImageFile());

              selectedFile = new File(pathOfDir + File.separator, image.getNameOfImageFile());

              setDropDowns();

              // Notify user of successful deletion of tag
              AlertBox okBox = new AlertBox();
              okBox.display(
                  "Tag Removed",
                  "The tag \"@" + deleteTagField.getText() + "\" " + "was successfully removed.");

              // Update list of current tags:
              listOfTags.setText(
                  listOfTags.getText().replaceFirst("@" + deleteTagField.getText() + " ", ""));

              deleteTagField.setText(""); // Empty text field

            } catch (IOException e) {
              appManager.logExceptions(e);
            }
          } else {
            deleteTagField.setText(""); // Empty the text field
          }
        });

    // Button for moving to other directory
    Label moveLabel = new Label();
    moveLabel.setText("Choose a directory to move the current image to");
    moveLabel.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
    setItems(moveLabel);

    Button moveButton = new Button();
    moveButton.setText("Choose a Directory");
    moveButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
    setItems(moveButton);
    moveButton.setOnAction(
            event -> {

              DirectoryChooser dirChooser = new DirectoryChooser();
              dirChooser.setInitialDirectory(new File(appManager.getUserHomeDir()));
              File file = dirChooser.showDialog(window);

              try {
                appManager.moveFile(selectedFile, file.getAbsolutePath());
              } catch (IOException e) {

                appManager.logExceptions(e);
              }
            });

    // button for opening a text file of all logging
    Label logFileLabel = new Label();
    logFileLabel.setText("See the text File of history of Renaming:");
    logFileLabel.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
    setItems(logFileLabel);

    Button LogButton = new Button();
    LogButton.setText("See History File of All Renaming");
    LogButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
    setItems(LogButton);
    LogButton.setOnAction(

        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {

            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File(appManager.getUserHomeDir()));
            fileChooser
                .getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("text Files", "userFileAddress.txt"));

            //        File file1 = new
            // File(appManager.getUserHomeDir()+File.separator+"userFileAddress.txt");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.setInitialFileName("userFileAddress.txt");
            AlertBox boxOfName = new AlertBox();
            boxOfName.display(
                "The name of history File is userFileAddress.txt",
                "Please Search for "
                    + "userFileAddress.txt in "
                    + "the search bar to see the file. ");

            File file2 = fileChooser.showOpenDialog(window);

            if (file2 != null) {

              // first check if Desktop is supported by Platform or not
              if (!Desktop.isDesktopSupported()) {
                System.out.println("Desktop is not supported");
                return;
              }


              Desktop desktop = Desktop.getDesktop();
              if (file2.exists())
                try {

                  desktop.open(file2);
                } catch (IOException e) {
                  e.printStackTrace();
                }

            }
            else {
              AlertBox box = new AlertBox();
              box.display(
                      "There is no history File yet.",
                      "You do not have a history of Image" + " renaming yet.Please ");
            }
              }
            });
//
    // ******************** Back to Scene 1 Button ********************
    Button backButton = new Button();
    backButton.setText("Back");
    backButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
    setItems(backButton);

    backButton.setOnAction(
        event -> {
          window.setScene(scene1);
          dropDownTagsByName.getItems().clear();
          dropDownTagsByDate.getItems().clear();
          dropDownOldNames.getItems().clear();
        });

    //        // +++ case1 : labels and buttons and textFields of choose by date

    update(selectedFile);

    // ******************** Change Tag by Date Button ********************
    Button changeByDateButton = new Button();
    changeByDateButton.setText("Change Tags to this Date's Tag");
    changeByDateButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
    setItems(changeByDateButton);

    changeByDateButton.setOnAction(
        event -> {
          update(selectedFile);

          if (listOfImageTags != null) {
            for (ImageTag tag : listOfImageTags) {
              if (tag.getTagDate().toString().equals(dropDownTagsByDate.getValue())) {

                System.out.println(

                    "tag name is  " + tag.toString() + " and value in menu is  " + dropDownTagsByDate.getValue());
                System.out.println(tag.getTagDate().toString().equals(dropDownTagsByDate.getValue()));
                boolean checker = appManager.manageAddingTag(image, tag.getTagName());

                if (image != null && checker) {

                  try {
                    appManager.renameFile(selectedFile, image.getNameOfImageFile());
                  } catch (IOException e) {
                    appManager.logExceptions(e);
                  }

                  selectedFile = new File(pathOfDir + File.separator, image.getNameOfImageFile());
                  delFromDropDown(tag);

                  setDropDowns();
                }
                break;
              }
            }
          }
        });

    // ******************** Change by Name Option ********************
    setDropDowns();

    // ....button that changes the tag by name
    Button changeByNameButton = new Button();
    changeByNameButton.setText("Add this Old Tag");
    changeByNameButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
    setItems(changeByNameButton);

    changeByNameButton.setOnAction(
        event -> {
          update(selectedFile);

          if (listOfImageTags != null) {
            for (ImageTag tag : listOfImageTags) {
              if (tag.toString().equals(dropDownTagsByName.getValue())) {

                System.out.println(
                    "tag name is  " + tag.toString() + " and value in menu is  " + dropDownTagsByName.getValue());
                System.out.println(tag.toString().equals(dropDownTagsByName.getValue()));
                boolean checker = appManager.manageAddingTag(image, tag.getTagName());

                if (image != null && checker) {

                  try {
                    appManager.renameFile(selectedFile, image.getNameOfImageFile());

                  } catch (IOException e) {
                    appManager.logExceptions(e);
                  }

                  selectedFile = new File(pathOfDir + File.separator, image.getNameOfImageFile());
                  delFromDropDown(tag);
                  setDropDowns();
                }
                break;
              }
            }
          }
        });

    // ******************** Change to Old Name Option ********************
    Button changeToOldNameButton = new Button();
    changeToOldNameButton.setText("Change to Old Name");
    changeToOldNameButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
    setItems(changeToOldNameButton);

    changeToOldNameButton.setOnAction(
        event -> {
          update(selectedFile);
          if (image.getListOfNames() != null) {
            for (String name : image.getListOfNames()) {
              if (name.equals(dropDownOldNames.getValue())) {
                image.revert(name);
                try {
                  appManager.renameFile(selectedFile, name);
                  dropDownOldNames.getItems().remove(name);
                } catch (IOException e) {
                  e.printStackTrace();
                }

                selectedFile = new File(pathOfDir + File.separator, image.getNameOfImageFile());
                setDropDowns();
                break;
              }
            }
          }

        }
    );

    // ******************** Close the Program ********************
    window.setOnCloseRequest(e ->{
      e.consume();
      try {
        closeProgram();
      } catch (IOException e1) {
        appManager.logExceptions(e1);
      }
    });

    // ******************** Scene Layout ********************
    GridPane grid2 = new GridPane();
    grid2.setPadding(new Insets(20, 20, 20, 20));
    grid2.setHgap(10);
    grid2.setVgap(10);

    GridPane.setConstraints(currentTags, 2, 2);
    GridPane.setConstraints(listOfTags, 2, 3);
    GridPane.setConstraints(addButton, 10, 6);
    GridPane.setConstraints(addTagField, 2, 6);
    GridPane.setConstraints(deleteButton, 10, 7);
    GridPane.setConstraints(deleteTagField, 2, 7);
    GridPane.setConstraints(moveLabel, 2, 10);
    GridPane.setConstraints(moveButton, 2, 12);
    GridPane.setConstraints(logFileLabel, 2, 16);
    GridPane.setConstraints(LogButton, 2, 18);
    GridPane.setConstraints(backButton, 4, 35);

    // Case 1:
    GridPane.setConstraints(dropDownTagsByDate, 2, 30);
    GridPane.setConstraints(changeByDateButton, 10, 30);
    GridPane.setConstraints(thisDateTags, 2, 30);

    // Case 2:
    GridPane.setConstraints(dropDownTagsByName, 2, 31);
    GridPane.setConstraints(changeByNameButton, 10, 31);
    GridPane.setConstraints(dropDownOldNames, 2, 32);
    GridPane.setConstraints(changeToOldNameButton, 10, 32);


    grid2.getChildren().addAll(
            currentTags,
            listOfTags,
            addButton,
            addTagField,
            deleteTagField,
            deleteButton,
            dropDownTagsByDate,
            changeByDateButton,
            dropDownTagsByName,
            changeByNameButton,
            backButton,
            moveButton,
            moveLabel,
            logFileLabel,
            thisDateTags,
            LogButton,
            changeToOldNameButton,
            dropDownOldNames);

    scene2 = new Scene(grid2, 700, 700);

    window.setScene(scene1);
    window.show();
  }

  /**
   * Helper method: finds and fits to the screen the image from file
   *
   * @param file the image file
   */
  private void setImage(File file) {
    if (file != null) {
      selectedFile = file;
      setItems(listOfTags);
      Image image1 = null;

      try {
        // Adapted from:
        // http://www.bigsoft.co.uk/blog/index.php/2010/02/02/file-renameto-always-fails-on-windows
        // Retrieved on Nov 14, 2017
        FileInputStream filePath = null;
        try {
          filePath = new FileInputStream(selectedFile.getAbsolutePath());
          image1 = new Image(filePath);
        } finally {
          if (filePath != null) {
            filePath.close();
          }
        }
      } catch (IOException e) {
        appManager.logExceptions(e);
      }
      fitImage(image1);
    }
  }

  /**
   * Helper method: fits image to screen.
   *
   * @param image the image to fit
   */
  private void fitImage(Image image){
    selectedImageView.setImage(image);
    GridPane.setConstraints(selectedImageView, 25, 7);
    selectedImageView.setFitHeight(250);
    selectedImageView.setFitWidth(250);
    selectedImageView.setPreserveRatio(true);

    setItems(selectedImageView);

    if (!grid1.getChildren().contains(selectedImageView)) {
      grid1.getChildren().add(selectedImageView);
    }
  }

  /**
   * Set the alignment of buttons and menus in the scenes .
   *
   * @param item : Node
   */
  private void setItems(Node item) {
    GridPane.setValignment(item, VPos.CENTER);
    GridPane.setHalignment(item, HPos.CENTER);

    GridPane.setHgrow(item, Priority.NEVER);
    GridPane.setVgrow(item, Priority.NEVER);
  }

  /**
   * Updates the selected file.
   *
   * @param selectedFile: the file selected to update
   */
  private void update(File selectedFile) {

    if (selectedFile != null) {
      pathOfDir = appManager.makeDirPath(selectedFile);
      nameOfFile = selectedFile.getName();
      textField.setText(selectedFile.getName());
    }

    if (pathOfDir != null) {
      image = appManager.manageDirectory(pathOfDir, nameOfFile);
    }

    if (image != null) {

      imageDatesOfTags = appManager.getListOfTagsDates(image);
      listOfImageTags = appManager.getListOfOldTags(image);
      imageNameOfTags = appManager.getListOfTagsNames(image);

//      listOfTags.setText(appManager.getCurrTagsString(image));
    }
  }

  /** Sets the drop down menu. */
  private void setDropDowns() {
    update(selectedFile);
    if (imageDatesOfTags != null && imageDatesOfTags.size() != 0) {

      for (String tag : imageDatesOfTags) {
        if (!dropDownTagsByDate.getItems().contains(tag)) {
          dropDownTagsByDate.getItems().add(tag);
        }
      }
      String s = imageDatesOfTags.get(0);
      dropDownTagsByDate.setValue(s);
    } else {
      dropDownTagsByDate.setValue(null);
    }

    if (imageNameOfTags != null && imageNameOfTags.size() != 0) {
      for (String tag : imageNameOfTags) {
        if (!dropDownTagsByName.getItems().contains(tag)) {
          dropDownTagsByName.getItems().add(tag);
        }
      }
      String s = imageNameOfTags.get(0);
      dropDownTagsByName.setValue(s);
    } else {
      dropDownTagsByName.setValue(null);
    }
    dropDownOldNames.setValue(null);

    // THIS PART
    /*if (image.getListOfNames().size() != 0){
      String t = image.getListOfNames().get(0);
      dropDownOldNames.setValue(t);
    }*/

//    if (image != null && image.getListOfNames() != null) {
//      for (String name : image.getListOfNames()) {
//        if (!dropDownOldNames.getItems().contains(name)) {

    if (image != null && image.getListOfNames() != null){
      for (String name: image.getListOfNames()){
        if ((!dropDownOldNames.getItems().contains(name)) && (!name.equals(image.getNameOfImageFile()))){
//
          dropDownOldNames.getItems().add(name);
        }
      }
    }
  }

  /**
   * Deletes the tag from the drop down menu.
   *
   * @param tag: the ImageTag to delete
   */
  private void delFromDropDown(ImageTag tag) {
    String tagDate = tag.toStringByDate();
    String tagName = tag.toStringByDate();

    if (!dropDownTagsByDate.getItems().contains(tagDate)) {
      String s = imageNameOfTags.get(0);

      dropDownTagsByDate.getItems().remove(tagDate);

      dropDownTagsByDate.setValue(s);
    }

    if (!dropDownTagsByName.getItems().contains(tagName)) {
      String s = imageNameOfTags.get(0);

      dropDownTagsByName.getItems().remove(tagName);
      dropDownTagsByName.setValue(s);
    }
  }

  /** Makes sure the user wants to close the program and exits the program. */
  private void closeProgram() throws IOException {
    AlertBox close = new AlertBox();

    boolean answer = close.display(null, "Are you sure you want to exit?");
    if (answer) {
      appManager.serializeDirectories();
      window.close();
    }
  }
}
