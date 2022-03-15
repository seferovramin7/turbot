package com.turboparser.turbo.entity;


import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString
@Table(name="SpecificVehicle")
public class SpecificVehicle {

    @javax.persistence.Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String generalInfo;
    private String ownerName;
    private String phone;
    private Long lotId;
    private String price;
    private String description;
}
