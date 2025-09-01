package ganadinote.todolist.mapper;

import ganadinote.common.domain.Pet;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
// [이름 통일] 인터페이스 이름을 PetTodoMapper로 합니다.
public interface PetTodoMapper { 
    List<Pet> getPetsByMbrCd(int mbrCd);
    
 // [추가!] petCd로 특정 펫 1건의 정보를 조회하는 메소드
    Pet getPetByCd(int petCd);
}