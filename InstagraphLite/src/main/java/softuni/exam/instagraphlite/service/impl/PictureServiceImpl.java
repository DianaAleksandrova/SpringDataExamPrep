package softuni.exam.instagraphlite.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.instagraphlite.models.Picture;
import softuni.exam.instagraphlite.models.dto.PictureSeedDto;
import softuni.exam.instagraphlite.repository.PictureRepository;
import softuni.exam.instagraphlite.service.PictureService;
import softuni.exam.instagraphlite.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

@Service
public class PictureServiceImpl implements PictureService {
    private static final String PICTURES_FILE_PATH = "src/main/resources/files/pictures.json";

    private final PictureRepository pictureRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final Gson gson;

    public PictureServiceImpl(PictureRepository pictureRepository,
                              ModelMapper modelMapper, ValidationUtil validationUtil, Gson gson) {
        this.pictureRepository = pictureRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.gson = gson;
    }

    @Override
    public boolean areImported() {
        return pictureRepository.count() > 0;
    }

    @Override
    public String readFromFileContent() throws IOException {
        return Files.readString(Path.of(PICTURES_FILE_PATH));
    }

    @Override
    public String importPictures() throws IOException {
        StringBuilder builder = new StringBuilder();

        PictureSeedDto[] pictureSeedDto = gson.fromJson(readFromFileContent(),PictureSeedDto[].class);

        Arrays.stream(pictureSeedDto)
                .filter(pictureSeedDto1 -> {
                    boolean isValid = validationUtil.isValid(pictureSeedDto1);
                    Picture picture = pictureRepository.findByPath(pictureSeedDto1.getPath()).orElse(null);
                    if (picture != null){
                        isValid = false;
                    }

                    builder.append(isValid ? String.format("Successfully imported Picture, with size %.2f",
                            pictureSeedDto1.getSize())
                            : "Invalid Picture");
                    builder.append(System.lineSeparator());
                    return isValid;
                })
                .map(pictureSeedDto1 -> modelMapper.map(pictureSeedDto1, Picture.class))
                .forEach(pictureRepository::save);

        return builder.toString();
    }

    @Override
    public String exportPictures() {
        return null;
    }
}
