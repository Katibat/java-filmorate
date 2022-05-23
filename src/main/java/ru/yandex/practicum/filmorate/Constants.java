package ru.yandex.practicum.filmorate;

import java.time.LocalDate;
import java.util.Set;

public class Constants {
    public static final String DESCENDING_ORDER = "desc";
    public static final String ASCENDING_ORDER = "asc";

    public static final Set<String> SORTS = Set.of(ASCENDING_ORDER, DESCENDING_ORDER);
    public static final LocalDate BIRTHDAY_CINEMA = LocalDate.of(1895, 12, 28);
    public static final LocalDate TODAY_DATE = LocalDate.now();
}