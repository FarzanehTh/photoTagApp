package ImageTagApp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/* A directory of Files. */
public class Directory implements Iterable<ImageFile>, Serializable {

//  private static final long serialVersionUID = -3196010565417344348L;

  /* This Directory name .*/
  private String dirName;

  /* This Directory list of files .*/
  private ArrayList<ImageFile> listOfImageFiles = new ArrayList<>();

  /* This Directory list of names of files .*/
  private ArrayList<String> listOfImageNames = new ArrayList<>();

  public Directory(String path) {

    this.dirName = path;
  }

  /**
   * Returns the name of this Directory.
   *
   * @return the name of this Directory
   */
  String getDirName() {
    return dirName;
  }

  /**
   * Returns the list of ImageFiles in this Directory.
   *
   * @return this Directory's list of ImageFiles
   */
  public ArrayList<ImageFile> getListOfImageFiles() {
    return listOfImageFiles;
  }

  /**
   * Adds file to this Directory
   *
   * @param file the ImageFile to be added
   */
  void addToListOfImageFiles(ImageFile file) {
    this.listOfImageFiles.add(file);
  }

  /**
   * Deletes file from this Directory
   *
   * @param file the ImageFile to be deleted
   */
  public void deleteFromList(ImageFile file) {
    this.listOfImageFiles.remove(file);
  }

  /**
   * Returns the list of image names in this Directory.
   *
   * @return this Directory's list of image names
   */
  public ArrayList<String> getListOfImageNames() {
    return listOfImageNames;
  }

  /**
   * Adds a name to this Directory's list of image names.
   *
   * @param name the file name to be added
   */
  public void addToListOFImageNames(String name) {
    this.listOfImageNames.add(name);
  }

  /**
   * Deletes name from this Directory's list of image names.
   *
   * @param name the file name to be deleted
   */
  public void deleteFromNames(String name) {
    this.listOfImageNames.remove(name);
  }

  @Override
  public Iterator<ImageFile> iterator() {
    return new DirectoryIterator();
  }

  private class DirectoryIterator implements Iterator<ImageFile> {

    /* the next index to return. */
    int nextIndex;

    @Override
    public boolean hasNext() {
      return listOfImageFiles.size() != nextIndex;
    }

    @Override
    public ImageFile next() {
      ImageFile next = listOfImageFiles.get(nextIndex);
      nextIndex += 1;
      return next;
    }
  }
}
