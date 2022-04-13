package com.odde.doughnut.testability.builders;

import com.odde.doughnut.models.UserModel;
import java.sql.Timestamp;
import java.time.*;

public class TimestampBuilder {
  private int day;
  private int hour;
  ZoneId userTimeZone = null;

  public TimestampBuilder of(int day, int hour) {
    this.day = day;
    this.hour = hour;
    return this;
  }

  public TimestampBuilder forWhereTheUserIs(UserModel userModel) {
    userTimeZone = userModel.getTimeZone();
    return this;
  }

  public Timestamp please() {
    if (userTimeZone == null) {
      throw new RuntimeException("We don't know the user's time zone yet");
    }
    ZonedDateTime userDateTime = ZonedDateTime.of(1989, 1, 1, hour, 0, 0, 0, userTimeZone);
    ZonedDateTime utc = userDateTime.withZoneSameInstant(ZoneId.systemDefault());

    return Timestamp.valueOf(utc.plusDays(day).toLocalDateTime());
  }
}
