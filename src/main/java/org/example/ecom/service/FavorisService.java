package org.example.ecom.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.example.ecom.model.Client;
import org.example.ecom.model.Favoris;
import org.example.ecom.model.Product;
import org.example.ecom.repository.ClientRepo;
import org.example.ecom.repository.FavorisRepo;
import org.example.ecom.repository.ProductRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FavorisService {
    private final ClientRepo clientRepo;
    private final ProductRepo productRepo;
    private final FavorisRepo favorisRepo;

    public List<Favoris> getFavoris(){
         return favorisRepo.findAll();
     }

     public void deleteFavorisById(Long id){
         favorisRepo.deleteById(id);
     }

     public Favoris addFavoris(Favoris favoris, Long productId, Long clientId){
         Product product = productRepo.findById(productId).orElseThrow(()->new EntityNotFoundException("product not found"));
         Client client = clientRepo.findById(clientId).orElseThrow(()->new EntityNotFoundException("client not found"));
         favoris.setClient(client);
         favoris.setProduct(product);
         return favorisRepo.save(favoris);
     }

     public List<Product> getProductsFavorisByClientId(Long clientId) {
         return favorisRepo.findFavorisByClientId(clientId)
                 .stream()
                 .map(Favoris::getProduct)
                 .collect(Collectors.toList());
     }

}
