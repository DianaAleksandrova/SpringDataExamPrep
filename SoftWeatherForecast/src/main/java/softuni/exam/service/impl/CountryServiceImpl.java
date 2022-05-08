package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.CountrySeedDto;
import softuni.exam.models.entity.Country;
import softuni.exam.repository.CountryRepository;
import softuni.exam.service.CountryService;
import softuni.exam.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

@Service
public class CountryServiceImpl implements CountryService {
    private static final String COUNTRIES_FILE_PATH = "src/main/resources/files/json/countries.json";

    private final CountryRepository countryRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final Gson gson;

    public CountryServiceImpl(CountryRepository countryRepository, ModelMapper modelMapper,
                              ValidationUtil validationUtil, Gson gson) {
        this.countryRepository = countryRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.gson = gson;
    }

    @Override
    public boolean areImported() {
        return countryRepository.count() > 0;
    }

    @Override
    public String readCountriesFromFile() throws IOException {
        return Files.readString(Path.of(COUNTRIES_FILE_PATH));
    }

    @Override
    public String importCountries() throws IOException {
        StringBuilder builder = new StringBuilder();
        CountrySeedDto[] countrySeedDto = gson.fromJson(readCountriesFromFile(),CountrySeedDto[].class);

        Arrays.stream(countrySeedDto)
                .filter(countrySeedDto1 -> {
                    boolean isValid = validationUtil.isValid(countrySeedDto1);
                    Optional<Country> country = countryRepository.findByCountryName(countrySeedDto1.getCountryName());
                    if (country.isPresent()){
                        isValid = false;
                    }
                    builder.append(isValid ? String.format("Successfully imported country %s - %s",
                            countrySeedDto1.getCountryName(),countrySeedDto1.getCurrency())
                            : "Invalid country");
                    builder.append(System.lineSeparator());
                    return isValid;
                })
                .map(countrySeedDto1 -> modelMapper.map(countrySeedDto1, Country.class))
                .forEach(countryRepository::save);

        return builder.toString();
    }

    @Override
    public Country findById(Long id) {

        return countryRepository.findById(id).orElse(null);
    }
}
