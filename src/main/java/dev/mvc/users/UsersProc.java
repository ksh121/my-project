package dev.mvc.users;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.mvc.tool.Security;
 
@Component("dev.mvc.users.UsersProc")
public class UsersProc implements UsersProcInter {
  
  @Autowired
  private UsersDAOInter usersDAO;
  
  @Autowired
  private Security security;
  
  public UsersProc(){
    // System.out.println("-> usersProc created.");
  }

  @Override
  public int checkID(String id) {
    int cnt = this.usersDAO.checkID(id);
    return cnt;
  }

//  @Override
//  public int create(UsersVO usersVO) {
//    int cnt = this.usersDAO.create(usersVO);
//    return cnt;
//  }
  
  @Override
  public int create(UsersVO usersVO) {
    String passwd = usersVO.getPasswd();
//    Security security = new Security(); // dev.mvc.tool
//    String passwd_encoded = security.aesEncode(passwd); // 암호화
    String passwd_encoded = this.security.aesEncode(passwd);
    usersVO.setPasswd(passwd_encoded); // 암호화된 패스워드 저장
    
    // usersVO.setPasswd(new Security().aesEncode(usersVO.getPasswd())); // 단축형
    
    int cnt = this.usersDAO.create(usersVO); // 회원가입  
    return cnt;
  }
 
  @Override
  public ArrayList<UsersVO> list() {
    ArrayList<UsersVO> list = this.usersDAO.list();
    return list;
  }
  
  @Override
  public UsersVO read(int userno) {
    UsersVO usersVO = this.usersDAO.read(userno);
    return usersVO;
  }

  @Override
  public UsersVO readById(String id) {
    UsersVO usersVO = this.usersDAO.readById(id);
    return usersVO;
  }

  @Override
  public boolean isUsers(HttpSession session){
    boolean sw = false; // 로그인하지 않은 것으로 초기화
    
    if(session.getAttribute("grade") != null) {
      if (((String)session.getAttribute("grade")).equals("admin") ||
       ((String)session.getAttribute("grade")).equals("users")) {
        sw = true;
      }
    }
   
    return sw;
  }

  @Override
  public boolean isAdmin(HttpSession session){
    boolean sw = false; // 로그인하지 않은 것으로 초기화
    
     if(session.getAttribute("grade") != null) {
       
      if (((String)session.getAttribute("grade")).equals("admin")) {
        sw = true;
      }
     }
    
    return sw;
  }
  
  @Override
  public int update(UsersVO usersVO) {
    int cnt = this.usersDAO.update(usersVO);
    return cnt;
  }
  
  @Override
  public int delete(int userno) {
    int cnt = this.usersDAO.delete(userno);
    return cnt;
  }
  
//  @Override
//  public int passwd_check(HashMap<String, Object> map) {
//    int cnt = this.usersDAO.passwd_check(map);
//    return cnt;
//  }
//
//  @Override
//  public int passwd_update(HashMap<String, Object> map) {
//    int cnt = this.usersDAO.passwd_update(map);
//    return cnt;
//  }
//  
//  @Override
//  public int login(HashMap<String, Object> map) {
//    int cnt = this.usersDAO.login(map);
//    return cnt;
//  }
  
  @Override
  public int login(HashMap<String, Object> map) {
    String passwd = (String)map.get("passwd");
    map.put("passwd", new Security().aesEncode(passwd));
    int cnt = this.usersDAO.login(map);
    
    return cnt;
  }

  @Override
  public int passwd_check(HashMap<String, Object> map) {
    String passwd = (String)map.get("passwd");
    map.put("passwd", new Security().aesEncode(passwd));    
    int cnt = this.usersDAO.passwd_check(map);
    return cnt;
  }

  @Override
  public int passwd_update(HashMap<String, Object> map) {
    String passwd = (String)map.get("passwd");
    map.put("passwd", new Security().aesEncode(passwd));     
    int cnt = this.usersDAO.passwd_update(map);
    return cnt;
  }
  
  
  
}