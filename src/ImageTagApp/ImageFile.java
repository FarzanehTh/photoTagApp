package ImageTagApp;

import java.io.Serializable;
import java.util.ArrayList;

import java.util.Date;

import java.util.HashMap;

/* An ImageFile representation. */

public class ImageFile implements Serializable{

//  private static final long serialVersionUID = 400L;

  /* This ImageFile name */
  private String name;

/*  *//* This ImageFile dateOfLastUpdate *//*
  private String dateOfLastUpdate;*/

  private String NameBefore ;

  /* This ImageFile listOfOldTags */
  private ArrayList<ImageTag> listOfOldTags = new ArrayList<>();

  /* This ImageFile listOfTags */
  private ArrayList<ImageTag> listOfTags = new ArrayList<>();


//  /*This ImageFile's listOfNames */
//  private ArrayList<String> listOfNames = new ArrayList<>();


  private HashMap<String, ArrayList<ImageTag>> oldNamesToTags = new HashMap<>();

  public ImageFile(String path) {
    this.name = path;
    this.oldNamesToTags.put(this.name, new ArrayList<>(this.getListOfOldTags()));
  }

  /**
   * Reverts this ImageFile's name to the oldName and reverts all the tags to the tags associated with the old name.
   *
   * @param oldName the name to revert this ImageFile to
   */
  public void revert(String oldName){
    if (this.oldNamesToTags.containsKey(oldName)){
      this.oldNamesToTags.put(this.name, this.listOfTags);
      this.listOfOldTags.addAll(this.listOfTags);
      this.name = oldName;
      this.listOfTags = new ArrayList<>(this.oldNamesToTags.get(oldName));
      this.oldNamesToTags.remove(oldName);
    }
  }

  public String getNameOfImageFile() {
    return name;
  }

  ArrayList<ImageTag> getListOfOldTags() {
    return new ArrayList<>(listOfOldTags);
  }

  void addTag(ImageTag tag) {
    this.oldNamesToTags.put(this.name, new ArrayList<>(this.getListOfTags()));
    String temName = this.name;
    this.NameBefore = temName;
    this.name = "@" + tag.getTagName() + " " + temName;
    this.listOfTags.add(tag);
  }

  public ArrayList<String> getListOfNames(){
    return new ArrayList<>(oldNamesToTags.keySet());}

  void deleteTag(ImageTag tag) {
    this.oldNamesToTags.put(this.name, new ArrayList<>(this.listOfTags));

    String tem = this.name;
    int i = tem.indexOf(tag.getTagName());
    int t = tag.getTagName().length();
    String temName = tem.substring(0, i - 1) + tem.substring(i + t, tem.length());
    if (temName.startsWith(" ") || temName.startsWith("\n") || temName.startsWith("\t")) {
      this.name = temName.trim();
    } else {
      this.name = temName;
    }
    this.listOfTags.remove(tag);
    this.listOfOldTags.add(tag);
  }

  @Override
  public String toString() {


    Date date = new Date();
    String title =   "Date of Modification" + " || " + "Current Name of File" + " || " + " Previous Name of File";
    int len = this.listOfOldTags.size();

    return "     "  + title + "\n" + "  " + date + " || " + this.name + " || " + this.NameBefore + ".";
  }

  ArrayList<ImageTag> getListOfTags() {
    return new ArrayList<>(listOfTags);
  }
}
