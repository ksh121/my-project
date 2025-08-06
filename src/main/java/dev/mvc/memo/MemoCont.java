package dev.mvc.memo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequestMapping("/memo")
public class MemoCont {

  @Autowired
  @Qualifier("dev.mvc.memo.MemoProc")  // 명시적으로 MemoProc Bean을 사용하도록 지정
  private MemoProcInter memoProc;

  @GetMapping("/create")
  public String createForm() {
    return "memo/create";  // 메모 작성 페이지로 이동
  }

  @PostMapping("/create")
  public String create(@ModelAttribute MemoVO memoVO, 
                           @SessionAttribute("userno") int userno, Model model) {
    // userno, contentsno 세션과 요청에서 가져오기
    memoVO.setUserno(userno);

    int cnt = memoProc.create(memoVO);
    if (cnt == 1) {
      return "redirect:/memo/list";
    } else {
      return "memo/create";  // 실패 시 다시 작성 폼
    }
  }

  @GetMapping("/list")
  public String list(Model model) {
    model.addAttribute("memoList", memoProc.list_all());  // 메모 목록 조회
    return "memo/list";  // 메모 목록 페이지로 이동
  }

  @GetMapping("/read")
  public String read(@RequestParam(name = "memono", defaultValue = "0") int memono, Model model) {
    MemoVO memoVO = memoProc.read(memono);  // 메모 상세 조회
    model.addAttribute("memoVO", memoVO);
    return "memo/read";  // 메모 상세 페이지로 이동
  }

  @GetMapping("/update")
  public String updateForm(@RequestParam(name = "memono", defaultValue = "0") int memono, Model model) {
    MemoVO memoVO = memoProc.read(memono);  // 수정 폼에 기존 데이터 전달
    model.addAttribute("memoVO", memoVO);
    return "memo/update";  // 메모 수정 페이지로 이동
  }

  @PostMapping("/update")
  public String update(@ModelAttribute MemoVO memoVO) {
    int cnt = memoProc.update(memoVO);  // 제목 + 내용 수정
    if (cnt == 1) {
      return "redirect:/memo/read?memono=" + memoVO.getMemono();  // 수정 성공 시 상세 페이지 이동
    } else {
      return "memo/update";  // 실패 시 다시 수정 폼
    }
  }

  @GetMapping("/delete")
  public String delete(@RequestParam(name = "memono", defaultValue = "0") int memono, Model model) {
    MemoVO memoVO = memoProc.read(memono);  // 삭제할 메모 조회
    model.addAttribute("memoVO", memoVO);
    return "memo/delete";  // 삭제 확인 페이지로 이동
  }
  
  @PostMapping("/delete")
  public String delete_proc(@RequestParam(name = "memono", defaultValue = "0") int memono) {
    int cnt = memoProc.delete(memono);  // 메모 삭제
    return "redirect:/memo/list";  // 삭제 후 목록 이동
  }

}
