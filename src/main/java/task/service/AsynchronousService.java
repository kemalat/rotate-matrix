package task.service;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import task.config.ConfigProperties;
import task.exception.FileNotValidException;
import task.model.Dimension;
import task.util.ExtensionValidator;
import task.util.NumericValidator;

@Service
@Slf4j
public class AsynchronousService {

  @Autowired
  private TaskExecutor taskExecutor;

  @Autowired
  private ConfigProperties config;


  /**
   * Creates the instance from Runnable interface and starts thread for processing input file. If input file content and format is valid, 2D
   * array is generated, rotated Rotated matrix content is appended to input file. Input file extension is changed to .out and file is
   * renamed.
   */
  public void executeAsynchronously() {
    ExtensionValidator extensionValidator = new ExtensionValidator();
    taskExecutor.execute(() -> {
      log.info("Async Task started");

      boolean running = true;
      String path = System.getProperty("user.dir").concat(File.separator).concat(config.getInputDirectory());
      Path dir = Paths.get(path);
      while (running) {

        try {
          Thread.sleep(config.delayInMilis);

          Optional<Path> lastFilePath;
          lastFilePath = Files.list(dir)    // get the stream with full directory listing
              .filter(f -> !Files.isDirectory(f)) // exclude subdirectories from listing
              .filter(f -> extensionValidator.validate(f.toFile().getName()))  //filter only file with .in or .input extension
              .max(Comparator.comparingLong(f -> f.toFile().lastModified()));

          if (lastFilePath.isPresent()) {
            File inputFile = lastFilePath.get().toFile();
            log.info("Input file path : {}", inputFile.getPath());
            Dimension dimension;

            try {
              dimension = validateFileContent(inputFile.getPath());
            } catch (FileNotValidException e) {
              log.error(e.getMessage(), e);
              continue;
            }

            int[][] matrix = readArrayFromFile(inputFile.getPath(), dimension);
            rotateMatrix90(matrix);
            updateFile(inputFile.getPath(), matrix);
            File out = changeExtension(lastFilePath.get().toFile(), ".out");
            inputFile.renameTo(out);
          }
        } catch (InterruptedException e) {
          log.error(e.getMessage(), e);
          running = false;
        } catch (IOException e) {
          log.error(e.getMessage(), e);
          log.warn("Unrecoverable exception, stopping the thread");
          running = false;
        }
      }
      log.info("Async Task finished");
    });
  }

  /**
   * Update the input file content by appending rotated 2D array content as matrix
   *
   * @param path Input file path
   * @param matrix rotated 2D integer array
   */
  private void updateFile(String path, int[][] matrix) throws IOException {
    FileWriter fw = new FileWriter(path, true);
    BufferedWriter bw = new BufferedWriter(fw);
    PrintWriter out = new PrintWriter(bw);
    out.println("\n-----");
    for (int row = 0; row < matrix.length; row++) {
      for (int col = 0; col < matrix[row].length; col++) {
        out.print(matrix[row][col]);
        if (col < matrix[row].length - 1) {
          out.print(",");
        }
      }
      out.print("\n");
    }
    out.close();
    log.info("File updated.");

  }

  /**
   * Rotate square matrix by 90 degree clockwise direction. e.g. N =3 (1,1) moves to (1,3), (3,3) moves to (3,1)
   *
   * @param a 2D array as NxN matrix
   */
  private void rotateMatrix90(int[][] a) {

    log.info("Array to be rotated {}", Arrays.deepToString(a));

    int n = a[0].length;
    // Traverse each cycle
    for (int i = 0; i < n / 2; i++) {
      for (int j = i; j < n - i - 1; j++) {
        // Swap elements of each cycle in clockwise direction
        int temp = a[i][j];
        a[i][j] = a[n - 1 - j][i];
        a[n - 1 - j][i] = a[n - 1 - i][n - 1 - j];
        a[n - 1 - i][n - 1 - j] = a[j][n - 1 - i];
        a[j][n - 1 - i] = temp;
      }
    }
    log.info("Rotated array {}", Arrays.deepToString(a));
  }

  /**
   * Validates input file. Counts each line as row. Accepts each comma delimited value as column value
   *
   * @param path Input file path
   * @return 2D integer array
   * @throws {@link FileNotFoundException}
   * @throws {@link FileNotValidException}
   */
  private Dimension validateFileContent(String path) throws FileNotFoundException, FileNotValidException {
    NumericValidator numericValidator = new NumericValidator();

    Scanner sc = new Scanner(new BufferedReader(new FileReader(path)));
    List<Integer> perRowColCountList = new ArrayList<>();
    int rowCount = 0;
    int perRowColCount = 0;

    while (sc.hasNextLine()) {
      String line = sc.nextLine();
      rowCount++;

      String[] perRowColValues = line.split(",");
      for (String colValue : perRowColValues) {
        if (!numericValidator.isNumeric(colValue)) {
          sc.close();
          throw new FileNotValidException("Non-numeric value encountered in the file");
        }
      }

      perRowColCount = line.split(",").length;
      if (perRowColCount == 0) {
        sc.close();
        throw new FileNotValidException("No value found in the row by parsing with delimiter[,]");
      }
      perRowColCountList.add(perRowColCount);
    }

    if (rowCount == 0) {
      throw new FileNotValidException("Row count is zero");
    }

    boolean isColCountValid = true;
    Integer[] a = new Integer[perRowColCountList.size()];
    a = perRowColCountList.toArray(a);
    for (int i = 0; i < a.length; i++) {
      for (int k = i + 1; k < a.length; k++) {
        if (a[i] != a[k]) {
          isColCountValid = false;
          break;
        }
      }
      if (!isColCountValid) {
        break;
      }
    }

    if (!isColCountValid) {
      throw new FileNotValidException("Column number of row does not match with other rows");
    }

    if (perRowColCount != rowCount) {
      throw new FileNotValidException(
          "Only square matrix can be rotated in-place. Dimension of a rectangular must be changed from MxN to NxM. New matrix must be created");
    }

    log.info("File content is valid");
    return Dimension.builder().colNum(perRowColCount).rowNum(rowCount).build();


  }

  /**
   * Reads input file line by line, parse each line with given delimeter to get 2D array elements. Creates 2D array, fills array with
   * element values
   *
   * @param path Input file path
   * @param dimension Matrix dimension object a rowNum and colNum
   * @return 2D integer array
   * @throws {@link FileNotFoundException}
   */
  private int[][] readArrayFromFile(String path, Dimension dimension) throws FileNotFoundException {

    Scanner sc = new Scanner(new BufferedReader(new FileReader(path)));
    int rows = dimension.getRowNum();
    int columns = dimension.getColNum();
    int[][] myArray = new int[rows][columns];
    while (sc.hasNextLine()) {
      for (int i = 0; i < myArray.length; i++) {
        String[] line = sc.nextLine().trim().split(",");
        for (int j = 0; j < line.length; j++) {
          myArray[i][j] = Integer.parseInt(line[j]);
        }
      }
    }
    sc.close();
    return myArray;

  }

  /**
   * Changes the file extension with the given one. Creates new file object using the previous file's content with new extension
   *
   * @param f Input file object
   * @param newExtension is the new file extension which will replace the old.
   * @return {@link File} object with new extension
   */
  public File changeExtension(File f, String newExtension) {
    int i = f.getName().lastIndexOf('.');
    String name = f.getName().substring(0, i);
    return new File(f.getParent() + "/" + name + newExtension);
  }

}

