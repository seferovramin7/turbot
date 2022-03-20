package com.turboparser.turbo.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "SEARCH_PARAMETER")
public class SearchParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MESSAGE_ID")
    private Long messageId;

    @Column(name = "MAKE")
    private String make;

    @Column(name = "MODEL")
    private String model;

    @Column(name = "PRICE_MIN")
    private Long minPrice;

    @Column(name = "PRICE_MAX")
    private Long maxPrice;

    @Column(name = "YEAR_MIN")
    private Long minYear;

    @Column(name = "YEAR_MAX")
    private Long maxYear;

    @ManyToOne
    @JoinColumn(name = "CHAT_ID")
    private Chat chat;

}
