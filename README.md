# Swag Labs Mobile Automation (Appium + Java + TestNG)

Android UI automation for the **Sauce Labs "Swag Labs" sample app** (v2.7.1), built with the Page Object Model, TestNG and Allure reporting.

## Tech stack

| Area | Tool |
|---|---|
| Language | Java 17 |
| Mobile automation | Appium Java Client 8.6.0 (UiAutomator2) + Selenium 4.13 |
| Test runner | TestNG 7.10 |
| Reporting | Allure 2.25 (screenshots + video attached on failure) |
| Build | Maven (Surefire 3.5.2, AspectJ weaver for `@Step`) |
| Config | `.env` via dotenv-java, per-device `.properties` capabilities |

## Project structure

```
swag-lab-app-automation/
├── pom.xml
├── SmokeTestSuite.xml            # runs tests in group "smoke"
├── RegressionTestSuite.xml       # runs tests in group "regression"
├── .env                          # credentials (git-ignored)
└── src/test
    ├── java
    │   ├── pages/                # Page Objects
    │   │   ├── BasePage          # waits, click, type, scroll, assertions helpers
    │   │   ├── CommonLocators    # header: menu, cart, cart badge
    │   │   ├── LoginPage
    │   │   ├── ProductsPage
    │   │   ├── ProductDetailsPage
    │   │   ├── CartPage
    │   │   ├── CheckoutInformationPage
    │   │   ├── CheckoutOverviewPage
    │   │   ├── CheckoutCompletePage
    │   │   └── MenuPage
    │   ├── testcases/            # Test classes
    │   │   ├── AuthenticatedTest # base class: logs in before each test
    │   │   ├── LoginPageTestCases
    │   │   ├── ProductsPageTestCases
    │   │   ├── ProductDetailsTestCases
    │   │   ├── CartPageTestCases
    │   │   ├── CheckoutTestCases
    │   │   └── MenuTestCases
    │   └── utilities/
    │       ├── BaseDriver        # creates/quits AndroidDriver per test, screenshot + video on failure
    │       ├── ConfigReader      # -D property > OS env var > .env > default
    │       ├── PropertyReader    # loads capabilities file
    │       ├── TestData          # users, password, checkout data
    │       ├── Product           # enum of the 6 products + expected prices
    │       └── TestListener      # console logging of test lifecycle
    └── resources
        ├── Android.SauceLabs.Mobile.Sample.app.2.7.1.apk
        └── capabilities_<device>.properties
```

## Prerequisites

1. **JDK 17** and **Maven 3.8+**
2. **Node.js** and **Appium 2.x** with the UiAutomator2 driver
   ```bash
   npm install -g appium
   appium driver install uiautomator2
   ```
3. **Android SDK / platform-tools** (`adb` on your PATH)
4. An Android **real device** (USB debugging on) or an **emulator**
5. *(Optional)* **Allure CLI** to view reports: <https://allurereport.org/docs/install/>

## Setup

1. **Install the app** on the device:
   ```bash
   adb install src/test/resources/Android.SauceLabs.Mobile.Sample.app.2.7.1.apk
   ```
2. **Create `.env`** in the project root:
   ```
   STANDARD_USER=standard_user
   LOCKED_OUT_USER=locked_out_user
   PROBLEM_USER=problem_user
   PASSWORD=secret_sauce
   ```
   These are the public demo credentials and are also the built-in defaults. `.env` is git-ignored.
3. **Configure your device** – copy an existing file in `src/test/resources/` to `capabilities_<name>.properties` and edit it:
   ```properties
   platformName=Android
   udid=<run `adb devices` to get it>
   deviceName=My Phone
   appPackage=com.swaglabsmobileapp
   appActivity=com.swaglabsmobileapp.MainActivity
   automationName=UiAutomator2
   ```
   Keep the capability keys consistent across files (either all with the `appium:` prefix or none).
4. **Start the Appium server** (tests connect to `http://127.0.0.1:4723`):
   ```bash
   appium
   ```

## Running tests

The device profile is chosen with `-Ddevice=<name>`, which loads `capabilities_<name>.properties` (default: `redmi_note_13`).

```bash
# Smoke suite (default suite configured in pom.xml)
mvn clean test -Ddevice=redmi_note_13

# Regression suite
mvn clean test -Ddevice=moto_g85_5G -Dsurefire.suiteXmlFiles=RegressionTestSuite.xml

# Override credentials at run time
mvn clean test -DSTANDARD_USER=standard_user -DPASSWORD=secret_sauce
```

Config priority: `-D` system property → OS environment variable → `.env` → default.

### Test groups

| Group | Meaning |
|---|---|
| `smoke` | Fast critical-path checks (login, add to cart, purchase, logout) |
| `regression` | Full suite (includes all smoke tests) |
| `negative` | Invalid-input / error-message tests |

## Reports and artifacts

- **Allure results** are written to `allure-results/` (or `target/allure-results/`).
  ```bash
  allure serve allure-results
  ```
- On **failure**, a screenshot and a screen recording are attached to the Allure report and also saved to `target/screenshots/` and `target/videos/`.
- Console logs for each test (start, pass/fail, duration, summary) come from `TestListener`.

## Test coverage

| Area | Scenarios |
|---|---|
| Login | page elements, placeholder/info texts, valid login, locked-out user, invalid/missing credentials (data-driven), autofill hint |
| Products | header controls, all 6 products listed, add/remove, cart badge, sort by name and price |
| Product details | name/price/description, add/remove from details, back navigation |
| Cart | empty cart, items listed, remove item(s), continue shopping, checkout |
| Checkout | required-field validation, cancel, totals (item total + tax = total), complete purchase |
| Menu | menu entries, logout, all items |

## Writing new tests

1. **Page object** – create a class in `pages/` extending `BasePage`. Keep locators `private`, expose actions and state methods, and return the next page object on navigation. Prefer `AppiumBy.accessibilityId("test-...")` over XPath.
   ```java
   public ProductsPage addToCart(Product product) {
       scrollToProduct(product.displayName());
       click(productButton(product.displayName(), ADD_DESC));
       return this;
   }
   ```
2. **Test class** – in `testcases/`, extend `AuthenticatedTest` (logged-in tests) or `BaseDriver` (e.g. login tests). Tag tests with groups and Allure annotations:
   ```java
   @Test(priority = 1, description = "...", groups = {"regression", "smoke"})
   ```
3. **Register** the class in `SmokeTestSuite.xml` / `RegressionTestSuite.xml`.
4. **Assert in the test**, not in the page object: page objects return values/booleans, tests call `Assert` / `SoftAssert`.

## Conventions

- One fresh Appium session (and app state) per test, created in `BaseDriver.@BeforeMethod`.
- Tests must be independent: never rely on another test's state.
- Test data lives in `TestData` / `Product`, not inside tests.
- Use `scrollToText(...)` / `scrollUntilVisible(...)` for items below the fold – the product list is virtualised, so off-screen items are not in the page source.

## Troubleshooting

| Problem | Fix |
|---|---|
| `Failed to load properties file` | The `-Ddevice` name doesn't match a `capabilities_<name>.properties` file |
| Session can't be created | Check `adb devices`, that Appium is running on port 4723, and that the UDID in the capabilities file is correct |
| App not found / wrong activity | Install the APK and verify `appPackage` / `appActivity` |
| `NoSuchElementException` on a screen | Locator changed – confirm it in Appium Inspector |
| Tests start already logged in | Remove `noReset=true` from the capabilities file |
| No Allure data | Make sure the run used the AspectJ agent (configured in `pom.xml` `argLine`) and point `allure serve` at the right results folder |

## Notes

- The tests target the **Android** build only.
- Locators follow the app's `test-*` accessibility ids; if you change the APK version, re-verify them with Appium Inspector.
