package com.sprawler.external.myinfo.entity.person.decrypted.vehicle;

import com.sprawler.external.myinfo.entity.person.decrypted.base.CodeObject;
import com.sprawler.external.myinfo.entity.person.decrypted.base.ValueObject;


public record Vehicle (
        ValueObject vehicleno,
        ValueObject type,
        ValueObject iulabelno,
        ValueObject make,
        ValueObject model,

        ValueObject chassisno,
        ValueObject engineno,
        ValueObject motorno,
        ValueObject yearofmanufacture,
        ValueObject firstregistrationdate,
        ValueObject originalregistrationdate,

        ValueObject coecategory,
        ValueObject coeexpirydate,
        ValueObject roadtaxexpirydate,
        ValueObject quotapremium,
        ValueObject openmarketvalue,

        ValueObject co2emission,
        CodeObject status,

        ValueObject primarycolour,
        ValueObject secondarycolour,

        ValueObject attachment1,
        ValueObject attachment2,
        ValueObject attachment3,
        ValueObject scheme,

        ValueObject thcemission,
        ValueObject coemission,
        ValueObject noxemission,
        ValueObject pmemission,
        ValueObject enginecapacity,
        ValueObject powerrate,
        ValueObject propellant,

        ValueObject maximumunladenweight,
        ValueObject maximumladenweight,
        ValueObject minimumparfbenefit,
        ValueObject nooftransfers,
        ValueObject vpc,

        String classification,
        String source,
        String lastupdated
) {

}
