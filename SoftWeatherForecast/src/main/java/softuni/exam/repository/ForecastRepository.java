package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import softuni.exam.models.entity.DaysOfWeek;
import softuni.exam.models.entity.Forecast;

import java.util.List;
import java.util.Optional;

@Repository
public interface ForecastRepository extends JpaRepository<Forecast,Long> {

    @Query("select f from Forecast f " +
            "WHERE f.city.id = :city AND f.daysOfWeek = :dayOfWeek ")
    Optional<Forecast> findByDaysOfWeekAndCityId(DaysOfWeek dayOfWeek, Long city);

    @Query("SELECT f FROM Forecast f " +
            "WHERE f.daysOfWeek = 'SUNDAY' AND f.city.population < 150000 " +
            "ORDER BY f.maxTemperature desc ,f.id ")
    List<Forecast> findForecastByCityNameMinTemperatureMaxTemperatureSunriseAndSunset();
}
