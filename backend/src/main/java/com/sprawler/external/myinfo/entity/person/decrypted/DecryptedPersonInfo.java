package com.sprawler.external.myinfo.entity.person.decrypted;

import com.sprawler.external.myinfo.entity.person.decrypted.base.CodeMetadata;
import com.sprawler.external.myinfo.entity.person.decrypted.base.ValueMetadata;
import com.sprawler.external.myinfo.entity.person.decrypted.child.BirthChild;
import com.sprawler.external.myinfo.entity.person.decrypted.child.SponsoredChild;
import com.sprawler.external.myinfo.entity.person.decrypted.contact.Mobile;
import com.sprawler.external.myinfo.entity.person.decrypted.contact.RegAdd;
import com.sprawler.external.myinfo.entity.person.decrypted.cpf.CpfBalances;
import com.sprawler.external.myinfo.entity.person.decrypted.cpf.CpfLife;
import com.sprawler.external.myinfo.entity.person.decrypted.cpf.CpfMonthlyPayouts;
import com.sprawler.external.myinfo.entity.person.decrypted.schemes.Chas;
import com.sprawler.external.myinfo.entity.person.decrypted.schemes.GenerationPackage;
import com.sprawler.external.myinfo.entity.person.decrypted.vehicle.Vehicle;

import java.util.List;

public record DecryptedPersonInfo (
     ValueMetadata partialuinfin,
     ValueMetadata uinfin,

     ValueMetadata name,
     ValueMetadata hanyupinyinname,
     ValueMetadata aliasname,
     ValueMetadata hanyupinyinaliasname,
     ValueMetadata marriedname,

     CodeMetadata sex,
     CodeMetadata race,
     CodeMetadata secondaryrace,
     CodeMetadata dialect,
     CodeMetadata nationality,
     ValueMetadata dob,

     CodeMetadata birthcountry,
     CodeMetadata residentialstatus,
     ValueMetadata passportnumber,
     ValueMetadata passportexpirydate,

     RegAdd regadd,
     CodeMetadata housingtype,
     CodeMetadata hdbtype,
    // hdbownership
     ValueMetadata ownerprivate,

     ValueMetadata email,
     Mobile mobileno,

     CodeMetadata marital,
     ValueMetadata marriagecertno,
     CodeMetadata countryofmarriage,
     ValueMetadata marriagedate,
     ValueMetadata divorcedate,

     List<BirthChild> childrenbirthrecords,
     List<SponsoredChild> sponsoredchildrenrecords,

     ValueMetadata occupation,
     ValueMetadata employment,
     CodeMetadata passtype,
     ValueMetadata passstatus,
     ValueMetadata passexpirydate,
     ValueMetadata employmentsector,

     List<Vehicle> vehicles,

    // drivinglicence

    // academicqualifications

    // ltavocationallicences

     Chas chas,
     GenerationPackage merdekagen,
     GenerationPackage pioneergen,

    // noa-basic
    // noa
    // noahistory-basic
    // noahistory

    // cpfcontributions
    // cpfemployers
     CpfBalances cpfbalances,
    // cpfhousingwithdrawal
    // cpfhomeprotectionscheme
    // cpfdependantprotectionscheme
    // cpfinvestmentscheme
    // cpfmedishieldlife
    // cpfrstucurrentyeartaxrelief
    // cpfrstuselftopupamount
     CpfLife cpflife,
     CpfMonthlyPayouts cpfmonthlypayouts
) {

}
