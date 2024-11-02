package com.sprawler.external.myinfo.dto.person.decrypted;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sprawler.external.myinfo.dto.person.decrypted.base.CodeMetadata;
import com.sprawler.external.myinfo.dto.person.decrypted.base.ValueMetadata;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DecryptedPersonInfo {
    private ValueMetadata partialuinfin;
    private ValueMetadata uinfin;
    private ValueMetadata name;
    private ValueMetadata hanyupinyinname;
    private ValueMetadata aliasname;
    private ValueMetadata hanyupinyinaliasname;
    private ValueMetadata marriedname;

    private CodeMetadata sex;
    private CodeMetadata race;
    private CodeMetadata secondaryrace;
    private CodeMetadata dialect;
    private CodeMetadata nationality;

    private ValueMetadata dob;
    private CodeMetadata birthcountry;
    private CodeMetadata residentialstatus;
    private ValueMetadata passportnumber;
    private ValueMetadata passportexpirydate;

// regadd

    private CodeMetadata housingtype;
    private CodeMetadata hdbtype;
    // hdb ownership

    private ValueMetadata ownerprivate;
    ValueMetadata email;
    // mobileno

    private CodeMetadata marital;
    private ValueMetadata marriagecertno;
    private CodeMetadata countryofmarriage;
    private ValueMetadata marriagedate;
    private ValueMetadata divorcedate;
    // childrenbirthrecords
    // sponsoredchildrenrecords

    private ValueMetadata occupation;
    private ValueMetadata employment;
    private CodeMetadata passtype;
    private ValueMetadata passstatus;
    private ValueMetadata passexpirydate;
    private ValueMetadata employmentsector;

    // vehicles
    // drivinglicence
    // academicqualifications

    // ltavocationallicences
    // merdekagen
    // pioneergen

    // noa-basic
    // noa
    // noahistory-basic
    // noahistory

    // cpfcontributions
    // cpfemployers
    // cpfbalances
    // cpfhousingwithdrawal
    // cpfhomeprotectionscheme
    // cpfdependantprotectionscheme
    // cpfinvestmentscheme
    // cpfmedishieldlife
    // cpfrstucurrentyeartaxrelief
    // cpfrstuselftopupamount
    // cpflife
    // cpfmonthlypayouts



}
