package com.reactioncommerce.tags.repository;

import com.reactioncommerce.tags.model.Tag;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TagRepository extends ReactiveMongoRepository<Tag, String> {
    Mono<Tag> findByIdAndShopId(String id, String shopId);
    Mono<Tag> findBySlugAndShopId(String slug, String shopId);
    Flux<Tag> findByShopId(String shopId);
    Mono<Boolean> existsBySlugAndShopId(String slug, String shopId);
}
