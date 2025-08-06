package dev.mvc.band;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import dev.mvc.catego.CategoDAOInter;
import dev.mvc.catego.CategoProcInter;
import dev.mvc.catego.CategoVO;

@SpringBootTest
class BandApplicationTests {
  // CateDAO interface 객체를 만들 수 없으나, 
  // Spring이 CateDAOInterface를 자동으로 구현하여 
  // 객체를 생성하여 cateDAO 변수에 할당함
  @Autowired // CateDAOInter를 구현한 클래스의 객체를 생성하여 cateDAO 변수에 할당
  private CategoDAOInter categoDAO;
  
  @Autowired // Spring이 CateProcInter를 구현한 CateProc 클래스의 객체를 생성하여 할당
  @Qualifier("dev.mvc.catego.CategoProc")
  private CategoProcInter categoProc;

    @Test
    void contextLoads() {
    }
    @Test // 자동 실행
    public void testCreate() {
      CategoVO catego = new CategoVO();
      
      catego.setCategono(1);
      catego.setName("합주");
      catego.setTitle("Oddities");
      catego.setArtist("The Poles");
      catego.setDifficulty("어려움");
      catego.setVisible("N");
      catego.setRdate("2025-03-18");
      
     int cnt = this.categoDAO.create(catego);
     System.out.println("-> : " + cnt);
      
    }

}