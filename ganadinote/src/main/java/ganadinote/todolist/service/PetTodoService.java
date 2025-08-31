package ganadinote.todolist.service;

import ganadinote.common.domain.Pet;
import java.util.List;

// [이름 변경] PetService -> PetTodoService
public interface PetTodoService { 
    List<Pet> getPetsByMbrCd(int mbrCd);
    
 // [추가!] petCd로 특정 펫 1건의 정보를 조회하는 기능 명세
    Pet getPetByCd(int petCd);
}