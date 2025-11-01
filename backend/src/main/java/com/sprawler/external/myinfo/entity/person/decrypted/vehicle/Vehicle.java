package com.sprawler.external.myinfo.entity.person.decrypted.vehicle;

import com.sprawler.external.myinfo.entity.person.decrypted.base.CodeObject;
import com.sprawler.external.myinfo.entity.person.decrypted.base.ValueObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Vehicle {
    private ValueObject vehicleno;
    private ValueObject type;
    private ValueObject iulabelno;
    private ValueObject make;
    private ValueObject model;

    private ValueObject chassisno;
    private ValueObject engineno;
    private ValueObject motorno;
    private ValueObject yearofmanufacture;
    private ValueObject firstregistrationdate;
    private ValueObject originalregistrationdate;

    private ValueObject coecategory;
    private ValueObject coeexpirydate;
    private ValueObject roadtaxexpirydate;
    private ValueObject quotapremium;
    private ValueObject openmarketvalue;

    private ValueObject co2emission;
    private CodeObject status;

    private ValueObject primarycolour;
    private ValueObject secondarycolour;

    private ValueObject attachment1;
    private ValueObject attachment2;
    private ValueObject attachment3;
    private ValueObject scheme;

    private ValueObject thcemission;
    private ValueObject coemission;
    private ValueObject noxemission;
    private ValueObject pmemission;
    private ValueObject enginecapacity;
    private ValueObject powerrate;
    private ValueObject propellant;

    private ValueObject maximumunladenweight;
    private ValueObject maximumladenweight;
    private ValueObject minimumparfbenefit;
    private ValueObject nooftransfers;
    private ValueObject vpc;

    private String classification;
    private String source;
    private String lastupdated;
}
