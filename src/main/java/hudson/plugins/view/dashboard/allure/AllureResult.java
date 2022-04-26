package hudson.plugins.view.dashboard.allure;

import hudson.model.Job;

public class AllureResult {

  private final Job job;
  protected int total;
  protected int passed;
  protected int failed;
  protected int broken;
  protected int skipped;
  protected int unknown;

  public AllureResult(
      Job job, int total, int passed, int failed, int broken, int skipped, int unknown) {
    super();
    this.job = job;
    this.total = total;
    this.passed = passed;
    this.failed = failed;
    this.broken = broken;
    this.skipped = skipped;
    this.unknown = unknown;
  }

  public Job getJob() {
    return job;
  }

  public int getTotal() {
    return total;
  }

  public int getPassed() {
    return passed;
  }

  public double getPassedPct() {
    return getPct(passed);
  }

  public int getFailed() {
    return failed;
  }

  public double getFailedPct() {
    return getPct(failed);
  }

  public int getBroken() {
    return broken;
  }

  public double getBrokenPct() {
    return getPct(broken);
  }

  public int getSkipped() {
    return skipped;
  }

  public double getSkippedPct() {
    return getPct(skipped);
  }

  public int getUnknown() {
    return unknown;
  }

  public double getUnknownPct() {
    return getPct(unknown);
  }

  private double getPct(double dividendValue) {
    return total != 0 ? ((double) dividendValue / total) : 0d;
  }
}
