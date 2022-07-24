# Spring Audit Trail

## Ready

- 트랜잭션 내에서 동작하므로 선언적 트랜잭션(AOP Transaction)을 설정합니다.

## Install
Add libarary to build path.
```
./build/libs/audit-0.0.1-plain.jar'
```

## Configuration (Required)
다음과 같이 Spring Context에 AuditAdvisorConfig를 등록하면 설정이 완료됩니다.
```java
import org.springframework.context.annotation.Configuration;
import spring.lims.audit.config.AuditAdvisorConfig;

@Configuration
public class AuditConfig extends AuditAdvisorConfig {
}
```

<br/>

> ### Custom Configuration (Optional)
기본값 대신 다른 설정을 적용하고자 할 때, 다음과 같이 AuditConfigurer 인터페이스를 구현하여 설정을 변경할 수 있습니다.

```java
@Configuration
public class AuditTrailConfigurer implements AuditConfigurer {

    @Override
    public RecordScope recordScope() {
        return RecordScope.TRANSACTION;
    }

    @Override
    public DisplayType displayType() {
        return DisplayType.COMMENT;
    }

    @Override
    public StringConvertCase convertCase() {
        return StringConvertCase.CAMEL_TO_SNAKE;
    }

    @Override
    public DatabaseType databaseType() {
        return DatabaseType.ORACLE;
    }
}
```

- #### RecordScope
  + <b>TRANSANCTION</b> (기본값) : 변경 전 상태와 최종 변경 상태에 대한 이력을 동일한 ID값을 기준으로 제공합니다. 이 옵션일 때, AuditEventListener에서 구독하는 AuditTrail은 마지막으로 변경이 일어난 쿼리의 Audit 데이터를 제공합니다.
  + <b>EACH</b> : 수행되는 모든 변경에 대해 이력을 동일한 ID값을 기준으로 제공합니다.

- #### DisplayType
  + <b>COMMENT</b> (기본값) : 제공되는 변경 이력에 컬럼의 코멘트를 보여줍니다. 코멘트 정보가 없다면 컬럼명이 출력됩니다.
  + <b>COLUMN</b> : 제공되는 변경 이력에 컬럼명을 보여줍니다.

- #### StringConvertCase
  + <b>CAMEL_TO_SNAKE</b> (기본값) : Java Field가 camel case, DB column이 snake case일 때 사용합니다.  
  + <b>SNAKE_TO_CAMEL</b>
  + <b>NONE</b> : Case 변환 작업을 하지 않습니다.
- #### DatabaseType
  + <b>ORACLE</b> (기본값) 

<br/>

※ @AuditId가 userId인 테이블이 있고, userId가 '1'인 사용자와 '2'인 사용자의 정보가 동일한 트랜잭션 내에서 변경될 때, 각각 id를 기준으로 이력을 추적합니다. 즉, userId가 '1'인 사용자의 이력과 '2'인 사용자의 이력이 각각 추적됩니다.

## Usage

> ### Define Audit Entity
```java
@AuditEntity(name = "Member")
public class Member {
  @AuditId
  private Integer userId;
  @AuditId
  private Integer userSeq;
  private String loginId;
  private String userNm;
  private LocalDate lastModifiedDate;
  private String lastModifiedUserId;
}
```
- @AuditEntity( name: 테이블명 )
  + 테이블과 1:1로 매핑되는 객체이며 여기에 정의된 필드를 기준으로 Audit 변경 이력이 작성됩니다.
- @Audit
  + 어노테이션을 통해 해당 테이블의 식별키를 명시합니다. <br/> @Audit 쿼리가 실행 될 때 넘긴 파라미터에서 AuditId 필드의 값을 매핑한 뒤 해당 Id 값으로 변경되는 이력을 추적합니다.

<br/>

> ### Declaritive Audit
```java
@Audit(target = Member.class)
int updateMember(MemberDto dto);
```
- @Audit(target : Class<?>, label : String, title : String) <br/>
  + 변경 이력을 구독할 repository interface에 선언합니다. <br/>
  위 예시를 보면 Entity.class 내 @AuditId로 선언된 필드를 파라미터 dto에서 찾아 해당 값을 기준으로 변경 이력을 저장합니다.

<br/>

> ### Subscribe Transaction Event
```java
@Configuration
public class AuditTrailEventListener implements AuditEventListener {

    @Override
    public void beforeCommit(List<AuditTrail> auditTrails) {
        auditTrails.forEach(audit -> {
            CommandType commandType = audit.getCommandType();
            String content = audit.getContent();
            String label = audit.getLabel();
            String title = audit.getTitle();
            Map<String, Object> id = audit.getId();
            Map<String, Object> param = audit.getParam();
        });
    }

}
```

@Audit을 명시한 쿼리가 포함된 트랜잭션을 구독하는 Listener입니다. <br/> 
트랜잭션이 종료되기 전, 후에 맞게 Audit 이력 객체를 통해 원하는 작업을 처리할 수 있습니다.

- #### AuditEventLister
| Method       | Description                                                                         |
|:-------------|:------------------------------------------------------------------------------------|
| beforeCommit | Audit이 수행되는 트랜잭션이 커밋되기 전에 호출됩니다. Unchecked 예외가 발생하면 rollback됩니다.
| afterCommit  | Audit이 수행되는 트랜잭션이 커밋된 후 호출됩니다. 예외가 발생해도 rollback되지 않습니다.


- #### AuditTrail

RecordScope 설정값이 TRANSACTION이라면 가장 마지막에 변경이 일어난 @Audit 데이터를 제공합니다.

| Property    | Type  | Description                                                                  |                                                          |
|:------------|:-------|:----------------------------------------------------------------------------|:---------------------------------------------------------|
| CommandType | Enum  | 수행된 작업의 구분값입니다.                                                      | INSERT, UPDTE, DELETE                                    
| Content     | String| 변경되기 이전값과 이후값을 비교하여 생성된 문자열입니다. 여러 행 출력 시 ','로 구분됩니다. | { 제목: \`테스트 제목\` -> \`시험기록부\`, 작성자: \`테스트\` -> \`관리자\` } 
| Label       | String| @Audit 어노테이션에서 부여한 label값입니다.                                       
| Title       | String| @Audit 어노테이션에서 부여한 title값입니다.                                     
| Id          | Map   | @Audit이 선언된 쿼리가 수행될 때 사용한 Parameter에서 @AuditId에 해당하는 정보입니다.       
| Param       | Map   | @Audit이 선언된 쿼리가 수행될 때 사용한 Parameter 정보입니다. 


## supports
- mybatis
