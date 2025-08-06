package dev.mvc.band;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import dev.mvc.catego.CategoProcInter;
import dev.mvc.catego.CategoVO;
import dev.mvc.catego.CategoVOMenu;
import dev.mvc.tool.Security;

@Controller
public class HomeCont {  
  @Autowired
  @Qualifier("dev.mvc.catego.CategoProc")
  private CategoProcInter categoProc; // CateProc class 객체가 생성되어 할당
  
  @Autowired
  private Security security;
  
  public HomeCont() {
    System.out.println("-> HomeCnont created.");
  }
  
  // http://localhost:9091
  // http://localhost:9091/index.do
  @GetMapping(value={"/", "/index.do"}) 
  public String home(Model model) {
//    ArrayList<CateVO> menu = this.cateProc.list_all_categrp_y();
//    model.addAttribute("menu", menu);
    
    ArrayList<CategoVOMenu> menu = this.categoProc.menu();
    model.addAttribute("menu", menu);
    
    model.addAttribute("word", ""); // 시작페이지는 검색을 하지 않음
    
    return "index";  // /templates/index.html
  }
  
}