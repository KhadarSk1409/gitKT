package com.vo.checklist;

import com.codeborne.selenide.SelenideElement;
import com.vo.BaseTest;
import org.junit.jupiter.api.*;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static reusables.ReuseActions.elementLocators;
import static reusables.ReuseActionsChecklistCreation.createNewChecklist;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Verify Even Trigger Functionality")
public class VerifyEventTriggerFunctionalityTest extends BaseTest {

    @Test
    @DisplayName("Verify Drag and Drop Event Trigger and selected Integrations are appearing or not ")
    @Order(1)
    public void verifyEventTriggerAndIntegrationSelection(){
        createNewChecklist(); //Creates new checklist and Checklist designer will open
        $(elementLocators("PreviewChecklistButton")).should(appear);
        $(elementLocators("PublishChecklistTemplateButton")).should(exist);

        SelenideElement sourceEventTrigger = $(elementLocators("SourceEventTrigger"));
        SelenideElement targetChecklistFlow = $(elementLocators("TargetChecklistFlow"));

        //Get the initial locations of Source and Target elements
        int sourceETXOffset = sourceEventTrigger.getLocation().getX();
        int sourceETYOffset = sourceEventTrigger.getLocation().getY();
        int targetXOffset = targetChecklistFlow.getLocation().getX();
        int targetYOffset = targetChecklistFlow.getLocation().getY();

        //Drag and Drop Form Element to checklist flow
        $(elementLocators("SourceFormElement")).should(exist).doubleClick();
        $(elementLocators("FormCreationWizard")).should(appear);
        $(byText(elementLocators("ChooseFromLibrary"))).click();
        $(elementLocators("FormsGridContainer")).should(appear); //Forms available in Library will appear
        $(elementLocators("FormsAvailableInTable")).should(exist).getSize();
        String selectedFormName = $(elementLocators("FirstFormNameInTheTable")).getText();
        System.out.println(selectedFormName);
        $(elementLocators("FirstFormAvailableInTable")).click(); //Select the first form available in the list
        $(elementLocators("TargetListInChecklistFlow")).shouldHave(text(selectedFormName)); //Verify whether the selected form is available in the Checklist flow or not

        //Drag and Drop Label element to checklist flow
        $(elementLocators("SourceLabelElement")).should(exist).doubleClick();

        //Drag n Drop Event Trigger to checklist flow
        actions().clickAndHold(sourceEventTrigger).moveToElement(targetChecklistFlow).build().perform();
        int ETXOffset = (targetXOffset-sourceETXOffset)+50;
        int ETYOffset=  (sourceETYOffset-targetYOffset)-150;
        actions().moveByOffset(ETXOffset,ETYOffset).release().build().perform();
        $(elementLocators("EventTriggerEditor")).should(appear);
        SelenideElement Integration1 = $(elementLocators("IntegrationInputField")).should(exist).shouldHave(text("VAT Number Verification"));
        String SelectedIntegration = Integration1.getText();
        $(elementLocators("OkButton")).should(exist).click();
        $(elementLocators("TargetListInChecklistFlow")).shouldHave(text(SelectedIntegration)); //Verify whether the selected form is available in the Checklist flow or not

        //Drag and Drop another Event Trigger to checklist flow
        actions().clickAndHold(sourceEventTrigger).moveToElement(targetChecklistFlow).build().perform();
        int ETXOffset2 = (targetXOffset-sourceETXOffset)+60;
        int ETYOffset2=  (sourceETYOffset-targetYOffset)-135;
        actions().moveByOffset(ETXOffset2,ETYOffset2).release().build().perform();
        $(elementLocators("EventTriggerEditor")).should(appear);
        $(elementLocators("IntegrationInputField")).should(exist).click();
        $(elementLocators("SecondIntegrationInTheList")).should(appear).click();
        SelenideElement Integration2 = $(elementLocators("IntegrationInputField")).shouldHave(text("IBAN Number Verification"));
        String SelectedIntegration2 = Integration2.getText();
        $(elementLocators("OkButton")).should(exist).click();
        $(elementLocators("TargetListInChecklistFlow")).shouldHave(text(SelectedIntegration2)); //Verify whether the selected form is available in the Checklist flow or not

        //Drag and Drop Event Trigger to Post Processing flow
        $(elementLocators("SourceEventTrigger")).doubleClick();
        $(elementLocators("EventTriggerEditor")).should(appear);
        $(elementLocators("IntegrationInputField")).should(exist).click();
        $(elementLocators("ThirdIntegrationInTheList")).should(appear).click();
        SelenideElement Integration3 = $(elementLocators("IntegrationInputField")).shouldHave(text("SAP Purchase Order Lookup Example"));
        String SelectedIntegration3 = Integration3.getText();
        $(elementLocators("OkButton")).should(exist).click();
        $(elementLocators("TargetPostProcessing")).shouldHave(text(SelectedIntegration3)); //Verify whether the selected form is available in the Checklist flow or not

        //Drag and Drop PDF Generator and upload a file
        $(elementLocators("SourcePDFGenerator")).doubleClick();
        $(byText(elementLocators("UploadPDFTemplateInUSLetterOrA4PageFormat"))).should(appear);
        $(elementLocators("UploadPdfInput")).uploadFromClasspath("samplePDF.pdf");
        $(elementLocators("AttachFileIcon")).should(appear);
        $(elementLocators("FileUploadedMessage")).should(appear);
        $(elementLocators("SubmitButton")).click(); //Click on Submit Button
        $(elementLocators("PDFEditor")).should(appear);
        $(elementLocators("OkButton")).should(exist).click();
        $(elementLocators("TargetPostProcessing")).shouldHave(text("samplePDF.pdf")).$(elementLocators("PenIconToEditPDF")).click();
        $(elementLocators("ItemContainer")).shouldHave(text("EVENT DATA"));
        $(elementLocators("SecondListItem")).should(exist).click();
    }
}
