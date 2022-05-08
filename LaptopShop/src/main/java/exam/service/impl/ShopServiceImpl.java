package exam.service.impl;

import exam.dto.ShopSeedRootDto;
import exam.model.Shop;
import exam.repository.ShopRepository;
import exam.repository.TownRepository;
import exam.service.ShopService;
import exam.util.ValidationUtil;
import exam.util.XmlParser;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service
public class ShopServiceImpl implements ShopService {
    private static final String SHOP_FILE_PATH = "src/main/resources/files/xml/shops.xml";

    private final ShopRepository shopRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final XmlParser xmlParser;
    private final TownRepository townRepository;

    public ShopServiceImpl(ShopRepository shopRepository, ModelMapper modelMapper,
                           ValidationUtil validationUtil, XmlParser xmlParser, TownRepository townRepository) {
        this.shopRepository = shopRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.xmlParser = xmlParser;
        this.townRepository = townRepository;
    }

    @Override
    public boolean areImported() {
        return shopRepository.count() > 0;
    }

    @Override
    public String readShopsFileContent() throws IOException {
        return Files.readString(Path.of(SHOP_FILE_PATH));
    }

    @Override
    public String importShops() throws JAXBException, FileNotFoundException {
        StringBuilder builder = new StringBuilder();
        ShopSeedRootDto shopSeedRootDto = xmlParser.fromFile(SHOP_FILE_PATH,ShopSeedRootDto.class);

        shopSeedRootDto.getShopSeedDto()
                .stream()
                .filter(shopSeedDto -> {
                    boolean isValid = validationUtil.isValid(shopSeedDto);
                    Optional<Shop> shop = shopRepository.findByName(shopSeedDto.getName());
                    if (shop.isPresent()){
                        isValid = false;
                    }
                    builder.append(isValid ? String.format("Successfully imported Shop %s - %.0f",
                            shopSeedDto.getName(),shopSeedDto.getIncome())
                            : "Invalid Shop");
                    builder.append(System.lineSeparator());
                    return isValid;
                })
                .map(shopSeedDto -> {
                    Shop shop = modelMapper.map(shopSeedDto,Shop.class);
                    shop.setTown(townRepository.findByName(shopSeedDto.getTown().getName()));
                    return shop;
                })
                .forEach(shopRepository::save);

        return builder.toString();
    }
}
