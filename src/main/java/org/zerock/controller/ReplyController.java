package org.zerock.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.domain.Criteria;
import org.zerock.domain.ReplyPageDTO;
import org.zerock.domain.ReplyVO;
import org.zerock.service.ReplyService;

import lombok.AllArgsConstructor;

@RequestMapping("/replies/")
@RestController
/* @Log4j */

//@Setter 주입 이용하거나 @AllArgsConstructor를 이용해 
//ReplyService 타입의 객체를 필요로하는 생성자 만들어 사용(스프링 4.3 이상)
@AllArgsConstructor
public class ReplyController {

	private ReplyService service;
	
	
	@PostMapping(value = "/new",
			//JSON방식의 데이터만 처리
			consumes = "application/json",
			//문자열을 반환
			produces = { MediaType.TEXT_PLAIN_VALUE})
	//@RequestBody를 적용해 JSON 데이터를 ReplyVO 타입으로 변환하도록 지정
	public ResponseEntity<String> create(@RequestBody ReplyVO vo) {
		System.out.println("ReplyVO: " + vo);
		int insertCount = service.register(vo);
		System.out.println("Reply INSERT COUNT: "+ insertCount);
		//삼항 연산자 처리
		return insertCount ==1 ? new ResponseEntity<>("success", HttpStatus.OK) : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		
	}
	@GetMapping(value = "/pages/{bno}/{page}",
			produces = {MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_JSON_UTF8_VALUE })
	public ResponseEntity<ReplyPageDTO> getList (
			//게시물의 번호는 @PathVariable을 이용해서 파라미터로 처리
			@PathVariable("page")int page,
			@PathVariable("bno") Long bno) {

		//@GetMapping(value = "/pages/{bno}/{page}"의 'page'값은 Criteria를 생성해서 직접 처리해야 함
		Criteria cri = new Criteria(page, 10);
		System.out.println("get Reply List bno: "+ bno);
		System.out.println("cri:" + cri);
		return new ResponseEntity<>(service.getListPage(cri, bno), HttpStatus.OK);
	}
	
	@GetMapping(value = "/{rno}",
			produces = { MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_JSON_UTF8_VALUE } )
	public ResponseEntity<ReplyVO> get(@PathVariable("rno") Long rno) {
		System.out.println("get:" + rno);
		return new ResponseEntity<>(service.get(rno), HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/{rno}", produces = { MediaType.TEXT_PLAIN_VALUE})
	public ResponseEntity<String> remove(@PathVariable("rno") Long rno) {
		System.out.println("remove: " + rno);
		return service.remove(rno) == 1 ? new ResponseEntity<>("success", HttpStatus.OK) : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@RequestMapping(method = { RequestMethod.PUT, RequestMethod.PATCH} , 
			value = "/{rno}",
			consumes = "application/json",
			produces = {MediaType.TEXT_PLAIN_VALUE })
	public ResponseEntity<String> modify (@RequestBody ReplyVO vo, @PathVariable("rno") Long rno) {
		vo.setRno(rno);
		System.out.println("rno: " + rno);
		System.out.println("modify: " + vo);
		return service.modify(vo) == 1 ? new ResponseEntity<>("success", HttpStatus.OK) : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);	
	}
	
}
