package com.vo.formDashboard;

import com.codeborne.selenide.Condition;
import com.vo.BaseTest;
import org.junit.jupiter.api.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Set;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static reusables.ReuseActions.elementLocators;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Menu actions validations")

public class MenuActionsTest extends BaseTest {
    @BeforeAll
    @DisplayName("Open Sample Form")
    public static void navigateFormDashboardFavoritesForms() {
        open("/dashboard/Sample_Form");
    }

    @Test
    @DisplayName("Verify Clicking on PDF opens new window")
    @Order(1)
    public void verifyClickPDFOpensTabWithJspdfBtn() {
        $(elementLocators("SubMenu")).should(exist).click(); //SubMenu button on Form Dashboard
        $(elementLocators("PdfInSubMenu")).should(exist).shouldHave(Condition.text("PDF")).click();

        //Handling multiple windows using Selenide
        Set<String> windowHandles = getWebDriver().getWindowHandles();
        System.out.println("Available windows are " + windowHandles);
        switchTo().window(1);
        $("#btnJsPdf").should(appear);
        switchTo().window(0);

}

    @Test
    @DisplayName("Verify Click on Edit Form Permission opens dialog Form Authorization")
    @Order(2)
    public void verifyClickOnEditFormPermOpensDialog() {
        $(elementLocators("SubMenu")).should(exist).click(); //SubMenu button on Form Dashboard
        $(elementLocators("EditFormPermissionsInSubMenu")).should(exist).shouldHave(Condition.text("Edit Form Permissions")).click(); //Edit permissions in submenu
        $(elementLocators("UsersContainer")).should(exist); //Form Authorization dialog container
        $(elementLocators("CancelButton")).should(exist).click(); //
    }

    @Test
    @DisplayName("Verify Click on Data Capture should open a modal dialog with Data Capture properties")
    @Order(3)
    public void verifyClickOnDataCaptureOpensModalDialog() {
        $(elementLocators("SubMenu")).should(exist).click(); //SubMenu button on Form Dashboard
        $(elementLocators("DataCaptureInSubMenu")).should(exist).shouldHave(Condition.text("Data Capture")).click();
        $(elementLocators("DataCaptureWindow")).should(appear);
        $(elementLocators("CancelDataCaptureButton")).should(exist).click();
        $(elementLocators("DataCaptureCancelConfirmButton")).should(exist).click(); //Confirmation dialog shown
    }

    @Test
    @DisplayName("Verify Click on copy form dataset url into Clipboard")
    @Order(4)
    public void verifyClickOnCopyFormDatasetCopiesUrl() {
        $(elementLocators("SubMenu")).should(exist).click(); //SubMenu button on Form Dashboard
        $(elementLocators("CopyFormDatasetURLToClipboard")).should(exist).shouldHave(Condition.text("Copy Form Dataset URL to Clipboard")).click();
        String clipBoardUrl = getClipboardContent();
        System.out.println("clipboard content: " + clipBoardUrl);
        assertTrue(clipBoardUrl.contains("vo-tenant-subscription-key"));
        assertTrue(clipBoardUrl.contains("MS-EXCEL"));
        assertTrue(clipBoardUrl.contains("targetLocale"));
        assertTrue(clipBoardUrl.contains("dateEpoch"));
    }
}
