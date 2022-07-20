package hudson.plugins.view.dashboard.allure;

import static org.junit.Assert.assertSame;

import org.junit.Test;

public class AllureResultTest {

  /** Test of format method, of class TestStatisticsPortlet. */
  @Test
  public void testAllureResultPct() {
    AllureResult allureResult = new AllureResult(null, 0, 0, 0, 0, 0, 0);
    assertSame(0.0, allureResult.getPassedPct());
  }
}
