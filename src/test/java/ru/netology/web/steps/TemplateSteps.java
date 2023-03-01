package ru.netology.web.steps;

import com.codeborne.selenide.Selenide;
import io.cucumber.java.ru.И;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Пусть;
import io.cucumber.java.ru.Тогда;
import org.junit.jupiter.api.Assertions;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.DashboardPage;
import ru.netology.web.page.LoginPage;
import ru.netology.web.page.TransferPage;
import ru.netology.web.page.VerificationPage;


public class TemplateSteps {
    private static LoginPage loginPage;
    private static DashboardPage dashboardPage;
    private static VerificationPage verificationPage;
    private static TransferPage transferPage;

    @Пусть("открыта страница с формой авторизации {string}")
    public void openAuthPage(String url) {
        loginPage = Selenide.open(url, LoginPage.class);
    }

    @Когда("пользователь пытается авторизоваться с именем {string} и паролем {string}")
    public void loginWithNameAndPassword(String login, String password) {
        verificationPage = loginPage.validLogin(login, password);
    }

    @И("пользователь вводит проверочный код 'из смс' {string}")
    public void setValidCode(String verificationCode) {
        dashboardPage = verificationPage.validVerify(verificationCode);
    }

    @Тогда("происходит успешная авторизация и пользователь попадает на страницу 'Личный кабинет'")
    public void verifyDashboardPage() {
        dashboardPage.isOpen();
    }

    @Пусть("пользователь залогинен под тестовым пользователем с именем {string} и паролем {string}")
    public void fullAuth(String login, String password) {
        loginPage = Selenide.open("http://localhost:9999/", LoginPage.class);
        DataHelper.AuthInfo user = new DataHelper.AuthInfo(login, password);
        String code = new DataHelper.VerificationCode("12345").getCode();
        verificationPage = loginPage.validLogin(login, password);
        dashboardPage = verificationPage.validVerify(code);
    }

    @Когда("пользователь переводит {int} рублей с карты с номером {string} на свою {int} карту с главной страницы")
    public void transfer(int amount, String cardNumber, int cardTo) {
        String cardTwoId = dashboardPage.getCardOnPage(cardTo).getAttribute("data-test-id");;
        dashboardPage
                .transferTo(cardTwoId)
                .sendTransferRound(cardNumber, amount * 100);
    }

    @Тогда("баланс его {int} карты из списка на главной странице должен стать {int} рублей")
    public void checkBalance(int cardNumber, int balance) {
        dashboardPage.isOpen();
        String cardId = dashboardPage.getCardOnPage(cardNumber).getAttribute("data-test-id");
        int balanceActual = dashboardPage.getCardBalance(cardId);
        int balanceExpected = balance * 100;

        Assertions.assertEquals(balanceExpected, balanceActual);
    }

}