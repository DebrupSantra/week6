package com.example.blog;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BlogApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void managesCategoryPostAndModeratedComment() throws Exception {
        String categoryResponse = mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Engineering","description":"Technical articles"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Engineering"))
                .andReturn().getResponse().getContentAsString();
        long categoryId = objectMapper.readTree(categoryResponse).get("id").asLong();

        String postResponse = mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"First post","content":"Post body","author":"Alex","categoryIds":[%d]}
                                """.formatted(categoryId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categories[0].id").value(categoryId))
                .andReturn().getResponse().getContentAsString();
        long postId = objectMapper.readTree(postResponse).get("id").asLong();

        String commentResponse = mockMvc.perform(post("/api/posts/{postId}/comments", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"content":"A useful comment","author":"Sam"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.approved").value(false))
                .andReturn().getResponse().getContentAsString();
        long commentId = objectMapper.readTree(commentResponse).get("id").asLong();

        mockMvc.perform(patch("/api/comments/{id}/moderation", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"approved":true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.approved").value(true));

        mockMvc.perform(put("/api/posts/{id}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Updated post","content":"Updated body","author":"Alex","categoryIds":[]}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated post"));

        mockMvc.perform(get("/api/posts")
                        .param("author", "alex")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));

        mockMvc.perform(delete("/api/posts/{id}", postId))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/comments/{id}", commentId))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/categories/{id}", categoryId))
                .andExpect(status().isNoContent());
    }

    @Test
    void reportsValidationAndMissingResources() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"","content":"","author":"","categoryIds":null}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.title").exists())
                .andExpect(jsonPath("$.validationErrors.content").exists());

        mockMvc.perform(get("/api/posts/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
