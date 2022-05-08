package com.example.football.service.impl;

import com.example.football.models.dto.PlayerRootDto;
import com.example.football.models.entity.Player;
import com.example.football.models.entity.Stat;
import com.example.football.models.entity.Team;
import com.example.football.models.entity.Town;
import com.example.football.repository.PlayerRepository;
import com.example.football.repository.StatRepository;
import com.example.football.repository.TeamRepository;
import com.example.football.repository.TownRepository;
import com.example.football.service.PlayerService;
import com.example.football.util.ValidationUtil;
import com.example.football.util.XmlParser;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@Service
public class PlayerServiceImpl implements PlayerService {
    private static final String PLAYERS_FILE_PATH = "src/main/resources/files/xml/players.xml";

    private final PlayerRepository playerRepository;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;
    private final XmlParser xmlParser;
    private final TeamRepository teamRepository;
    private final TownRepository townRepository;
    private final StatRepository statRepository;


    public PlayerServiceImpl(PlayerRepository playerRepository,
                             ValidationUtil validationUtil, ModelMapper modelMapper,
                             XmlParser xmlParser, TeamRepository teamRepository, TownRepository townRepository, StatRepository statRepository) {
        this.playerRepository = playerRepository;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
        this.xmlParser = xmlParser;
        this.teamRepository = teamRepository;
        this.townRepository = townRepository;
        this.statRepository = statRepository;
    }


    @Override
    public boolean areImported() {
        return playerRepository.count() > 0;
    }

    @Override
    public String readPlayersFileContent() throws IOException {

        return Files.readString(Path.of(PLAYERS_FILE_PATH));
    }

    @Override
    public String importPlayers() throws JAXBException, FileNotFoundException {
        StringBuilder builder = new StringBuilder();
        PlayerRootDto playerRootDto = xmlParser.fromFile(PLAYERS_FILE_PATH, PlayerRootDto.class);

        playerRootDto.getPlayerDto()
                .stream()
                .filter(playerDto -> {
                    boolean isValid = validationUtil.isValid(playerDto);
                    builder.append(isValid ? String.format("Successfully imported Player %s %s - %s",
                            playerDto.getFirstName(),playerDto.getLastName(),playerDto.getPosition().toString())
                            : "Invalid Player");
                    builder.append(System.lineSeparator());
                    return isValid;
                })
                .map(playerDto -> {
                    Player player = modelMapper.map(playerDto,Player.class);
                    Town town =townRepository.findByName(playerDto.getTownName().getName());
                    Team team = teamRepository.findByName(playerDto.getTeamName().getName()).orElse(null);
                    Stat stat = statRepository.findById(playerDto.getStat().getId()).orElse(null);
                    player.setTown(town);
                    player.setTeam(team);
                    player.setStat(stat);
                    return player;
                })
                .forEach(playerRepository::save);

        return builder.toString();
    }

    @Override
    public String exportBestPlayers() {
        //	•	 after 01-01-1995 and before 01-01-2003
        LocalDate before = LocalDate.of(2003,1,1);
        LocalDate after = LocalDate.of(1995,1,1);

        StringBuilder builder = new StringBuilder();

        playerRepository
                .findBestPlayerByPassingAndShootingAndEndurance(after, before)
                .forEach(player -> {
                    builder.append(String.format("Player - %s %s%n" +
                            "\tPosition - %s%n" +
                            "\tTeam - %s%n" +
                            "\tStadium - %s%n",
                            player.getFirstName(),player.getLastName(),
                            player.getPosition(),player.getTeam().getName()
                            ,player.getTeam().getStadiumName()));
                });


        return builder.toString();
    }
}
