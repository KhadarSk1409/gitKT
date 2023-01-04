package com.vo.checklist;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.vo.BaseTest;
import org.junit.jupiter.api.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Set;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static reusables.ReuseActions.elementLocators;
import static reusables.ReuseActionsChecklistCreation.createNewChecklist;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Verify new form edit actions in checklist creation")
public class VerifyFormEditActionsInChecklistCreationTest extends BaseTest {

    @Test
    @DisplayName("Verify Create checklist with new form and verify edit actions")
    @Order(1)
    public void createAndVerifyChecklistWithNewForm() {

        createNewChecklist();
        $(elementLocators("PreviewChecklistButton")).should(appear);
        $(elementLocators("PublishChecklistTemplateButton")).should(exist);

        //Move the Form Element from components to checklist flow and create a new form
        $(elementLocators("SourceFormElement")).should(exist).doubleClick();

        $(byText(elementLocators("SelectAForm"))).should(appear); // Form selection wizard will appear
        $(byText(elementLocators("CreateANewForm"))).click(); //Click on Create a New Form
        $(elementLocators("FormCreationWizard")).should(appear); //New form creation wizard will appear
        $(elementLocators("FormTitleInputField")).should(exist).setValue("DemoForm"); //Enter Form Title
        $(elementLocators("DescriptionInputField")).setValue("This is a sample test form"); //Enter Form Description
        $(elementLocators("CreateNewFormButton")).should(exist).shouldBe(enabled).click(); //Click on Create Form
        $(elementLocators("TargetListInChecklistFlow")).shouldHave(text("DemoForm")); //Verify whether the created form is available in the Checklist flow or not

        //Drag and Drop LABEL field to Checklist flow
        $(elementLocators("SourceLabelElement")).should(exist).doubleClick();

        //Verify newly created form is available in checklist flow or not
        $(elementLocators("TargetListInChecklistFlow")).shouldHave(text("DemoForm")).$(elementLocators("EditFormButton")).click();

        Set<String> handles = getWebDriver().getWindowHandles();
        System.out.println("No of windows in active are " + handles.size());

        //Switch to form designer window and do necessary actions
        switchTo().window(1);
        $(elementLocators("LeftFormDashboardHeader")).should(appear).shouldHave(Condition.text("DemoForm"));
        $(elementLocators("PublishButton")).should(exist);
        $(elementLocators("BlockR1C1")).should(appear).click();
        $(elementLocators("TemplateList")).should(exist);
        $(elementLocators("TextField")).should(appear).click();
        $(elementLocators("BlockR1C1PenIcon")).should(exist).click();
        $(elementLocators("TextFieldLabel")).setValue(" Added");
        $(elementLocators("PublishButton")).should(exist).click();
        $(elementLocators("ConfirmPublish")).should(exist).click(); //Click on Confirm button
        $(elementLocators("ConfirmationMessage")).should(appear).shouldHave(Condition.text("The form was published successfully"));

        //Switch back to Checklist window to verify whether the form state is changed to published or not
        switchTo().window(0);
        $(elementLocators("ConfirmationMessage")).should(appear).shouldHave(Condition.text("checklist template constituents was updated outside of this editor. Checklist template was reloaded"), Duration.ofSeconds(5));
        $(elementLocators("TargetListInChecklistFlow")).shouldHave(text("DemoForm")).$(elementLocators("PreviewItemButton")).should(exist).click();
        $(elementLocators("DataContainer")).should(appear);
        $(elementLocators("AddedBlock2")).should(exist);
        $(elementLocators("CloseFillFormButton")).should(exist).click();

        //Switch to form designer window and do necessary actions
        switchTo().window(1);
        $(elementLocators("LeftFormDashboardHeader")).should(appear).shouldHave(Condition.text("DemoForm"));
        $(elementLocators("DashboardDataOptions")).should(exist).click();
        $(byText("Edit Form Design")).should(appear).click();
        $(elementLocators("BlockR2C2")).should(appear).click();
        $(elementLocators("TemplateList")).should(exist);
        $(elementLocators("TextField")).should(appear).click();
        $(elementLocators("BlockR2C2PenIcon")).should(exist).click();
        $(elementLocators("TextFieldLabel")).setValue(" 2 Added");
        $(elementLocators("PublishButton")).should(exist).click();
        $(elementLocators("ConfirmPublish")).should(exist).click(); //Click on Confirm button
        $(elementLocators("ConfirmationMessage")).should(appear).shouldHave(Condition.text("The form was published successfully"));
        closeWindow();

        //Switch back to Checklist window to verify whether the second field added is reflecting or not in the designer form
        switchTo().window(0);
        $(elementLocators("ConfirmationMessage")).should(appear).shouldHave(Condition.text("checklist template constituents was updated outside of this editor. Checklist template was reloaded"), Duration.ofSeconds(5));
        $(elementLocators("TargetListInChecklistFlow")).shouldHave(text("DemoForm")).shouldNotHave(text("not published"));
        $(elementLocators("TargetListInChecklistFlow")).shouldHave(text("DemoForm")).$(elementLocators("PreviewItemButton")).should(exist).click();
        $(elementLocators("DataContainer")).should(appear);
        $(elementLocators("AddedBlock")).should(exist);
        $(elementLocators("AddedBlock2")).should(exist);
        $(elementLocators("CloseFillFormButton")).should(exist).click();

        //Verify the edit actions by selecting a form from the list available
        $(elementLocators("SourceFormElement")).should(exist).doubleClick();

        $(byText(elementLocators("SelectAForm"))).should(appear);
        $(byText(elementLocators("ChooseFromLibrary"))).click();
        $(elementLocators("FormsGridContainer")).should(appear); //Forms available in Library will appear
        $(elementLocators("FormsAvailableInTable")).should(exist).getSize();
        String selectedForm2 = $(elementLocators("SecondFormNameInTheTable")).getText();
        System.out.println(selectedForm2);
        String form2DataID = $(elementLocators("SecondFormAvailableInTable")).should(exist).getAttribute("data-id");
        $(elementLocators("SecondFormAvailableInTable")).click(); //Select the Second form available in the list
        $(elementLocators("TargetListInChecklistFlow")).shouldHave(text(selectedForm2)); //Verify whether the selected form is available in the Checklist flow or not
        assert form2DataID != null;
        SelenideElement form2 = $(elementLocators("TargetListInChecklistFlow")).find(byAttribute("id", form2DataID)).should(exist);

        $(form2).$(elementLocators("EditFormButton")).click();

        //Switch to form designer window and do necessary actions
        switchTo().window(1);
        $(elementLocators("LeftFormDashboardHeader")).should(appear).shouldHave(Condition.text(selectedForm2));
        $(elementLocators("PublishButton")).should(exist);
        $(elementLocators("BlockR1C1")).should(appear).click();
        $(elementLocators("TemplateList")).should(exist);
        $(elementLocators("TextField")).should(appear).click();
        $(elementLocators("BlockR1C1PenIcon")).should(exist).click();
        $(elementLocators("TextFieldLabel")).setValue(" Added");
        $(elementLocators("PublishButton")).should(exist).click();
        $(elementLocators("ConfirmPublish")).should(exist).click(); //Click on Confirm button
        $(elementLocators("ConfirmationMessage")).should(appear).shouldHave(Condition.text("The form was published successfully"));
        closeWindow();

        //Switch back to Checklist window to verify whether the form state is changed to published or not
        switchTo().window(0);
        $(elementLocators("ConfirmationMessage")).should(appear).shouldHave(Condition.text("checklist template constituents was updated outside of this editor. Checklist template was reloaded"), Duration.ofSeconds(5));
        $(elementLocators("TargetListInChecklistFlow")).shouldHave(text(selectedForm2)).shouldNotHave(text("not published"));
        $(elementLocators("TargetListInChecklistFlow")).shouldHave(text(selectedForm2)).$(elementLocators("PreviewItemButton")).should(exist).click();
        $(elementLocators("DataContainer")).should(appear);
        $(elementLocators("AddedBlock")).should(exist);
        $(elementLocators("CloseFillFormButton")).should(exist).click();

    }
}
