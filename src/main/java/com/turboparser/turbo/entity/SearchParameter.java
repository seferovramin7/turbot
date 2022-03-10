package com.turboparser.turbo.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "SEARCH_PARAMETER")
@Data
public class SearchParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

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
    private com.turboparser.turbo.entity.Chat chat;

}
