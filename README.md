## 실행 방법

### 요구 사항
- JDK 25 (build.gradle.kts 의 toolchain 설정과 일치하는지 확인)
- 별도의 DB 설치는 필요 없음 (H2 파일 DB를 사용하며, 실행하면 프로젝트 루트에 `db_dev.mv.db`가 생성됨)

### 실행
```bash
cp .env.example .env   # JWT_SECRET 값을 32자 이상 임의 문자열로 채우기
./gradlew bootRun      # Windows: .\gradlew bootRun
```

로그에 `Started DevMission2Application`이 출력되면 실행이 완료된 것입니다. 서버는 `http://localhost:8080`에서 동작하며, 종료는 `Ctrl + C`입니다.

## API 명세
### 회원가입 — `POST /api/v1/members`
인증 불필요

요청
```json
{ "email": "user1@example.com", "password": "password1234", "nickname": "유저1" }
```

응답 `201 Created`
```json
{ "id": 1, "email": "user1@example.com", "nickname": "유저1", "createDate": "2026-09-25T10:00:00" }
```

실패: 이메일 형식 오류·비밀번호 10자 미만 → `400`, 이메일 중복 → `409`

### 로그인 — `POST /api/v1/auth/login`
인증 불필요

요청
```json
{ "email": "user1@example.com", "password": "password1234" }
```

응답 `200 OK`
```json
{ "accessToken": "eyJhbGciOi...", "tokenType": "Bearer" }
```

실패: 이메일 없음·비밀번호 불일치 모두 같은 `401` (어느 쪽이 틀렸는지 구분하지 않음)

### 내 정보 조회 — `GET /api/v1/members/me`
인증 필요 (`Authorization: Bearer {accessToken}`)

응답 `200 OK`
```json
{ "id": 1, "email": "user1@example.com", "nickname": "유저1", "createDate": "2026-09-25T10:00:00" }
```

실패: 토큰 없음·유효하지 않음 → `401`

### 글 목록 조회 — `GET /api/v1/posts?page=0&size=10`
인증 불필요

응답 `200 OK`
```json
{
  "content": [
    {
      "id": 2,
      "title": "두 번째 글",
      "content": "안녕하세요",
      "authorId": 1,
      "authorNickname": "유저1",
      "createDate": "2026-09-25T18:53:54",
      "modifyDate": "2026-09-25T18:53:54"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 2,
  "totalPages": 1
}
```
`page`는 0부터 시작, `size`는 1~50 사이로 제한됩니다. `id` 내림차순(최신순)으로 정렬됩니다.

> 댓글 수(`commentCount`)는 댓글 API 구현 후 응답에 추가할 예정입니다.

### 글 상세 조회 — `GET /api/v1/posts/{postId}`
인증 불필요

응답 `200 OK`: 목록 항목과 같은 모양

실패: 존재하지 않는 글 id → `404`

### 글 작성 — `POST /api/v1/posts`
인증 필요

요청
```json
{ "title": "첫 글", "content": "안녕하세요" }
```

응답 `201 Created`: 상세 조회와 같은 모양

실패: 토큰 없음 → `401`

### 글 수정 — `PUT /api/v1/posts/{postId}`
인증 필요 (작성자 본인만)

요청
```json
{ "title": "수정된 제목", "content": "수정된 내용" }
```

응답 `200 OK`: 상세 조회와 같은 모양 (`modifyDate` 갱신)

실패: 토큰 없음 → `401`, 존재하지 않는 글 → `404`, 작성자 아님 → `403`

### 글 삭제 — `DELETE /api/v1/posts/{postId}`
인증 필요 (작성자 본인만)

응답: `204 No Content`

실패: 토큰 없음 → `401`, 존재하지 않는 글 → `404`, 작성자 아님 → `403`

삭제 시 해당 글에 달린 댓글도 함께 삭제합니다(`CommentRepository.deleteByPostId`).


### 오류 응답 모양 (모든 오류 동일)
```json
{
  "status": 400,
  "code": "INVALID_INPUT",
  "message": "잘못된 요청입니다.",
  "errors": [{ "field": "password", "message": "비밀번호는 10자 이상 16자 이하여야 합니다." }]
}
```
`errors`는 입력 검증 오류가 아니면 빈 배열입니다.

## 설계 설명

### 로그인 방식: JWT
서버가 상태를 갖지 않는 REST API라 세션 대신 JWT를 선택했습니다. `Authorization: Bearer` 헤더로 토큰을 받고 STATELESS로 운영하며, 서버를 여러 대로 늘려도 세션 공유 문제가 없습니다. 대신 발급한 토큰을 서버에서 즉시 폐기할 수 없어 만료를 30분으로 짧게 잡았고, Refresh Token은 이번 범위에서 제외했습니다.

## N+1 문제 해결

글 목록을 가져올 때, `Post`가 `Member`를 `@ManyToOne(fetch = LAZY)`로 참조하고 있어서 아무 처리 없이 `findAll()`만 쓰면 글마다 작성자를 조회하는 쿼리가 추가로 나갑니다(글이 N개면 작성자 조회도 N번 — N+1).

```java
@EntityGraph(attributePaths = "author")
Page<Post> findAll(Pageable pageable);

@EntityGraph(attributePaths = "author")
Optional<Post> findWithAuthorById(Long id);
```

`@EntityGraph`로 `author`를 함께 가져오도록 지정하면, Hibernate가 이를 `JOIN`으로 바꿔서 글 목록 쿼리 한 번에 작성자까지 같이 조회합니다. 실제로 글 2개 조회 시 콘솔에는 `posts`와 `members`를 조인한 쿼리가 **한 번만** 찍히는 것을 확인했습니다.

```sql
select p1_0.id, p1_0.author_id, a1_0.id, a1_0.email, a1_0.nickname, ...
from posts p1_0
join members a1_0 on a1_0.id = p1_0.author_id
order by p1_0.id desc
offset ? rows fetch first ? rows only
```

글 개수를 늘려도 이 쿼리 수는 그대로 유지되므로(N+1 아님), 작성자 조회에 대해서는 N+1이 발생하지 않습니다.

> 댓글 수 집계는 컬렉션 페치 조인을 페이징과 함께 쓰면 메모리 페이징 문제가 생기기 때문에, `Comment` 쪽에 `post_id IN (...) GROUP BY` 형태의 별도 집계 쿼리로 분리할 예정입니다. (댓글 API 구현 예정)