package exam.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "towns")
@XmlAccessorType(XmlAccessType.FIELD)
public class TownSeedRootDto {

    @XmlElement(name = "town")
    private List<TownSeedDto> townSeedDto;

    public TownSeedRootDto() {
    }

    public List<TownSeedDto> getTownSeedDto() {
        return townSeedDto;
    }

    public void setTownSeedDto(List<TownSeedDto> townSeedDto) {
        this.townSeedDto = townSeedDto;
    }
}
