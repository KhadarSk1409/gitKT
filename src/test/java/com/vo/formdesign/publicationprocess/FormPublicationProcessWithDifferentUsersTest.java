package com.vo.formdesign.publicationprocess;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.vo.BaseTest;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.Duration;

import static com.codeborne.selenide.CollectionCondition.itemWithText;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static reusables.ReuseActions.createNewForm;
import static reusables.ReuseActions.elementLocators;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Verify the form publication process with multiple users")
public class FormPublicationProcessWithDifferentUsersTest extends BaseTest {

    @Test
    @DisplayName("Verify Create a form and publish with multiple users")
    public void formPublicationProcessWithDifferentUsers() {
        shouldLogin(UserType.USER_01);
        //Create a New Form
        Pair<String, String> formName=createNewForm();
        String actualFormName= formName.getKey();
        $(elementLocators("CreateFormButton")).should(exist).shouldBe(enabled).click(); //Click on Create Form
        $(elementLocators("LeftFormDashboardHeader")).should(appear);
        $(elementLocators("BlockR1C1")).should(exist).click(); //Click on + to add a field
        $(elementLocators("TemplateCard")).should(appear).$(elementLocators("TextField")).click(); //Add one field
        $(elementLocators("ElementProperties")).should(exist);
        $(elementLocators("DesignerMenu")).should(exist).click();
        $(elementLocators("ConfigPublication")).should(exist).click(); //Should click on Configure publication process
        $(elementLocators("EnablePublicationProcessCheckBox")).should(exist).click();
        $(elementLocators("nextButton")).should(exist).click();
        $(elementLocators("PublicationWithOneApproval")).shouldBe(checked); //Publication with one approval should be checked
        $(elementLocators("nextButton")).should(exist).click(); //Click on Next
        $(elementLocators("FreeUserSelection")).should(exist).click(); //Click on Free User Selection
        $(elementLocators("UserSelectionInput")).should(exist).click(); //Click on SelUser to select the user

        //Select GUI Tester 02 to Approve and Publish
        $(elementLocators("Popover")).should(appear);
        $$(elementLocators("ListOfOptions")).findBy(text("GUI Tester 02")).click(); //Click on the selected user
        $(elementLocators("EndUserCanOverwrite")).should(exist).shouldBe(enabled).click();
        $(elementLocators("nextButton")).should(exist).shouldBe(enabled).click(); //Click on Next
        String initialVerNumStr = $(elementLocators("InitialVersion")).should(exist).getText(); //Fetch version before publishing
        $(elementLocators("ButtonSave")).should(exist).shouldBe(enabled).click(); //Click on Save
        $(elementLocators("InitialVersion")).shouldNotHave(text(initialVerNumStr)); //Verify that version previous version is not present
        $(elementLocators("PublishButton")).should(exist).click();
        $(elementLocators("ConfirmPublish")).should(exist).shouldBe(enabled).click();
        $(elementLocators("ConfirmationMessage")).should(appear).shouldHave(Condition.text("The form requires approval before publishing. It will be published once approved"));

        shouldLogin(UserType.USER_02); //Should login as GUI Tester 02
        $(elementLocators("TasksCardInDashboard")).should(exist);
        $(elementLocators("TasksCardInDashboard")).find(byAttribute("data-form-name", actualFormName )).should(exist)
                .$(elementLocators("QuickApprove")).should(exist).click(); //Click on quick approve
        $(elementLocators("TasksCardInDashboard")).find(byAttribute("data-form-name", actualFormName )).should(disappear,Duration.ofSeconds(15));
        $(elementLocators("ConfirmationMessage")).should(appear).shouldHave(Condition.text("New form version was successfully published."));

        //Verify the form approved by GUI Tester 01 is Published or not
        shouldLogin(UserType.USER_01); //Should login as GUI Tester 01
        $(elementLocators("NavigateToLibrary")).should(exist).hover().click(); //Hover and click on Library
        $(elementLocators("DataCapture")).should(exist).hover();
        SelenideElement formsListTable = $(elementLocators("FormsList")).shouldBe(visible);
        ElementsCollection formRows = formsListTable.$$(elementLocators("FormsAvailableInTable"));
        System.out.println(" Form Count is " + formRows.size());

        if (formRows.size() == 0) {
            System.out.println("No Forms available");
            return;
        }
        formRows.forEach(rowEl -> {
            String finalFormName = rowEl.$(elementLocators("FinalFormName")).getText();
            if (finalFormName.equals(actualFormName)) {
                rowEl.$(elementLocators("FormsStateInTable")).shouldHave(Condition.text("Published"));
            }
        });
    }
}
