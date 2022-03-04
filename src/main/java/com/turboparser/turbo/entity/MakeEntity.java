package com.turboparser.turbo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MakeEntity {

    @javax.persistence.Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String make;

    private int makeId;

}
