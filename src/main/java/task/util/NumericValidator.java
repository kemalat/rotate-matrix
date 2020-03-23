package task.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumericValidator {

  private Pattern pattern;
  private Matcher matcher;

  private static final String NUMBER_PATTERN = "-?\\d+(\\.\\d+)?";

  public NumericValidator(){
    pattern = Pattern.compile(NUMBER_PATTERN);
  }

  /** Validates string input with regular expression
   *
   *
   * @param strNum String object
   * @return true if string contains only numeric values otherwise false
   */
  public boolean isNumeric(final String strNum){

    if (strNum == null) {
      return false;
    }
    matcher = pattern.matcher(strNum);
    return matcher.matches();

  }
}
