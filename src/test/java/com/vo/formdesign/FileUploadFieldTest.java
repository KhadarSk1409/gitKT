package com.vo.formdesign;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.commands.PressEnter;
import com.vo.BaseTest;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static java.lang.Integer.parseInt;
import static reusables.ReuseActions.createNewForm;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("FileUploadField Tests")
public class FileUploadFieldTest extends BaseTest {

    enum FileUploadFieldIds {
        textfield_label,
        checkbox_disableLabel,
        textfield_help,
        checkbox_required,
        numberField_maxFileSize,
        numberField_minCount,
        numberField_maxCount;
    }

    @Test
    @Order(1)
    @DisplayName("precondition")
    public void precondition() {
        createNewForm();
        $("#wizard-createFormButton").should(exist).click();
        $("#btnFormDesignPublish").should(exist); //Verify that user has navigated to form design

        String blockId = "#block-loc_en-GB-r_1-c_1"; //Need to change later as of now _1 is returning two results
        String initialVerNumStr = $("#formMinorversion").should(exist).getText(); //Initial version
        $(blockId).shouldBe(visible).click();
        $("#formMinorversion").shouldNotHave(text(initialVerNumStr)); //Verify that version is increased

        //Click on Show More
        $("#template_basis_list").find(byText("Show More")).should(exist).click();

        $("#li-template-FileUploadField-03").should(appear).click();
        $(blockId).$(".fa-pen").closest("button").shouldBe(visible).click(); //Click on Edit
        $("#formelement_properties_card").should(appear);

        //Verify that default value in Label field is "File Upload Field"
        $(By.id(FileUploadFieldTest.FileUploadFieldIds.textfield_label.name())).shouldHave(text("File Upload Field"));

        $("#panel2a-header").should(exist).click(); //Advanced section dropdown

        //options for text field should exist:
        Arrays.asList(FileUploadFieldTest.FileUploadFieldIds.values()).forEach(fileUploadFieldId -> $(By.id(fileUploadFieldId.name())).shouldBe(visible));

        $("#blockButtonDelete").shouldBe(visible).click();
        $("#li-template-FileUploadField-03").should(disappear);

    }

    @Order(2)
    @DisplayName("createNewFormulaDesignForFileUploadfields")
    @ParameterizedTest
    @CsvFileSource(resources = "/fileupload_field_test_data.csv", numLinesToSkip = 1)
    public void alltextfield(Integer row, Integer col, Integer colSpan,
                             String text_label,
                             String checkbox_disableLabel,
                             String text_help,
                             String checkbox_required,
                             String text_max_file_size,
                             String text_numberField_minCount,
                             String text_numberField_maxCount,
                             String edit_values,
                             String preselection_value


    ) {

        String blockId = "#block-loc_en-GB-r_" + row + "-c_" + col;
        //create new block, if not exist
        if (!$(blockId).exists()) {
            String prevBlockId = "#block-loc_en-GB-r_" + (row - 1) + "-c_" + col;
            $(prevBlockId + " .add-row").shouldBe(visible).click();
        }
        String initialVerNumStr = $("#formMinorversion").should(exist).getText(); //Fetch initial version
        $(blockId).shouldBe(visible).click();
        $("#li-template-FileUploadField-03").should(exist).click();
        $(blockId).$(".fa-pen").closest("button").shouldBe(visible).click(); //Click on Edit
        $("#formMinorversion").shouldNotHave(text(initialVerNumStr)); //Verify that version has increased


        //Label
        if (StringUtils.isNotEmpty(text_label)) {
            String initialVerNumStr1 = $("#formMinorversion").should(exist).getText(); //Fetch initial version
            selectAndClear(By.id(FileUploadFieldTest.FileUploadFieldIds.textfield_label.name()))
                    .setValue(text_label).sendKeys(Keys.TAB);
            $("#formMinorversion").shouldNotHave(text(initialVerNumStr1)); //Verify that version has increased
            $(By.id(FileUploadFieldTest.FileUploadFieldIds.textfield_label.name())).shouldHave(text(text_label)); //The Label
            $(blockId).shouldHave(text(text_label));
        }

        //Hide(disable) Label
        if (StringUtils.isNotEmpty(checkbox_disableLabel)) {
            String initialVerNumStr1 = $("#formMinorversion").should(exist).getText(); //Fetch initial version
            String checkBoxId = "#" + FileUploadFieldTest.FileUploadFieldIds.checkbox_disableLabel.name();
            $(checkBoxId).shouldBe(visible).click();
            $("#formMinorversion").shouldNotHave(text(initialVerNumStr1)); //Verify that version has increased
            $(checkBoxId + " input").shouldBe(selected);
            $(blockId).shouldNotHave(value(checkbox_disableLabel)).waitUntil(appears, 4000);
        }

        //Help
        if (StringUtils.isNotEmpty(text_help)) {
            String initialVerNumStr1 = $("#formMinorversion").should(exist).getText(); //Fetch initial version
            selectAndClear(By.id(FileUploadFieldTest.FileUploadFieldIds.textfield_help.name()))
                    .setValue(text_help).sendKeys(Keys.TAB);
            $("#formMinorversion").shouldNotHave(text(initialVerNumStr1)); //Verify that version has increased
            $(blockId).shouldHave(text(text_help)).waitUntil(appears, 4000);
        }

        //required
        if (StringUtils.isNotEmpty(checkbox_required)) {
            String checkBoxId = "#" + FileUploadFieldTest.FileUploadFieldIds.checkbox_required.name();
            String initialVerNumStr1 = $("#formMinorversion").should(exist).getText(); //Fetch initial version
            $(checkBoxId).shouldBe(visible).click();
            //$(checkBoxId + " input").shouldHave(value("true"));
            $("#formMinorversion").shouldNotHave(text(initialVerNumStr1)); //Verify that version has increased
            $(checkBoxId + " input").shouldBe(selected);
            $(blockId).should(exist).shouldHave(text("*"));
        }

        //Maximum file size
        if(StringUtils.isNotEmpty(text_max_file_size)){
            $(By.id(FileUploadFieldTest.FileUploadFieldIds.numberField_maxFileSize.name())).should(exist);
            selectAndClear(By.id(FileUploadFieldTest.FileUploadFieldIds.numberField_maxFileSize.name()))
                    .setValue(text_max_file_size).sendKeys(Keys.TAB);
            $(By.id(FileUploadFieldTest.FileUploadFieldIds.numberField_maxFileSize.name())).shouldHave(value(text_max_file_size)); //Verify that Max file size has entered value
        }


        //Enter Minimum Value
        if (StringUtils.isNotEmpty(text_numberField_minCount)) {

            $(By.id(FileUploadFieldTest.FileUploadFieldIds.numberField_minCount.name())).should(exist); //Verify that Minimum count field exists

            String initialVerNumStr2 = $("#formMinorversion").should(exist).getText(); //Fetch initial version
            selectAndClear(By.id(FileUploadFieldTest.FileUploadFieldIds.numberField_minCount.name()))
                    .setValue(text_numberField_minCount).sendKeys(Keys.TAB);
            $("#formMinorversion").shouldNotHave(text(initialVerNumStr2)); //Verify that version has increased

            $(By.id(FileUploadFieldTest.FileUploadFieldIds.numberField_minCount.name())).shouldHave(value(text_numberField_minCount));

        }

        //Enter Maximum Value
        if (StringUtils.isNotEmpty(text_numberField_maxCount)) {
            String initialVerNumStr2 = $("#formMinorversion").should(exist).getText(); //Fetch initial version
            selectAndClear(By.id(FileUploadFieldIds.numberField_maxCount.name()))
                    .setValue(text_numberField_maxCount).sendKeys(Keys.TAB);
            $("#formMinorversion").shouldNotHave(text(initialVerNumStr2)); //Verify that version has increased

            $("#numberField_maxCount").shouldHave(value(text_numberField_maxCount)).waitUntil(appears, 4000);

            //Verify that if Max count is less than Min count, relevant errors should be shown:
            if(StringUtils.isNotEmpty(text_numberField_minCount)) {
                int int_text_numberField_minCount = parseInt(text_numberField_minCount);
                int int_text_numberField_maxCount = parseInt(text_numberField_maxCount);
                if(int_text_numberField_minCount > int_text_numberField_maxCount){
                    String errorMaxCount1 = "The maximum value "+text_numberField_maxCount+" is less than minimum value "+text_numberField_minCount;
                    $("#numberField_maxCount-helper-text").should(exist).shouldHave(text(errorMaxCount1));
                }
            }

        }

        //Values
        if (StringUtils.isNotEmpty(edit_values)) {

            String[] values = edit_values.split(",");

            $("#formelement_properties_card .editForm").should(exist).click(); //Click on edit value pen icon
            $("#form-value-list-card-dialog_content").should(exist); //Value List Editor window

            //Deleting the existing rows:
            List<SelenideElement> delBtn = $$("div.ag-pinned-right-cols-container .ag-row .fa-trash-alt");
            int countDelBtn = $$("div.ag-pinned-right-cols-container .ag-row .fa-trash-alt").size();
            for (int n = countDelBtn; n >= 1; n--) {
                String strDeleteBtn = ".ag-row:nth-child(" + n + ") .fa-trash-alt"; //Delete the n th row
                $(strDeleteBtn).click();
                $(strDeleteBtn).waitUntil(disappear, 10000);
            }

            //Add rows in value list editor for the number of labels
            if (!$("div.ag-pinned-right-cols-container .ag-row").exists()) {
                for (int x = 0; x < values.length; x++) {
                    $("#form-value-list-card-dialog_content .fa-plus").should(exist).click();
                }
            }

            List<String> preselected = new ArrayList<>();
            if (StringUtils.isNotEmpty(preselection_value)) {
                preselected = Arrays.asList(preselection_value.split(","));
            }

            for (int i = 1; i <= values.length; i++) {
                //Click on label option
                String labelSelector = "div.ag-body-viewport .ag-center-cols-viewport .ag-row:nth-child(" + i + ") .ag-cell:nth-child(2)";
                $(labelSelector).should(exist).doubleClick();
                String labelValue = values[i - 1];
                $("div.ag-popup input.ag-input-field-input").sendKeys(Keys.BACK_SPACE); //Clear the default value in label field
                $("div.ag-popup input.ag-input-field-input").setValue(labelValue).sendKeys(Keys.ENTER);
                $(labelSelector).shouldHave(text(labelValue));

                if (preselected.contains(labelValue)) {
                    String checkboxSelector = "div.ag-pinned-left-cols-container .ag-row:nth-child(" + i + ") input";
                    $(checkboxSelector).should(exist).click();
                    $(checkboxSelector).shouldBe(checked);
                }
            }

            //Click on close button
            $("#form-value-list-card-dialog_actions #btnClosePropertiesForm").should(exist).click();
        }

    }

}
