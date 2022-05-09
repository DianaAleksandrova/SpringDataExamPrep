package softuni.exam.instagraphlite.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.instagraphlite.models.Picture;
import softuni.exam.instagraphlite.models.Post;
import softuni.exam.instagraphlite.models.User;
import softuni.exam.instagraphlite.models.dto.PostSeedRootDto;
import softuni.exam.instagraphlite.repository.PictureRepository;
import softuni.exam.instagraphlite.repository.PostRepository;
import softuni.exam.instagraphlite.repository.UserRepository;
import softuni.exam.instagraphlite.service.PostService;
import softuni.exam.instagraphlite.util.ValidationUtil;
import softuni.exam.instagraphlite.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class PostServiceImpl implements PostService {
    private static final String POSTS_FILE_PATH = "src/main/resources/files/posts.xml";

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final XmlParser xmlParser;
    private final UserRepository userRepository;
    private final PictureRepository pictureRepository;

    public PostServiceImpl(PostRepository postRepository, ModelMapper modelMapper,
                           ValidationUtil validationUtil, XmlParser xmlParser, UserRepository userRepository, PictureRepository pictureRepository) {
        this.postRepository = postRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.xmlParser = xmlParser;
        this.userRepository = userRepository;
        this.pictureRepository = pictureRepository;
    }

    @Override
    public boolean areImported() {
        return postRepository.count() > 0;
    }

    @Override
    public String readFromFileContent() throws IOException {
        return Files.readString(Path.of(POSTS_FILE_PATH));
    }

    @Override
    public String importPosts() throws IOException, JAXBException {
        StringBuilder builder = new StringBuilder();
        PostSeedRootDto postSeedRootDto = xmlParser.fromFile(POSTS_FILE_PATH,PostSeedRootDto.class);

        postSeedRootDto.getPostSeedDto()
                .stream()
                .filter(postSeedDto -> {
                    boolean isValid = validationUtil.isValid(postSeedDto);
                    User user = userRepository.findByUsername(postSeedDto.getUser().getUsername()).orElse(null);
                    Picture picture = pictureRepository.findByPath(postSeedDto.getPicture().getPath()).orElse(null);

                    if (user == null || picture == null){
                        isValid = false;
                    }

                    builder.append(isValid ? String.format("Successfully imported Post, made by %s",
                            postSeedDto.getUser().getUsername())
                            : "Invalid Post");
                    builder.append(System.lineSeparator());
                    return isValid;
                })
                .map(postSeedDto -> {
                    Post post = modelMapper.map(postSeedDto,Post.class);
                    post.setUser(userRepository.findByUsername(postSeedDto.getUser().getUsername()).orElse(null));
                    post.setPicture((pictureRepository.findByPath(postSeedDto.getPicture().getPath()).orElse(null)));
                    return post;
                })
                .forEach(postRepository::save);
        return builder.toString();
    }
}
