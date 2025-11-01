package com.sprawler.external.myinfo.entity.person.decrypted.child;

import com.sprawler.external.myinfo.entity.person.decrypted.base.CodeObject;
import com.sprawler.external.myinfo.entity.person.decrypted.base.ValueObject;

import java.util.List;

public record BirthChild (
     ValueObject birthcertno,
     ValueObject name,
     ValueObject hanyupinyinname,
     ValueObject aliasname,
     ValueObject hanyupinyinaliasname,
     ValueObject marriedname,
     CodeObject sex,
     CodeObject race,
     CodeObject secondaryrace,
     CodeObject dialect,
     CodeObject lifestatus,
     ValueObject dob,
     ValueObject tob,
     List<VaccinationRequirements> vaccinationrequirements,
     ValueObject sgcitizenatbirthind,
     String classification,
     String source,
     String lastupdated
) {
}
