package backend.listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 * MEMBER 3: TEST REPORTING DEMONSTRATION
 * ============================================================
 * 
 * This custom TestNG listener provides enhanced test reporting:
 * 
 * 1. Console output with visual formatting
 * 2. Custom log file generation
 * 3. Test statistics tracking
 * 4. Detailed failure reporting
 * 5. Summary generation
 * 
 * Usage: Add to testng.xml:
 * <listeners>
 *     <listener class-name="backend.listeners.MediWayTestListener"/>
 * </listeners>
 * 
 * ============================================================
 */
public class MediWayTestListener implements ITestListener {
    
    // Counters for test statistics
    private int passedTests = 0;
    private int failedTests = 0;
    private int skippedTests = 0;
    private long startTime;
    
    // Log file writer
    private PrintWriter logWriter;
    
    // Store test results for detailed reporting
    private List<TestResultInfo> passedList = new ArrayList<>();
    private List<TestResultInfo> failedList = new ArrayList<>();
    private List<TestResultInfo> skippedList = new ArrayList<>();
    
    // ============================================================
    // onStart - Called when the test suite starts
    // ============================================================
    
    @Override
    public void onStart(ITestContext context) {
        startTime = System.currentTimeMillis();
        
        // Initialize log file
        try {
            logWriter = new PrintWriter(new FileWriter("test-output/mediway-test-log.txt"));
        } catch (IOException e) {
            System.err.println("Warning: Could not create log file: " + e.getMessage());
        }
        
        String banner = "\n" +
            "╔══════════════════════════════════════════════════════════════════════════╗\n" +
            "║                                                                          ║\n" +
            "║   ███╗   ███╗███████╗██████╗ ██╗    ██╗    █████╗ ██╗   ██╗              ║\n" +
            "║   ████╗ ████║██╔════╝██╔══██╗██║    ██║   ██╔══██╗╚██╗ ██╔╝              ║\n" +
            "║   ██╔████╔██║█████╗  ██║  ██║██║    ██║   ███████║ ╚████╔╝               ║\n" +
            "║   ██║╚██╔╝██║██╔══╝  ██║  ██║██║    ██║   ██╔══██║  ╚██╔╝                ║\n" +
            "║   ██║ ╚═╝ ██║███████╗██████╔╝██║    ╚██╗██╗██║  ██║   ██║                ║\n" +
            "║   ╚═╝     ╚═╝╚══════╝╚═════╝ ╚═╝     ╚═╝╚═╝╚═╝  ╚═╝   ╚═╝                ║\n" +
            "║                                                                          ║\n" +
            "║              TESTNG TEST EXECUTION REPORT                                ║\n" +
            "║                                                                          ║\n" +
            "╠══════════════════════════════════════════════════════════════════════════╣\n" +
            "║  Suite:     " + padRight(context.getName(), 58) + "  ║\n" +
            "║  Started:   " + padRight(getCurrentTime(), 58) + "  ║\n" +
            "║  Host:      " + padRight(context.getHost(), 58) + "  ║\n" +
            "╚══════════════════════════════════════════════════════════════════════════╝\n";
        
        log(banner);
        log("\n[TEST EXECUTION STARTED]\n");
    }
    
    // ============================================================
    // onTestStart - Called when each test method starts
    // ============================================================
    
    @Override
    public void onTestStart(ITestResult result) {
        String testInfo = String.format(
            "\n▶ STARTING: %s.%s",
            result.getTestClass().getRealClass().getSimpleName(),
            result.getMethod().getMethodName()
        );
        
        log(testInfo);
        
        if (result.getMethod().getDescription() != null) {
            log("  Description: " + result.getMethod().getDescription());
        }
    }
    
    // ============================================================
    // onTestSuccess - Called when a test passes
    // ============================================================
    
    @Override
    public void onTestSuccess(ITestResult result) {
        passedTests++;
        long duration = result.getEndMillis() - result.getStartMillis();
        
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getRealClass().getSimpleName();
        
        passedList.add(new TestResultInfo(className, testName, duration, null));
        
        log(String.format("  ✅ PASSED: %s (%dms)", testName, duration));
    }
    
    // ============================================================
    // onTestFailure - Called when a test fails
    // ============================================================
    
    @Override
    public void onTestFailure(ITestResult result) {
        failedTests++;
        long duration = result.getEndMillis() - result.getStartMillis();
        
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getRealClass().getSimpleName();
        Throwable error = result.getThrowable();
        
        failedList.add(new TestResultInfo(className, testName, duration, error));
        
        log(String.format("  ❌ FAILED: %s (%dms)", testName, duration));
        
        if (error != null) {
            log("  └─ Error: " + error.getClass().getSimpleName() + ": " + error.getMessage());
            
            // Log stack trace to file only
            if (logWriter != null) {
                logWriter.println("\n--- Stack Trace ---");
                error.printStackTrace(logWriter);
                logWriter.println("--- End Stack Trace ---\n");
            }
        }
    }
    
    // ============================================================
    // onTestSkipped - Called when a test is skipped
    // ============================================================
    
    @Override
    public void onTestSkipped(ITestResult result) {
        skippedTests++;
        
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getRealClass().getSimpleName();
        
        skippedList.add(new TestResultInfo(className, testName, 0, result.getThrowable()));
        
        log(String.format("  ⏭️  SKIPPED: %s", testName));
        
        if (result.getThrowable() != null) {
            log("  └─ Reason: " + result.getThrowable().getMessage());
        }
    }
    
    // ============================================================
    // onFinish - Called when the test suite completes
    // ============================================================
    
    @Override
    public void onFinish(ITestContext context) {
        long totalTime = System.currentTimeMillis() - startTime;
        int totalTests = passedTests + failedTests + skippedTests;
        double passRate = totalTests > 0 ? (passedTests * 100.0 / totalTests) : 0;
        
        StringBuilder summary = new StringBuilder();
        
        // Summary Header
        summary.append("\n╔══════════════════════════════════════════════════════════════════════════╗\n");
        summary.append("║                     TEST EXECUTION SUMMARY                               ║\n");
        summary.append("╠══════════════════════════════════════════════════════════════════════════╣\n");
        summary.append("║                                                                          ║\n");
        summary.append(String.format("║  📊 STATISTICS                                                           ║\n"));
        summary.append(String.format("║  ├─ Total Tests:     %-50s  ║\n", totalTests));
        summary.append(String.format("║  ├─ ✅ Passed:       %-50s  ║\n", passedTests));
        summary.append(String.format("║  ├─ ❌ Failed:       %-50s  ║\n", failedTests));
        summary.append(String.format("║  ├─ ⏭️  Skipped:      %-50s  ║\n", skippedTests));
        summary.append(String.format("║  ├─ Pass Rate:       %-50s  ║\n", String.format("%.2f%%", passRate)));
        summary.append(String.format("║  └─ Duration:        %-50s  ║\n", formatDuration(totalTime)));
        summary.append("║                                                                          ║\n");
        
        // Progress Bar
        int barWidth = 40;
        int passedWidth = (int) ((passedTests * barWidth) / Math.max(totalTests, 1));
        int failedWidth = (int) ((failedTests * barWidth) / Math.max(totalTests, 1));
        int skippedWidth = barWidth - passedWidth - failedWidth;
        
        String progressBar = "█".repeat(Math.max(0, passedWidth)) + 
                           "▓".repeat(Math.max(0, failedWidth)) + 
                           "░".repeat(Math.max(0, skippedWidth));
        
        summary.append(String.format("║  [%s] ║\n", progressBar));
        summary.append(String.format("║   %-68s   ║\n", "█ Passed  ▓ Failed  ░ Skipped"));
        summary.append("║                                                                          ║\n");
        
        // Failed Tests Details
        if (!failedList.isEmpty()) {
            summary.append("╠══════════════════════════════════════════════════════════════════════════╣\n");
            summary.append("║  ❌ FAILED TESTS                                                         ║\n");
            summary.append("╠══════════════════════════════════════════════════════════════════════════╣\n");
            
            for (int i = 0; i < failedList.size(); i++) {
                TestResultInfo info = failedList.get(i);
                summary.append(String.format("║  %d. %-66s  ║\n", i + 1, 
                    truncate(info.className + "." + info.testName, 66)));
                if (info.error != null) {
                    summary.append(String.format("║     └─ %-62s  ║\n", 
                        truncate(info.error.getMessage(), 62)));
                }
            }
            summary.append("║                                                                          ║\n");
        }
        
        // Skipped Tests Details
        if (!skippedList.isEmpty()) {
            summary.append("╠══════════════════════════════════════════════════════════════════════════╣\n");
            summary.append("║  ⏭️  SKIPPED TESTS                                                        ║\n");
            summary.append("╠══════════════════════════════════════════════════════════════════════════╣\n");
            
            for (int i = 0; i < skippedList.size(); i++) {
                TestResultInfo info = skippedList.get(i);
                summary.append(String.format("║  %d. %-66s  ║\n", i + 1, 
                    truncate(info.className + "." + info.testName, 66)));
            }
            summary.append("║                                                                          ║\n");
        }
        
        // Final Status
        summary.append("╠══════════════════════════════════════════════════════════════════════════╣\n");
        if (failedTests == 0) {
            summary.append("║  🎉 ALL TESTS PASSED!                                                    ║\n");
        } else {
            summary.append("║  ⚠️  SOME TESTS FAILED - Please review the failures above               ║\n");
        }
        summary.append("╠══════════════════════════════════════════════════════════════════════════╣\n");
        summary.append(String.format("║  Completed: %-60s  ║\n", getCurrentTime()));
        summary.append("╚══════════════════════════════════════════════════════════════════════════╝\n");
        
        log(summary.toString());
        
        // Generate HTML Report snippet
        generateHtmlReportSnippet(totalTests, passedTests, failedTests, skippedTests, totalTime);
        
        // Close log file
        if (logWriter != null) {
            logWriter.close();
        }
    }
    
    // ============================================================
    // Helper Methods
    // ============================================================
    
    private void log(String message) {
        System.out.println(message);
        if (logWriter != null) {
            logWriter.println(message);
            logWriter.flush();
        }
    }
    
    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    private String padRight(String s, int width) {
        if (s == null) s = "N/A";
        if (s.length() > width) {
            return s.substring(0, width - 3) + "...";
        }
        return String.format("%-" + width + "s", s);
    }
    
    private String truncate(String s, int maxLength) {
        if (s == null) return "N/A";
        if (s.length() > maxLength) {
            return s.substring(0, maxLength - 3) + "...";
        }
        return s;
    }
    
    private String formatDuration(long millis) {
        if (millis < 1000) {
            return millis + "ms";
        } else if (millis < 60000) {
            return String.format("%.2fs", millis / 1000.0);
        } else {
            long minutes = millis / 60000;
            long seconds = (millis % 60000) / 1000;
            return String.format("%dm %ds", minutes, seconds);
        }
    }
    
    private void generateHtmlReportSnippet(int total, int passed, int failed, int skipped, long duration) {
        try {
            // Create directory if it doesn't exist
            java.nio.file.Path outputDir = java.nio.file.Paths.get("target/test-reports");
            java.nio.file.Files.createDirectories(outputDir);
            
            PrintWriter htmlWriter = new PrintWriter(new FileWriter("target/test-reports/mediway-summary.html"));
            htmlWriter.println("<!DOCTYPE html>");
            htmlWriter.println("<html><head><title>MEDI.WAY Test Summary</title>");
            htmlWriter.println("<style>");
            htmlWriter.println("body { font-family: Arial, sans-serif; margin: 20px; background: #f5f5f5; }");
            htmlWriter.println(".container { max-width: 800px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }");
            htmlWriter.println("h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }");
            htmlWriter.println(".stats { display: flex; justify-content: space-around; margin: 20px 0; }");
            htmlWriter.println(".stat { text-align: center; padding: 15px; border-radius: 8px; }");
            htmlWriter.println(".stat.passed { background: #d4edda; color: #155724; }");
            htmlWriter.println(".stat.failed { background: #f8d7da; color: #721c24; }");
            htmlWriter.println(".stat.skipped { background: #fff3cd; color: #856404; }");
            htmlWriter.println(".stat .number { font-size: 36px; font-weight: bold; }");
            htmlWriter.println(".progress { height: 30px; background: #e9ecef; border-radius: 15px; overflow: hidden; margin: 20px 0; }");
            htmlWriter.println(".progress-bar { height: 100%; display: inline-block; }");
            htmlWriter.println(".progress-bar.passed { background: #28a745; }");
            htmlWriter.println(".progress-bar.failed { background: #dc3545; }");
            htmlWriter.println(".progress-bar.skipped { background: #ffc107; }");
            htmlWriter.println("</style></head><body>");
            htmlWriter.println("<div class='container'>");
            htmlWriter.println("<h1>🏥 MEDI.WAY Test Results</h1>");
            htmlWriter.println("<p>Generated: " + getCurrentTime() + " | Duration: " + formatDuration(duration) + "</p>");
            
            // Stats boxes
            htmlWriter.println("<div class='stats'>");
            htmlWriter.printf("<div class='stat passed'><div class='number'>%d</div>Passed</div>%n", passed);
            htmlWriter.printf("<div class='stat failed'><div class='number'>%d</div>Failed</div>%n", failed);
            htmlWriter.printf("<div class='stat skipped'><div class='number'>%d</div>Skipped</div>%n", skipped);
            htmlWriter.println("</div>");
            
            // Progress bar
            double passedPct = total > 0 ? (passed * 100.0 / total) : 0;
            double failedPct = total > 0 ? (failed * 100.0 / total) : 0;
            double skippedPct = total > 0 ? (skipped * 100.0 / total) : 0;
            
            htmlWriter.println("<div class='progress'>");
            htmlWriter.printf("<div class='progress-bar passed' style='width: %.1f%%'></div>", passedPct);
            htmlWriter.printf("<div class='progress-bar failed' style='width: %.1f%%'></div>", failedPct);
            htmlWriter.printf("<div class='progress-bar skipped' style='width: %.1f%%'></div>", skippedPct);
            htmlWriter.println("</div>");
            
            htmlWriter.printf("<p><strong>Pass Rate: %.2f%%</strong></p>%n", passedPct);
            
            htmlWriter.println("</div></body></html>");
            
            htmlWriter.close();
            System.out.println("\n📄 Custom HTML report generated: target/test-reports/mediway-summary.html\n");
        } catch (IOException e) {
            System.err.println("Warning: Could not create HTML summary: " + e.getMessage());
        }
    }
    
    // ============================================================
    // Inner class to store test result info
    // ============================================================
    
    private static class TestResultInfo {
        final String className;
        final String testName;
        final long duration;
        final Throwable error;
        
        TestResultInfo(String className, String testName, long duration, Throwable error) {
            this.className = className;
            this.testName = testName;
            this.duration = duration;
            this.error = error;
        }
    }
}
