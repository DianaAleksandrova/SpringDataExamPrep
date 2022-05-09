package softuni.exam.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import softuni.exam.models.entity.Passenger;

import java.util.List;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger,Long> {

    Passenger findByEmail(String email);

    @Query("select p from Passenger p " +
            "order by size(p.tickets) desc,p.email ")
    List<Passenger> findPassengersOrderByTicketsCountDescendingThenByEmail();
}
