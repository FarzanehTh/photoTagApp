package ImageTagApp;

import java.io.Serializable;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ImageTag implements Serializable{


//  private static final long serialVersionUID = 300L;

  /* This ImageTag name */
  private String tagName;
  /* This ImageTag pattern */
  private static Pattern pattern = Pattern.compile(".+");
  /* This ImageTag date.*/
  private Date tagDate;

  /**
   * Constructs a new ImageTag
   *
   * @param name the name of this ImageTag
   */
  public ImageTag(String name) {
    this.tagName = name;
    this.tagDate = new Date();
  }

  /**
   * Returns this ImageTag's Pattern.
   *
   * @return this ImageTag's Pattern
   */
  static Pattern getPattern() {
    return pattern;
  }

  /**
   * Returns this ImageTag's name.
   *
   * @return this ImageTag's name
   */
  public String getTagName() {
    return tagName;
  }

  /**
   * Returns the String representation of this ImageTag's date.
   *
   * @return the String representation of this ImageTag's date
   */
  public String toStringByDate() {
    return tagDate.toString();
  }

  /**
   * Returns this ImageTag's name.
   *
   * @return this ImageTag's name
   */
  @Override
  public String toString() {
    return tagName;
  }

  /**
   * Returns this ImageTag's date.
   *
   * @return this ImageTag's date
   */
  public Date getTagDate() {
    return tagDate;
  }
}
