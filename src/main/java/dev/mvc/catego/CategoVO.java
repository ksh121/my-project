package dev.mvc.catego;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

//CREATE TABLE catego(
//    CATEGONO                            NUMBER(10)     NOT NULL     PRIMARY KEY,  
//    NAME                              VARCHAR2(30)  NOT NULL,
//    title                               VARCHAR(30)       NOT NULL,
//    artist                              VARCHAR(30),
//    difficulty                             VARCHAR(10)     ,
//    VISIBLE                           CHAR(1)      DEFAULT 'N'    NOT NULL,
//    RDATE                             DATE          NOT NULL
//);
@Setter @Getter @ToString
public class CategoVO {
  /** 카테고리 번호, Sequence에서 자동 생성 */  
  private Integer categono=0;

  /** 카테고리 이름 */
  @NotEmpty(message="카테고리 입력은 필수 항목입니다.")
  @Size(min=2, max=10, message="카테고리 이름은 최소 2자에서 최대 10자입니다.")
  private String name;
  
  /** 곡 제목 */
  @NotNull(message="곡 제목은 필수 항목입니다")
  @Size(min=1, max = 50, message="곡명은 최소 1자에서 최대 50자입니다.")
  private String title;
  
  /** 아티스트명 */
  //@NotEmpty(message="은 필수 항목입니다.")
  @Size(min=1, max= 50, message="아티스트명은 최소 1자에서 50자입니다.")
  private String artist;
  
  /** 아티스트명 */
  //@NotEmpty(message="은 필수 항목입니다.")
  @Size(min=1, message="난이도는 최소 1자입니다.")
  private String difficulty;
  
  /** 관련 자료수 */
  @NotNull(message="관련 자료수는 필수 입력 항목입니다.")
  @Min(value=0)
  @Max(value=1000000)
  private Integer cnt=0;
  
  /** 출력 순서 */  
  private Integer seqno = 1;
  
  /** 출력 모드 */
  @NotEmpty(message="출력 모드는 필수 항목입니다.")
  @Pattern(regexp="^[YN]$", message="Y 또는 N만 입력 가능합니다.")
  private String visible = "N";
  
  /** 등록일, sysdate 자동 생성 */
  private String rdate = "";

//  @Override
//  public String toString() {
//    return "CateVO [cateno=" + cateno + ", name=" + name + ", cnt=" + cnt + ", seqno=" + seqno + ", visible=" + visible
//        + ", rdate=" + rdate + "]";
//  }
  
  // CateVO(cateno=1, name=캠핑, cnt=1, seqno=1, visible=N, rdate=2024-09-03 12:15:57)
    
}