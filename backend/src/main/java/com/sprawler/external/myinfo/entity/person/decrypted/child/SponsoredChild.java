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
public class SponsoredChild {
    private ValueObject nric;
    private ValueObject name;
    private ValueObject hanyupinyinname;
    private ValueObject aliasname;
    private ValueObject hanyupinyinaliasname;
    private ValueObject marriedname;
    private CodeObject sex;
    private CodeObject race;
    private CodeObject secondaryrace;
    private CodeObject dialect;
    private ValueObject dob;
    private CodeObject birthcountry;
    private CodeObject lifestatus;
    private CodeObject residentialstatus;
    private CodeObject nationality;
    private ValueObject scprgrantdate;
    private List<VaccinationRequirements> vaccinationrequirements;
    private ValueObject sgcitizenatbirthind;
    private String classification;
    private String source;
    private String lastupdated;
}