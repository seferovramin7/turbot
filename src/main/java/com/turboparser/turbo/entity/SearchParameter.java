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

    @ManyToOne
    private com.turboparser.turbo.entity.City city;

    @Column(name = "PRICE_MIN")
    private Long minPrice;

    @Column(name = "PRICE_MAX")
    private Long maxPrice;

    @Column(name = "ROOM")
    private Long numberOfRoom;

    @ManyToOne
    private com.turboparser.turbo.entity.Chat chat;

}
