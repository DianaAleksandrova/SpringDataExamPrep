package com.example.football.models.dto;

import com.example.football.models.entity.Position;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class PlayerDto {

    @XmlElement(name = "first-name")
    @Size(min = 2)
    private String firstName;
    @XmlElement(name = "last-name")
    @Size(min = 2)
    private String lastName;
    @XmlElement(name = "email")
    @Email
    private String email;
    @XmlElement(name = "birth-date")
    private String birthDate;
    @XmlElement(name = "position")
    @NotNull
    private String position;
    @XmlElement(name = "town")
    private TownNameDto townName;
    @XmlElement(name = "team")
    private TeamNameDto teamName;
    @XmlElement(name = "stat")
    private StatIdDto stat;

    public PlayerDto() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public TownNameDto getTownName() {
        return townName;
    }

    public void setTownName(TownNameDto townName) {
        this.townName = townName;
    }

    public TeamNameDto getTeamName() {
        return teamName;
    }

    public void setTeamName(TeamNameDto teamName) {
        this.teamName = teamName;
    }

    public StatIdDto getStat() {
        return stat;
    }

    public void setStat(StatIdDto stat) {
        this.stat = stat;
    }
}
