package com.vo;

import com.codeborne.selenide.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import utils.SelenideLogReport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.URL;
import java.rmi.Remote;
import java.time.Duration;
import java.util.*;
import java.util.logging.Level;

import static com.codeborne.selenide.ClickOptions.usingDefaultMethod;
import static com.codeborne.selenide.CollectionCondition.itemWithText;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static reusables.ReuseActions.elementLocators;


@Execution(ExecutionMode.SAME_THREAD)
@ExtendWith({SelenideLogReport.class})
public abstract class BaseTest {

    protected static String TEST_BASE_URL = null;
    public static String SAUCE_USERNAME = null;
    public static String SAUCE_ACCESS_KEY = null;
    public static ThreadLocal<String> BROWSER_CONFIG = ThreadLocal.withInitial(() -> "local");
    public static ThreadLocal<String> SAUCE_SESSION_ID = new ThreadLocal<>();
    public static ThreadLocal<RemoteWebDriver> WEB_DRIVER = new ThreadLocal<>();
    public static ThreadLocal<Map<String, Boolean>> ALREADY_LOGGED_IN = ThreadLocal.withInitial(HashMap::new);
    public static ThreadLocal<Map<String, UserType>> CURRENT_USER = ThreadLocal.withInitial(HashMap::new);
    public static ThreadLocal<Boolean> IGNORE_BEFORE_AND_AFTER_LIFECYCLE = ThreadLocal.withInitial(() -> Boolean.FALSE);

    public enum UserType {
        MAIN_TEST_USER("TEST_USER_EMAIL", "TEST_USER_PASSWORD"),
        USER_01("TEST_USER_EMAIL_01", "TEST_USER_PASSWORD_01"),
        USER_02("TEST_USER_EMAIL_02", "TEST_USER_PASSWORD_02"),
        USER_03("TEST_USER_EMAIL_03", "TEST_USER_PASSWORD_03");

        String envUserEmail, envUserPassword;

        UserType(String envUserEmail, String envUserPassword) {
            this.envUserEmail = envUserEmail;
            this.envUserPassword = envUserPassword;
        }

        public String userEmail() {
            return System.getenv(this.envUserEmail);
        }

        public String password() {
            return System.getenv(this.envUserPassword);
        }
    }

    @BeforeAll
    public static void setup() {
        if (IGNORE_BEFORE_AND_AFTER_LIFECYCLE.get()) {
            System.out.println(Thread.currentThread().getName() + " ignoring junit lifecycle setup");
            return;
        }
        try {
            String browserConfig = BROWSER_CONFIG.get();
            System.out.println("init webdriver with browserConfig " + browserConfig);
            Configuration.browserSize = "1920x1080";

            String buildId = Optional.ofNullable(System.getenv("BUILD_ID")).orElse("NO_BUILD_ID");

            String[] configOptions = browserConfig.split(":");
            String target = configOptions[0];

            TEST_BASE_URL = System.getenv("TEST_BASE_URL");
            Configuration.baseUrl = Optional.ofNullable(TEST_BASE_URL).orElse("https://visualorbit.fireo.net");
            //Configuration.baseUrl = "http://localhost:3000";
            Configuration.timeout = 30000;
            //Configuration.fastSetValue = true;
            //Configuration.clickViaJs = true;
            //Configuration.headless = true;

            if("local".equals(target)) {
                LoggingPreferences logPrefs = new LoggingPreferences();
                logPrefs.enable(LogType.BROWSER, Level.ALL);
                logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
                Configuration.browserCapabilities.setCapability("goog:loggingPrefs", logPrefs);
            }

            if ("sauce".equals(target) && WEB_DRIVER.get() == null) {
                String platform = Optional.of(configOptions[1]).orElse("Windows 10");
                String browser = Optional.of(configOptions[2]).orElse("chrome");
                String version = Optional.of(configOptions[3]).orElse("");

                SAUCE_USERNAME = System.getenv("SAUCE_USERNAME");
                SAUCE_ACCESS_KEY = System.getenv("SAUCE_ACCESS_KEY");
                String SAUCE_URL = "https://" + SAUCE_USERNAME + ":" + SAUCE_ACCESS_KEY + "@ondemand.eu-central-1.saucelabs.com:443/wd/hub";

                System.out.println("using sauce labs url: " + SAUCE_URL);


                DesiredCapabilities caps = new DesiredCapabilities();
                caps.setCapability("platformName", platform);
                caps.setCapability("browserName", browser);
                caps.setCapability("browserVersion", version);

                caps.setCapability("sauce:options", new LinkedHashMap() {{
                    put("username", SAUCE_USERNAME);
                    put("accessKey", SAUCE_ACCESS_KEY);
                    put("seleniumVersion", "");
                    put("build", buildId);
                    put("name", browserConfig);
                    put("maxDuration", 3600);
                    put("idleTimeout", 1000);
                    put("screenResolution", "1920x1200");
                }});
                //profile.content_settings.exceptions.clipboard
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.setExperimentalOption("prefs", new LinkedHashMap(){{
                    //0 is default , 1 is enable and 2 is disable
                    put("profile.content_settings.exceptions.clipboard", getClipBoardSettingsMap(1));
                }});

                caps.setCapability(ChromeOptions.CAPABILITY, chromeOptions);

                RemoteWebDriver driver = new RemoteWebDriver(new URL(SAUCE_URL), caps);
                WebDriverRunner.setWebDriver(driver);
                WEB_DRIVER.set(driver);
            }

            shouldLogin(UserType.MAIN_TEST_USER);


            //setAppLanguageToEnglish(); //Newly added
        } catch (Throwable t) {
            System.out.println("error on test setup: ");
            t.printStackTrace();
        }
    }

    private static Map<String, Object> getClipBoardSettingsMap(int settingValue) {
        Map cbPreference = new LinkedHashMap() {{
            put("[*.],*", new LinkedHashMap() {{
                put("last_modified", String.valueOf(System.currentTimeMillis()));
                put("setting", settingValue);
            }});
        }};
        return cbPreference;
    }

    public String getClipboardContent() {
        if (BROWSER_CONFIG.get().startsWith("local")) {
            return Selenide.clipboard().getText();
        }
        executeJavaScript("async function getCBContents() { " +
                "try { " +
                "  window.cb = await navigator.clipboard.readText(); " +
                "  console.log(\"Pasted content: \", window.cb); " +
                "} catch (err) { " +
                "  console.error(\"Failed to read clipboard contents: \", err); " +
                "  window.cb = \"Error : \" + err; " +
                "} } " +
                "getCBContents();");
        Object result = Optional.ofNullable(executeJavaScript("return window.cb;")).orElse("null");
        return result.toString();
    }

    // we close browser manually
    @AfterAll
    public static void tearDown() throws FileNotFoundException {
        Optional<String> currentSessionId = getCurrentSessionId();
        if (IGNORE_BEFORE_AND_AFTER_LIFECYCLE.get()) {
            System.out.println(Thread.currentThread().getName() + " ignoring junit lifecycle tearDown");
            return;
        }
        try {
            deleteForm();
            System.out.println("tearing down test!!!, closing the webdriver");
        } catch (Throwable ignore) {
            System.out.println("unable to delete forms: " + ignore.getMessage());
        } finally {
            try{
                LogEntries logEntries = getWebDriver().manage().logs().get(LogType.BROWSER);

                //Routing the browser console logs to a file
                String logFilePath = "build/reports/outputFile";
                //Instantiating the File class
                File fileOut = new File(logFilePath);
                //Instantiating the PrintStream class
                PrintStream stream = new PrintStream(fileOut);
                System.out.println("The browser logs will be saved in a file " + fileOut.getAbsolutePath());
                System.setOut(stream);

                for (LogEntry entry : logEntries) {
                    System.out.println(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());

                }
            } catch (Throwable ignore) {
                System.out.println("unable to retrieve logs from web driver: " + ignore.getMessage());
            }
            closeWebDriver();
            ALREADY_LOGGED_IN.get().put(currentSessionId.orElse(""), Boolean.FALSE);
        }
    }

    private static boolean appHeaderAppear() {
        boolean result = false;
        try {
            $(elementLocators("AppHeader")).should(appear, Duration.ofSeconds(15));
            result = true;
        } catch (Throwable t) {
            System.out.println("App Header is not presented ");
            t.printStackTrace();
        }
        return result;
    }

    public static void shouldLogin(UserType targetUser) {
        Optional<String> currentSessionId = getCurrentSessionId();
        if (currentSessionId.isPresent() &&
                (Boolean.TRUE.equals(ALREADY_LOGGED_IN.get().get(currentSessionId.get())) &&
                targetUser != CURRENT_USER.get().get(currentSessionId.get()))) {
            //logging out Current User
            $(elementLocators("UserSettingsPopover")).click(); //Click on User Settings Popover
            $(elementLocators("LogoutButton")).should(exist).click(); //Click on Logout
            $(elementLocators("ConfirmLogOutButton")).should(appear).click(); //Logout Confirmation
            boolean presenceOfPickAnAccount = $(elementLocators("PickAnAccount")).is(exist);
            if (presenceOfPickAnAccount) {
                $(byText(CURRENT_USER.get().get(currentSessionId.get()).userEmail())).shouldBe(visible).click();
                Wait().until((input) -> WebDriverRunner.url().endsWith("logoutsession"));
            }
            ALREADY_LOGGED_IN.get().put(currentSessionId.get(), Boolean.FALSE);
        }
        if (currentSessionId.isEmpty() ||
                Boolean.FALSE.equals(ALREADY_LOGGED_IN.get().getOrDefault(currentSessionId.orElse(""), Boolean.FALSE))) {
            open("");
            setSauceJobId();
            Wait().until((input) -> WebDriverRunner.url().contains("oauth2/v2.0/authorize"));
            boolean presenceUseOtherAccount = $(elementLocators("UseAnotherAccount")).is(exist);
            if (presenceUseOtherAccount) {
                $(elementLocators("UseAnotherAccount")).shouldBe(visible).click();
            }
            $(By.name(elementLocators("EmailInputField"))).should(appear).setValue(targetUser.userEmail());
            $(elementLocators("ButtonNext")).shouldBe(visible).click();
            $(elementLocators("DisplayName")).shouldHave(text(targetUser.userEmail()));

            $(By.name(elementLocators("PasswordInputField"))).should(appear).setValue(targetUser.password());
            $(elementLocators("ButtonNext")).shouldBe(visible).click();

            //stay signed in?
            boolean presenceOfStaySignedIn = $(elementLocators("ButtonNext")).is(exist);
            if (presenceOfStaySignedIn) {
                $(elementLocators("ButtonNext")).shouldBe(visible).click();
            }

            appHeaderAppear();
            boolean presenceOfPickAnAccount = $(elementLocators("PickAnAccount")).is(exist);
            if (presenceOfPickAnAccount) {
                // String valueToBeClicked = "//small[contains(text(),"+"'"+TEST_USER_EMAIL+"')]";
                $(byText(targetUser.userEmail())).shouldBe(visible).click();
            }

            //  assertEquals(title(), "VisualOrbit App");
            assertTrue(title().contains("VisualOrbit"));
            currentSessionId = getCurrentSessionId();
            if ( currentSessionId.isEmpty()) {
                throw new RuntimeException("Session ID not present after opening browser and completed login process");
            }
            ALREADY_LOGGED_IN.get().put(currentSessionId.get(), Boolean.TRUE);
            CURRENT_USER.get().put(currentSessionId.get(), targetUser);
        }
    }

    private static Optional<String> getCurrentSessionId() {
        try {
            String sessionID = ((RemoteWebDriver) getWebDriver()).getSessionId().toString();
            return Optional.ofNullable(sessionID);
        } catch (Throwable t) {
            return Optional.empty();
        }
    }

    // This method is commented as some of the ID's were changed
    // And the App's default language is set to ENGLISH
   /* public static void setAppLanguageToEnglish() {
        $("#toDashboard").shouldBe(visible);
        //if already in english -> skip
        if ($("#toDashboard").has(text("Launchpad"))) {
            return;
        }
        $("#btnUserSettingsPopover").should(exist).click(); //Wait until the element is visible on Dashboard and click on it
        $("#myPreferences").click(); //Click on preferences
        $("#userSettingsView").should(appear);
        $("#account_settings").shouldBe(visible).click(); //Account Settings
        String existingAppLanguage = $("#defaultLocale").should(exist).getText(); //Check the existing value of Language selected
        if (existingAppLanguage.contains("German")) {
            $("#defaultLocale").click();
            $("#defaultLocaleSelectMenu").should(appear);
            $$("#defaultLocaleSelectMenu li").shouldHave(texts("German - Germany", "English - Great Britain"));
            $$("#defaultLocaleSelectMenu li").findBy(text("English - Great Britain")).click();
            $("#btnSave").shouldBe(visible).click(); //Save the changes
            $("#btnSave").shouldHave(text("Save"));
            $("#toDashboard").click(); //Click on Home button
            $("#btnCreateForm").should(exist).click(); //Verify that user is on Dashboard page and click on Create form
        }
        //$("#user").should(exist).click(); //Click on Use icon and close the menu preferences
        $("#toDashboard").should(exist).click(); //Click on Launchpad
    }*/

    private static void setSauceJobId() {
        WebDriver webDriver = getWebDriver();
        if (webDriver instanceof RemoteWebDriver) {
            SessionId sessionId = ((RemoteWebDriver) webDriver).getSessionId();
            SAUCE_SESSION_ID.set(sessionId.toString());
        }
    }

    protected static void applyLabelForTestForms() {
        $(elementLocators("LabelContainer")).shouldBe(visible);
        if (!$(elementLocators("LabelInputField")).has(text("guitest"))) {
            $(elementLocators("LabelsDropdownButton")).should(exist).click();
            $(elementLocators("Popover")).should(appear);
            try {
                $$(elementLocators("ListOfOptions")).shouldHave(itemWithText("guitest"), Duration.ofSeconds(7));
                $$(elementLocators("ListOfOptions")).findBy(text("guitest")).click();
            } catch (Throwable t) {
                $(elementLocators("InputLabel")).setValue("guitest");
                $(elementLocators("Popover")).should(appear);
                $$(elementLocators("ListOfOptions")).shouldHave(itemWithText("Add \"guitest\""));
                $$(elementLocators("ListOfOptions")).findBy(text("Add \"guitest\"")).click();
            }
            $(elementLocators("LabelInputField")).shouldHave(text("guitest"));
        }
    }

    //Form Deletion with label: guitest
    public static void deleteForm() {
        open("/library/forms");
        $(elementLocators("CreateNewFormButton")).should(appear); //ensure library is loaded and create new form button is visible
        executeJavaScript("document.querySelector('#btnCleanupTestForms').style.visibility = 'inherit'"); //make hidden cleanup button visible for the test user
        $(elementLocators("CleanUpButton")).should(appear).click(usingDefaultMethod()).should(disappear, Duration.of(10, MINUTES));
    }

    protected static SelenideElement selectAndClear(String cssSelector) {
        return selectAndClear(By.cssSelector(cssSelector));
    }

    public static SelenideElement selectAndClear(By selector) {
        $(selector).should(exist).sendKeys(Keys.CONTROL, Keys.COMMAND, "a"); //Select the text (Ctrl + a)
        $(selector).sendKeys(Keys.DELETE); //Delete the selected text
        return $(selector).shouldBe(empty);
    }
}