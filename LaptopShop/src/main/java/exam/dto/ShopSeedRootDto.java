package exam.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "shops")
@XmlAccessorType(XmlAccessType.FIELD)
public class ShopSeedRootDto {

    @XmlElement(name = "shop")
    private List<ShopSeedDto> shopSeedDto;

    public ShopSeedRootDto() {
    }

    public List<ShopSeedDto> getShopSeedDto() {
        return shopSeedDto;
    }

    public void setShopSeedDto(List<ShopSeedDto> shopSeedDto) {
        this.shopSeedDto = shopSeedDto;
    }
}
