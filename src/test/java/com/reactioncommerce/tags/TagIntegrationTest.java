package com.reactioncommerce.tags;

import com.reactioncommerce.tags.model.Tag;
import com.reactioncommerce.tags.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureHttpGraphQlTester
@ActiveProfiles("test")
@Testcontainers
public class TagIntegrationTest {
    
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0")
        .withCommand("--replSet", "rs0");
    
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }
    
    @Autowired
    private HttpGraphQlTester graphQlTester;
    
    @Autowired
    private TagRepository tagRepository;
    
    @BeforeEach
    void setUp() {
        tagRepository.deleteAll().block();
    }
    
    @Test
    void shouldCreateTagWithSlugGeneration() {
        String mutation = """
            mutation {
                addTag(input: {
                    name: "Test Tag"
                    shopId: "shop123"
                    isVisible: true
                }) {
                    tag {
                        _id
                        name
                        slug
                    }
                }
            }
            """;
        
        graphQlTester.document(mutation)
            .execute()
            .path("addTag.tag.name").entity(String.class).isEqualTo("Test Tag")
            .path("addTag.tag.slug").entity(String.class).isEqualTo("test-tag");
    }
    
    @Test
    void shouldGetTagBySlug() {
        Tag tag = Tag.builder()
            .name("Test Tag")
            .slug("test-tag")
            .shopId("shop123")
            .isVisible(true)
            .isDeleted(false)
            .isTopLevel(false)
            .build();
        tagRepository.save(tag).block();
        
        String query = """
            query {
                tag(slugOrId: "test-tag", shopId: "shop123") {
                    name
                    slug
                }
            }
            """;
        
        graphQlTester.document(query)
            .execute()
            .path("tag.name").entity(String.class).isEqualTo("Test Tag")
            .path("tag.slug").entity(String.class).isEqualTo("test-tag");
    }
    
    @Test
    void shouldNotShowInvisibleTagToRegularUser() {
        Tag tag = Tag.builder()
            .name("Hidden Tag")
            .slug("hidden-tag")
            .shopId("shop123")
            .isVisible(false)
            .isDeleted(false)
            .isTopLevel(false)
            .build();
        tagRepository.save(tag).block();
        
        String query = """
            query {
                tag(slugOrId: "hidden-tag", shopId: "shop123", shouldIncludeInvisible: false) {
                    name
                }
            }
            """;
        
        graphQlTester.document(query)
            .execute()
            .errors()
            .expect(error -> error.getMessage().contains("Tag not found"));
    }
    
    @Test
    void shouldPreventDuplicateSlugs() {
        Tag tag1 = Tag.builder()
            .name("Tag One")
            .slug("duplicate-slug")
            .shopId("shop123")
            .isVisible(true)
            .isDeleted(false)
            .isTopLevel(false)
            .build();
        tagRepository.save(tag1).block();
        
        String mutation = """
            mutation {
                addTag(input: {
                    name: "Tag Two"
                    slug: "Duplicate Slug"
                    shopId: "shop123"
                    isVisible: true
                }) {
                    tag {
                        slug
                    }
                }
            }
            """;
        
        graphQlTester.document(mutation)
            .execute()
            .errors()
            .expect(error -> error.getMessage().contains("already in use"));
    }
    
    @Test
    void shouldGetTagById() {
        Tag tag = Tag.builder()
            .name("Test Tag By ID")
            .slug("test-tag-by-id")
            .shopId("shop123")
            .isVisible(true)
            .isDeleted(false)
            .isTopLevel(false)
            .build();
        Tag savedTag = tagRepository.save(tag).block();
        
        String query = """
            query($slugOrId: String!, $shopId: ID!) {
                tag(slugOrId: $slugOrId, shopId: $shopId) {
                    _id
                    name
                    slug
                }
            }
            """;
        
        graphQlTester.document(query)
            .variable("slugOrId", savedTag.getId())
            .variable("shopId", "shop123")
            .execute()
            .path("tag._id").entity(String.class).isEqualTo(savedTag.getId())
            .path("tag.name").entity(String.class).isEqualTo("Test Tag By ID")
            .path("tag.slug").entity(String.class).isEqualTo("test-tag-by-id");
    }
    
    @Test
    void shouldUpdateTag() {
        Tag tag = Tag.builder()
            .name("Original Name")
            .slug("original-slug")
            .shopId("shop123")
            .isVisible(true)
            .isDeleted(false)
            .isTopLevel(false)
            .build();
        Tag savedTag = tagRepository.save(tag).block();
        
        String mutation = """
            mutation($id: ID!, $name: String!, $slug: String, $shopId: ID!, $isVisible: Boolean!, $displayTitle: String) {
                updateTag(input: {
                    id: $id
                    name: $name
                    slug: $slug
                    shopId: $shopId
                    isVisible: $isVisible
                    displayTitle: $displayTitle
                }) {
                    tag {
                        _id
                        name
                        slug
                        displayTitle
                    }
                }
            }
            """;
        
        graphQlTester.document(mutation)
            .variable("id", savedTag.getId())
            .variable("name", "Updated Name")
            .variable("slug", "updated-slug")
            .variable("shopId", "shop123")
            .variable("isVisible", true)
            .variable("displayTitle", "Updated Display Title")
            .execute()
            .path("updateTag.tag.name").entity(String.class).isEqualTo("Updated Name")
            .path("updateTag.tag.slug").entity(String.class).isEqualTo("updated-slug")
            .path("updateTag.tag.displayTitle").entity(String.class).isEqualTo("Updated Display Title");
    }
    
    @Test
    void shouldListTags() {
        Tag tag1 = Tag.builder()
            .name("Tag One")
            .slug("tag-one")
            .shopId("shop123")
            .isVisible(true)
            .isDeleted(false)
            .isTopLevel(false)
            .position(1)
            .build();
        
        Tag tag2 = Tag.builder()
            .name("Tag Two")
            .slug("tag-two")
            .shopId("shop123")
            .isVisible(true)
            .isDeleted(false)
            .isTopLevel(false)
            .position(2)
            .build();
        
        tagRepository.save(tag1).block();
        tagRepository.save(tag2).block();
        
        String query = """
            query {
                tags(shopId: "shop123") {
                    nodes {
                        name
                        slug
                    }
                    totalCount
                }
            }
            """;
        
        graphQlTester.document(query)
            .execute()
            .path("tags.totalCount").entity(Integer.class).isEqualTo(2)
            .path("tags.nodes[0].name").entity(String.class).isEqualTo("Tag One")
            .path("tags.nodes[1].name").entity(String.class).isEqualTo("Tag Two");
    }
}
