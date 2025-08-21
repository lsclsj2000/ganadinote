package ganadinote.main.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ganadinote.common.domain.Pet;
import ganadinote.main.service.MainService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mainapi")
public class MainAPIController {
	
	private final MainService mainService;
	@GetMapping("pets/{mbrCd}")
	public ResponseEntity<List<Pet>> getPetInfoByMbrCd(@PathVariable String mbrCd){
			List<Pet> petList = mainService.getPetInfoByMbrCd(mbrCd);
		return ResponseEntity.ok(petList);
	}
	

}
