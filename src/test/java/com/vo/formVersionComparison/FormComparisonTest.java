package com.vo.formVersionComparison;

import com.codeborne.selenide.Condition;
import com.vo.BaseTest;
import org.junit.jupiter.api.*;

import java.time.Duration;

import static com.codeborne.selenide.CollectionCondition.itemWithText;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.$$;
import static reusables.ReuseActions.elementLocators;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Verify the form versions")
public class FormComparisonTest extends BaseTest {

    @Test
    @DisplayName("Open the Compare Test Forms form")
    @Order(1)
    public void openFromComparisonform(){

        open("/compareForms/comparision-test/9/0");

    }

    @Test
    @DisplayName("Verify the difference between the form versions")
    @Order(2)
    public void verifyTheFormComparison() {

        $(elementLocators("FormComponents")).should(exist);
        $(elementLocators("FormComparisonGrid")).should(exist);
        $(elementLocators("SourceFormInputField")).shouldHave(value("Compare Test Forms 9.0"));
        $(elementLocators("SourceFormPopupOpener")).should(exist).click();
        $(elementLocators("Popover")).should(appear);
        $$(elementLocators("ListOfOptions")).shouldHave(itemWithText("Compare Test Forms 1.0"));
        $$(elementLocators("ListOfOptions")).findBy(text("Compare Test Forms 1.0")).click();
        $(elementLocators("CompareButton")).should(exist).click();
        $(elementLocators("TargetBlockR1C1")).should(exist)
                .$(byAttribute("aria-label", "Edited")).should(appear); //Verify the 1st text field is marked as Edited
        $(elementLocators("TargetBlockR3C2")).should(exist)
                .$(byAttribute("aria-label", "New")).should(appear); //Verify the 2nd text field is marked as Added
        $(elementLocators("TargetBlockR1C1EditedBtn")).should(exist).click();
        $(elementLocators("PopoverWindow")).should(appear).shouldHave(Condition.text("EDITED"));
        $(elementLocators("PopoverWindow")).should(exist).click();
        $(elementLocators("TargetBlockR3C2NewBtn")).should(exist).click();
        $(elementLocators("PopoverWindow")).should(appear).shouldHave(Condition.text("NEW FIELD"));
        $(elementLocators("PopoverWindow")).should(exist).click();
        $(elementLocators("EditedCheckBox")).should(exist).click(); //Click on Edited
        $(elementLocators("TargetBlockR1C1")).should(exist)
                .$(byAttribute("aria-label", "Edited")).shouldNot(appear); //Verify the 1st text field is marked Edited is visible or not
        $(elementLocators("NewCheckBox")).should(exist).click(); //Click on New
        $(elementLocators("TargetBlockR3C2")).should(exist)
                .$(byAttribute("aria-label", "New")).shouldNot(appear); //Verify the 2nd text field is marked as Added is visible or not
        $(elementLocators("EditedCheckBox")).should(exist).click(); //Click on Edited
        $(elementLocators("NewCheckBox")).should(exist).click(); //Click on New

        //Verifying Version 2.0
        $(elementLocators("SourceFormInputField")).shouldHave(value("Compare Test Forms 1.0"));
        $(elementLocators("SourceFormPopupOpener")).should(exist).click();
        $(elementLocators("Popover")).should(appear);
        $$(elementLocators("ListOfOptions")).shouldHave(itemWithText("Compare Test Forms 2.0"), Duration.ofSeconds(10));
        $$(elementLocators("ListOfOptions")).findBy(text("Compare Test Forms 2.0")).click();
        $(elementLocators("CompareButton")).should(exist).click();
        $(elementLocators("TargetBlockR1C1")).should(exist)
                .$(byAttribute("aria-label", "Edited")).should(exist); //Verfiy the 1st text field is marked as Edited

        //Verifying Version 3.0
        $(elementLocators("SourceFormInputField")).shouldHave(value("Compare Test Forms 2.0"));
        $(elementLocators("SourceFormPopupOpener")).should(exist).click();
        $(elementLocators("Popover")).should(appear);
        $$(elementLocators("ListOfOptions")).shouldHave(itemWithText("Compare Test Forms 3.0"), Duration.ofSeconds(10));
        $$(elementLocators("ListOfOptions")).findBy(text("Compare Test Forms 3.0")).click();
        $(elementLocators("CompareButton")).should(exist).click();
        $(elementLocators("SourceBlockR1C3")).should(exist)
                .$(byAttribute("aria-label", "Removed")).should(exist); //Verify the text field is marked as Removed
        $(elementLocators("RemovedCheckBox")).should(exist).click(); //Click on Removed
        $(elementLocators("SourceBlockR1C3")).should(exist)
                .$(byAttribute("aria-label", "Removed")).shouldNot(exist); //Verify the text field is marked as Removed is visible or not
        $(elementLocators("RemovedCheckBox")).should(exist).click(); //Click on Removed

        //Verifying Version 4.0
        $(elementLocators("SourceFormInputField")).shouldHave(value("Compare Test Forms 3.0"));
        $(elementLocators("SourceFormPopupOpener")).should(exist).click();
        $(elementLocators("Popover")).should(appear);
        $$(elementLocators("ListOfOptions")).shouldHave(itemWithText("Compare Test Forms 4.0"), Duration.ofSeconds(10));
        $$(elementLocators("ListOfOptions")).findBy(text("Compare Test Forms 4.0")).click();
        $(elementLocators("CompareButton")).should(exist).click();
        $(elementLocators("TargetBlockR1C1")).should(exist)
                .$(byAttribute("aria-label", "Edited")).should(appear); //Verify the text field is marked as Edited

        //Verifying Version 5.0
        $(elementLocators("SourceFormInputField")).shouldHave(value("Compare Test Forms 4.0"));
        $(elementLocators("SourceFormPopupOpener")).should(exist).click();
        $(elementLocators("Popover")).should(appear);
        $$(elementLocators("ListOfOptions")).shouldHave(itemWithText("Compare Test Forms 5.0"), Duration.ofSeconds(10));
        $$(elementLocators("ListOfOptions")).findBy(text("Compare Test Forms 5.0")).click();
        $(elementLocators("CompareButton")).should(exist).click();
        $(elementLocators("SourceBlockR1C1")).should(exist).shouldHave(Condition.text("First Text Field")).should(exist);
        $(elementLocators("TargetBlockR1C1")).should(exist).shouldHave(Condition.text("First Text Field")).should(exist);
        $(elementLocators("SourceBlockR1C2")).should(exist).shouldHave(Condition.text("Second Text Field")).should(exist);
        $(elementLocators("TargetBlockR3C2")).should(exist).shouldHave(Condition.text("Second Text Field")).should(exist);

    }
}
