# Praca Inżynierska 

#### --- Category --- 
  - ##### *get     /categories*
  - ##### *post    /categories*
  - ##### *get     /categories/{categoryId}*
  - ##### *put     /categories/{categoryId}*
  - ##### *delete  /categories/{categoryId}*


#### --- Comment --- 

  - ##### *get     /comments*
  - ##### *post    /comments*
  - ##### *get     /comments/{categoryId}*
  - ##### *put     /comments/{categoryId}*
  - ##### *delete  /comments/{categoryId}*
  - ##### *patch   /comments/{categoryId}/like*


#### --- Group --- 

  - ##### *get     /groups*
  - ##### *post    /groups*
  - ##### *get     /groups/{groupId}*
  - ##### *get     /groups/{value}*
  - ##### *put     /groups/{groupId}*
  - ##### *delete  /groups/{groupId}*
  - ##### *post    /groups/{groupId}/follow*
  - ##### *delete  /groups/{groupId}/unfollow*


#### --- Post --- 

  - ##### *get     /posts*
  - ##### *post    /posts*
  - ##### *get     /posts/{postId}*
  - ##### *put     /posts/{postId}*
  - ##### *delete  /posts/{postId}*
  - ##### *post    /posts/{postId}/bookmark*
  - ##### *delete  /posts/{postId}/unbookmark*

#### --- Tag --- 

  - ##### *get     /tags*
  - ##### *post    /tags*
  - ##### *get     /tags/{tagId}*
  - ##### *put     /tags/{tagId}*
  - ##### *delete  /tags/{tagId}*
  - ##### *post    /tags/{tagId}/follow*
  - ##### *delete  /tags/{tagId}/unfollow*

#### --- Upload --- 

  - ##### *get     /uploads*
  - ##### *post    /uploads/image*
  - ##### *post    /uploads/video*
  - ##### *get     /uploads/{uploadId}*
  - ##### *put     /uploads/{uploadId}*
  - ##### *delete  /uploads/{uploadId}*


#### --- User --- 

  - ##### *get     /users*
  - ##### *get     /users/{userId}*
  - ##### *put     /users/{userId}*
  - ##### *delete  /users/{userId}*
  - ##### *post    /users/{userId}/follow*
  - ##### *delete  /users/{userId}/unfollow*

#### --- User Resources --- 

  - ##### *get     /users/{userId)/resources/following*
  - ##### *get     /users/{userId)/resources/followers*
  - ##### *get     /users/{userId)/resources/bookmarks*
