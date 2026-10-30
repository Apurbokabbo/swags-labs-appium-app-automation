package utilities;

import io.appium.java_client.android.AndroidDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    private static final Logger logger = LoggerFactory.getLogger(TestListener.class);
    private static final String LINE =
            "-------------------------------------------------------------------------------------------------------";

    @Override
    public void onTestStart(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        MDC.put("test", methodName);

        logger.info(LINE);
        logger.info("🚀 Starting test: {}", methodName);
        logger.info("🖥️  Operating System: {}", System.getProperty("os.name"));
        logger.info("📱 Device profile: {}", System.getProperty("device", "Motorola"));

        // Read real device details from the live session (safe if driver is missing)
        try {
            AndroidDriver driver = BaseDriver.getDriver();
            logger.info("🤖 Device name: {}", driver.getCapabilities().getCapability("appium:deviceName"));
            logger.info("🔑 UDID: {}", driver.getCapabilities().getCapability("appium:udid"));
            logger.info("📦 App package: {}", driver.getCurrentPackage());
        } catch (Exception e) {
            logger.info("📱 Device details not available yet");
        }
        logger.info(LINE);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        long duration = result.getEndMillis() - result.getStartMillis();
        logger.info("✅ Test passed: {} ({} ms)", result.getMethod().getMethodName(), duration);
        MDC.remove("test");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("⏭️ Test skipped: {}", result.getMethod().getMethodName());
        MDC.remove("test");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        long duration = result.getEndMillis() - result.getStartMillis();

        logger.info(LINE);
        Throwable failureReason = result.getThrowable();
        if (failureReason != null) {
            logger.error("❌ Test failed: {} ({} ms)", methodName, duration, failureReason);
        } else {
            logger.error("❌ Test failed: {} ({} ms) | No exception found", methodName, duration);
        }
        logger.info(LINE);
        MDC.remove("test");
    }

    @Override
    public void onFinish(ITestContext context) {
        int total = context.getAllTestMethods().length;
        int passed = context.getPassedTests().size();
        int failed = context.getFailedTests().size();
        int skipped = context.getSkippedTests().size();

        logger.info("=======================================================================================================");
        logger.info("📦 Test suite: {}", context.getName());
        logger.info("📊 Total: {} | Passed: {} | Failed: {} | Skipped: {}", total, passed, failed, skipped);

        logger.info(LINE);
        logger.info("✅ Passed Tests:");
        context.getPassedTests().getAllResults().forEach(r ->
                logger.info("   - {}", r.getMethod().getMethodName()));

        logger.info("❌ Failed Tests:");
        context.getFailedTests().getAllResults().forEach(r ->
                logger.info("   - {}", r.getMethod().getMethodName()));

        logger.info("⏭️ Skipped Tests:");
        context.getSkippedTests().getAllResults().forEach(r ->
                logger.info("   - {}", r.getMethod().getMethodName()));
        logger.info("=======================================================================================================");
    }
}