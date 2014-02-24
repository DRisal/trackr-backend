package de.techdev.trackr.web.api;

import de.techdev.trackr.domain.ContactPerson;
import de.techdev.trackr.domain.support.ContactPersonDataOnDemand;
import de.techdev.trackr.web.MockMvcTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Moritz Schulze
 */
public class ContactPersonResourceTest extends MockMvcTest {

    private String contactPersonJson = "{\"salutation\": \"salutation_1\", \"firstName\" : \"firstName_1\", \"lastName\": \"lastName_1\", \"email\": \"email@test.com\", \"phone\": \"123452345\", \"company\": \"/companies/0\"}";

    @Autowired
    private ContactPersonDataOnDemand contactPersonDataOnDemand;

    @Before
    public void setUp() throws Exception {
        contactPersonDataOnDemand.init();
    }

    @Test
    public void root() throws Exception {
        mockMvc.perform(
                get("/contactPersons")
                        .session(basicSession()))
               .andExpect(status().isOk())
               .andExpect(content().contentType(standardContentType));
    }

    @Test
    public void one() throws Exception {
        ContactPerson contactPerson = contactPersonDataOnDemand.getRandomObject();
        mockMvc.perform(
                get("/contactPersons/" + contactPerson.getId())
                        .session(basicSession()))
               .andExpect(status().isOk())
               .andExpect(content().contentType(standardContentType));
    }

    @Test
    public void postAllowedForSupervisor() throws Exception {
        mockMvc.perform(
                post("/contactPersons")
                        .session(supervisorSession())
                        .content(contactPersonJson))
               .andExpect(status().isCreated());
    }

    @Test
    public void postNotAllowedForEmployee() throws Exception {
        mockMvc.perform(
                post("/contactPersons")
                        .session(basicSession())
                        .content(contactPersonJson))
               .andExpect(status().isForbidden());
    }

    @Test
    public void constraintViolation() throws Exception {
        mockMvc.perform(
                post("/contactPersons")
                        .session(supervisorSession())
                        .content("{}"))
               .andExpect(status().isBadRequest());
    }
}
