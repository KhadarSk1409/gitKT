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

import static com.codeborne.selenide.CollectionCondition.itemWithText;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static reusables.ReuseActions.createNewForm;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Verify Form Publication Process with One Approver")
public class FormPublicationProcessWithOneApproverTest extends BaseTest {

    @Test
    @DisplayName("Create a form and publish with One Approval")
    public void formPublicationWithOneApprover() {

        Pair<String, String> formName=createNewForm();
        String actualFormName= formName.getKey();
        $("#wizard-createFormButton").should(exist).shouldBe(enabled).click(); //Click on Create Form
        $("#formDashboardHeaderLeft").should(appear);
        $("#block-loc_en-GB-r_1-c_1").should(exist).click(); //Click on + to add a field
        $("#template_card").should(appear).$("#li-template-Textfield-04").click(); //Add one field
        $("#formtree_card").should(exist);
        $("#formelement_properties_card").should(exist);
        $("#nav_button").should(exist).click();
        $("#designer_panel_menu ul").$(byText("Configure publication process"))
                .should(exist).click(); //Should click on Configure publication process
        $("#ckbApprovalProcessRequired").should(exist).click();
        $("#btnNext").should(exist).click();
        $("#rb_Basic_Approve_Form_Process").shouldBe(checked); //Publication with one approval should be checked
        $("#btnNext").should(exist).click(); //Click on Next
        $("#ckb_first_tApproverFreeUserSelection").should(exist).click(); //Click on Free User Selection
        $("#fc_first_UserSelect").$("#selUser").should(exist).click(); //Click on SelUser to select the user
        //Select GUI Tester 01 to Approve and Publish
        $(".MuiAutocomplete-popper").should(appear);
        $$(".MuiAutocomplete-popper li").shouldHave(itemWithText("GUI Tester 01guitester01@visualorbit.com"), 5000);
        $$(".MuiAutocomplete-popper li").findBy(text("GUI Tester 01guitester01@visualorbit.com")).click(); //Click on the selected user
        $("#sw_first_UserCanOverwrite").should(exist).click();
        $("#btnNext").should(exist).click(); //Click on Next
        $("#designer_tab_Publications div:nth-child(7)").should(exist).shouldHave(text("Ready and Save"));
        $("#btnSave").should(exist).click(); //Click on Save
        $("#btnFormDesignPublish").should(exist).click();
        $("#form-publish-dialog").$("#btnConfirm").should(exist).shouldBe(enabled).click();
        $("#client-snackbar").should(appear).shouldHave(Condition.text("The form requires approval before publishing. It will be published once approved"));

        //Should login as GUI Tester 01
        shouldLogin(UserType.USER_01);
        SelenideElement table = $("#tasksCard .MuiTableBody-root").shouldBe(visible);
        ElementsCollection rows = table.$$("tr");
        System.out.println(" Tasks Count is " + rows.size());

        if (rows.size() == 0) {
            System.out.println("No Tasks available");
            return;
        }
        rows.forEach(rowEl -> {
            String form = rowEl.$("td:nth-child(3)").getText();

            if (form.equals(actualFormName)) {
                rowEl.$(".fa-check").closest("button").should(exist).shouldBe(enabled).click();
            }
        });
        $(table).shouldNotHave(Condition.attribute(actualFormName));
        $("#client-snackbar").should(appear)
                .shouldHave(Condition.text("New form version was successfully published"));
        $("#toDashboard").should(exist).click();

        //Verify the form approved by GUI Tester 01 is Published or not
        shouldLogin(UserType.MAIN_TEST_USER); //Should login as GUI Tester

        SelenideElement formListTable = $("#formListTable .MuiTableBody-root").shouldBe(visible);

        ElementsCollection formRows = formListTable.$$("tr");
        System.out.println(" Form Count is " + formRows.size());

        if (formRows.size() == 0) {
            System.out.println("No Forms available");
            return;
        }
        formRows.forEach(rowEl -> {
            String finalFormName = rowEl.$("td:nth-child(2)").getText();
            if (finalFormName.equals(actualFormName)) {
                rowEl.$("td:nth-child(3)").shouldHave(Condition.text("Published"));
            }
        });
    }
}