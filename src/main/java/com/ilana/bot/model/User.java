package com.ilana.bot.model;


import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "user")
public class User {

    @Id
    @Column(name = "id")
    private Long chatId;
    private String firstName;
    private String userName;
    private String choice;
    private String language;
    private Long lastRuWordId;
    private Long lastEnWordId;
    private Timestamp registeredAt;

}
