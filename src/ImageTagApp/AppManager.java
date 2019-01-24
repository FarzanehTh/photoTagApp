package ImageTagApp;

import MainApp.AlertBox;

import java.io.File;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.*;
import java.util.regex.Matcher;

public class AppManager implements Serializable {

  // private static final long serialVersionUID = 100L;

  /* This AppManager's map of Directories.*/
  private HashMap<String, ImageTagApp.Directory> listOfDirectories;

  /* This AppManager's logger for exceptions.*/
  private static Logger loggerEX = Logger.getLogger(AppManager.class.getName() + "2");
  /* This AppManager's logger for user .*/
  private static Logger loggerUser = Logger.getLogger(AppManager.class.getName());

  /* This AppManager's handler for logging user's tags.*/
  private static Handler UserHandler;

  /* This AppManager's handler for logging exceptions.*/
  private static Handler ExHandler;

  /* This AppManager's OS .*/
  private static String OS = System.getProperty("os.name").toLowerCase();

  /* This AppManager's current user home directory address.*/
  private String currentUsersHomeDir = System.getProperty("user.home");

  /* This AppManager's path of serialized file.*/
  private String serializedFilePath = currentUsersHomeDir + File.separator + "serialize.ser";

  /* This AppManager's File handler address for user's logging.*/
  private String userFileAddress = "userFileAddress.txt";
  /* This AppManager's File handler address for exceptions's logging.*/
  private String ExFile = currentUsersHomeDir + File.separator + "ExFile.txt";

  /**
   * Constructs a new AppManager
   *
   * @throws IOException
   * @throws ClassNotFoundException
   */
  public AppManager() throws IOException, ClassNotFoundException {

    listOfDirectories = new HashMap<>();

    /* associate the loggers with their handlers.*/
    loggerEX.setLevel(Level.ALL);
    loggerUser.setLevel(Level.ALL);

    setExHandler();
    setUserHandler();
    if (UserHandler != null) {
      System.out.println("File handler is build.");
      loggerUser.addHandler(UserHandler);
    }
    if (ExHandler != null) {
      loggerEX.addHandler(ExHandler);
    }

    /* read from file in the given path otherwise make a new file.*/
    File file = new File(serializedFilePath);

    if (file.exists()) {
      deserializeDirectories();
    } else {
      try {
        Boolean created = file.createNewFile();
        System.out.println("ser File is build.");
        if (!created) {
          loggerEX.log(Level.ALL, "serialized file is not created");
        }
      } catch (IOException e) {
        loggerEX.log(Level.ALL, e.toString());
      }
    }

    File userFile = new File("userFileSerialized.txt");

    if (userFile.exists()) {
      deserializeUserFiles();
    } else {
      try {
        Boolean created = userFile.createNewFile();
        if (!created) {
          loggerEX.log(Level.ALL, "user file is not created");
        }
      } catch (IOException e) {
        loggerEX.log(Level.ALL, e.toString());
      }
    }
  }

  /**
   * Set this AppManger's User Handler.
   *
   * @throws IOException
   * @throws SecurityException
   */
  private void setUserHandler() throws IOException, SecurityException {
    try {
      File file = new File("userFileAddress.txt");
      if (!file.exists()) {
        try {
          boolean created = file.createNewFile();
          UserHandler = new FileHandler("userFileAddress.txt");
          UserHandler.setFormatter(new SimpleFormatter());
          UserHandler.setLevel(Level.ALL);

          //        user = file;
          System.out.println("user file is created now");
          if (!created) {
            loggerEX.log(Level.ALL, "user file not created");
          }

        } catch (IOException e) {
          loggerEX.log(Level.ALL, e.toString());
        }
      }else {
      UserHandler = new FileHandler("userFileAddress.txt", true);
      UserHandler.setFormatter(new SimpleFormatter());
      UserHandler.setLevel(Level.ALL);}
    } catch (IOException | SecurityException ex) {
      loggerEX.log(Level.ALL, ex.toString());
    }
  }

  /**
   * Set this AppManager's Exception's Handler.
   *
   * @throws IOException
   * @throws SecurityException
   */
  private void setExHandler() throws IOException, SecurityException {
    try {
      File Ex = new File(this.ExFile);
      if (!Ex.exists()) {
        try {
          boolean created = Ex.createNewFile();
          {
            if (!created) {
              loggerEX.log(Level.ALL, "Not created");
            }
          }
        } catch (IOException e) {
          loggerEX.log(Level.ALL, e.toString());
        }
        ExHandler = new FileHandler("ExFile.txt");
        ExHandler.setLevel(Level.ALL);
      }
    } catch (IOException | SecurityException ex) {
      loggerEX.log(Level.ALL, ex.toString());
    }
  }

  /* Get list of directories of this AppManager .*/
  public HashMap<String, Directory> getListOfDirectories() {
    return listOfDirectories;
  }

  /**
   * Adds a Directory to the listOfDirectories of this AppManager.
   *
   * @param dir : Directory to be added
   */
  public void addToListOfDirectories(Directory dir) {
    this.listOfDirectories.put(dir.getDirName(), dir);
  }

  /* Serialize this AppManager's listOfDirectories.
   * @param path : String
   */
  public void serializeDirectories() throws IOException {
    try {

      FileOutputStream file = new FileOutputStream(serializedFilePath);

      BufferedOutputStream bufferedFile = new BufferedOutputStream(file);
      ObjectOutputStream output = new ObjectOutputStream(bufferedFile);

      /* serialize the output */

      output.writeObject(this.listOfDirectories);
      output.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
    serializeUserFile();
  }

  /** Deserialize this AppManager's list of directories . */
  private void deserializeDirectories() throws FileNotFoundException, ClassNotFoundException {

    try {

      FileInputStream file = new FileInputStream(serializedFilePath);
      BufferedInputStream bufferedFile = new BufferedInputStream(file);
      ObjectInputStream input = new ObjectInputStream(bufferedFile);

      /* deserialize the input */

      try {
        HashMap<String, Directory> map = (HashMap<String, Directory>) input.readObject();
        Set<String> set = map.keySet();
        for (String s : set) {
          listOfDirectories.put(s, map.get(s));
          input.close();
        }
      } catch (EOFException e) {
        System.out.println(e.toString());
      }
    } catch (IOException e) {
      System.out.println(e.toString());
    }
  }

  private void serializeUserFile() throws IOException {
    try {

      OutputStream file = new FileOutputStream("userFileSerialized.txt");
      //      Files.copy(Paths.get(this.userFileAddress), file);

      BufferedOutputStream bufferedFile = new BufferedOutputStream(file);
      ObjectOutputStream output = new ObjectOutputStream(bufferedFile);
      Path path = Paths.get("userFileAddress.txt");
      byte[] byteArray = Files.readAllBytes(path);

      /* serialize the output */

      output.writeObject(byteArray);
      output.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** Deserialize this AppManager's list of directories . */
  private void deserializeUserFiles() throws FileNotFoundException, ClassNotFoundException {

    try {

      InputStream file = new FileInputStream("userFileSerialized.txt");
      //      Files.copy(file,Paths.get(this.userFileAddress),REPLACE_EXISTING);
      BufferedInputStream bufferedFile = new BufferedInputStream(file);
      ObjectInputStream input = new ObjectInputStream(bufferedFile);
      //      Path path = Paths.get(this.userFileAddress);
      //      byte[] byteArray = Files.readAllBytes(path);

      /* deserialize the input */

      try {

        byte[] byteArray = (byte[]) input.readObject();
        //        byte[] byteArrayDeserialize = (byte[]) input.readObject();
        ArrayList<String> txtArray = new ArrayList<>();
        for (byte b : byteArray) {
          txtArray.add(Byte.valueOf(b).toString());
        }

        Files.write(Paths.get("userFileAddress.txt"), txtArray);

        input.close();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    } catch (EOFException e) {
      System.out.println(e.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Manages this AppManager's Directory of the ImageFiles .
   *
   * @param path : String
   * @param nameOfFile : String
   */
  public ImageFile manageDirectory(String path, String nameOfFile) {
    Directory dir = null;

    if (listOfDirectories.containsKey(path)) {
      dir = listOfDirectories.get(path);

      for (ImageFile file : dir) {
        if (file.getNameOfImageFile().equals(nameOfFile)) {
          return file;
        }
      }
    }
    if (dir != null) {
      ImageFile image = new ImageFile(nameOfFile);
      dir.addToListOfImageFiles(image);
      return image;
    } else {
      Directory newDir = new Directory(path);
      listOfDirectories.put(path, newDir);
      ImageFile img = new ImageFile(nameOfFile);
      newDir.addToListOfImageFiles(img);
      return img;
    }
  }

  /**
   * Manage adding the tag to the image .
   *
   * @param image the ImageFile to tag
   * @param tag the name of the tag to add
   */
  public boolean manageAddingTag(ImageFile image, String tag) {

    // check if the tag name is valid.
    if (isValidTag(tag)) {
      image.addTag(new ImageTag(tag));
      return true;
    } else {
      // show a dialog window and ask for a valid tag.
      AlertBox alertBox = new AlertBox();
      alertBox.display(
              "Invalid name for a tag",
              "This tag name is invalid, please choose a tag name containing"
                      + " only letters and numbers");
      return false;
    }
  }

  /**
   * Manage deleting the tag from the image .
   *
   * @param image the ImageFile to delete the tag from
   * @param oldTag the name of the tag to be deleted
   */
  public boolean manageDeletingTag(ImageFile image, String oldTag) {
    ArrayList<ImageTag> tempList = image.getListOfTags();

    for (ImageTag tag : tempList) {
      if (tag.getTagName().equals(oldTag)) {
        image.deleteTag(tag);
        return true;
      }
    }
    // Display error message
    AlertBox alertBox = new AlertBox();
    alertBox.display("Error", "" + "There is no such tag.");
    return false;
  }

  /**
   * Get list of dates of old tags of the imageFile .
   *
   * @param image the image to get a list of tags from
   * @return a list of tag dates
   */
  public ArrayList<String> getListOfTagsDates(ImageFile image) {
    ArrayList<String> listOfTags = new ArrayList<>();
    for (ImageTag tag : image.getListOfOldTags()) {
      listOfTags.add(tag.toStringByDate());
    }
    return listOfTags;
  }

  /**
   * Get list of tags names of the imageFile .
   *
   * @param image the ImageFile to get tag names from
   * @return a list of the image's tag names
   */
  public ArrayList<String> getListOfTagsNames(ImageFile image) {
    ArrayList<String> listOfTags = new ArrayList<>();
    for (ImageTag tag : image.getListOfOldTags()) {
      listOfTags.add(tag.toString());
    }
    return listOfTags;
  }

  /**
   * Returns a list of old tags of the imageFile.
   *
   * @param image the ImageFile to get old tags from
   * @return a list of the image's old tags
   */
  public ArrayList<ImageTag> getListOfOldTags(ImageFile image) {

    return image.getListOfOldTags();
  }

  // Adapted from :
  // https://stackoverflow.com/questions/24199679/rename-all-files-in-a-folder-using-java
  // Retrieved Nov 7, 2017.
  /**
   * Rename the name of the file in the directory of the user .
   *
   * @param file the file to be renamed
   * @param newName the new name of the file
   * @throws IOException if a file with the new name already exists
   */
  public void renameFile(File file, String newName) throws IOException {

    String path = makeDirPath(file);

    File newFile = new File(path + File.separator + newName);

    if (newFile.exists()) throw new java.io.IOException("file exists");

    boolean success = file.renameTo(newFile);
  }

  // Adapted from :
  // https://stackoverflow.com/questions/4645242/how-to-move-file-from-one-location-to-another-location-in-java
  // Retrieved Nov 7, 2017.
  /**
   * Move the file to a new directory in newPath.
   *
   * @param file the File to move
   * @param newPath the file's new path
   * @throws IOException
   */
  public void moveFile(File file, String newPath) throws IOException {

    Path pathTo = Paths.get(newPath + File.separator + file.getName());

    System.out.println(pathTo);

    Path formPath = Paths.get(file.getAbsolutePath());
    Files.move(formPath, pathTo);
  }

  public void logRenaming(ImageFile file) {

    loggerUser.log(Level.ALL, file.toString());
  }

  public void logExceptions(Exception e) {

    loggerEX.log(Level.ALL, e.toString(), e);
  }

  public String makeDirPath(File file) throws NullPointerException {

    if (file != null) {

      int i = file.getAbsolutePath().lastIndexOf(File.separator);

      System.out.println(file.getAbsolutePath().substring(0, i));

      return file.getAbsolutePath().substring(0, i);
    }
    return " ";
  }

  /* Check if the name is a valid tag name .
   * @param name the name of the tag to be checked.
   */
  private Boolean isValidTag(String name) {

    // Matcher matcher = ImageTag.getPattern().matcher(name);
    // return matcher.matches();
    String specialChars = ".,<>/?\\|}]{[+=_-)(*&^%$#@!~`\"':;";
    for (int i = 0; i < specialChars.length() - 1; i++) {
      if (name.contains(String.valueOf(specialChars.charAt(i)))) {
        return false;
      }
    }
    return true;
  }

  public String getUserHomeDir() {
    return currentUsersHomeDir;
  }
}
