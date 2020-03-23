package task.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtensionValidator{

  private Pattern pattern;
  private Matcher matcher;

  private static final String IMAGE_PATTERN =
      "([^\\s]+(\\.(?i)(in|input))$)";

  public ExtensionValidator(){
    pattern = Pattern.compile(IMAGE_PATTERN);
  }

  /** Validates input file extension with regular expression(*.in or *.input)
   *
   *
   * @param file 2D array file as input for validation
   * @return true if extension is valid otherwise false
   */
  public boolean validate(final String file){

    matcher = pattern.matcher(file);
    return matcher.matches();

  }
}
