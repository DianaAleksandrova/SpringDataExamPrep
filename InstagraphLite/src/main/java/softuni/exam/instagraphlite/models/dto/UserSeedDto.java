package softuni.exam.instagraphlite.models.dto;

import com.google.gson.annotations.Expose;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserSeedDto {

    @Expose
    @Size(min = 2,max = 19)
    @NotNull
    private String username;
    @Expose
    @Size(min = 4)
    private String password;
    @Expose
    private String profilePicture;

    public UserSeedDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
