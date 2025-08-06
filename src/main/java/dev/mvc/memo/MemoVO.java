package dev.mvc.memo;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemoVO {
  private int memono; 
  private String title;         // 메모 번호
  private String content;     // 메모 내용
  private Date rdate;         // 등록일
  private int userno;         // 작성자 번호
}
