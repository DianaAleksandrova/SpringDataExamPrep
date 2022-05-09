package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import softuni.exam.models.entity.Car;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car,Long> {

    @Query("SELECT c FROM Car c order by size(c.pictures) desc,c.make ")
    List<Car> findCarOrderByPictureDescThanByOrderAndByMake();

}
