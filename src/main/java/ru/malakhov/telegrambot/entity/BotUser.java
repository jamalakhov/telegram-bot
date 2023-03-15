package ru.malakhov.telegrambot.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BotUser {
    private Long id;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String phoneNumber;
    private boolean registration;
    private LocalDateTime dateCreate;

    @Override
    public String toString() {
        String builder = "\nFirst name: " + firstName +
                "\nLast name: " + lastName +
                "\nUser name: " + userName +
                "\nPhone: " + phoneNumber +
                "\nEmail: " + email;

        return builder.trim();
    }
}