package com.example.football.models.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "players")
@XmlAccessorType(XmlAccessType.FIELD)
public class PlayerRootDto {

    @XmlElement(name = "player")
    private List<PlayerDto> playerDto;

    public PlayerRootDto() {
    }

    public List<PlayerDto> getPlayerDto() {
        return playerDto;
    }

    public void setPlayerDto(List<PlayerDto> playerDto) {
        this.playerDto = playerDto;
    }
}
