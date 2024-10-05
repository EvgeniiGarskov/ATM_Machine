package com.example.atm_mashine;

import com.example.atm_machine.Helper;
import javafx.application.Platform;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelperTest {

    private static Helper helper;
    private TextField textField;

    @BeforeAll
    public static void initJFX() {

        Platform.startup(() -> {});
    }

    @BeforeEach
    public void initEach() {

        helper = Helper.getHelper();
        textField = new TextField();
    }

    @Test
    public void addTextLimiterShouldLimitTextToMaxCharacters() {

        Helper.addTextLimiter(textField, 6);

        textField.setText("123456789");

        assertEquals("123456", textField.getText());

        textField.setText("12345qwe");

        assertEquals("12345", textField.getText());
    }

    @Test
    public void addTextCapitalLetterShouldCapitalizeFirstCharacter() {

        Helper.addTextCapitalLetter(textField, 30);

        textField.setText("ййй");
        assertEquals("Ййй", textField.getText());

        textField.setText("qЙqЙ");
        assertEquals("ЙЙ", textField.getText());
    }

    @Test
    public void addTextCapitalLetterShouldLimitTextToMaxCharacters() {

        Helper.addTextCapitalLetter(textField, 30);

        textField.setText("ТомТомТомТомТомТомТомТомТомТомм");
        assertEquals("ТомТомТомТомТомТомТомТомТомТом", textField.getText());
    }

    @Test
    public void showDateTimeReturnCurrentDateAndTime() {

        String inputFormat = "yyyy-MM-dd HH:mm:ss";

        LocalDateTime today = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(inputFormat);
        String expectedDateTime = dateTimeFormatter.format(today);

        assertEquals(expectedDateTime, helper.showDateTime(inputFormat));
    }
}
