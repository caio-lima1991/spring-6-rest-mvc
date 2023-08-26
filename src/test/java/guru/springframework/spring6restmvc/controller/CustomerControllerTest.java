package guru.springframework.spring6restmvc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.services.CustomerService;
import guru.springframework.spring6restmvc.services.CustomerServiceImpl;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;
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
	
    @Captor
    ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Captor
    ArgumentCaptor<CustomerDTO> customerArgumentCaptor;
	
    @BeforeEach
    void setUp() {
    	customerServiceImpl = new CustomerServiceImpl();
    }
    
    @Test
    void testPatchCustomer() throws JsonProcessingException, Exception {
    	CustomerDTO customer = customerServiceImpl.getAllCustomers().get(0);
    	
    	Map<String, Object> customerMap = new HashMap<>();
    	customerMap.put("name", "New Name");
    	
    	mockMvc.perform(patch(CustomerController.CUSTOMER_PATH_ID, customer.getId())
    			.contentType(MediaType.APPLICATION_JSON)
    			.accept(MediaType.APPLICATION_JSON)
    				.content(objectMapper.writeValueAsString(customerMap)))
    			.andExpect(status().isNoContent());
	    verify(customerService).patchCustomerById(uuidArgumentCaptor.capture(), customerArgumentCaptor.capture());

        assertThat(customer.getId()).isEqualTo(uuidArgumentCaptor.getValue());
        assertThat(customerMap.get("name")).isEqualTo(customerArgumentCaptor.getValue().getName());
    }
    
    
    @Test
    void testDeleteCustomer() throws Exception {
    	CustomerDTO customer = customerServiceImpl.getAllCustomers().get(0);
    	
    	mockMvc.perform(delete(CustomerController.CUSTOMER_PATH_ID, customer.getId())
    			.accept(MediaType.APPLICATION_JSON))
    	.andExpect(status().isNoContent());
    	
    	verify(customerService).deleteCustomerById(uuidArgumentCaptor.capture());
    	assertThat(customer.getId()).isEqualTo(uuidArgumentCaptor.getValue());
    }
    
    @Test
    void testUpdateCustomer() throws Exception {
    	CustomerDTO customer = customerServiceImpl.getAllCustomers().get(0);
    	
    	mockMvc.perform(put(CustomerController.CUSTOMER_PATH_ID, customer.getId())
    			.accept(MediaType.APPLICATION_JSON)
    				.contentType(MediaType.APPLICATION_PROBLEM_JSON)
    				.content(objectMapper.writeValueAsString(customer)))
    			.andExpect(status().isNoContent());
    	verify(customerService).updateCustomerById(any(UUID.class), any(CustomerDTO.class));
    }
    
    @Test
    void testCreateNewCustomer() throws Exception{
    	CustomerDTO customer = customerServiceImpl.getAllCustomers().get(0);
    	customer.setVersion(null);
    	customer.setId(null);
    	
    	given(customerService.saveNewCustomer(any(CustomerDTO.class)))
    			.willReturn(customerServiceImpl.getAllCustomers().get(1));
    	
    	mockMvc.perform(post(CustomerController.CUSTOMER_PATH)
    			.accept(MediaType.APPLICATION_JSON)
    				.contentType(MediaType.APPLICATION_JSON)
    				.content(objectMapper.writeValueAsString(customer)))
    			.andExpect(status().isCreated())
    			.andExpect(header().exists("Location"));
    }
	
	@Test
	void tesListCustomer() throws Exception{
		given(customerService.getAllCustomers()).willReturn(customerServiceImpl.getAllCustomers());
		
		mockMvc.perform(get(CustomerController.CUSTOMER_PATH)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()", is(3)));
	}
	
	@Test
	void tesCustomerById() throws Exception{
		CustomerDTO testCustomer = customerServiceImpl.getAllCustomers().get(0);
		
		given(customerService.getCustomerById(testCustomer.getId()))
			.willReturn(Optional.of(testCustomer));
		
		mockMvc.perform(get(CustomerController.CUSTOMER_PATH_ID, testCustomer.getId())
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.name", is(testCustomer.getName())));
	}
}
