package com.sprawler.external.myinfo.entity.person.decrypted.child;

import com.sprawler.external.myinfo.entity.person.decrypted.base.CodeObject;
import com.sprawler.external.myinfo.entity.person.decrypted.base.ValueObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BirthChild {
    private ValueObject birthcertno;
    private ValueObject name;
    private ValueObject hanyupinyinname;
    private ValueObject aliasname;
    private ValueObject hanyupinyinaliasname;
    private ValueObject marriedname;
    private CodeObject sex;
    private CodeObject race;
    private CodeObject secondaryrace;
    private CodeObject dialect;
    private CodeObject lifestatus;
    private ValueObject dob;
    private ValueObject tob;
    private List<VaccinationRequirements> vaccinationrequirements;
    private ValueObject sgcitizenatbirthind;
    private String classification;
    private String source;
    private String lastupdated;
}