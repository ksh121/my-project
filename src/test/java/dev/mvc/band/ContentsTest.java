package dev.mvc.band;

import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import dev.mvc.catego.CategoProcInter;
import dev.mvc.contents.ContentsProcInter;
import dev.mvc.users.UsersProcInter;

@SpringBootTest
public class ContentsTest {
  @Autowired
  @Qualifier("dev.mvc.users.UsersProc") // "dev.mvc.admin.AdminProc"라고 명명된 클래스
  private UsersProcInter usersProc; // AdminProcInter를 구현한 AdminProc 클래스의 객체를 자동으로 생성하여 할당

  @Autowired
  @Qualifier("dev.mvc.catego.CategoProc")  // @Component("dev.mvc.catego.categoProc")
  private CategoProcInter categoProc;
  
  @Autowired
  @Qualifier("dev.mvc.contents.ContentsProc") // @Component("dev.mvc.contents.ContentsProc")
  private ContentsProcInter contentsProc;
  
  @Test
  public void testRead() {
    HashMap<String, Object> hashMap = new HashMap<String, Object>();
    hashMap.put("contentsno", 1);
    hashMap.put("passwd", "1234");
    
    System.out.println("-> cnt: " + this.contentsProc.password_check(hashMap));
    
  }
}