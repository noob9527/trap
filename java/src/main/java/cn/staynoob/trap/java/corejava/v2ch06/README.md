# 6 The Date and Time API
## 1 The Time Line
Historically, the fundamental time unit-this second-was derived from Earth's rotation around its axis. There are 24 hours or 24 * 60 * 60 = 86400 seconds in a full revolution, so it seems just a question of astronomical measurements to precisely define a second. Unfortunately, Earth wobbles slightly, and a more precise definition was needed. In 1967, a new precise definition of a second, matching the historical definition, was derived from an intrinsic property of atoms of caesium-133. Since then, a network of atomic clocks keeps the official time.\
Ever so often, the official time keepers synchronize the absolute time with the rotation of Earth. At first, the official seconds were slightly adjusted, but starting in 1972, "leap seconds" were occasionally inserted. (In theory, a second might need to be removed once in a while, but that has not yet happened.) There is talk of changing the system again. Clearly, leap seconds are a pain, and many computer systems instead use "smoothing" where time is artificially slowed down or sped up just before the leap second, keeping 86,400 seconds per day.\
In Java, an Instant represents a point on the time line. An origin, called the epoch, is arbitrarily set at midnight of January 1, 1970 at the prime meridian
that passes through the Greenwich Royal Observatory in London. This is the same convention used in the UNIX/POSIX time. Starting from that origin, time is measured in 86,400 seconds per day, forward and backward, to nanosecond precision.\
To find out the difference between two instants, use the static method `Duration.between.`.
## 2 Local Dates
Now let us turn from absolute time to human time. There are two kinds of human time in the Java API, local date/time and zoned time. Local date/time has no associated time zone information, so it does not correspond to a precise instant of time.\
There are many calculations where time zones are not required, and in some cases they can even by a hindrance. Suppose you schedule a meeting every week at 10:00. If you add 7 days to the last zoned time, and you happen to cross the daylight savings time boundary, the meeting will be an hour too early or to late.\
For that reason, the API designers recommend that you do not use zoned time unless you really want to represent absolute time instances. Birthdays, holidays, schedule times, and so on are usually best represented as local dates or times.\
Recall that the difference between two time instants is a Duration. The equivalent for local dates is a Period, which expresses a number of elapsed years, months, or days.\
In addition to `LocalDate`, there are also classes `MonthDay`, `YearMonth`, and Year to describe partial dates. For example, December 25(with the year unspecified) can be represented as `MonthDay`.

> Daylight saving time (abbreviated DST), sometimes referred to as daylight savings time in US, Canadian and Australian speech, and known as British Summer Time (BST) in the UK and just summer time in some countries, is the practice of advancing clocks during summer months so that evening daylight lasts longer, while sacrificing normal sunrise times. Typically, regions that use daylight saving time adjust clocks forward one hour close to the start of spring and adjust them backward in the autumn to standard time.

## 3 Date Adjusters
For scheduling applications, you often need to compute dates such as "the first Tuesday of every month." The `TemporalAdjusters` class provides a number of static methods for common adjustments. You pass the result of an adjustment method to the with method. For example, the first Tuesday of a month can be computes like this:
```java
LocalDate firstTuesday = LocalDate.of(year, month, 1).with(
        TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY)
);
```

## 4 Local Time
There is a `LocalDateTime` class representing a date and time. That class is suitable for storing points in time in a fixed time zone--for example, for a schedule of classes or events. However, if you need to make calculations that span the daylight savings time, or if you need to deal with users in different time zones, you should use the `ZonedDateTime` class that we discuss next.

## 5 Zoned Time
The Internet Assigned Numbers Authority(IANA) keeps a database of all known time zones around the world, which is updated several times per year. The bulk of the updates deals with the changing rules for daylight savings time. Java uses the IANA database.\
Each time zone has an ID, such as America/New_York or Europe/Berlin. To find out all available time zones, call `ZoneId.getAvailableZoneIds`. At the time of this writing, there were almost 600 IDs.\
Given a time zone ID, the static method ZoneId.of(id) yields a ZoneId object. You can use that object to turn a LocalDateTime object into a ZonedDateTime object, or you can construct a ZonedDateTime by calling the static method ZonedDateTime.of(...). This is a specific instant in time. Call instance.toInstant to get the Instant. Conversly, if you have an instant in time, call instant.atZone to get the ZonedDateTime.\
When daylight savings time starts, clocks advance by an hour. What happens when you construct a time that falls into the skipped hour? For example, in 2013, Central Europe switched to daylight savings time on March 31 at 2:00. If you try to construct nonexistent time March 31 2:30, you actually get 3:30. Conversely, when daylight time ends, clocks are set back by an hour, and there are two instants with the same local time! When you construct a time within that span, you get the earlier of the two.\
You also need to pay attention when adjusting a date across daylight savings time boundaries. For example, if you set a meeting for next week, don;t add a duration of seven days, Instead, use the Period class.
```java
// incorrect
ZonedDateTime nextMeeting = meeting.plus(Duration.ofDays(7));
// correct
ZonedDateTime nextMeeting = meeting.plus(Period.ofDays(7));
```

## 6 Formatting and Parsing
The DateTimeFormatter class provides three kinds of formatters to print a date/time value:
- Predefined standard formatters
- Locale-specific formatters
- Formatters with custom patterns
To use one of the standard formatters, simply call its format method. The standard formatters are mostly intended for machine-readable timestamps. To present dates and times to human readers, use a locale-specific formatter. Finally you can roll your own date format by specifying a pattern.
