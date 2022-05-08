package com.example.football.service.impl;

import com.example.football.models.dto.StatDto;
import com.example.football.models.dto.StatRootDto;
import com.example.football.models.entity.Stat;
import com.example.football.repository.StatRepository;
import com.example.football.service.StatService;
import com.example.football.util.ValidationUtil;
import com.example.football.util.XmlParser;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service
public class StatServiceImpl implements StatService {

    private static final String STAT_FILE_PATH = "src/main/resources/files/xml/stats.xml";

    private final StatRepository statRepository;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;
    private final XmlParser xmlParser;

    public StatServiceImpl(StatRepository statRepository, ValidationUtil validationUtil, ModelMapper modelMapper, XmlParser xmlParser) {
        this.statRepository = statRepository;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
        this.xmlParser = xmlParser;
    }

    @Override
    public boolean areImported() {
        return statRepository.count() > 0;
    }

    @Override
    public String readStatsFileContent() throws IOException {
        return Files.readString(Path.of(STAT_FILE_PATH));
    }

    @Override
    public String importStats() throws IOException, JAXBException {
        StringBuilder builder = new StringBuilder();
        StatRootDto statRootDto = xmlParser.fromFile(STAT_FILE_PATH,StatRootDto.class);

        statRootDto.getStatDto().stream()
                .filter(statDto -> {
                    boolean isValid = validationUtil.isValid(statDto);
                    Optional<Stat> stat = statRepository.findByPassingAndShootingAndEndurance(
                            statDto.getPassing(),statDto.getShooting(),statDto.getEndurance());
                    if (stat.isPresent()){
                        return false;
                    }
                    builder.append(isValid ? String.format("Successfully imported Stat %.2f - %.2f - %.2f",
                            statDto.getPassing(),statDto.getShooting(),statDto.getEndurance())
                            : "Invalid Stat");
                    builder.append(System.lineSeparator());
                    return isValid;
                })
                .map(statDto -> modelMapper.map(statDto,Stat.class))
                .forEach(statRepository::save);

        return builder.toString();
    }

    @Override
    public Stat findById(Long id) {

        return statRepository.findById(id).orElse(null);
    }
}
