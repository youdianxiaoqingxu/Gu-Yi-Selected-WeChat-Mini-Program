package com.guyi.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.guyi.common.BusinessException;
import com.guyi.dto.AddressDTO;
import com.guyi.entity.Address;
import com.guyi.mapper.AddressMapper;
import com.guyi.vo.AddressVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressMapper addressMapper;

    public List<AddressVO> list(Integer userId) {
        return addressMapper.selectList(new LambdaQueryWrapper<Address>()
                        .eq(Address::getUserId, userId)
                        .orderByDesc(Address::getIsDefault)
                        .orderByDesc(Address::getId))
                .stream()
                .map(address -> BeanUtil.copyProperties(address, AddressVO.class))
                .toList();
    }

    public AddressVO add(Integer userId, AddressDTO dto) {
        Address address = new Address();
        address.setUserId(userId);
        applyDto(address, dto);

        boolean isFirst = addressMapper.selectCount(new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId)) == 0;
        // 第一条地址自动设为默认，或显式指定为默认时先清除其他默认
        if (isFirst || (dto.getIsDefault() != null && dto.getIsDefault() == 1)) {
            clearDefault(userId);
            address.setIsDefault(1);
        } else {
            address.setIsDefault(0);
        }
        addressMapper.insert(address);
        return BeanUtil.copyProperties(address, AddressVO.class);
    }

    public AddressVO update(Integer userId, Integer id, AddressDTO dto) {
        Address address = requireAddress(userId, id);
        applyDto(address, dto);

        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            clearDefault(userId);
            address.setIsDefault(1);
        }
        addressMapper.updateById(address);
        return BeanUtil.copyProperties(address, AddressVO.class);
    }

    public void remove(Integer userId, Integer id) {
        requireAddress(userId, id);
        addressMapper.deleteById(id);
    }

    public void setDefault(Integer userId, Integer id) {
        Address address = requireAddress(userId, id);
        clearDefault(userId);
        address.setIsDefault(1);
        addressMapper.updateById(address);
    }

    private Address requireAddress(Integer userId, Integer id) {
        Address address = addressMapper.selectById(id);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(404, "收货地址不存在");
        }
        return address;
    }

    private void clearDefault(Integer userId) {
        addressMapper.update(null, new LambdaUpdateWrapper<Address>()
                .eq(Address::getUserId, userId)
                .set(Address::getIsDefault, 0));
    }

    private void applyDto(Address address, AddressDTO dto) {
        address.setName(dto.getName());
        address.setPhone(dto.getPhone());
        address.setProvince(dto.getProvince());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setDetail(dto.getDetail());
    }
}
