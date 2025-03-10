package utils;

import org.assertj.core.api.SoftAssertions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static utils.ConstantUtils.DATE_TIME_FORMAT;

public class DateTimeUtils {
    public static void verifyDateTime(SoftAssertions softAssertions, String targetDateTime, LocalDateTime timeBefore, LocalDateTime timeAfter) {
        LocalDateTime localDateTime = LocalDateTime.parse(targetDateTime, DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
        softAssertions.assertThat(localDateTime.isAfter(timeBefore)).isTrue();
        softAssertions.assertThat(localDateTime.isBefore(timeAfter)).isTrue();
    }

    public static void verifyDateTimeDb(SoftAssertions softAssertions, LocalDateTime targetDateTime, LocalDateTime timeBefore, LocalDateTime timeAfter) {
        softAssertions.assertThat(targetDateTime.isAfter(timeBefore)).isTrue();
        softAssertions.assertThat(targetDateTime.isBefore(timeAfter)).isTrue();
    }
}
