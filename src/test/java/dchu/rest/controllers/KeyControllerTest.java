package dchu.rest.controllers;

import dchu.core.entities.Key;
import dchu.core.services.KeysRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class KeyControllerTest {
    @InjectMocks
    private KeyController keyController;
    private MockMvc mockMvc;

    @Mock
    KeysRepository keysRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(keyController).build();
    }

//    @Test
//    public void getExistingKey() throws Exception {
//        Key key = new Key();
//        key.setId(1L);
//        key.setPublicKey("testABC");
//        key.setPrivateKey("private");
//
//        when(keysRepository.findByPublicKey("testABC")).thenReturn(key);
//
//        mockMvc.perform(get("/rest/keys/testABC"))
//                .andDo(print())
//                .andExpect(jsonPath("$.publicKey", is(key.getPublicKey())))
//                .andExpect(jsonPath("$.links[*].href", hasItem(endsWith("/keys/testABC"))))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void getNonExistingKey() throws Exception {
//
//        when(keysRepository.findByPublicKey("nonExistingKey")).thenReturn(null);
//
//        mockMvc.perform(get("/rest/keys/1"))
//                .andDo(print())
//                .andExpect(status().isNotFound());
//    }

}
