import com.codeborne.selenide.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.w3c.dom.Text;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static org.openqa.selenium.remote.tracing.EventAttribute.setValue;

public class CardDeliveryTest {

/*    @BeforeAll
    static void setupAll() {
        Configuration.browser = "firefox";
        Configuration.holdBrowserOpen = false;

    } */

    public String generateDate(int daysToAdd, String formatter) {
        return LocalDate.now().plusDays(daysToAdd).format(DateTimeFormatter.ofPattern(formatter));
    }

    @Test
    void shouldSuccessOrderCard() {
        String planningDate = generateDate(4, "dd.MM.yyyy");
        open("http://localhost:9999/");
        SelenideElement form = $("fieldset");
        form.$("[data-test-id=city] input").setValue("Красноярск");
        form.$("[data-test-id=date] input").
                press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE).setValue(planningDate);
        form.$("[data-test-id=name] input").setValue("Петр Петров");
        form.$("[data-test-id=phone] input").setValue("+79234578465");
        form.$("[data-test-id=agreement]").click();
        form.$$("button").find(exactText("Забронировать")).click();
        $("[data-test-id=notification] .notification__content").
                shouldBe(visible, Duration.ofSeconds(15)).
                shouldHave(Condition.exactText("Встреча успешно забронирована на " + planningDate));
    }

    @Test
    void shouldRefuseAnOrderWhenCityIsNotAvailable() {
        String planningDate = generateDate(4, "dd.MM.yyyy");
        open("http://localhost:9999/");
        SelenideElement form = $("fieldset");
        form.$("[data-test-id=city] input").setValue("Братск");
        form.$("[data-test-id=date] input").
                press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE).setValue(planningDate);
        form.$("[data-test-id=name] input").setValue("Петр Петров");
        form.$("[data-test-id=phone] input").setValue("+79234578465");
        form.$("[data-test-id=agreement]").click();
        form.$$("button").find(exactText("Забронировать")).click();
        $("[data-test-id=city] .input__sub").shouldBe(visible).
                shouldHave(Condition.exactText("Доставка в выбранный город недоступна"));
    }

    @Test
    void shouldRefuseAnOrderWhenDateIsNotAvailable() {
        String planningDate = generateDate(2, "dd.MM.yyyy");
        open("http://localhost:9999/");
        SelenideElement form = $("fieldset");
        form.$("[data-test-id=city] input").setValue("Санкт-Петербург");
        form.$("[data-test-id=date] input").
                press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE).setValue(planningDate);
        form.$("[data-test-id=name] input").setValue("Петр Петров");
        form.$("[data-test-id=phone] input").setValue("+79234578465");
        form.$("[data-test-id=agreement]").click();
        form.$$("button").find(exactText("Забронировать")).click();
        $("[data-test-id=date] .input__sub").shouldBe(visible).
                shouldHave(Condition.exactText("Заказ на выбранную дату невозможен"));
    }

    @Test
    void shouldRefuseAnOrderWhenPhoneIsInvalid() {
        String planningDate = generateDate(3, "dd.MM.yyyy");
        open("http://localhost:9999/");
        SelenideElement form = $("fieldset");
        form.$("[data-test-id=city] input").setValue("Санкт-Петербург");
        form.$("[data-test-id=date] input").
                press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE).setValue(planningDate);
        form.$("[data-test-id=name] input").setValue("Петр Петров");
        form.$("[data-test-id=phone] input").setValue("79234578465");
        form.$("[data-test-id=agreement]").click();
        form.$$("button").find(exactText("Забронировать")).click();
        $("[data-test-id=phone] .input__sub").shouldBe(visible).
                shouldHave(Condition.text("Телефон указан неверно."));

    }

    @Test
    void shouldChooseCityWithDropDownList() {
        String planningDate = generateDate(4, "dd.MM.yyyy");
        open("http://localhost:9999/");
        SelenideElement form = $("fieldset");
        form.$("[data-test-id=city] input").setValue("Мо");
        $$("[class=popup__container] .menu-item__control").
                find(exactText("Москва")).shouldBe(visible).click();
        form.$("[data-test-id=date] input").
                press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE).setValue(planningDate);
        form.$("[data-test-id=name] input").setValue("Петр Петров");
        form.$("[data-test-id=phone] input").setValue("+79234578465");
        form.$("[data-test-id=agreement]").click();
        form.$$("button").find(exactText("Забронировать")).click();
        $("[data-test-id=notification] .notification__content").
                shouldBe(visible, Duration.ofSeconds(15)).
                shouldHave(Condition.exactText("Встреча успешно забронирована на " + planningDate));
    }

    @Test
    void shouldChooseDateWithWidget() {
        String planningDate = generateDate(15, "dd.MM.yyyy");
        open("http://localhost:9999/");
        SelenideElement form = $("fieldset");
        form.$("[data-test-id=city] input").setValue("Красноярск");
        $("[class=input__icon] .icon_name_calendar").click();
        if (!generateDate(3,"MM").equals(generateDate(15,"MM"))) {
            $("[class=popup__container] .calendar__arrow_direction_right:not(.calendar__arrow_double)").click();
        }
        $$("[class=calendar__row] .calendar__day").find(exactText(generateDate(15,"d"))).
                shouldBe(visible).click();
        form.$("[data-test-id=name] input").setValue("Петр Петров");
        form.$("[data-test-id=phone] input").setValue("+79234578465");
        form.$("[data-test-id=agreement]").click();
        form.$$("button").find(exactText("Забронировать")).click();
        $("[data-test-id=notification] .notification__content").
                shouldBe(visible, Duration.ofSeconds(15)).
                shouldHave(Condition.exactText("Встреча успешно забронирована на " + planningDate));
    }

}