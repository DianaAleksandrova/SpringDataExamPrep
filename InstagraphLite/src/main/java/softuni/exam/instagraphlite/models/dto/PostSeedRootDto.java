package softuni.exam.instagraphlite.models.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "posts")
@XmlAccessorType(XmlAccessType.FIELD)
public class PostSeedRootDto {

    @XmlElement(name = "post")
    private List<PostSeedDto> postSeedDto;

    public PostSeedRootDto() {
    }

    public List<PostSeedDto> getPostSeedDto() {
        return postSeedDto;
    }

    public void setPostSeedDto(List<PostSeedDto> postSeedDto) {
        this.postSeedDto = postSeedDto;
    }
}
