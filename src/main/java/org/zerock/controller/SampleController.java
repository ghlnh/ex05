package org.zerock.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.print.attribute.HashAttributeSet;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.domain.SampleVO;
import org.zerock.domain.Ticket;

@RestController
@RequestMapping("/sample")

public class SampleController {

	//produces 속성은 해당 메서드가 생성하는 MIME 타입을 의미
	@GetMapping(value = "/getText", produces = "text/plain; charset =UTF-8")
	public String getText() {
		System.out.println("MIME TYPE: " + MediaType.TEXT_PLAIN_VALUE);
		
		return "안녕하세용~";
	}
	
	@GetMapping(value = "getSample",
			produces = { MediaType.APPLICATION_JSON_UTF8_VALUE,
						MediaType.APPLICATION_XML_VALUE})
	public SampleVO getSample() {
		return new SampleVO (112, "스타", "로드");
	}
	
	@GetMapping(value = "getList")
	public List<SampleVO> getList() {
				//1~10미만 까지의 루프를 처리하면서 SampleVO 객체 생성
		return IntStream.range(1, 10).mapToObj(i -> new SampleVO(i , i+ "First", i +"Last"))
				//List<SampleVO>로 만듦
				.collect(Collectors.toList());
	}
	
	@GetMapping(value = "/getMap")
	public Map<String, SampleVO> getMap() {
		Map<String, SampleVO> map = new HashMap<String, SampleVO>();
		map.put("First", new SampleVO(111, "그루트", "주니어"));
		return map;
	}
	
	@GetMapping(value = "/check", params = { "height", "weight"})
	public ResponseEntity<SampleVO> check(Double height, Double weight) {
		SampleVO vo = new SampleVO(0, "" + height, "" + weight);
		
		ResponseEntity<SampleVO> result = null;
		
		if (height < 150) {
			result = ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(vo);
		}else {
			result = ResponseEntity.status(HttpStatus.OK).body(vo);
		}
		return result;
	}
	//@PathVariavle을 적용하고 싶은 경우에는 {}를 이용해서 변수명 지정하고, 
	@GetMapping("/product/{cat}/{pid}")
	public String[] getPath(
		//@PathVariable을 이용해서 지정된 이름의 변숫값 얻을 수 있음
		@PathVariable("cat") String cat,
		@PathVariable("pid") Integer pid) {
		return new String[] { "category: " + cat, "productid: " + pid };
	}
	
	//@RequestBody가 말그대로 요청(request)한 내용(body)를 처리하기 때문에
	//일반적인 파라미터 전달방식이 아닌 @PostMapping 사용
	@PostMapping("/ticket")
	public Ticket convert(@RequestBody Ticket ticket) {
		System.out.println("convert...........ticket" + ticket);
		return ticket;
	}
}
