package com.mykhailopavliuk.controller;

import com.github.plushaze.traynotification.animations.Animations;
import com.github.plushaze.traynotification.notification.Notifications;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.mykhailopavliuk.configuration.application.AdminProperties;
import com.mykhailopavliuk.exception.DatabaseOperationException;
import com.mykhailopavliuk.exception.EntityNotFoundException;
import com.mykhailopavliuk.model.Settings;
import com.mykhailopavliuk.model.User;
import com.mykhailopavliuk.service.SettingsService;
import com.mykhailopavliuk.service.UserService;
import com.mykhailopavliuk.util.SceneHandler;
import com.mykhailopavliuk.util.TrayNotificationHandler;
import com.mykhailopavliuk.util.ValidationHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@FxmlView("/view/sign-up.fxml")
public class SignUpController implements Initializable {

    private final UserService userService;

    private final SettingsService settingsService;

    private final AdminProperties adminProperties;

    private final FxWeaver fxWeaver;

    private Settings.DisplayMode currentDisplayMode;

    @FXML
    private Pane formPane;
    @FXML
    private Label signUpLabel;
    @FXML
    private JFXTextField inputEmail;
    @FXML
    private JFXPasswordField inputPassword;
    @FXML
    private JFXPasswordField inputConfirmPassword;
    @FXML
    private Label emailValidationLabel;
    @FXML
    private Label confirmPasswordValidationLabel;
    @FXML
    private Label passwordValidationLabel;
    @FXML
    private JFXButton signUpButton;
    @FXML
    private Pane paneWithLogo;

    @Autowired
    public SignUpController(UserService userService, SettingsService settingsService, AdminProperties adminProperties, FxWeaver fxWeaver) {
        this.userService = userService;
        this.settingsService = settingsService;
        this.adminProperties = adminProperties;
        this.fxWeaver = fxWeaver;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentDisplayMode = settingsService.read().getDisplayMode();
        initializeStyles();

        setVisibilityOfValidationLabels(false);

        inputEmail.textProperty().addListener(event -> {
            emailValidationLabel.setVisible(true);
            if (inputEmail.getText().isEmpty()) {
                emailValidationLabel.setText("Email can't be empty");
            } else if (!inputEmail.getText().matches(ValidationHandler.getEmailRegex().getRegex())) {
                emailValidationLabel.setText(ValidationHandler.getEmailRegex().getMessage());
            } else {
                emailValidationLabel.setVisible(false);
            }
        });

        inputPassword.textProperty().addListener(event -> {
            passwordValidationLabel.setVisible(true);
            if (inputPassword.getText().isEmpty()) {
                passwordValidationLabel.setText("Password can't be empty");
            } else if (!inputPassword.getText().matches(ValidationHandler.getPasswordValidation().getRegex())) {
                passwordValidationLabel.setText(ValidationHandler.getPasswordValidation().getMessage());
            } else {
                passwordValidationLabel.setVisible(false);
            }
        });

        inputConfirmPassword.textProperty().addListener(event -> {
            confirmPasswordValidationLabel.setVisible(true);
            if (inputConfirmPassword.getText().isEmpty()) {
                confirmPasswordValidationLabel.setText("Confirm your password");
            } else {
                confirmPasswordValidationLabel.setVisible(false);
            }
        });
    }

    private void initializeStyles() {
        inputEmail.setFocusColor(Paint.valueOf(currentDisplayMode.getPrimaryColor()));
        inputEmail.setStyle("-fx-text-fill: " + currentDisplayMode.getFontColorOnBackground());
        inputPassword.setFocusColor(Paint.valueOf(currentDisplayMode.getFontColorOnFormItems()));
        inputPassword.setStyle("-fx-text-fill: " + currentDisplayMode.getFontColorOnBackground());
        inputConfirmPassword.setFocusColor(Paint.valueOf(currentDisplayMode.getFontColorOnFormItems()));
        inputConfirmPassword.setStyle("-fx-text-fill: " + currentDisplayMode.getFontColorOnBackground());
        signUpLabel.setTextFill(Paint.valueOf(currentDisplayMode.getFontColorOnBackground()));
        signUpButton.setStyle("-fx-background-color: " + currentDisplayMode.getPrimaryColor());
        paneWithLogo.setStyle("-fx-background-color: " + currentDisplayMode.getPrimaryColor());
        formPane.setStyle("-fx-background-color: " + currentDisplayMode.getBackgroundColor());
    }

    @FXML
    public void handleSignInButton(ActionEvent event) {
        SceneHandler.goToSignInScene(event, settingsService, fxWeaver);
    }

    @FXML
    public void handleSignUpButton(ActionEvent event) {
        if (!validateForm()) {
            return;
        }

        String email = inputEmail.getText();
        String password = inputPassword.getText();

        if (email.equals(adminProperties.getEmail())) {
            emailValidationLabel.setVisible(true);
            emailValidationLabel.setText("Email is not valid");
            return;
        }

        try {
            userService.readByEmail(email);
            emailValidationLabel.setVisible(true);
            emailValidationLabel.setText("Email is already used");

        } catch (EntityNotFoundException e) {
            try {
                userService.create(new User(
                        userService.getAvailableId(),
                        email,
                        password.toCharArray(),
                        null
                ));

                TrayNotificationHandler.notify(
                        "Congratulations",
                        "You successfully created an account! Now you can sign in",
                        Notifications.SUCCESS,
                        Animations.POPUP,
                        Paint.valueOf(currentDisplayMode.getPrimaryColor()),
                        Duration.seconds(3)
                );

                handleSignInButton(event);

            } catch (DatabaseOperationException exception) {
                invokeTrayNotificationError(exception);
            }
        } catch (DatabaseOperationException exception) {
            invokeTrayNotificationError(exception);
        }
    }

    private boolean validateForm() {
        setVisibilityOfValidationLabels(true);

        if (inputEmail.getText().isEmpty()) {
            emailValidationLabel.setText("Email can't be empty");
        } else if (!inputEmail.getText().matches(ValidationHandler.getEmailRegex().getRegex())) {
            emailValidationLabel.setText(ValidationHandler.getEmailRegex().getMessage());
        } else {
            emailValidationLabel.setVisible(false);
        }

        if (inputPassword.getText().isEmpty()) {
            passwordValidationLabel.setText("Password can't be empty");
        } else if (!inputPassword.getText().matches(ValidationHandler.getPasswordValidation().getRegex())) {
            passwordValidationLabel.setText(ValidationHandler.getPasswordValidation().getMessage());
        } else {
            passwordValidationLabel.setVisible(false);
        }

        if (inputConfirmPassword.getText().isEmpty()) {
            confirmPasswordValidationLabel.setText("Confirm your password");
        } else if (!passwordValidationLabel.isVisible() && !inputPassword.getText().equals(inputConfirmPassword.getText())) {
            confirmPasswordValidationLabel.setText("Passwords not match");
            inputConfirmPassword.setText("");
        } else {
            confirmPasswordValidationLabel.setVisible(false);
        }

        return !emailValidationLabel.isVisible() && !passwordValidationLabel.isVisible() && !confirmPasswordValidationLabel.isVisible();
    }

    private void setVisibilityOfValidationLabels(boolean visibility) {
        emailValidationLabel.setVisible(visibility);
        passwordValidationLabel.setVisible(visibility);
        confirmPasswordValidationLabel.setVisible(visibility);
    }

    private void invokeTrayNotificationError(RuntimeException exception) {
        TrayNotificationHandler.notify(
                "Error",
                exception.getMessage(),
                Notifications.ERROR,
                Animations.POPUP,
                Paint.valueOf("#fc5b5b"),
                Duration.seconds(3)
        );
    }
}
