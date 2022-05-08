package exam.repository;

import exam.model.Laptop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LaptopRepository extends JpaRepository<Laptop,Long> {
    Optional<Laptop> findByMacAddress(String macAddress);

    @Query("SELECT l from Laptop l " +
            "JOIN Town t ON t.name = l.shop.town.name " +
            "order by l.cpuSpeed desc ,l.ram desc ,l.storage desc ,l.macAddress ")
    List<Laptop> findBestLaptop();
}
