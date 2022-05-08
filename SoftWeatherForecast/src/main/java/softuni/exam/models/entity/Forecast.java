package softuni.exam.models.entity;

import javax.persistence.*;
import javax.print.attribute.standard.MediaSize;
import java.time.LocalTime;

@Entity
@Table(name = "forecasts")
public class Forecast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "days_of_week",nullable = false)
    private DaysOfWeek daysOfWeek;
    @Column(name = "max_temperature",nullable = false)
    private Double maxTemperature;
    @Column(name = "min_max_temperature",nullable = false)
    private Double minMaxTemperature;
    @Column(name = "sunrise",nullable = false)
    private LocalTime sunrise;
    @Column(name = "sunset",nullable = false)
    private LocalTime sunset;
    @ManyToOne
    private City city;

    public Forecast() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DaysOfWeek getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(DaysOfWeek daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public Double getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(Double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public Double getMinMaxTemperature() {
        return minMaxTemperature;
    }

    public void setMinMaxTemperature(Double minMaxTemperature) {
        this.minMaxTemperature = minMaxTemperature;
    }

    public LocalTime getSunrise() {
        return sunrise;
    }

    public void setSunrise(LocalTime sunrise) {
        this.sunrise = sunrise;
    }

    public LocalTime getSunset() {
        return sunset;
    }

    public void setSunset(LocalTime sunset) {
        this.sunset = sunset;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
