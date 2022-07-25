package hudson.plugins.view.dashboard.allure;

import static hudson.plugins.view.dashboard.allure.AllureZipUtils.listEntries;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.junit.Test;

public class AllureZipUtilTest {

  /** Test of ListEntries, of class AllureZipUtils. */
  @Test
  public void testListEntries() {
    String pathToFile =
        Objects.requireNonNull(getClass().getResource("allure-report.zip")).getPath();
    String pathToElement = "allure-report".concat("/widgets");
    ZipFile archive;
    try {
      archive = new ZipFile(pathToFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    List<ZipEntry> entries = listEntries(archive, pathToElement);
    assertNotNull(entries);
    assertEquals(15, entries.size());
  }

  @Test
  public void testListEntriesEmptyPath() {
    String pathToFile =
        Objects.requireNonNull(getClass().getResource("allure-report.zip")).getPath();
    String pathToElement = "";
    ZipFile archive;
    try {
      archive = new ZipFile(pathToFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    List<ZipEntry> entries = listEntries(archive, pathToElement);
    assertNotNull(entries);
    assertEquals(73, entries.size());
  }
}
