package com.turboparser.turbo.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="SpecificVehicle")
public class SpecificVehicle {

    @javax.persistence.Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String carName;
    private String name;
    private String phone;
    private Long lotId;
    private String price;
    private String description;
}
