package com.sprawler.external.myinfo.dto.person.decrypted;

import com.sprawler.external.myinfo.dto.person.decrypted.base.CodeMetadata;
import com.sprawler.external.myinfo.dto.person.decrypted.base.ValueMetadata;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecryptedPersonInfo {
    ValueMetadata partialuinfin;
    ValueMetadata uinfin;
    ValueMetadata name;
    ValueMetadata hanyupinyinname;
    ValueMetadata aliasname;
    ValueMetadata hanyupinyinaliasname;
    ValueMetadata marriedname;

    CodeMetadata sex;
    CodeMetadata race;
    CodeMetadata secondaryrace;
    CodeMetadata dialect;
    CodeMetadata nationality;

    ValueMetadata dob;
    CodeMetadata birthcountry;
    CodeMetadata residentialstatus;
    ValueMetadata passportnumber;
    ValueMetadata passportexpirydate;

// regadd

    CodeMetadata housingtype;
    CodeMetadata hdbtype;
    // hdb ownership

    ValueMetadata ownerprivate;
    ValueMetadata email;
    // mobileno

    CodeMetadata marital;
    ValueMetadata marriagecertno;
    CodeMetadata countryofmarriage;
    ValueMetadata marriagedate;
    ValueMetadata divorcedate;
    // childrenbirthrecords
    // sponsoredchildrenrecords

    ValueMetadata occupation;
    ValueMetadata employment;
    CodeMetadata passtype;
    ValueMetadata passstatus;
    ValueMetadata passexpirydate;
    ValueMetadata employmentsector;

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
