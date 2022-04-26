package hudson.plugins.view.dashboard.allure;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/** eroshenkoam. 11.04.17 */
public final class AllureZipUtils {

  private AllureZipUtils() {}

  public static List<ZipEntry> listEntries(ZipFile zip, String path) {
    final Enumeration<? extends ZipEntry> entries = zip.entries();
    final List<ZipEntry> files = new ArrayList<>();
    while (entries.hasMoreElements()) {
      final ZipEntry entry = entries.nextElement();
      if (entry.getName().startsWith(path)) {
        files.add(entry);
      }
    }
    return files;
  }
}
