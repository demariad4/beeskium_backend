package com.dave.beeskium.utils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class LocalTimeStringConverter implements AttributeConverter<LocalTime, String> {

    private static final DateTimeFormatter STORAGE_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public String convertToDatabaseColumn(LocalTime attribute) {
        if (attribute == null) {
            return null;
        }
        return STORAGE_FORMAT.format(attribute);
    }

    @Override
    public LocalTime convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }

        try {
            return LocalTime.parse(dbData.trim(), DateTimeFormatter.ISO_LOCAL_TIME);
        } catch (DateTimeParseException error) {
            throw new IllegalArgumentException("Formato orario non valido: " + dbData, error);
        }
    }
}
