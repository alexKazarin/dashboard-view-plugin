package hudson.plugins.view.dashboard.allure;

import static hudson.plugins.view.dashboard.allure.AllureZipUtils.listEntries;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import hudson.FilePath;
import hudson.matrix.MatrixProject;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.TopLevelItem;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class AllureUtil {

  private static final List<String> BUILD_STATISTICS_KEYS =
      Arrays.asList("passed", "failed", "broken", "skipped", "unknown", "total");

  /** "Cache" if matrix plugin is installed, so exception handling is only triggered once. */
  private static boolean matrixPluginInstalled = true;

  /**
   * Summarize the last test results from the passed set of jobs. If a job doesn't include any
   * tests, add a 0 summary.
   */
  public static AllureResultSummary getAllureResultSummary(
      Collection<TopLevelItem> jobs, boolean hideZeroTestProjects) {
    AllureResultSummary summary = new AllureResultSummary();

    for (TopLevelItem item : jobs) {
      if (isMatrixJob(item)) {
        MatrixProject mp = (MatrixProject) item;

        for (Job configuration : mp.getActiveConfigurations()) {
          summarizeJob(configuration, summary, hideZeroTestProjects);
        }
      } else if (item instanceof Job) {
        Job job = (Job) item;
        summarizeJob(job, summary, hideZeroTestProjects);
      }
    }

    return summary;
  }

  private static void summarizeJob(
      Job job, AllureResultSummary summary, boolean hideZeroTestProjects) {
    boolean addBlank = true;
    Run lastBuildRun = job.getLastBuild();
    if (lastBuildRun != null) {
      final FilePath report =
          new FilePath(lastBuildRun.getRootDir()).child("archive/allure-report.zip");

      try {
        if (report.exists()) {
          try (ZipFile archive = new ZipFile(report.getRemote())) {
            String reportPath = "allure-report";
            Optional<ZipEntry> summaryZipEntry = getSummary(archive, reportPath, "export");
            if (!summaryZipEntry.isPresent()) {
              summaryZipEntry = getSummary(archive, reportPath, "widgets");
            }
            if (summaryZipEntry.isPresent()) {
              try (InputStream is = archive.getInputStream(summaryZipEntry.get())) {
                final ObjectMapper mapper = new ObjectMapper();
                final JsonNode summaryJson = mapper.readTree(is);
                final JsonNode statisticJson = summaryJson.get("statistic");
                final Map<String, Integer> statisticsMap = new HashMap<>();
                for (String key : BUILD_STATISTICS_KEYS) {
                  statisticsMap.put(key, statisticJson.get(key).intValue());
                }
                addBlank = false;
                if (statisticsMap.get("total") > 0 || !hideZeroTestProjects)
                  summary.addAllureResult(
                      new AllureResult(
                          job,
                          statisticsMap.get("total"),
                          statisticsMap.get("passed"),
                          statisticsMap.get("failed"),
                          statisticsMap.get("broken"),
                          statisticsMap.get("skipped"),
                          statisticsMap.get("unknown")));
              }
            }
          }
        }
      } catch (IOException | InterruptedException ignore) {
      }
    }

    if (addBlank && !hideZeroTestProjects) {
      summary.addAllureResult(new AllureResult(job, 0, 0, 0, 0, 0, 0));
    }
  }

  private static Optional<ZipEntry> getSummary(
      final ZipFile archive, final String reportPath, final String location) {
    List<ZipEntry> entries = listEntries(archive, reportPath.concat("/").concat(location));
    Optional<ZipEntry> summaryZipEntry =
        Iterables.tryFind(
            entries,
            input ->
                input != null
                    && input
                        .getName()
                        .equals(reportPath.concat("/").concat(location).concat("/summary.json")));
    return summaryZipEntry;
  }

  public static AllureResult getAllureResult(Run run) {

    if (run != null) {
      final FilePath report = new FilePath(run.getRootDir()).child("archive/allure-report.zip");

      try {
        if (report.exists()) {
          try (ZipFile archive = new ZipFile(report.getRemote())) {
            String reportPath = "allure-report";
            Optional<ZipEntry> summaryZipEntry = getSummary(archive, reportPath, "export");
            if (!summaryZipEntry.isPresent()) {
              summaryZipEntry = getSummary(archive, reportPath, "widgets");
            }
            if (summaryZipEntry.isPresent()) {
              try (InputStream is = archive.getInputStream(summaryZipEntry.get())) {
                final ObjectMapper mapper = new ObjectMapper();
                final JsonNode summaryJson = mapper.readTree(is);
                final JsonNode statisticJson = summaryJson.get("statistic");
                final Map<String, Integer> statisticsMap = new HashMap<>();
                for (String key : BUILD_STATISTICS_KEYS) {
                  statisticsMap.put(key, statisticJson.get(key).intValue());
                }
                if (statisticsMap.get("total") > 0) {
                  return new AllureResult(
                      run.getParent(),
                      statisticsMap.get("total"),
                      statisticsMap.get("passed"),
                      statisticsMap.get("failed"),
                      statisticsMap.get("broken"),
                      statisticsMap.get("skipped"),
                      statisticsMap.get("unknown"));
                }
              }
            }
          }
        }
      } catch (IOException | InterruptedException ignore) {
      }
      return new AllureResult(run.getParent(), 0, 0, 0, 0, 0, 0);
    }
    return null;
  }

  private static final boolean isMatrixJob(TopLevelItem item) {
    if (!matrixPluginInstalled) {
      return false;
    }
    try {
      return item instanceof MatrixProject;
    } catch (NoClassDefFoundError x) {
      matrixPluginInstalled = false;
    }
    return false;
  }
}
