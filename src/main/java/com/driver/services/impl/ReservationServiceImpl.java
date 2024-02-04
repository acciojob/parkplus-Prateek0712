package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;

    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
        Optional<User> optionalUser = userRepository3.findById(userId);
        Optional<ParkingLot> optionalParkingLot = parkingLotRepository3.findById(parkingLotId);
        if (optionalUser.isEmpty() || optionalParkingLot.isEmpty()) {
            throw new Exception("Cannot make reservation");
        }
        ParkingLot parkingLot = optionalParkingLot.get();
        List<Spot> spotList = parkingLot.getSpotList();
        if (spotList.size() <= 0) throw new Exception("Cannot make reservation");
        Spot reservedSpot = null;
        int minAmt = Integer.MAX_VALUE;
        for (Spot s : spotList) {
            int wheels = 0;
            if (s.getSpotType() == SpotType.TWO_WHEELER) {
                wheels = 2;
            } else if (s.getSpotType() == SpotType.FOUR_WHEELER) {
                wheels = 4;
            } else if (s.getSpotType() == SpotType.OTHERS) {
                wheels = 24;
            }
            if (!s.getOccupied() && numberOfWheels <= wheels && s.getPricePerHour() * timeInHours < minAmt) {
                minAmt = s.getPricePerHour() * timeInHours;
                reservedSpot = s;
            }
        }
        if (reservedSpot == null) {
            throw new Exception("Cannot make reservation");
        }
        User user = optionalUser.get();
        Reservation reservation = new Reservation();
        reservation.setNumberOfHours(timeInHours);
        reservation.setUser(user);
        reservation.setSpot(reservedSpot);
        user.getReservationList().add(reservation);
        reservedSpot.getReservationList().add(reservation);
        reservedSpot.setOccupied(Boolean.FALSE);
        reservation = reservationRepository3.save(reservation);
        return reservation;
    }
}