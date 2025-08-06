package dev.mvc.catego;

import java.util.ArrayList;
import java.util.Map;

import dev.mvc.catego.CategoVOMenu;
import dev.mvc.catego.CategoVO;

// MyBATIS 기준으로 추상 메소드를 만들면 Spring Boot가 자동으로 class로 구현함.
public interface CategoDAOInter {
  /**
   * <pre>
   * MyBATIS: <insert id="create" parameterType="dev.mvc.catego.CategoVO">
   * insert: INSERT SQL, 처리후 등록된 레코드 갯수를 리턴
   * id: 자바 메소드명
   * parameterType: MyBATIS가 전달받는 VO 객체 타입
   * </pre>
   * @param CategoVO
   * @return 등록된 레코드 갯수
   */
  public int create(CategoVO CategoVO);
  
  /**
   * 전체 목록
   * @return
   */
  public ArrayList<CategoVO> list_all();
  
  /**
   * 조회
   * @param categono
   * @return
   */
  public CategoVO read(int categono);
  
  /**
   * 수정
   * @param categono
   * @return
   */
  public int update(CategoVO categoVO);
  
  /**
   * 삭제 처리
   * delete id="delete" parameterType="Integer"
   * @param int
   * @return 삭제된 레코드 갯수
   */
  public int delete(int categono);
  
  /**
   * 우선순위 높임 10등 -> 1등
   * @param categono
   * @return
   */
  public int update_seqno_forward(int categono);
  
  /**
   * 우선순위 낮춤 1등 -> 10등
   * @param categono
   * @return
   */
  public int update_seqno_backward(int categono);
    
  /** 카테고리 공개 설정
   * @param categono
   * @return
   */
  public int update_visible_y(int categono);
  
  /**
   * 카테고리 비공개 설정
   * @param categono
   * @return
   */
  public int update_visible_n(int categono);
  
  /**
   * 공개된 대분류만 출력
   * @param categoVO
   * @return
   */
  public ArrayList<CategoVO> list_all_grp_y();
  
  /**
   * 특정 그룹의 중분류 출력
   * @param categoVO
   * @return
   */
  public ArrayList<CategoVO> list_all_name_y(String name);
  
  /**
   * 화면 상단 메뉴
   * @return
   */
  public ArrayList<CategoVOMenu> menu();
  
  /**
   * 카테고리 그룹 목록
   * @return
   */
  public ArrayList<String> grpset();
  
  /**
   * 검색, 전체 목록
   * @return
   */
  public ArrayList<CategoVO> list_search(String word);
  
  /**
   * 검색, 전체 레코드 갯수
   * @return
   */
  public int list_search_count(String word); 
  
  /**
   * 검색, 전체 목록
   * @param map
   * @return
   */
  public ArrayList<CategoVO> list_search_paging(Map map);


  public int updateCntByCategono(int categono);
  public int updateCntByName(String name);



  
}
