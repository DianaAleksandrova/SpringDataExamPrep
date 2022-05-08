package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ForecastSeedRootDto;
import softuni.exam.models.entity.Forecast;
import softuni.exam.repository.ForecastRepository;
import softuni.exam.service.CityService;
import softuni.exam.service.ForecastService;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service
public class ForecastServiceImpl implements ForecastService {
    private static final String FORECAST_FILE_PATH = "src/main/resources/files/xml/forecasts.xml";

    private final ForecastRepository forecastRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final XmlParser xmlParser;
    private final CityService cityService;

    public ForecastServiceImpl(ForecastRepository forecastRepository, ModelMapper modelMapper,
                               ValidationUtil validationUtil, XmlParser xmlParser, CityService cityService) {
        this.forecastRepository = forecastRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.xmlParser = xmlParser;
        this.cityService = cityService;
    }

    @Override
    public boolean areImported() {
        return forecastRepository.count() > 0;
    }

    @Override
    public String readForecastsFromFile() throws IOException {
        return Files.readString(Path.of(FORECAST_FILE_PATH));
    }

    @Override
    public String importForecasts() throws IOException, JAXBException {
        StringBuilder builder = new StringBuilder();
        ForecastSeedRootDto forecastSeedRootDto = xmlParser.fromFile(FORECAST_FILE_PATH,ForecastSeedRootDto.class);

        forecastSeedRootDto.getForecastSeedDto()
                .stream()
                .filter(forecastSeedDto -> {
                    boolean isValid = validationUtil.isValid(forecastSeedDto);
                    Optional<Forecast> forecast = forecastRepository.findByDaysOfWeekAndCityId(forecastSeedDto.getDayOfWeek(),
                            forecastSeedDto.getCity());
                    if (forecast.isPresent()){
                        isValid = false;
                    }
                    builder.append(isValid ? String.format("Successfully import forecast %s - %.2f",
                            forecastSeedDto.getDayOfWeek(),forecastSeedDto.getMaxTemperature())
                            : "Invalid forecast");
                    builder.append(System.lineSeparator());

                    return isValid;
                })
                .map(forecastSeedDto -> {
                    Forecast forecast = modelMapper.map(forecastSeedDto,Forecast.class);
                    forecast.setCity(cityService.findById(forecastSeedDto.getCity()));
                    return forecast;
                })
                .forEach(forecastRepository::save);
        return builder.toString();
    }

    @Override
    public String exportForecasts() {
        StringBuilder builder = new StringBuilder();
        forecastRepository.findForecastByCityNameMinTemperatureMaxTemperatureSunriseAndSunset()
                .forEach(forecast -> {
                    builder.append(String.format("\t•\tCity: %s:\n" +
                                    "   \t\t-min temperature: %.2f\n" +
                                    "   \t\t--max temperature: %.2f\n" +
                                    "   \t\t---sunrise: %s\n" +
                                    "\t\t----sunset: %s\n",
                            forecast.getCity().getCityName(),
                            forecast.getMinMaxTemperature(),
                            forecast.getMaxTemperature(),
                            forecast.getSunrise(),
                            forecast.getSunrise()));
                });

        return builder.toString().trim();
    }
}
