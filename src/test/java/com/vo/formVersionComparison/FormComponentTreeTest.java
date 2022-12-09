package com.vo.formVersionComparison;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.vo.BaseTest;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;

import java.time.Duration;

import static com.codeborne.selenide.CollectionCondition.itemWithText;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.$$;
import static reusables.ReuseActions.elementLocators;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Verify the form versions and form tree components")
public class FormComponentTreeTest extends BaseTest{

    @Test
    @DisplayName("Open the from to be compared")
    @Order(1)
    public void openFormComparisonForm(){

        open("/compareForms/comparision-test/9/0");

    }

    @Test
    @DisplayName("Verify the difference between the form versions and tree components")
    @Order(2)
    public void verifyTheFormVersion(){

        SelenideElement sourceFirstBlock= $(elementLocators("SourceBlockR1C1"));
        SelenideElement targetFirstBlock= $(elementLocators("TargetBlockR1C1"));
        SelenideElement targetSecondBlock= $(elementLocators("TargetBlockR3C2"));
        SelenideElement targetThirdBlock= $(elementLocators("TargetBlockR4C1"));

        $(elementLocators("FormComponents")).should(exist);
        $(elementLocators("FormComparisonGrid")).should(exist);
        $(elementLocators("SourceFormInputField")).shouldHave(value("Compare Test Forms 9.0"));

        //Verify version 1.0 with version 9.0
        $(elementLocators("SourceFormPopupOpener")).should(exist).click();
        $(elementLocators("Popover")).should(appear);
        $$(elementLocators("ListOfOptions")).shouldHave(itemWithText("Compare Test Forms 1.0"));
        $$(elementLocators("ListOfOptions")).findBy(text("Compare Test Forms 1.0")).click();
        $(elementLocators("CompareButton")).should(exist).click();
        $(sourceFirstBlock).should(exist).$(byAttribute("aria-label", "Edited")).should(appear);
        $(targetFirstBlock).should(exist).$(byAttribute("aria-label", "Edited")).should(appear);
        $(targetSecondBlock).should(exist).$(byAttribute("aria-label", "New")).should(appear);
        $(targetThirdBlock).should(exist).$(byAttribute("aria-label","New")).should(appear);

        //Click on Target and Verify Form Component Tree
        $(elementLocators("FormPropertiesCard")).should(exist);
        $(elementLocators("TargetButton")).should(exist).click(); //Click on Target in Form Component tree
        $(elementLocators("FormElementsList")).should(exist);
        $(elementLocators("EditedButton")).should(exist).shouldHave(attribute("aria-label","Edited"));
        $(elementLocators("MovedButton")).should(exist).shouldHave(attribute("aria-label","Moved"));
        $(elementLocators("EditedBtnOnFirstTextField")).should(exist).shouldHave(attribute("aria-label","Edited"));
        $(elementLocators("NewBtnOnSecondTextField")).should(exist).shouldHave(attribute("aria-label","New"));
        $(elementLocators("NewBtnOnSelectField")).should(exist).shouldHave(attribute("aria-label","New"));

    }
}

