package com.vo.formDashboard;

import com.vo.BaseTest;
import org.junit.jupiter.api.*;

import java.time.Duration;

import static com.codeborne.selenide.ClickOptions.usingDefaultMethod;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static reusables.ReuseActions.elementLocators;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Form Dashboard components validations")

public class FormDashBoardComponentsTest extends BaseTest {
    @BeforeAll
    @DisplayName("Open Sample Form")
    public static void navigateFormDashboardFavoritesForms() {
        open("/dashboard/Sample_Form");
    }

    @Test
    @DisplayName("Main components in Form Dashboard")
    @Order(1)
    public void verifyMainComponentsInFormDashboard() {
        $(elementLocators("FormDashboardHeader")).should(appear);
        $(elementLocators("UserDataList")).should(exist);   //User Data Lists should be available
        $(elementLocators("MyTasks")).should(exist);  //My Tasks section should be avaialble
        $(elementLocators("MySubmissions")).should(exist); //My Submissions section should be avaialble
        $(elementLocators("AllSubmissions")).should(exist); //All Submissions section should be avaialble
        $(elementLocators("DataCapture")).should(exist); //Data Capture section should be avaialble
    }

    //Commented this method as Expand and Collapse functionality is taken off currently
   /* @Test
    @DisplayName("Verify Expand/Collapse and layout of Dashboard")
    @Order(2)
    public void verifyExpandCollapseAndLayoutOfDashboard() {
        $("#gridItemTasks .vo-expand-collapse").should(exist).click(); //Click on Expand button in My Tasks section
        $("#gridItemUserDataList").should(disappear); //User Data Lists should disappear
        $("#gridItemTasks .vo-expand-collapse").should(exist).click(); //Verify that Collapse button exists and click on that
        $("#gridItemUserDataList").should(appear); //User Data list appears again meaning My Tasks is collapsed again
        $("#full-width-tabpanel-MY_DATA .vo-expand-collapse").should(exist).click(); //Expand button in User Data list section
        $("#gridItemTasks").should(disappear); //My tasks section has disappeared when User Data List section is expanded
        $("#full-width-tabpanel-MY_DATA .vo-expand-collapse").should(exist).click(); //Collapse button in User Data list section and click it
        $("#gridItemTasks").should(appear); //My tasks section appears again

    }*/

    @Test
    @DisplayName("Verify Switching on User Data List Tabs should change visible tables")
    @Order(3)
    public void verifySwitchingOnUserDataListTabs() {
        $(elementLocators("Body")).click();
        $(elementLocators("UserDataList")).should(exist);   //User Data Lists should be available
        $(elementLocators("MySubmissions")).click(usingDefaultMethod()).shouldHave(cssClass("Mui-selected"));
        $(elementLocators("GridContainer")).shouldBe(visible); //Grid for My Submissions

        $(elementLocators("AllSubmissions")).click(usingDefaultMethod()).shouldHave(cssClass("Mui-selected"));
        $(elementLocators("GridContainer")).should(appear, Duration.ofSeconds(10)); //Grid for My Submissions

        $(elementLocators("DataCapture")).click(usingDefaultMethod()).shouldHave(cssClass("Mui-selected"));
        $(elementLocators("GridContainer")).should(appear); //Grid for Data Capture
    }

}
