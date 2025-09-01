# Spring Advanced Troubleshooting Guide

이 문서는 Spring 애플리케이션 개발 과정에서 자주 발생하는 문제와 해결 방법을 레벨별로 정리한 가이드입니다.

---

## Level 0: 의존성 주입 실패

### 원인
`FilterConfig`가 `JwtUtil` 빈을 생성자 주입받는 과정에서 실패합니다.  
`JwtUtil` 내부에서 `@Value("${jwt.secret.key}")`로 설정값을 주입받는데, `application.yml` 또는 `application.properties`에 `jwt.secret.key`가 없으면 null이 되어 초기화 과정에서 예외가 발생합니다.

### 해결 방법
`application.yml` 또는 `application.properties`에 JWT 시크릿 키를 설정합니다.

```yaml
jwt:
  secret:
    key: "임의의_보안키_설정"
```
---
## Level 1: HandlerMethodArgumentResolver 등록 누락

### 문제 상황
컨트롤러에서 `@AuthUser` 같은 커스텀 아규먼트 리졸버를 사용했지만, 요청 처리 시 예상대로 동작하지 않습니다.  
컨트롤러에서 `AuthUserArgumentResolver`를 통해 인증된 사용자 정보를 주입하려고 할 때 Null 값이 들어오거나 예외가 발생할 수 있습니다.

### 원인
- `AuthUserArgumentResolver`를 생성했지만 Spring MVC에 등록하지 않음
- `HandlerMethodArgumentResolver`는 Spring MVC가 자동으로 인식하지 않으므로 `WebMvcConfigurer`에 직접 등록해야 함

### 증상
- 컨트롤러 메서드에서 커스텀 아규먼트가 정상적으로 주입되지 않음
- NullPointerException 발생 가능
- 테스트에서 커스텀 아규먼트 관련 기능 실패

### 해결 방법
`WebMvcConfigurer`를 구현하여 아규먼트 리졸버를 등록합니다.

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthUserArgumentResolver());
    }
}
```
---
## Level 2: 서비스 로직 개선

### 문제 상황
회원 가입, 업데이트 등 서비스 로직에서 불필요한 연산이나 복잡한 조건문으로 인해 코드 가독성이 떨어지고 성능이 저하될 수 있습니다.

### 1. Early Return
- **원인**: 이미 존재하는 이메일일 경우 예외가 발생하면 `passwordEncoder` 연산은 필요하지 않습니다.
- **증상**: 불필요한 암호화 연산 수행, 코드 가독성 저하
- **해결 방법**: 조건 검사를 encode 이전에 수행하여 불필요한 연산을 방지합니다.

```java
if (userRepository.existsByEmail(email)) {
    throw new DuplicateEmailException("이미 존재하는 이메일입니다.");
}
String encodedPassword = passwordEncoder.encode(password);
```
---
### 2. 불필요한 if-else 제거

**문제 상황**  
서비스 로직에서 조건문 안에 또 다른 if문이 중첩되어 코드가 복잡합니다.

**원인**
- else 블록 내부에 추가적인 if문을 사용하여 가독성이 떨어짐
- 유지보수가 어려움

**증상**
- 코드 흐름 이해가 어려움
- 조건이 많아질수록 버그 발생 가능성 증가

**해결 방법**
- else를 제거하고 조건별로 독립적인 if문을 사용
---
### 3. 입력값 검증 (Validation)

### 문제 상황
서비스 단에서 입력값 검증을 수행하면 비즈니스 로직과 검증 로직이 혼재되어 코드가 복잡해집니다.

### 원인
- 서비스 단에서 이메일, 비밀번호 등 입력값을 직접 검증함
- DTO와 서비스가 역할이 섞여 유지보수가 어려움

### 증상
- 코드 가독성 저하
- 테스트 작성이 복잡해짐
- 입력 검증 로직 변경 시 서비스 코드까지 수정 필요

### 해결 방법
- DTO 단에서 먼저 입력값을 검증하고, 서비스 단에서는 비즈니스 로직에 집중합니다.

```java
@Data
public class UserRequest {
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
```
---
## Level 3: N+1 문제 해결

### 문제 상황
`Todo`와 연관된 `User`를 조회할 때 `fetch = LAZY` 설정으로 인해 반복 쿼리가 발생할 수 있습니다.  
대량 데이터 조회 시 성능 저하와 쿼리 지연 문제가 나타납니다.

### 원인
- 연관 엔티티가 LAZY로 설정되어 필요할 때마다 별도의 쿼리를 수행
- 반복 조회로 인해 N+1 문제가 발생

### 증상
- 페이지 조회 시 불필요한 쿼리가 다수 발생
- 성능 저하 및 DB 부하 증가
- 애플리케이션 응답 속도 느려짐

### 해결 방법
`@EntityGraph`를 활용하여 한 번의 쿼리로 연관 데이터를 함께 조회합니다.

```java
@EntityGraph(attributePaths = {"user"})
Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);
```
---
## Level 4: 테스트 및 예외 처리 개선

### Case 1: matches 메서드 인자 순서 오류

**문제 상황**  
테스트 코드에서 matches 메서드를 사용했지만, 인자 순서가 잘못되어 단위 테스트가 실패합니다.

**원인**
- matches 메서드 인자 순서가 잘못됨

**증상**
- 단위 테스트 실패
- 잘못된 검증 로직 수행

**해결 방법**
- 올바른 인자 순서로 수정하여 테스트를 통과시킴

```java
// 올바른 인자 순서 예시
assertTrue(Pattern.matches(expectedPattern, actualString));
```
---
### Case 2: Todo 없음 시 발생하는 예외

**문제 상황**  
Todo가 존재하지 않을 때 발생하는 예외가 NPE가 아니라 다른 예외이며, 메시지가 예상과 다릅니다.

**원인**
- Todo가 없는 경우 NPE가 아닌 다른 예외가 발생함

**증상**
- 테스트 실패
- 예상하지 못한 예외 발생

**해결 방법**
- 테스트 코드에서 정확한 예외 타입과 메시지를 검증하여 일관성 유지

```java
@Test
public void manager_목록_조회_시_Todo가_없다면_Invalid_에러를_던진다() {
    // given
    long todoId = 1L;
    given(todoRepository.findById(todoId)).willReturn(Optional.empty());

    // when & then
    InvalidRequestException exception = assertThrows(
        InvalidRequestException.class,
        () -> managerService.getManagers(todoId)
    );
    assertEquals("Todo not found", exception.getMessage());
}
```
---
### Case 3: 서비스 단 null 처리 누락

**문제 상황**  
서비스 단에서 `Todo`의 `user`가 `null`일 경우, NPE가 먼저 발생하여 원하는 예외가 던져지지 않습니다.

**원인**
- null 체크 누락

**증상**
- 잘못된 예외 발생
- 서비스 로직 불안정

**해결 방법**
- 조건문에서 null 체크를 추가하여 안전하게 예외 처리

```java
if (todo.getUser() == null || !ObjectUtils.nullSafeEquals(user.getId(), todo.getUser().getId())) {
    throw new InvalidRequestException("일정을 생성한 유저만 담당자를 지정할 수 있습니다.");
}