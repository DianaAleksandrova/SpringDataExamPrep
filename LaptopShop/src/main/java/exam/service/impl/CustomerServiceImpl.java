package exam.service.impl;

import com.google.gson.Gson;
import exam.dto.CustomerSeedRootDto;
import exam.model.Customer;
import exam.repository.CustomerRepository;
import exam.repository.TownRepository;
import exam.service.CustomerService;
import exam.util.ValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Service
public class CustomerServiceImpl implements CustomerService {
    private static final String CUSTOMERS_FILE_PATH = "src/main/resources/files/json/customers.json";

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final Gson gson;
    private final TownRepository townRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository,
                               ModelMapper modelMapper, ValidationUtil validationUtil, Gson gson, TownRepository townRepository) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.gson = gson;
        this.townRepository = townRepository;
    }

    @Override
    public boolean areImported() {
        return customerRepository.count() > 0;
    }

    @Override
    public String readCustomersFileContent() throws IOException {
        return Files.readString(Path.of(CUSTOMERS_FILE_PATH));
    }

    @Override
    public String importCustomers() throws IOException {
        StringBuilder builder = new StringBuilder();

        CustomerSeedRootDto[] customerSeedRootDto =
                gson.fromJson(readCustomersFileContent(),CustomerSeedRootDto[].class);

        Arrays.stream(customerSeedRootDto)
                .filter(customerSeedRootDto1 -> {
                    boolean isValid = validationUtil.isValid(customerSeedRootDto1);
                    builder.append(isValid ? String.format("Successfully imported Customer %s %s - %s",
                            customerSeedRootDto1.getFirstName(),
                            customerSeedRootDto1.getLastName(),
                            customerSeedRootDto1.getEmail())
                            : "Invalid Custoemr");
                    builder.append(System.lineSeparator());
                    return isValid;
                })
                .map(customerSeedRootDto1 -> {
                    Customer customer = modelMapper.map(customerSeedRootDto1,Customer.class);
                    customer.setTown(townRepository.findByName(customerSeedRootDto1.getTown().getName()));
                    return customer;
                })
                .forEach(customerRepository::save);

        return builder.toString();
    }
}
