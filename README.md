# jpa-association
# 1단계 - OneToMany (FetchType.EAGER)
- 미션의 목표
  - `연관 관계` 에 대한 신규 요구사항을 1단계 Query Builder 에 추가
  - 클린 코드를 유지 하면서 확장 가능 하도록 재구조화
- 요구 사항 1 - Select Join Query 만들기 (EAGER)
  - Sql 쿼리 문을 수정해 보자
- 요구 사항 2 - Join Query 를 만들어 Entity 화 해보기
  - FetchType.EAGER 인 경우
- 요구 사항 3 - Save 시 Insert Query
  - 연관 관계에 대한 데이터 저장 시 쿼리 만들어 보기
  - 부모 데이터가 있는 경우, 부모 데이터가 없는 경우 나누어서 구현

### 구현 기능 목록
- [ ] 연관관계에 따른 create query
- [ ] 연관관계가 있는 엔티티 조회를 위한 select join query
- [ ] 데이터베이스로부터 조회한 
