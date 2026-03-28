package com.alan.whiskey_store_java_spring_boot_backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WhiskeyStoreJavaSpringBootBackendApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void productCatalogueIsPublicAndSeeded() throws Exception {
		mockMvc.perform(get("/api/products"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()", greaterThan(0)))
				.andExpect(jsonPath("$[0].name").isNotEmpty());
	}

	@Test
	void storeInfoExposesSampleCard() throws Exception {
		mockMvc.perform(get("/api/store-info"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.sampleCardNumber").value("4242 4242 4242 4242"))
				.andExpect(jsonPath("$.sampleCardCvv").value("123"));
	}
}
