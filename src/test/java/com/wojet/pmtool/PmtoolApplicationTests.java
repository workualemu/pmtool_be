package com.wojet.pmtool;

// import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.wojet.pmtool.model.Client;
import com.wojet.pmtool.repository.ClientRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PmtoolApplicationTests {

	@Autowired
	ClientRepository clientRepository;

	@Test
	void contextLoads() {
		assertThat(true).isTrue();
	}

	@Test
	void canSaveClient() {
		Client client = new Client();
		client.setName("Test Client");
		clientRepository.save(client);
		assertThat(client.getId()).isNull();
	}

}
