package ganadinote.location.mapper;

import ganadinote.location.domain.LocationDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper // 또는 @Repository
public interface LocationMapper {

    // 회원 코드(mbrCd)로 위도와 경도 정보를 조회하는 메서드
    LocationDTO getMemberLocation(Integer mbrCd);

}