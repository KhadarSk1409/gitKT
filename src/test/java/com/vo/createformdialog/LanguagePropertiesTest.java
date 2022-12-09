package com.vo.createformdialog;

import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.SelenideElement;
import com.vo.BaseTest;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.*;

import java.util.List;

import static com.codeborne.selenide.ClickOptions.usingDefaultMethod;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static reusables.ReuseActions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Language Properties")
public class LanguagePropertiesTest extends BaseTest {

    //Fetch languages from 'preferred form creation locales'
    //Pre-requisite: User has selected English and German as Preferred languages in Preferences and Default as English
    @Test
    @DisplayName("Verify initial setup in Add Language screen")
    @Order(1)
    public void validateInitialSet() {
        //Create Form:
        Pair<String, String> formTitleDesc = createNewForm();
        String formTitle = formTitleDesc.getLeft();
        String formDesc = formTitleDesc.getRight();

        $(elementLocators("AdditionalOptionsButton")).shouldBe(enabled).click(); //Click on Additional Options
        SelenideElement lC = $(elementLocators("LanguagePropertiesContainer")).should(appear);
        lC.$(elementLocators("AddLanguageButton")).should(exist); //+ button in Add Language - confirmation that user has navigated

        //the buttons “BACK”, “CREATE FORM”, “NEXT” and “CANCEL” should all be enabled:
        $(elementLocators("BackButton")).shouldBe(enabled);
        $(elementLocators("CreateFormButton")).shouldBe(enabled);
        $(elementLocators("CancelButton")).shouldBe(enabled);
        $(elementLocators("NextButton")).shouldBe(enabled); //Next button

        //Define the table rows
        SelenideElement firstRow = lC.$(elementLocators("FirstRow")).should(exist);
        firstRow.$(elementLocators("LanguageField")).shouldHave(text("English - Great Britain")); // Row for English - Great Britain
        firstRow.$(elementLocators("TitleField")).shouldHave(text(formTitle)); //Form Title should have the same text as Title in Create Form
        firstRow.$(elementLocators("DescriptionField")).shouldHave(text(formDesc)); //Form Description text should be same as Description in Create form
        firstRow.$(elementLocators("DefaultField")).shouldHave(attribute("data-testid","CheckIcon")); //Default button is checked for first row - English - German

        SelenideElement secondRow = lC.$(elementLocators("SecondRow"));
        secondRow.$(elementLocators("LanguageField")).shouldHave(text("German - Germany")); //Row for German - Germany
        secondRow.$(elementLocators("TitleField")).shouldHave(text(formTitle)); //Form Title should have the same text as Title in Create Form
        secondRow.$(elementLocators("DescriptionField")).shouldHave(text(formDesc)); //Form Description text should be same as Description in Create form
        secondRow.$(elementLocators("DefaultField")).shouldHave(attribute("data-testid","CloseIcon")); //Default button is unchecked for secondary language
    }

    @Test
    @DisplayName("Verify Delete Language in Additional Language screen")
    @Order(2)
    public void verifyDeleteLanguage() {
        SelenideElement lC = $(elementLocators("LanguagePropertiesContainer")).shouldBe(visible);
        SelenideElement secondRow = lC.$(elementLocators("SecondRow")); //Row for German - German

        secondRow.$(elementLocators("EditButton")).should(exist).click(); //Click on edit button for German - German
        secondRow.$(elementLocators("SaveIcon")).should(appear);
        $(byText("Language properties")).should(exist).hover();
        secondRow.$(elementLocators("CancelIcon")).should(exist).click(); //Cancel the Edit
        secondRow.$(elementLocators("SaveIcon")).should(disappear);
        secondRow.should(appear);
        secondRow.$(elementLocators("EditButton")).should(appear); //User is back on the previous page
        secondRow.$(elementLocators("ButtonDelete")).should(appear).click(); //Delete button for German - German
        secondRow.should(disappear);
        lC.$$(elementLocators("TableRows")).shouldHave(size(1));
    }

    @Test
    @DisplayName("Verify Add Language in Additional Language screen")
    @Order(3)
    public void verifyAddLanguage() {
        SelenideElement lC = $(elementLocators("LanguagePropertiesContainer")).shouldBe(visible);
        lC.$(elementLocators("AddLanguageButton")).shouldBe(visible, enabled).click(); //Click on + button

        //Define the table rows
        SelenideElement secondRow = lC.$(elementLocators("SecondRow")).should(appear);
        SelenideElement firstRow = lC.$(elementLocators("FirstRow")).should(exist);

        secondRow.$(byAttribute("data-field", "title")).should(exist); //Column with Form Title should be enabled
        secondRow.$(byAttribute("data-field", "description")).should(exist); //Column with Form Title should be enabled
        secondRow.$(elementLocators("DefaultField"))
                .shouldHave(attribute("data-testid","CloseIcon")); //Added language should not be marked as Default
        secondRow.$(elementLocators("EditButton")).should(exist).click();
        secondRow.$(elementLocators("SaveIcon")).should(appear);
        secondRow.$(elementLocators("EmptyCheckBox")).click(); //checkbox should be checked to set German - Germany as Default
        secondRow.$(elementLocators("SaveIcon")).should(exist).click(usingDefaultMethod()).should(disappear);
        secondRow.$(elementLocators("DefaultField"))
                .shouldHave(attribute("data-testid","CheckIcon")); //Now German language in second row should be marked as default
        firstRow.$(elementLocators("DefaultField"))
                .shouldHave(attribute("data-testid","CloseIcon")); // And English in the first row should have 'X' icon

    }
}
