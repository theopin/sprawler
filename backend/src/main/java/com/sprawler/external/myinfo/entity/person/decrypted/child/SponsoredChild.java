package com.sprawler.external.myinfo.entity.person.decrypted.child;

import com.sprawler.external.myinfo.entity.person.decrypted.base.CodeObject;
import com.sprawler.external.myinfo.entity.person.decrypted.base.ValueObject;

import java.util.List;

public record SponsoredChild (
        ValueObject nric,
        ValueObject name,
        ValueObject hanyupinyinname,
        ValueObject aliasname,
        ValueObject hanyupinyinaliasname,
        ValueObject marriedname,
        CodeObject sex,
        CodeObject race,
        CodeObject secondaryrace,
        CodeObject dialect,
        ValueObject dob,

        CodeObject birthcountry,
        CodeObject lifestatus,
        CodeObject residentialstatus,
        CodeObject nationality,
        ValueObject scprgrantdate,
        List<VaccinationRequirements> vaccinationrequirements,
        ValueObject sgcitizenatbirthind,
        String classification,
        String source,
        String lastupdated
) {

}