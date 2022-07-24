package hudson.plugins.view.dashboard.allure;

import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static hudson.plugins.view.dashboard.allure.AllureZipUtils.listEntries;
import static org.junit.Assert.*;

public class AllureZipUtilTest {

  /** Test of ListEntries, of class AllureZipUtils. */
  @Test
  public void testListEntries() {
    String pathToFile = getClass().getResource("allure-report.zip").getPath();
    String pathToElement = "allure-report".concat("/widgets");
    ZipFile archive = null;
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
    String pathToFile = getClass().getResource("allure-report.zip").getPath();
    String pathToElement = "";
    ZipFile archive = null;
    try {
      archive = new ZipFile(pathToFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    List<ZipEntry> entries = listEntries(archive, pathToElement);
    assertNotNull(entries);
    assertEquals(73, entries.size());
  }

  @Test
  public void testListEntriesNullPath() {
    String pathToFile = getClass().getResource("allure-report.zip").getPath();
    ZipFile archive = null;
    try {
      archive = new ZipFile(pathToFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    List<ZipEntry> entries = listEntries(archive, null);
    assertNull(entries);
  }

  @Test
  public void testListEntriesNullArchive() {
    String pathToElement = "allure-report".concat("/widgets");
    List<ZipEntry> entries = listEntries(null, pathToElement);
    assertNull(entries);
  }
}
