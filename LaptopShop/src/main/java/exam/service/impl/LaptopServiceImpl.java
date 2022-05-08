package exam.service.impl;

import com.google.gson.Gson;
import exam.dto.LaptopSeedRootDto;
import exam.model.Laptop;
import exam.repository.LaptopRepository;
import exam.repository.ShopRepository;
import exam.service.LaptopService;
import exam.util.ValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

@Service
public class LaptopServiceImpl implements LaptopService {
    private static final String LAPTOPS_FILE_PATH ="src/main/resources/files/json/laptops.json";

    private final LaptopRepository laptopRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final Gson gson;
    private final ShopRepository shopRepository;

    public LaptopServiceImpl(LaptopRepository laptopRepository,
                             ModelMapper modelMapper, ValidationUtil validationUtil, Gson gson, ShopRepository shopRepository) {
        this.laptopRepository = laptopRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.gson = gson;
        this.shopRepository = shopRepository;
    }

    @Override
    public boolean areImported() {
        return laptopRepository.count() > 0;
    }

    @Override
    public String readLaptopsFileContent() throws IOException {
        return Files.readString(Path.of(LAPTOPS_FILE_PATH));
    }

    @Override
    public String importLaptops() throws IOException {
        StringBuilder builder = new StringBuilder();
        LaptopSeedRootDto[] laptopSeedRootDto = gson.fromJson(readLaptopsFileContent(),LaptopSeedRootDto[].class);

        Arrays.stream(laptopSeedRootDto)
                .filter(laptopSeedRootDto1 -> {
                    boolean isValid = validationUtil.isValid(laptopSeedRootDto1);
                    Optional<Laptop> laptop = laptopRepository.findByMacAddress(laptopSeedRootDto1.getMacAddress());
                    if (laptop.isPresent()){
                        isValid = false;
                    }
                    builder.append(isValid ? String.format("Successfully imported Laptop %s - %.2f - %d - %d",
                            laptopSeedRootDto1.getMacAddress(),
                            laptopSeedRootDto1.getCpuSpeed(),
                            laptopSeedRootDto1.getRam(),
                            laptopSeedRootDto1.getStorage())
                            : "Invalid Laptop");
                    builder.append(System.lineSeparator());
                    return isValid;
                })
                .map(laptopSeedRootDto1 -> {
                    Laptop laptop = modelMapper.map(laptopSeedRootDto1,Laptop.class);
                    laptop.setShop(shopRepository.findByName(laptopSeedRootDto1.getShop().getName()).orElse(null));
                    return laptop;
                })
                .forEach(laptopRepository::save);
        return builder.toString();
    }

    @Override
    public String exportBestLaptops() {
        StringBuilder builder = new StringBuilder();

        laptopRepository.findBestLaptop()
                .forEach(laptop -> {
                    builder.append(String.format("Laptop - %s%n" +
                            "*Cpu speed - %.2f%n" +
                            "**Ram - %d%n" +
                            "***Storage - %d%n" +
                            "****Price - %.2f%n" +
                            "#Shop name - %s%n" +
                            "##Town - %s%n",
                            laptop.getMacAddress(),
                            laptop.getCpuSpeed(),
                            laptop.getRam(),
                            laptop.getStorage(),
                            laptop.getPrice(),
                            laptop.getShop().getName(),
                            laptop.getShop().getTown().getName()));
                    builder.append(System.lineSeparator());
                });

        return builder.toString().trim();
    }
}
