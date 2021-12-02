package com.vo.mainDashboard;

import com.codeborne.selenide.Condition;
import com.vo.BaseTest;
import org.junit.jupiter.api.*;

import java.time.Duration;

import static com.codeborne.selenide.CollectionCondition.itemWithText;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static reusables.ReuseActions.elementLocators;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Verify the Form Properties")
public class EditFormPropertiesTest extends BaseTest {

    @Test
    @DisplayName("Open the Form Properties Test form")
    @Order(1)
    public void openFormDashboard(){
        open("/dashboard/Sample");
    }

    @Test
    @DisplayName("Edit the Form Properties Test form")
    @Order(2)
    public void editFormProperties() {
        $(elementLocators("LeftFormDashboardHeader")).should(appear, Duration.ofSeconds(30));
        $(elementLocators("SubMenu")).should(appear, Duration.ofSeconds(8)).click();
        $(elementLocators("EditFormDesignInSubMenu")).should(exist).click(); //Click on Edit Form Design
        $(elementLocators("FormStructure")).should(exist);
        $(elementLocators("ElementProperties")).should(exist);
        $(elementLocators("DesignerMenu")).should(exist).click();
        $(elementLocators("FormProperties")).click(); //Should click on Form Properties
        $(elementLocators("DesignerTab")).should(exist);
        $(elementLocators("IconSelection")).click();
        $(elementLocators("IconsWindow")).should(appear);

        String iconName= $(elementLocators("BusinessPersonIcon")).getAttribute("title");
        $(elementLocators("BusinessPersonIcon")).click(); //Should add selected Icon
        $(elementLocators("LabelsInput")).should(exist);
        $(elementLocators("FormLabelSelection")).click(); //Should click on Label
        $(elementLocators("Popover")).should(appear);
        $$(elementLocators("ListOfOptions")).shouldHave(itemWithText("SKB"), Duration.ofSeconds(8));
        $$(elementLocators("ListOfOptions")).findBy(text("SKB")).click(); //Click on the selected Label
        $(elementLocators("AddFormLang")).should(exist).click();
        $(elementLocators("GermanLang")).should(exist);
        String newLang=$(elementLocators("GermanLang")).getText();

        //Should Add another language
        $(elementLocators("SelectLanguage")).should(exist).click();
        $(elementLocators("PublishButton")).click(); //Click on Publish
        $(elementLocators("ConfirmPublish")).click();
        $(elementLocators("ConfirmationMessage")).should(appear).shouldHave(Condition.text("The form was published successfully"));

        //Verify the selected options
        $(elementLocators("Launchpad")).click(); //Click on Launchpad
        //open("/dashboard/Form_Properties_Sample"); //Open the Form
        open("/dashboard/Sample");//Open the Form designer

        $(elementLocators("LeftFormDashboardHeader")).should(exist);
        $(elementLocators("SubMenu")).should(appear, Duration.ofSeconds(8)).click();
        $(elementLocators("EditFormDesignInSubMenu")).should(exist).click(); //Click on Edit Form Design
        $(elementLocators("FormTreeIcon")).shouldHave(attributeMatching("data-src", ".*"+iconName+".*"));
        $(elementLocators("DesignerLanguage2")).shouldHave(text(newLang.toUpperCase())); //German language should exist
        $(elementLocators("DesignerMenu")).should(exist).click();
        $(elementLocators("FormProperties")).shouldHave(Condition.text("Form Properties")).click();
        $(elementLocators("DeleteLang2")).click(); //Delete the selected language
        $(elementLocators("ConfirmDelete")).click();
        $(elementLocators("PublishButton")).click(); //Click on Publish
        $(elementLocators("ConfirmPublish")).click();
        $(elementLocators("ConfirmationMessage")).should(appear).shouldHave(Condition.text("The form was published successfully"));
        $(elementLocators("LeftFormDashboardHeader")).should(exist);

    }
}
