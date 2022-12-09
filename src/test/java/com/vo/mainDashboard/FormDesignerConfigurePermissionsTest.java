package com.vo.mainDashboard;

import com.vo.BaseTest;
import org.junit.jupiter.api.*;

import java.time.Duration;

import static com.codeborne.selenide.CollectionCondition.itemWithText;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static reusables.ReuseActions.elementLocators;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Verify the Form Designer Configure Permissions")
public class FormDesignerConfigurePermissionsTest extends BaseTest {

    @Test
    @DisplayName("Open the Form Properties Test form")
    @Order(1)
    public void openFormDashboard(){
        open("/dashboard/Sample");
    }

    @Test
    @DisplayName("Verify the Configure Permissions in Form Properties Test form")
    @Order(2)
    public void configureFormPermissions() {
        $(elementLocators("LeftFormDashboardHeader")).should(appear);
        $(elementLocators("SubMenu")).should(appear).click();
        $(elementLocators("EditFormDesignInSubMenu")).should(exist).click(); //Click on Edit Form Design
        $(elementLocators("FormStructure")).should(exist);
        $(elementLocators("DesignerMenuContainer")).should(exist).click();
        $(elementLocators("ConfigPermissions")).click(); //Should click on Configure permissions
        $(elementLocators("FormPermissionsTab")).should(appear);
        $(elementLocators("AddUser")).should(exist).click(); //Click on "+" icon
        $(elementLocators("PermissionsWindow")).should(appear);
        $(elementLocators("SelectUserInput")).should(exist).click();
        $(elementLocators("Popover")).should(appear);
        $$(elementLocators("ListOfOptions")).shouldHave(itemWithText("GUI Tester 01"), Duration.ofSeconds(8));
        $$(elementLocators("ListOfOptions")).findBy(text("GUI Tester 01")).click(); //Click on the selected user
        $(elementLocators("ButtonConfirm")).should(exist).click(); //Click on Confirm
        $(elementLocators("AddedUser")).should(exist); //Newly added user should exists
        $(elementLocators("EditAddedUserPermissions")).should(exist).shouldBe(enabled).click(); //Click on edit pen icon
        $(elementLocators("PermissionsWindow")).should(exist);
        $(elementLocators("EditFormLabel")).should(exist).click(); //Click on Edit Form
        $(elementLocators("LeftArrow")).should(exist).click(); //Move selected element to the left
        $(elementLocators("ButtonConfirm")).should(exist).shouldBe(enabled).click(); //Click on Confirm
        $(elementLocators("SaveButton")).should(exist).click(); //Click on Save

        //Re-Opening the form to delete the added user
        $(elementLocators("Launchpad")).click(); //Click on Launchpad
        open("/dashboard/Sample");//Open the Form dashboard
        $(elementLocators("LeftFormDashboardHeader")).should(exist);
        $(elementLocators("SubMenu")).should(exist).click();
        $(elementLocators("EditFormDesignInSubMenu")).should(exist).click(); //Click on Edit Form Design
        $(elementLocators("DesignerMenuContainer")).should(exist).click(); //Click on Designer Menu container
        $(elementLocators("ConfigPermissions")).click(); //Should click on Configure permissions
        $(elementLocators("GrantedPermissions")).shouldNotHave(value("EDIT_FORM"));
        //Delete the selected User
        $(elementLocators("DeleteSelectedUser")).should(exist).click();
        $(elementLocators("SaveButton")).should(exist).click(); //Click on Save

    }
}
