package com.odde.doughnut.models;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

public abstract class TimestampOperations {
  public static Timestamp addHoursToTimestamp(Timestamp timestamp, int hoursToAdd) {
    ZonedDateTime zonedDateTime = timestamp.toInstant().atZone(ZoneId.of("UTC"));
    return Timestamp.from(zonedDateTime.plusHours(hoursToAdd).toInstant());
  }

  public static int getDayId(Timestamp timestamp, ZoneId timeZone) {
    ZonedDateTime userLocalDateTime = TimestampOperations.getZonedDateTime(timestamp, timeZone);
    return userLocalDateTime.getYear() * 366 + userLocalDateTime.getDayOfYear();
  }

  public static Timestamp alignByHalfADay(Timestamp currentUTCTimestamp, ZoneId timeZone) {
    final ZonedDateTime alignedZonedDt = alignDayAndHourByHalfADay(currentUTCTimestamp, timeZone);
    return Timestamp.from(alignedZonedDt.withMinute(0).withSecond(0).toInstant());
  }

  private static ZonedDateTime alignDayAndHourByHalfADay(
      Timestamp currentUTCTimestamp, ZoneId timeZone) {
    final ZonedDateTime zonedDateTime =
        TimestampOperations.getZonedDateTime(currentUTCTimestamp, timeZone);
    if (zonedDateTime.getHour() < 12) {
      return zonedDateTime.withHour(12);
    }
    return zonedDateTime.plusDays(1).withHour(0);
  }

  private static ZonedDateTime getZonedDateTime(Timestamp timestamp, ZoneId timeZone) {
    ZonedDateTime systemLocalDateTime = timestamp.toLocalDateTime().atZone(ZoneId.systemDefault());
    ZonedDateTime userLocalDateTime = systemLocalDateTime.withZoneSameInstant(timeZone);
    return userLocalDateTime;
  }

  public static long getDiffInHours(Timestamp currentUTCTimestamp, Timestamp nextReviewAt) {
    long diff = currentUTCTimestamp.getTime() - nextReviewAt.getTime();
    return TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS);
  }
}
