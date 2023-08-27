package guru.springframework.spring6restmvc.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import jakarta.transaction.Transactional;

@SpringBootTest
public class CostumerControllerIT {
    
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    CustomerController customerController;

    @Test
    void testGetByIdNotFound(){
        assertThrows(NotFoundException.class, 
            () -> {customerController.getCustomerById(UUID.randomUUID());});
    }

    @Test
    void testGetById(){
        Customer customer = customerRepository.findAll().get(0);
        CustomerDTO dto = customerController.getCustomerById(customer.getId()); 
        
        assertThat(dto).isNotNull();
    }

    @Test
    void testListAll(){
        List<CustomerDTO> dtos = customerController.listAllCustomers();

        assertThat(dtos.size()).isEqualTo(3);
    }

    @Rollback
    @Transactional
    @Test
    void testListAllEmptyList(){
        customerRepository.deleteAll();
        List<CustomerDTO> dtos = customerController.listAllCustomers();

        assertThat(dtos.size()).isEqualTo(0);
    }
}
