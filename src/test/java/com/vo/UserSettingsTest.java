package com.vo;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.*;
import static reusables.ReuseActions.elementLocators;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("User Settings Tests")
public class UserSettingsTest extends BaseTest {

    @Test
    @Order(1)
    @DisplayName("Appearance should change to Dark Theme")
    public void appearanceShouldChangeToDarkTheme() {
        $(elementLocators("UserSettingsPopover")).click(); //Click on User Settings Popover$("#myPreferences").should(appear).click(); //Click on preferences
        $(elementLocators("MyPreferences")).should(exist).click(); //Click on My Preferences
        $(elementLocators("Appearance")).should(exist).click(); //Click on Appearance
        SelenideElement body = $("body");

        if ($(elementLocators("DarkThemeSwitch")).is(checked)) //If Dark Theme is checked
        {
            System.out.println("Dark theme is checked");//Verify the background colour
            String backGroundColorBefore = body.getCssValue("background-color");
            System.out.println("When Dark Theme is checked, the backGroundColorBefore is: " + backGroundColorBefore);

            $(elementLocators("DarkTheme")).shouldBe(enabled);  //Verify that Dark Theme should be checked
            $(elementLocators("DarkTheme")).click(); //Now change the theme value from theme check box in appearance
            $(elementLocators("DarkTheme")).shouldNotBe(checked);  //Verify that Dark Theme should be checked
            $(elementLocators("DarkThemeSwitch")).shouldNotBe(checked);

            String backGroundColorAfter = body.getCssValue("background-color");
            System.out.println("When Dark Theme is checked,the backGroundColorAfter is: " + backGroundColorAfter);

            assertTrue(!(backGroundColorBefore.equalsIgnoreCase(backGroundColorAfter))); //Since the color change from Dark to White does not save, this assertion doesn't work

            //Now change the theme from GUI tester menu:
            $(elementLocators("DarkThemeSwitch")).click();
            $(elementLocators("DarkTheme")).shouldBe(enabled);
            $(elementLocators("ButtonSave")).click(); //Click on Save button

        } else if (!($(elementLocators("DarkThemeSwitch")).is(checked))) {
            System.out.println("Light theme is checked");

            //Verify the background colour
            String backGroundColorBefore = body.getCssValue("background-color");
            System.out.println("When Light Theme is checked, the backGroundColorBefore is: " + backGroundColorBefore);

            $(elementLocators("DarkTheme")).shouldNotBe(checked);  //Verify that Dark Theme should be checked
            $(elementLocators("DarkTheme")).click(); //Now change the theme value from theme check box in appearance
            $(elementLocators("DarkTheme")).shouldBe(visible, Duration.ofSeconds(5));  //Verify that Dark Theme should be checked
            $(elementLocators("DarkTheme")).is(checked);
            $(elementLocators("DarkThemeSwitch")).shouldBe(checked);

            //Issue: Light to Dark theme works but Dark to Light does not reflects unless page refreshed
            String backGroundColorAfter = body.getCssValue("background-color");
            System.out.println("When Light Theme is checked, the backGroundColorAfter is: " + backGroundColorBefore);

            assertTrue(!(backGroundColorBefore.equalsIgnoreCase(backGroundColorAfter))); //-Since the color change from Dark to White does not save, this assertion doesn't work

            //Now change the theme from GUI tester menu:
            $(elementLocators("DarkThemeSwitch")).click();
            $(elementLocators("DarkTheme")).shouldNotBe(checked);
            $(elementLocators("ButtonSave")).click(); //Click on Save button

        }

    }

    @Test
    @Order(2)
    @DisplayName("Should change users default language")
    public void shouldChangeUsersDefaultLanguage() {

        $(elementLocators("AccountSettings")).shouldBe(visible).click(); //Account Settings

        String existingAppLanguage = $(elementLocators("DefaultLocale")).should(exist).getText(); //Check the existing value of Language selected

        if (existingAppLanguage.contains("English")) {
            $(elementLocators("DefaultLocale")).click();
            $(elementLocators("LanguageMenu")).should(appear);
            $$(elementLocators("LanguageList")).shouldHave(texts("German - Germany", "English - Great Britain"));
            $$(elementLocators("LanguageList")).findBy(text("German - Germany")).click();
            $(elementLocators("ButtonSave")).shouldBe(visible).click(); //Save the changes
            $(elementLocators("ButtonSave")).shouldHave(text("Speichern")); //Verify that the app language is changed to German

        }
        String newexistingAppLanguage = $(elementLocators("DefaultLocale")).should(exist).getText(); //Check the existing value of Language selected
        if (newexistingAppLanguage.contains("German")) {
            $(elementLocators("DefaultLocale")).click();
            $(elementLocators("LanguageMenu")).should(appear);
            $$(elementLocators("LanguageList")).shouldHave(texts("German - Germany", "English - Great Britain"));
            $$(elementLocators("LanguageList")).findBy(text("English - Great Britain")).click();
            $(elementLocators("ButtonSave")).shouldBe(visible).click(); //Save the changes
            $(elementLocators("ButtonSave")).shouldHave(text("Save")); //Verify that the app language is changed to English
        }

    }

    @Test
    @Order(3)
    @DisplayName("As a User, I should be able to switch my Date & Time Format")
    public void verifySwitchDateTimeFormat() {
        $(elementLocators("Appearance")).should(exist).click(); //Click on Appearance
        $(elementLocators("DateTimeFormat")).should(exist).click(); //Click on Date & Time format

        List<SelenideElement> listDateTimeFormat = $$(elementLocators("DateTimeFormatList")); //Store all the elements in list

        int dateTimtFormDdCount = listDateTimeFormat.size();

        for (int i = 0; i < dateTimtFormDdCount; i++) {
            SelenideElement selenideElement = listDateTimeFormat.get(i); //Fetch i th element from Selenide elements list

            listDateTimeFormat.get(i).click(); //Click on i th element

            String dateTimeFormat = $(elementLocators("DateTimeFormat")).getText();

            String[] dateTimeFormatArr = dateTimeFormat.split(" "); //Create String array for Date & Time format split

            if (!(dateTimeFormatArr.length > 2)) {
                String resultDateTime = $(elementLocators("DataTimeFormatResult")).getAttribute("value"); //Get the date time populated in Result Date & Time
                String[] resultDateTimeArr = resultDateTime.split(" ");

                //Verify that the Date format selected in D&T format field is populated in Result D&T field
                assertTrue((dateTimeFormatArr[0].equals(resultDateTimeArr[0])), "The date in Date & Time Format Dropdown is " + dateTimeFormatArr[0] + " and in Result Date & Time is: " + resultDateTimeArr[0]);
            }
            $(elementLocators("DateTimeFormat")).should(exist).click(); //Click on Date & Time format

        }

    }


}
