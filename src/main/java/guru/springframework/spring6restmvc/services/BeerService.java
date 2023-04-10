package guru.springframework.spring6restmvc.services;

import java.util.List;
import java.util.UUID;

import guru.springframework.spring6restmvc.model.BeerDTO;

public interface BeerService {
	
    List<BeerDTO> listBeers();

    BeerDTO getBeerById(UUID id);

    BeerDTO saveNewBeer(BeerDTO beer);

    void updateBeerById(UUID beerId, BeerDTO beer);

    void deleteById(UUID beerId);

    void patchBeerById(UUID beerId, BeerDTO beer);
}
