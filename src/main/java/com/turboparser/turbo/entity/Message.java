package com.turboparser.turbo.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "MESSAGE", schema = "public")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "MESSAGE_ID")
    private Long messageId;

    @Column(name = "DATE")
    private Date date;

    @Lob
    @Column(name = "TEXT")
    private String text;

    @ManyToOne
    private User user;

    @ManyToOne
    @JoinColumn(name = "CHAT_ID")
    private Chat chat;
}
