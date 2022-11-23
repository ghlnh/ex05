package org.zerock.controller;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.domain.BoardAttachVO;
import org.zerock.domain.BoardVO;
import org.zerock.domain.Criteria;
import org.zerock.domain.PageDTO;
import org.zerock.service.BoardService;

import jdk.internal.org.jline.utils.Log;
import lombok.AllArgsConstructor;

//@Controller로 스프링의 빈으로 인식 할 수 있게 함
@Controller
/* @Log4j */
//'/board/'로 시작하는 모든 처리를 BoardController가 하도록 지정
@RequestMapping("/board/*")

/*BoardController는 BoardService에 의존적이므로
  @AllArgsConstructor를 이용해서 생성자를 만들고 자동으로 주입하도록 함
(만약 생성자 안 만들면 @Setter(onMethod_ ={ @Autowired }) 이용)  */

@AllArgsConstructor
public class BoardController {

	private BoardService service;
	
	/*
	 * @GetMapping("/list") public void list(Model model) {
	 * System.out.println("list"); //BoardServiceImpl 객체의 getlist()결과를 model에 담아 전달
	 * model.addAttribute("list", service.getlist()); }
	 */
	
	@GetMapping("/list")
	public void list(Criteria cri, Model model) {
		System.out.println("list: "+cri);
		model.addAttribute("list", service.getlist(cri));
		
		int total = service.getTotal(cri);
		System.out.println("total: "+ total);
		
		model.addAttribute("pageMaker", new PageDTO(cri, total));
	}
	
	@PostMapping("/register")
	/* 등록작업 후 , 다시 목록화면으로 이동하고
	 추가적으로 새롭게 등록된 게시물의 번호를 같이 전달하기 위해
	RedirectAttributes 파라미터로 지정 */
	public String register(BoardVO board, RedirectAttributes rttr) {
		System.out.println("==============================");
		System.out.println("register: " + board);
		
		if (board.getAttachList() !=null) {
			board.getAttachList().forEach(attach -> System.out.println(attach));
		}
		
		System.out.println("==============================");
		
		
		  service.register(board); 
		  
		  rttr.addFlashAttribute("result", board.getBno());
		 
		
		//스프링 MVC가 내부적으로 response.sendRedirect()처리해주기 위해서 "redirect:" 사용
		return "redirect:/board/list";
	}
	
	@GetMapping({"/get", "/modify"})
	//bno값을 좀 더 명시적으로 처리하기 위해 @RequestParam을 이용해서 지정.(생략해도 ok)
	//화면쪽으로 해당 번호의 게시물을 전달해야 하므로 Model을 파라미터로 지정
	public void get(@RequestParam("bno") Long bno, Model model, @ModelAttribute("cri") Criteria cri) {
		System.out.println("/get or modify");
		model.addAttribute("board", service.get(bno));
	}
	
	@PostMapping("/modify")
	public String modify(BoardVO board, RedirectAttributes rttr, @ModelAttribute("cri") Criteria cri) {
		System.out.println("modify: " + board);
		
		//service.modify()는 수정 여부를 boolean으로 처리하므로 이를 이용해서 성공한 경우
		//RedirectAttributes에 추가
		if(service.modify(board)) {
			rttr.addFlashAttribute("result", "success");
		}
		/*
		 * rttr.addAttribute("pageNum", cri.getPageNum()); rttr.addAttribute("amout",
		 * cri.getAmount()); rttr.addAttribute("type", cri.getType());
		 * rttr.addAttribute("keyword", cri.getKeyword());
		 */
		return "redirect:/board/list" + cri.getListLink();
	}
	
	@PostMapping("/remove")
	public String remove(@RequestParam("bno") Long bno
							,RedirectAttributes rttr
							,Criteria cri) {
		System.out.println("remove........" + bno);
		
		List<BoardAttachVO> attachList = service.getAttachList(bno);
		
		if(service.remove(bno)) {
			
			//delete Attach Files
			deleteFiles(attachList);
			
			rttr.addFlashAttribute("result", "success");
			/*
			 * rttr.addAttribute("pageNum", cri.getPageNum()); rttr.addAttribute("amout",
			 * cri.getAmount()); rttr.addAttribute("type", cri.getType());
			 * rttr.addAttribute("keyword", cri.getKeyword());
			 */
		}
		return "redirect:/board/list" + cri.getListLink();
	}
	
	@GetMapping("/register")
	//register()는 입력페이지를 보여주는 역할만함
	public void register() {
		
	}
	
	@GetMapping(value = "/getAttachList",
			produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public ResponseEntity<List<BoardAttachVO>> getAttachList(Long bno) {
		System.out.println("getAttachList" + bno);
		return new ResponseEntity<List<BoardAttachVO>>(service.getAttachList(bno), HttpStatus.OK);
	}
	
	private void deleteFiles(List<BoardAttachVO> attachList) {
		
		if(attachList == null || attachList.size() ==0) {
			return;
		}
		
		System.out.println("delete attach files..............");
		
		System.out.println(attachList);
		
		attachList.forEach(attach -> {
			try {
				Path file = Paths.get("C:\\upload\\" +attach.getUploadPath()+ "\\" + attach.getUuid()+ "_" + attach.getFileName());
				
				Files.deleteIfExists(file);
				
				if(Files.probeContentType(file).startsWith("image")) {
					
					Path thumbNail = Paths.get("C:\\upload\\" + attach.getUploadPath()+ "\\s_" + attach.getUuid() + "_" + attach.getFileName());
					
					Files.delete(thumbNail);
				}
			} catch (Exception e) {
				Log.error("delete file error" + e.getMessage());
			}//end catch
		});//end foreachd
	}
	
}
//org.zerock.controller 패키지는 servlet-context.xml에 기본으로 설정되어 있어 별도의 설정 필요X