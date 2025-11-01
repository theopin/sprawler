package com.sprawler.external.myinfo.entity.person.decrypted;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
import lombok.*;

import java.util.List;

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

    private RegAdd regadd;
    private CodeMetadata housingtype;
    private CodeMetadata hdbtype;
    // hdbownership
    private ValueMetadata ownerprivate;

    private ValueMetadata email;
    private Mobile mobileno;

    private CodeMetadata marital;
    private ValueMetadata marriagecertno;
    private CodeMetadata countryofmarriage;
    private ValueMetadata marriagedate;
    private ValueMetadata divorcedate;

    private List<BirthChild> childrenbirthrecords;
    private List<SponsoredChild> sponsoredchildrenrecords;

    private ValueMetadata occupation;
    private ValueMetadata employment;
    private CodeMetadata passtype;
    private ValueMetadata passstatus;
    private ValueMetadata passexpirydate;
    private ValueMetadata employmentsector;

    private List<Vehicle> vehicles;

    // drivinglicence

    // academicqualifications

    // ltavocationallicences

    private Chas chas;
    private GenerationPackage merdekagen;
    private GenerationPackage pioneergen;

    // noa-basic
    // noa
    // noahistory-basic
    // noahistory

    // cpfcontributions
    // cpfemployers
    private CpfBalances cpfbalances;
    // cpfhousingwithdrawal
    // cpfhomeprotectionscheme
    // cpfdependantprotectionscheme
    // cpfinvestmentscheme
    // cpfmedishieldlife
    // cpfrstucurrentyeartaxrelief
    // cpfrstuselftopupamount
    private CpfLife cpflife;
    private CpfMonthlyPayouts cpfmonthlypayouts;


}
