package io.pivotal.pal.wehaul.rental.domain;

import org.springframework.data.repository.CrudRepository;

public interface RentalRepository extends CrudRepository<Rental, ConfirmationNumber> {

}
