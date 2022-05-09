package softuni.exam.models.dto;

import softuni.exam.models.entity.Plane;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "planes")
@XmlAccessorType(XmlAccessType.FIELD)
public class PlaneSeedRootDto {

    @XmlElement(name = "plane")
    private List<PlaneSeedDto> planeSeedDto;

    public PlaneSeedRootDto() {
    }

    public List<PlaneSeedDto> getPlaneSeedDto() {
        return planeSeedDto;
    }

    public void setPlaneSeedDto(List<PlaneSeedDto> planeSeedDto) {
        this.planeSeedDto = planeSeedDto;
    }
}
