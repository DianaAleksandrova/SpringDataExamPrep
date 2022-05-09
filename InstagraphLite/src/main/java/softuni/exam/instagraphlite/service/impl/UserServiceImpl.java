package softuni.exam.instagraphlite.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.instagraphlite.models.Picture;
import softuni.exam.instagraphlite.models.User;
import softuni.exam.instagraphlite.models.dto.UserSeedDto;
import softuni.exam.instagraphlite.repository.PictureRepository;
import softuni.exam.instagraphlite.repository.UserRepository;
import softuni.exam.instagraphlite.service.UserService;
import softuni.exam.instagraphlite.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;

@Service
public class UserServiceImpl implements UserService {
    private static final String USERS_FILE_PATH = "src/main/resources/files/users.json";

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final Gson gson;
    private final PictureRepository pictureRepository;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper,
                           ValidationUtil validationUtil, Gson gson, PictureRepository pictureRepository) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.gson = gson;
        this.pictureRepository = pictureRepository;
    }

    @Override
    public boolean areImported() {
        return userRepository.count() > 0;
    }

    @Override
    public String readFromFileContent() throws IOException {
        return Files.readString(Path.of(USERS_FILE_PATH));
    }

    @Override
    public String importUsers() throws IOException {
        StringBuilder builder = new StringBuilder();
        UserSeedDto[] userSeedDto = gson.fromJson(readFromFileContent(),UserSeedDto[].class);

        Arrays.stream(userSeedDto)
                .filter(userSeedDto1 -> {
                    boolean isValid = validationUtil.isValid(userSeedDto1);
                    Picture picture = pictureRepository.findByPath(userSeedDto1.getProfilePicture()).orElse(null);
                    if (picture == null){
                        isValid = false;
                    }
                    builder.append(isValid ? String.format("Successfully imported User: %s",
                            userSeedDto1.getUsername())
                            : "Invalid User");
                    builder.append(System.lineSeparator());
                    return isValid;
                })
                .map(userSeedDto1 -> {
                    User user = modelMapper.map(userSeedDto1, User.class);
                    user.setProfilePicture(pictureRepository.findByPath(userSeedDto1.getProfilePicture()).orElse(null));
                    return user;
                })
                .forEach(userRepository::save);

        return builder.toString();
    }

    @Override
    public String exportUsersWithTheirPosts() {
        StringBuilder builder = new StringBuilder();

        return null;
    }
}
