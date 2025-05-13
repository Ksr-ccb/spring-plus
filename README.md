# 💡 [Spring 6기] 플러스 주차 개인 과제

### 🕰️ 개발 기간
04/30(수) ~ 05/15(목)

## ✏️과제 내용
### <필수> 
### Level. 코드 개선 퀴즈
 - @Transactional의 이해
 - JWT의 이해
 - JPA의 이해
 - 테스트 코드 퀴즈 - 컨트롤러 테스트의 이해
 - AOP의 이해
### Level. 2  
 - JPA Cascade
 - N+1
 - QueryDSL
 - Spring Security

### <도전>
###  Level 3 
 - QueryDSL 을 사용하여 검색 기능 만들기
 - Transaction 심화
 - AWS 활용 (비용이 나가서 중단했습니다)
![image](https://github.com/user-attachments/assets/a3f44b60-1cc9-417f-a83e-a49dcd669b15)
![image](https://velog.velcdn.com/images/tofha054/post/67b5c24b-8243-4888-9df9-54a438f29b70/image.png)
![image](https://velog.velcdn.com/images/tofha054/post/9a897f07-20d2-462e-b3d0-b26a0e5b3217/image.png)
![image](https://velog.velcdn.com/images/tofha054/post/cccb1681-34c5-4223-97fb-6276b5ded53d/image.png)
 - 대용량 데이터 처리


<br><hr><br>
# 📚 과제 풀이 과정
## 필수 Lv.1
### 1. @Transactional의 이해
@transactional(readOnly = true) 클래스에 붙이면 해당 클래스 내 모든 public 메서드에 대해 쓰기(INSERT, UPDATE, DELETE) 작업은 제한되며
연산을 수행해도 하이버네이트가 반영을 하지 않거나, 에러가 나는 경우도 있기 때문에 @transactional은 메서드마다 부여하는 것이 맞다고 생각해서 수정함.
![image](https://github.com/user-attachments/assets/b15437f9-3310-448b-8805-6c0d62682133)
![image](https://github.com/user-attachments/assets/19c29940-3fc0-4c60-881c-6a4846b1921c)

### 3. JPA의 이해
조건이 1.날씨, 2.수정일 시작, 3.수정일 끝 총 3가지에 모두가 옵셔널하기때문에 총 8개의 경우의 수가 나와서 동적 쿼리 생성하는 방향으로 수정함.
![image](https://github.com/user-attachments/assets/770b0219-3066-4d34-90dd-94a32bfe4ff2)
https://velog.io/@tofha054/Spring-JPQL-동적-쿼리-생성하기

### 4. 컨트롤러 테스트
테스트 코드의 then 부분을 Bad Request 반환 형태랑 맞도록 수정
![image](https://github.com/user-attachments/assets/35fc7b5c-ab9f-4379-8d8d-19c9cdf9c5a4)

### 5. AOP
UserAdminController 클래스의 changeUserRole() 메소드가 실행 전 동작하기 위해 어노테이션과 target 경로 변경
![image](https://github.com/user-attachments/assets/79fdd0b0-73ec-41be-9894-498be31cc18c)
