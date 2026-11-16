package com.provisio.execution.project;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/*
 * @WebMvcTest = the "web slice": starts ONLY the web layer (controllers, JSON
 * conversion, error handling). No services, no repositories, no database.
 */
@WebMvcTest
class ProjectControllerTest {

    // A fake HTTP client that calls the controllers in memory, no real network or port.
    @Autowired
    MockMvc mvc;

    // Puts a Mockito fake ProjectService into the Spring context instead of the real one.
    // By default every method returns "empty" (null, Optional.empty(), 0...).
    @MockitoBean
    ProjectService projectService;

    @Test
    void getProjectReturnsProjectAsJson() throws Exception {
        // Arrange: teach the fake what to answer.
        given(projectService.findById(1L)).willReturn(Optional.of(new Project(1L, "Apollo")));

        // Act + Assert: send GET /projects/1 and check status and JSON body.
        mvc.perform(get("/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Apollo"));
    }

    // Kata 1: a project that doesn't exist must give 404, not a crash (500).
    @Test
    void getMissingProjectReturns404() throws Exception {
        // Teach the fake: "project 99? I have nothing" (Optional.empty() = no value).
        given(projectService.findById(99L)).willReturn(Optional.empty());

        mvc.perform(get("/projects/99"))
                .andExpect(status().isNotFound());
    }

    // Sprint 1, kata 2b: GET /projects returns a JSON array of all projects.
    @Test
    void listProjectsReturnsJsonArray() throws Exception {
        given(projectService.findAll()).willReturn(List.of(new Project(1L, "Apollo"), new Project(2L, "Gemini")));

        mvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].name").value("Gemini"));
    }

    // ---- Kata 3: create, update, delete ----

    @Test
    void createReturns201WithLocationAndBody() throws Exception {
        given(projectService.create("Apollo")).willReturn(new Project(1L, "Apollo"));

        mvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Apollo\"}"))
                .andExpect(status().isCreated())
                // Location = where the new resource lives. REST convention for 201.
                .andExpect(header().string("Location", "/projects/1"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createRejectsNameLongerThan300Chars() throws Exception {
        String body = "{\"name\":\"" + "a".repeat(301) + "\"}";

        mvc.perform(post("/projects").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());

        // The point of validating in Java: the bad request never reaches the service or the DB.
        verify(projectService, never()).create(any());
    }

    @Test
    void createRejectsBlankName() throws Exception {
        mvc.perform(post("/projects").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"  \"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateReturns200WithUpdatedProject() throws Exception {
        given(projectService.update(1L, "Gemini")).willReturn(Optional.of(new Project(1L, "Gemini")));

        mvc.perform(put("/projects/1").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Gemini\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gemini"));
    }

    @Test
    void updateMissingProjectReturns404() throws Exception {
        given(projectService.update(99L, "Gemini")).willReturn(Optional.empty());

        mvc.perform(put("/projects/99").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Gemini\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteReturns204() throws Exception {
        given(projectService.delete(1L)).willReturn(true);

        mvc.perform(delete("/projects/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteMissingProjectReturns404() throws Exception {
        given(projectService.delete(99L)).willReturn(false);

        mvc.perform(delete("/projects/99"))
                .andExpect(status().isNotFound());
    }
}
