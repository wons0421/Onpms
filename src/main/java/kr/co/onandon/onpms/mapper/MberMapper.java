package kr.co.onandon.onpms.mapper;

import kr.co.onandon.onpms.dto.MberDto;
import kr.co.onandon.onpms.entity.Mber;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MberMapper {

    MberMapper INSTANCE = Mappers.getMapper(MberMapper.class);


    Mber joinReqToMber(MberDto.JoinRequestDto joinRequestDto);

    Mber loginReqToMber(MberDto.LoginRequestDto loginRequestDto);
}
