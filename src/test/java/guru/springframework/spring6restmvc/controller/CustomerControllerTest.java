package guru.springframework.spring6restmvc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import guru.springframework.spring6restmvc.model.Beer;
import guru.springframework.spring6restmvc.model.Customer;
import guru.springframework.spring6restmvc.services.BeerServiceImpl;
import guru.springframework.spring6restmvc.services.CustomerService;
import guru.springframework.spring6restmvc.services.CustomerServiceImpl;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.hamcrest.core.Is.is;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

	@Autowired
	MockMvc mockMvc;
	
    @Autowired
    ObjectMapper objectMapper;
	
	@MockBean
	CustomerService customerService;
	
	CustomerServiceImpl customerServiceImpl = new CustomerServiceImpl();
	
    @BeforeEach
    void setUp() {
    	customerServiceImpl = new CustomerServiceImpl();
    }
    
    @Test
    void testCreateNewCustomer() throws Exception{
    	Customer customer = customerServiceImpl.getAllCustomers().get(0);
    	customer.setVersion(null);
    	customer.setId(null);
    	
    	given(customerService.saveNewCustomer(any(Customer.class)))
    			.willReturn(customerServiceImpl.getAllCustomers().get(1));
    	
    	mockMvc.perform(post("/api/v1/customer")
    			.accept(MediaType.APPLICATION_JSON)
    				.contentType(MediaType.APPLICATION_JSON)
    				.content(objectMapper.writeValueAsString(customer)))
    			.andExpect(status().isCreated())
    			.andExpect(header().exists("Location"));
    }
	
	@Test
	void tesListCustomer() throws Exception{
		given(customerService.getAllCustomers()).willReturn(customerServiceImpl.getAllCustomers());
		
		mockMvc.perform(get("/api/v1/customer")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()", is(3)));
	}
	
	@Test
	void tesCustomerById() throws Exception{
		Customer testCustomer = customerServiceImpl.getAllCustomers().get(0);
		
		given(customerService.getCustomerById(testCustomer.getId()))
			.willReturn(testCustomer);
		
		mockMvc.perform(get("/api/v1/customer/" + testCustomer.getId())
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id", is(testCustomer.getId().toString())))
				.andExpect(jsonPath("$.name", is(testCustomer.getName())));
	}
}
