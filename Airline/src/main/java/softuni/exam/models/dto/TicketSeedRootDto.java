package softuni.exam.models.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "tickets")
@XmlAccessorType(XmlAccessType.FIELD)
public class TicketSeedRootDto {

    @XmlElement(name = "ticket")
    private List<TicketSeedDto> ticketSeedDto;

    public TicketSeedRootDto() {
    }

    public List<TicketSeedDto> getTicketSeedDto() {
        return ticketSeedDto;
    }

    public void setTicketSeedDto(List<TicketSeedDto> ticketSeedDto) {
        this.ticketSeedDto = ticketSeedDto;
    }
}
