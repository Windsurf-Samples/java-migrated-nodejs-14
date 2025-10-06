# Tags Plugin Future Enhancements Plan

## Overview
This document outlines the additional features and enhancements to be implemented for the Tags plugin after the initial migration is complete.

## Phase 2: Additional Mutations

### 1. removeTag Mutation

**Description:** Remove/soft-delete a tag from the system.

**Reference:** `node14-mongo-graphql/node_modules/@reactioncommerce/api-plugin-tags/src/mutations/removeTag.js`

**GraphQL Schema:**
```graphql
input RemoveTagInput {
  clientMutationId: String
  id: ID!
  shopId: ID!
}

type RemoveTagPayload {
  clientMutationId: String
  tag: Tag
}

extend type Mutation {
  removeTag(input: RemoveTagInput!): RemoveTagPayload!
}
```

**Implementation Notes:**
- Should set `isDeleted: true` (soft delete)
- Requires `reaction:legacy:tags/delete` permission
- Should emit event for audit trail
- Test case reference: `node14-mongo-graphql/tests/integration/api/mutations/removeTag/removeTag.test.js`

### 2. setTagHeroMedia Mutation

**Description:** Set or update the hero media URL for a tag.

**Reference:** `node14-mongo-graphql/node_modules/@reactioncommerce/api-plugin-tags/src/mutations/setTagHeroMedia.js`

**GraphQL Schema:**
```graphql
input SetTagHeroMediaInput {
  clientMutationId: String
  id: ID!
  shopId: ID!
  fileRecord: FileRecordInput
}

type SetTagHeroMediaPayload {
  clientMutationId: String
  tag: Tag!
}

extend type Mutation {
  setTagHeroMedia(input: SetTagHeroMediaInput!): SetTagHeroMediaPayload!
}
```

**Implementation Notes:**
- Updates heroMediaUrl field
- May require integration with file upload system
- Requires `reaction:legacy:tags/update` permission
- Test case reference: `node14-mongo-graphql/tests/integration/api/mutations/setTagHeroMedia/setTagHeroMedia.test.js`

## Phase 3: Full Permission System Integration

### Spring Security Integration

**Goal:** Implement comprehensive permission checking matching Node.js policies.

**Components to Implement:**

1. **Custom Permission Evaluator**
```java
public class ReactionPermissionEvaluator implements PermissionEvaluator {
  boolean hasPermission(Authentication auth, Object target, Object permission);
}
```

2. **Method Security Annotations**
```java
@PreAuthorize("hasPermission(#shopId, 'tag', 'create')")
public Mono<Tag> createTag(Tag tag, String shopId);
```

3. **Permission Policies Configuration**
- Translate policies.json to Spring Security configuration
- Implement role-based access control (RBAC)
- Handle resource-specific permissions (e.g., `reaction:legacy:tags:*`)

**Reference Permissions:**
- `reaction:legacy:tags/create` - Create new tags
- `reaction:legacy:tags/read` - Read public tags
- `reaction:legacy:tags/read:invisible` - Read invisible tags
- `reaction:legacy:tags/update` - Update existing tags
- `reaction:legacy:tags/delete` - Delete/soft-delete tags

**Policy Examples from Node.js:**
```json
{
  "subjects": ["reaction:groups:tag-managers"],
  "resources": ["reaction:legacy:tags"],
  "actions": ["create"],
  "effect": "allow"
}
```

## Phase 4: Advanced Querying and Pagination

### Cursor-Based Pagination

**Goal:** Implement full Relay-style cursor-based pagination.

**Components:**
- Cursor encoding/decoding (Base64 encoding of position)
- Edge types with cursor information
- PageInfo with proper hasNextPage/hasPreviousPage calculation
- Support for `first`, `last`, `before`, `after` arguments

**Reference:** GraphQL Cursor Connections Specification

### Tag Filtering and Search

**Goal:** Add regex-based filtering on tag names and slugs.

**Implementation:**
```java
public Flux<Tag> searchTags(String shopId, String filter) {
  Pattern pattern = Pattern.compile(filter, Pattern.CASE_INSENSITIVE);
  Criteria criteria = Criteria.where("shopId").is(shopId)
    .orOperator(
      Criteria.where("name").regex(pattern),
      Criteria.where("slug").regex(pattern)
    );
  // ...
}
```

**Reference:** `node14-mongo-graphql/node_modules/@reactioncommerce/api-plugin-tags/src/queries/tags.js` lines 43-50

## Phase 5: productsByTagId Query (Requires Products Plugin)

### Implementation Details

**Dependencies:**
- Product entity must be migrated first
- Products collection with `hashtags` field

**Complex Sorting Logic:**
1. Featured products first (sorted by position in `featuredProductIds` array)
2. Remaining products sorted by `createdAt` ascending
3. Uses `arrayJoinPlusRemainingQuery` utility for efficient aggregation

**Reference Implementation:**
`node14-mongo-graphql/node_modules/@reactioncommerce/api-plugin-tags/src/queries/productsByTagId.js`

**GraphQL Schema:**
```graphql
type TagProduct {
  _id: ID!
  createdAt: DateTime!
  position: Int
  title: String
}

type TagProductConnection {
  edges: [TagProductEdge]
  nodes: [TagProduct]
  pageInfo: PageInfo!
  totalCount: Int!
}

extend type Query {
  productsByTagId(
    shopId: ID!
    tagId: ID!
    after: String
    before: String
    first: Int
    last: Int
  ): TagProductConnection!
}
```

**MongoDB Aggregation Strategy:**
```javascript
// Pseudo-code for complex sorting
1. Find tag and extract featuredProductIds array
2. Find all products with tag in hashtags field
3. Create union:
   - Products in featuredProductIds (with position)
   - Remaining products (sorted by createdAt)
4. Apply pagination
```

**Test Coverage:**
- Products with featured sorting (test lines 115-135 in productsByTagId.test.js)
- Forward pagination with featured products
- Backward pagination crossing featured/non-featured boundary
- Edge cases with pagination boundaries

## Phase 6: Additional Features

### Tag Hierarchy

**Goal:** Support parent-child tag relationships.

**Fields to Leverage:**
- `subTagIds` - Array of child tag IDs
- `relatedTagIds` - Array of related tag IDs
- `isTopLevel` - Boolean flag for top-level tags

**GraphQL Extension:**
```graphql
extend type Tag {
  subTags(
    after: String
    before: String
    first: Int
    last: Int
    sortOrder: SortOrder = asc
    sortBy: TagSortByField = position
  ): TagConnection
}
```

### Bulk Operations

**Potential Additions:**
- Bulk tag creation
- Bulk tag updates
- Bulk tag deletion
- Import/export tags

### Event System

**Goal:** Emit events for tag lifecycle changes.

**Events to Implement:**
- `afterTagCreate`
- `afterTagUpdate`
- `afterTagDelete`

**Use Cases:**
- Cache invalidation
- Search index updates
- Audit logging
- Webhook triggers

## Testing Strategy

### Integration Test Coverage

**Additional Test Cases Needed:**
- Permission-based access control tests
- Cursor pagination edge cases
- Tag hierarchy navigation
- Featured products sorting with various scenarios
- Concurrent tag updates (optimistic locking)
- Tag filtering with special characters
- Metafields validation and querying

### Performance Testing

**Scenarios:**
- Large tag collections (10,000+ tags)
- Tags with many products (1,000+ products per tag)
- Complex queries with multiple filters
- Pagination performance with deep cursors

## Migration Notes

### Data Migration Considerations

When implementing these enhancements, consider:
- **Backward Compatibility**: Ensure changes don't break existing data
- **Index Optimization**: Add indexes for new query patterns
- **Schema Versioning**: Track schema changes for rollback capability

### Deployment Strategy

1. Deploy basic security structure (Phase 3) first
2. Add additional mutations (Phase 2) in separate release
3. Implement productsByTagId (Phase 5) after Products plugin migration
4. Add advanced features (Phase 6) incrementally

## Priority Order

1. **High Priority**: Phases 2 & 3 (mutations and security)
2. **Medium Priority**: Phase 4 (pagination and filtering)
3. **Low Priority**: Phase 6 (additional features)
4. **Blocked**: Phase 5 (requires Products plugin migration)

## Estimated Effort

- Phase 2: 2-3 days
- Phase 3: 4-5 days
- Phase 4: 3-4 days
- Phase 5: 4-5 days (after Products plugin available)
- Phase 6: Variable based on specific features

## References

- Node.js Implementation: `node14-mongo-graphql/node_modules/@reactioncommerce/api-plugin-tags/`
- Test Cases: `node14-mongo-graphql/tests/integration/api/`
- GraphQL Spec: https://spec.graphql.org/
- Relay Pagination: https://relay.dev/graphql/connections.htm
- Spring Security: https://spring.io/projects/spring-security
