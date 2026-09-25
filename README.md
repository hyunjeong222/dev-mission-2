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