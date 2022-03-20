package com.turboparser.turbo.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name="USER", schema = "public")
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @Column(name = "IS_BOT")
    private Boolean isBot;

    @Column(name = "FIRST_NAME", length = 150)
    private String firstName;

    @Column(name = "LAST_NAME", length = 150)
    private String lastName;

    @Column(name = "USERNAME", length = 150)
    private String username;

    @Column(name = "LANGUAGE_CODE", length = 3)
    private String languageCode;

}
