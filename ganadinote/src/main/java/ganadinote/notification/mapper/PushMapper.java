package ganadinote.notification.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import ganadinote.common.domain.PushSubscription;

@Mapper
public interface PushMapper {
	
	void addSubscription(PushSubscription subscription);
	
	List<PushSubscription> getSubInfoByMbrCd(@Param("mbrCd")Integer mbrCd);

}
