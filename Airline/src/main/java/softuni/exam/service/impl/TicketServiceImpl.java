package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.TicketSeedRootDto;
import softuni.exam.models.entity.Ticket;
import softuni.exam.repository.PassengerRepository;
import softuni.exam.repository.PlaneRepository;
import softuni.exam.repository.TicketRepository;
import softuni.exam.repository.TownRepository;
import softuni.exam.service.TicketService;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class TicketServiceImpl implements TicketService {
    private static final String TICKET_FILE_PATH = "src/main/resources/files/xml/tickets.xml";

    private final TicketRepository ticketRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final XmlParser xmlParser;
    private final TownRepository townRepository;
    private final PassengerRepository passengerRepository;
    private final PlaneRepository planeRepository;

    public TicketServiceImpl(TicketRepository ticketRepository, ModelMapper modelMapper,
                             ValidationUtil validationUtil, XmlParser xmlParser, TownRepository townRepository, PassengerRepository passengerRepository, PlaneRepository planeRepository) {
        this.ticketRepository = ticketRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.xmlParser = xmlParser;
        this.townRepository = townRepository;
        this.passengerRepository = passengerRepository;
        this.planeRepository = planeRepository;
    }

    @Override
    public boolean areImported() {
        return ticketRepository.count() > 0;
    }

    @Override
    public String readTicketsFileContent() throws IOException {

        return Files.readString(Path.of(TICKET_FILE_PATH));
    }

    @Override
    public String importTickets() throws JAXBException, FileNotFoundException {
        StringBuilder builder = new StringBuilder();
        TicketSeedRootDto ticketSeedRootDto = xmlParser.fromFile(TICKET_FILE_PATH,TicketSeedRootDto.class);

        ticketSeedRootDto.getTicketSeedDto()
                .stream()
                .filter(ticketSeedDto -> {
                    boolean isValid = validationUtil.isValid(ticketSeedDto);
                    builder.append(isValid ? String.format("Successfully imported Ticket %s - %s",
                            ticketSeedDto.getFromTown().getName(),ticketSeedDto.getToTown().getName())
                            : "Invalid Ticket");
                    builder.append(System.lineSeparator());
                    return isValid;
                })
                .map(ticketSeedDto -> {
                    Ticket ticket = modelMapper.map(ticketSeedDto,Ticket.class);
                    ticket.setFromTown(townRepository.findByName(ticketSeedDto.getFromTown().getName()));
                    ticket.setToTown(townRepository.findByName(ticketSeedDto.getToTown().getName()));
                    ticket.setPassenger(passengerRepository.findByEmail(ticketSeedDto.getPassenger().getEmail()));
                    ticket.setPlane(planeRepository.findByRegisterNumber(ticketSeedDto.getPlane().getRegisterNumber()));
                    return ticket;
                })
                .forEach(ticketRepository::save);

        return builder.toString();
    }
}
