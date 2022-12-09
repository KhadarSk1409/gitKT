package com.vo.formVersionComparison;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.vo.BaseTest;
import org.junit.jupiter.api.*;

import java.time.Duration;

import static com.codeborne.selenide.CollectionCondition.itemWithText;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.$$;
import static reusables.ReuseActions.elementLocators;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Verify the form versions")
public class FormComparisonExtensionTest extends BaseTest {

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

        SelenideElement firstBlock = $(elementLocators("SourceBlockR1C2"));
        SelenideElement secondBlock = $(elementLocators("TargetBlockR3C2"));
        SelenideElement extendedSourceBlock = $(elementLocators("SourceBlockR1C2"));
        SelenideElement secondTargetBlock = $(elementLocators("TargetBlockR3C2"));

        $(elementLocators("FormComponents")).should(exist);
        $(elementLocators("FormComparisonGrid")).should(exist);
        $(elementLocators("SourceFormInputField")).shouldHave(value("Compare Test Forms 9.0"));

        //Verifying version 5.0
        $(elementLocators("SourceFormPopupOpener")).should(exist).click();
        $(elementLocators("Popover")).should(appear);
        $$(elementLocators("ListOfOptions")).shouldHave(itemWithText("Compare Test Forms 5.0"));
        $$(elementLocators("ListOfOptions")).findBy(text("Compare Test Forms 5.0")).click();
        $(elementLocators("CompareButton")).should(exist).click();
        $(elementLocators("SourceBlockR1C1")).should(exist).shouldHave(Condition.text("First Text Field")).should(exist);
        $(firstBlock).should(exist)
                .$(byAttribute("aria-label", "Moved")).should(appear); //Verify the text field is marked as Moved
        int oneWidhtBlock = $(firstBlock).getRect().getWidth();

        $(secondBlock).should(exist)
                .$(byAttribute("aria-label", "Moved")).should(appear); //Verify the text field is marked as Moved
        int extendedBlockWidth = $(secondBlock).getRect().getWidth();

        Assertions.assertEquals(extendedBlockWidth/oneWidhtBlock, 3, "second block should have span of 2");

        $(elementLocators("TargetBlockR3C2MovedButton")).should(exist).click();
        $(elementLocators("PopoverWindow")).should(appear).shouldHave(Condition.text("MOVED"));
        $(elementLocators("PopoverWindow")).should(exist).click();
        $(elementLocators("MovedCheckBox")).should(exist).click(); //Click on Moved
        $(elementLocators("TargetBlockR3C2")).should(exist)
                .$(byAttribute("aria-label", "Moved")).shouldNot(appear); //Verify the text field is marked Moved is visible or not
        $(elementLocators("MovedCheckBox")).should(exist).click(); //Click on Moved

        //Verifying Version 6.0
        $(elementLocators("SourceFormInputField")).shouldHave(value("Compare Test Forms 5.0"));
        $(elementLocators("SourceFormPopupOpener")).should(exist).click();
        $(elementLocators("Popover")).should(appear);
        $$(elementLocators("ListOfOptions")).shouldHave(itemWithText("Compare Test Forms 6.0"), Duration.ofSeconds(10));
        $$(elementLocators("ListOfOptions")).findBy(text("Compare Test Forms 6.0")).click();
        $(elementLocators("CompareButton")).should(exist).click();
        $(extendedSourceBlock).should(exist)
                .$(byAttribute("aria-label", "Moved")).should(appear); //Verify the text field is marked Moved is visible or not
        int extendedSourceBlockWidth = $(extendedSourceBlock).getRect().getWidth();

        $(secondTargetBlock).should(exist)
                .$(byAttribute("aria-label", "Moved")).should(appear); //Verify the text field is marked as Moved
        int extendedTargetBlockWidth = $(secondTargetBlock).getRect().getWidth();

        Assertions.assertEquals(extendedTargetBlockWidth/extendedSourceBlockWidth, 1, "Both the blocks should have same span");

        //Verifying Version 7.0
        $(elementLocators("SourceFormInputField")).shouldHave(value("Compare Test Forms 6.0"));
        $(elementLocators("SourceFormPopupOpener")).should(exist).click();
        $(elementLocators("Popover")).should(appear);
        $$(elementLocators("ListOfOptions")).shouldHave(itemWithText("Compare Test Forms 7.0"), Duration.ofSeconds(10));
        $$(elementLocators("ListOfOptions")).findBy(text("Compare Test Forms 7.0")).click();
        $(elementLocators("CompareButton")).should(exist).click();

        //Verifying all the labels are visible or not in source and target Second Text Field Block
        // In Source Form Section
        $(elementLocators("SourceBlockR3C2")).should(exist).shouldHave(Condition.text("Second Text Field")).should(exist)
                .$(byAttribute("aria-label", "Moved")).shouldNot(appear);
        $(elementLocators("SourceBlockR3C2")).should(exist).shouldHave(Condition.text("Second Text Field")).should(exist)
                .$(byAttribute("aria-label", "Edited")).shouldNot(appear);
        $(elementLocators("SourceBlockR3C2")).should(exist).shouldHave(Condition.text("Second Text Field")).should(exist)
                .$(byAttribute("aria-label", "Removed")).shouldNot(appear);
        $(elementLocators("SourceBlockR3C2")).should(exist).shouldHave(Condition.text("Second Text Field")).should(exist)
                .$(byAttribute("aria-label", "New")).shouldNot(appear);
        $(elementLocators("SourceBlockR3C2")).should(exist).shouldHave(Condition.text("Second Text Field")).should(exist)
                .$(byAttribute("aria-label", "Values")).shouldNot(appear);

        // In Target Form Section
        $(elementLocators("TargetBlockR3C2")).should(exist).shouldHave(Condition.text("Second Text Field")).should(exist)
                .$(byAttribute("aria-label", "Moved")).shouldNot(appear);;
        $(elementLocators("TargetBlockR3C2")).should(exist).shouldHave(Condition.text("Second Text Field")).should(exist)
                .$(byAttribute("aria-label", "Edited")).shouldNot(appear);;
        $(elementLocators("TargetBlockR3C2")).should(exist).shouldHave(Condition.text("Second Text Field")).should(exist)
                .$(byAttribute("aria-label", "Removed")).shouldNot(appear);;
        $(elementLocators("TargetBlockR3C2")).should(exist).shouldHave(Condition.text("Second Text Field")).should(exist)
                .$(byAttribute("aria-label", "New")).shouldNot(appear);
        $(elementLocators("TargetBlockR3C2")).should(exist).shouldHave(Condition.text("Second Text Field")).should(exist)
                .$(byAttribute("aria-label", "Values")).shouldNot(appear);

    }
}
