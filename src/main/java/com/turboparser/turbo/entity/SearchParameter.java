package com.turboparser.turbo.entity;

import com.turboparser.turbo.constant.Currency;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "SEARCH_PARAMETER", schema = "public")
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

    @Column(name = "CURRENCY", length = 3)
    @Enumerated(EnumType.STRING)
    private Currency currency;

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
